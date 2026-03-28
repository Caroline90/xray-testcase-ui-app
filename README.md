# Xray Testcase UI App

A Spring Boot web application for creating, editing, importing, and exporting **Xray-compatible manual test cases**.

## What this app does

- Provides a browser-based UI for managing test cases and steps.
- Stores test cases using Spring Data JPA (with H2 runtime database).
- Exposes REST APIs to create, update, list, and delete test cases.
- Imports test cases from CSV and validates import data.
- Exports test cases into CSV format suitable for Xray/Jira workflows.
- Includes Swagger/OpenAPI UI support via `springdoc-openapi`.

## Tech stack

- Java + Spring Boot 3
- Spring Web + Thymeleaf
- Spring Data JPA
- H2 database
- OpenCSV
- JUnit 5 / Spring Boot Test

## Run locally

```bash
./gradlew bootRun
```

Then open:

- App UI: `http://localhost:8080/`
- API base: `http://localhost:8080/api/testcases`
- Swagger UI (if enabled by config): `http://localhost:8080/swagger-ui.html`

## Key API endpoints

- `GET /api/testcases` — list test cases
- `POST /api/testcases` — create test case
- `PUT /api/testcases/{id}` — update test case
- `DELETE /api/testcases/{id}` — delete test case
- `GET /api/testcases/export` — export CSV
- `POST /api/testcases/import` — import CSV file (`multipart/form-data`)

## Run tests

```bash
./gradlew test
```
