# Patient Demo

Spring Boot application for viewing and searching the supplied fake patient and action data.

This submission implements the AI-assisted task from the assessment.

The application provides a JSON API and Thymeleaf browser views backed by PostgreSQL.

## Technology

* Java 21
* Spring Boot
* Maven
* PostgreSQL 16
* Spring Data JPA
* Flyway
* Thymeleaf
* SpringDoc OpenAPI
* Docker Compose
* Testcontainers

## Running with Docker Compose

### Prerequisites

* Docker Desktop
* The supplied `dump.sql`

`dump.sql` is deliberately excluded from this repository as required by the task. Place the supplied `dump.sql` in the project root before starting the application.

Run:

```powershell
docker compose up --build
```

Docker Compose starts PostgreSQL, builds and starts the application, runs the Flyway migration and then loads the supplied patient and action data.

The data loader waits for the Flyway-managed tables before importing the data. It clears and reloads the supplied dataset, making the import safe to run repeatedly.

I verified the complete Compose startup more than once from a stopped environment.

Once started, the application is available at:

```text
http://localhost:8080/
```

Useful URLs:

```text
http://localhost:8080/                  Dashboard
http://localhost:8080/dashboard         Dashboard
http://localhost:8080/patients          Patient search and list
http://localhost:8080/swagger-ui.html   Swagger UI
http://localhost:8080/v3/api-docs       OpenAPI JSON
http://localhost:8080/actuator/health   Application health
```

Stop the application with `Ctrl+C`. The containers can then be removed with:

```powershell
docker compose down
```

## Running locally

### Prerequisites

* Java 21
* Maven, or use the included Maven wrapper
* PostgreSQL 16
* `psql`

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

## Implementation notes

### Patient status

Status is derived from the lifecycle timestamps and is mutually exclusive:

* `DISCHARGED` – discharge timestamp present
* `REGISTERED` – registration present and not discharged
* `INVITED` – otherwise

Verified against the supplied data: **3 Invited, 80 Registered, 17 Discharged**.

One patient is discharged without a registration timestamp. Discharge takes precedence, so the patient is classified as Discharged.

### Patient API

Patient search uses JPA Specifications for composable server-side filtering, pagination and sorting. Search covers name, NHS number and hospital ID. Filters currently include status, action module and invitation date.

DTOs are returned instead of JPA entities to avoid exposing internal persistence fields. Patient URLs use the public UUID rather than `entity_id`.

Tested locally with:

```text
GET /api/patients?page=0&size=10
GET /api/patients?search=Eddie&page=0&size=10
GET /api/patients?status=DISCHARGED
GET /api/patients?status=REGISTERED
GET /api/patients?status=INVITED
GET /api/patients?module=ASSESSMENT
GET /api/patients?status=REGISTERED&module=PROGRAMME&page=0&size=10
GET /api/patients/{uuid}
GET /api/patients/{uuid}/actions?page=0&size=10&sort=whenRecorded,asc
```
### Dashboard

The dashboard provides mutually exclusive counts for Invited, Registered and
Discharged patients, together with the total patient count.

Browser view:

```text
/                    Dashboard
/dashboard           Dashboard
/patients            Patient search and list
```

### Testing

Unit tests cover the derived Patient status and age logic, including:

* invited patients
* registered patients
* discharged patients
* discharge without registration
* birthdays before and after the reference date
* missing date of birth
* future date of birth
* REST API controllers
* Thymeleaf view controllers

JUnit 5 and Mockito are used for unit testing. These tests use mocked dependencies where appropriate and do not require a running PostgreSQL database.

Run the unit tests without the PostgreSQL integration test:

```powershell
.\mvnw test -Dtest='!PatientApiIntegrationTest'
```
PatientApiIntegrationTest provides integration testing against a real PostgreSQL database using Testcontainers.

This test requires Docker to be installed and running.

With newer Docker Desktop versions, Testcontainers may require a compatible Docker API version. On Windows PowerShell:

```powershell
"api.version=1.44" | Set-Content "$HOME\.docker-java.properties"
Get-Content "$HOME\.docker-java.properties"
.\mvnw test -Dtest=PatientApiIntegrationTest
```
Given the time-boxed nature of the assessment, I kept the PostgreSQL integration test under the existing
`src/test/java` structure rather than introducing a separate integration-test source directory and additional
Maven configuration. 
In a larger project, I would separate unit and integration tests so they can be run independently.

### Logging

Search requests log pagination, sorting, supplied-filter flags and result counts. Search text and patient identifiers 
are not logged.

## Implementation problems and trade-offs

The assessment was time-boxed, so I prioritised the database migration and repeatable data loading, dashboard, patient search/detail views, REST endpoints and tests rather than attempting to partially implement every requirement.

### 1. Docker data-loading startup race – fixed

**Location:** `docker-compose.yml` and `scripts/import-data.sh`

Initially the data-loader could start once PostgreSQL was healthy but before the application had completed its Flyway migration. This caused the loader to attempt to truncate tables that did not yet exist.

This would make application startup unreliable. I fixed it by making the loader wait until the Flyway-created Patient and Action tables exist before importing the supplied data. I then repeated the Compose build/start process to verify the fix.

### 2. Authentication and role-based access – not implemented

**Location:** REST and MVC endpoints

The requested clinical read-only and admin roles have not been implemented. Consequently there are no role credentials supplied with this demo.

This would be a high-severity issue for real patient data. Before production use I would add authentication using Spring Security with the organisation's identity provider, enforce role-based endpoint access, restrict exports to administrators and add appropriate security auditing.

### 3. Large-scale search performance – not fully evaluated

**Location:** patient search specifications and database queries

Free-text search and combined filters work for the supplied dataset, but I have not generated the requested approximately 100x dataset or produced query plans for the slowest endpoint.

### 4. Paging and sorting validation – incomplete

**Location:** patient list API and MVC controller

Paging and sorting are handled server-side, but the allowed sort fields and requested page sizes should be explicitly validated and bounded.

Without this, invalid sort properties can produce avoidable client/server errors and excessively large page requests could create unnecessary database and application load. I would whitelist sortable fields, cap page size and return a consistent `400` response for invalid requests.

### 5. N+1/query-count regression test – not implemented

**Location:** persistence/integration tests

The application has service and controller tests plus a real PostgreSQL integration test, but I did not complete the requested query-count/N+1 regression test.

### 6. Remaining analytical views – not implemented

The funnel/conversion metrics and weekly active patient time-series were not completed within the time box.

I chose to leave these clearly unimplemented rather than add calculations that I had not had time to verify against the supplied data.

### 7. CSV export – not implemented

The requested streaming CSV export was not completed.

## AI-assisted development and verification

I used AI assistance during development for implementation suggestions, test ideas, debugging and reviewing the assessment requirements. I treated generated suggestions as starting points rather than assuming their output was correct.

One AI-assisted review of the supplied data incorrectly identified **79 Registered and 4 Invited** patients. Testing the API returned **80 Registered and 3 Invited**, so I queried the supplied data directly in PostgreSQL and confirmed that the API figures were correct.

During Docker setup, I also verified suggested changes by rebuilding and running the complete Compose environment rather than documenting the configuration as working before seeing it run. This exposed a real startup race between the data-loader and Flyway. I fixed the loader ordering and repeated the build/start process successfully.

The final verified dashboard figures are **3 Invited, 80 Registered, 17 Discharged and 100 Total**.
