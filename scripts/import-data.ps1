param(
    [string]$DumpFile = ".\dump.sql",
    [string]$Database = "patient_demo",
    [string]$HostName = "localhost",
    [int]$Port = 5432,
    [string]$Username = "postgres",
    [string]$PsqlPath = "psql"
)

# Check that the supplied dump exists.
if (-not (Test-Path $DumpFile)) {
    Write-Error "Dump file not found: $DumpFile"
    exit 1
}

# Locate psql.
# By default we expect it to be on PATH, but a full path can also be supplied.
if ($PsqlPath -eq "psql") {
    $psqlCommand = Get-Command psql -ErrorAction SilentlyContinue

    if (-not $psqlCommand) {
        Write-Error "psql was not found on PATH. Add PostgreSQL's bin directory to PATH or use -PsqlPath."
        exit 1
    }

    $PsqlPath = $psqlCommand.Source
}
elseif (-not (Test-Path $PsqlPath)) {
    Write-Error "psql not found at: $PsqlPath"
    exit 1
}

# Read the supplied pg_dump file.
$lines = Get-Content $DumpFile

function Get-CopyBlock {
    param(
        [string[]]$Content,
        [string]$TableName
    )

    $startPattern = "COPY public.$TableName "
    $start = -1

    for ($i = 0; $i -lt $Content.Length; $i++) {
        if ($Content[$i].StartsWith($startPattern)) {
            $start = $i
            break
        }
    }

    if ($start -eq -1) {
        throw "Could not find COPY block for table '$TableName'."
    }

    $block = @()

    for ($i = $start; $i -lt $Content.Length; $i++) {
        $block += $Content[$i]

        # PostgreSQL COPY data ends with \.
        if ($Content[$i] -eq "\.") {
            return $block
        }
    }

    throw "COPY block for table '$TableName' did not have an end marker."
}

try {
    # The supplied dump contains Action data before Patient data.
    # We deliberately extract both blocks and load Patient first because
    # action.patient_entity_id has a foreign key to patient.entity_id.
    $patientBlock = Get-CopyBlock -Content $lines -TableName "patient"
    $actionBlock = Get-CopyBlock -Content $lines -TableName "action"

    $tempFile = Join-Path $env:TEMP "patient-demo-import.sql"

    $output = @(
        "\set ON_ERROR_STOP on"
        "BEGIN;"
        "TRUNCATE TABLE action, patient;"
    )

    $output += $patientBlock
    $output += $actionBlock

    $output += @(
        "COMMIT;"
    )

    # Write UTF-8 without a BOM so psql receives clean SQL.
    $utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllLines(
            $tempFile,
            $output,
            $utf8WithoutBom
    )

    Write-Host "Loading supplied data into $Database..."

    & $PsqlPath `
        --host=$HostName `
        --port=$Port `
        --username=$Username `
        --dbname=$Database `
        --file=$tempFile

    if ($LASTEXITCODE -ne 0) {
        Write-Error "Data import failed."
        exit $LASTEXITCODE
    }

    Write-Host "Data import completed successfully."
}
catch {
    Write-Error $_
    exit 1
}
finally {
    if ($tempFile -and (Test-Path $tempFile)) {
        Remove-Item $tempFile -Force
    }
}