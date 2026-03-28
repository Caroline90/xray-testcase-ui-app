# Xray Testcase UI App

A Spring Boot web application for managing **Xray-compatible manual test cases** through a browser UI and REST API. It supports end-to-end testcase authoring workflows: create/edit testcases, manage step-by-step actions, import from CSV with validation feedback, and export to CSV for Xray/Jira usage.

## Features

- Browser-based testcase editor built with Thymeleaf + vanilla JavaScript.
- Persistent storage using Spring Data JPA.
- REST API for CRUD operations on testcases.
- CSV export in an Xray-oriented format.
- CSV import endpoint with validation results and preview support.
- Swagger/OpenAPI integration for API exploration.

## Tech Stack

- Java 17+
- Spring Boot 3.2.x
- Spring Web + Thymeleaf
- Spring Data JPA
- H2 (runtime)
- OpenCSV
- JUnit 5 + Spring Boot Test

## Prerequisites

- JDK 17 or newer
- No manual database setup required for local development

## Running Locally

Start the app:

```bash
./gradlew bootRun
```

Default URLs (this project is configured for **port 9090**):

- UI: http://localhost:9090/
- REST API base: http://localhost:9090/api/testcases
- Swagger UI: http://localhost:9090/swagger-ui/index.html

## Running Tests

```bash
./gradlew test
```

## API Reference (Core Endpoints)

Base path: `/api/testcases`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/testcases` | List all testcases |
| POST | `/api/testcases` | Create a testcase |
| PUT | `/api/testcases/{id}` | Update a testcase |
| DELETE | `/api/testcases/{id}` | Delete a testcase |
| GET | `/api/testcases/export` | Export all testcases to CSV |
| POST | `/api/testcases/import` | Import CSV (`multipart/form-data`, file field: `file`) |

### Create/Update Payload Example

```json
{
  "summary": "Verify login with valid credentials",
  "precondition": "User account exists",
  "testType": "Manual",
  "component": "Authentication",
  "steps": [
    { "action": "Open login page", "data": "", "expected": "Login page is displayed" },
    { "action": "Enter valid username and password", "data": "test_user", "expected": "Credentials are accepted" },
    { "action": "Click Sign In", "data": "", "expected": "Dashboard is displayed" }
  ]
}
```

## CSV Workflow Notes

- **Export:** each testcase is emitted with one or more rows. The first row contains testcase metadata plus first step; subsequent rows contain additional step data.
- **Import:** send a CSV file to `/api/testcases/import`; the response includes validation status information (`valid`, `errors`, and parsed `preview`).

## Project Structure

```text
src/main/java/com/example/xray/
├── controller/   # Web + REST controllers
├── model/        # JPA entities (TestCase, TestStep)
├── repository/   # Spring Data repositories
└── service/      # Business logic + CSV handling

src/main/resources/
├── templates/    # Thymeleaf UI
└── application.properties
```

## Notes

- The UI is intentionally lightweight and server-rendered, with front-end behavior implemented directly in the template.
- H2 is used as a runtime dependency for quick local startup.

