## Data assumptions and inconsistencies
I found one patient in the supplied data who has a discharge date but no registration date. 
I therefore did not assume that the lifecycle dates would always be complete.
For the dashboard I derive the current status by checking discharge first, then registration, then invitation. 
This also means each patient belongs to one status only.

### Loading the supplied data

The database schema is managed separately by Flyway. The supplied PostgreSQL
dump is used only as the source of the fake patient and action test data.

`dump.sql` is deliberately excluded from this repository as required by the
task. Before running the local data import, place the supplied `dump.sql` file
in the root directory of the project.

`scripts/import-data.ps1` extracts the Patient and Action data from the dump.
The supplied dump contains the Action data before the Patient data, so the
script deliberately loads Patients first to satisfy the foreign-key relationship.

The import clears and reloads the supplied demo dataset, so it can be run
repeatedly during development.

If `psql` is available on PATH:

    .\scripts\import-data.ps1

If `psql` is not on PATH, provide its location explicitly. For example, with
PostgreSQL 16 installed in the default Windows location:

    .\scripts\import-data.ps1 -PsqlPath "C:\Program Files\PostgreSQL\16\bin\psql.exe"

I verified the import by running it twice and confirming that the database
contained 100 Patients and 3,183 Actions after both runs.