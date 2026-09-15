# Patient Demo

Spring Boot application for viewing and searching the supplied fake patient and action data.

The application provides a JSON API and Thymeleaf browser views backed by PostgreSQL.

## Technology

* Java 21
* Spring Boot
* Maven
* PostgreSQL 16
* Spring Data JPA
* Flyway
* Thymeleaf

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

### Logging

Search requests log pagination, sorting, supplied-filter flags and result counts. Search text and patient identifiers are not logged.

### AI verification

An initial AI-assisted review of the supplied data incorrectly identified **79 Registered** and **4 Invited** patients. Testing the API in Postman returned **80 Registered** and **3 Invited**, so I checked the data directly in PostgreSQL and confirmed the API results were correct.
