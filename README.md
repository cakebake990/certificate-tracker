# Certificate Tracker

Certificate Tracker is an internal, server-rendered application for maintaining a certificate inventory and understanding operational impact. The current implementation includes certificate search, detail, and a five-step Add Certificate wizard; renewal guidance and operational work tracking remain future work.

## Technology

Java 21, Spring Boot 4.1.1, Maven, Spring MVC, Thymeleaf, Spring Security, Bean Validation, Spring Data JPA, Microsoft SQL Server, Flyway, and Spring Boot Test.

## Repository layout

- `src/main/java/com/viapath/certificatetracker` — application bootstrap and domain-oriented packages as features are added.
- `src/main/resources` — profiles, Flyway migrations, templates, and static assets.
- `src/test/java` — deterministic foundation tests.
- `docs` — governed product, architecture, UX, data, testing, and operations documentation.

## Prerequisites

- JDK 21
- Maven 3.6.3 or newer
- Access to Microsoft SQL Server only when using a database-dependent profile

Confirm `java -version` points to Java 21 before building.

## Build and test

```powershell
mvn test
mvn package
```

Run the database-independent foundation shell with:

```powershell
mvn spring-boot:run
```

Then open `http://localhost:8080/`. The default profile intentionally disables database, JPA, and Flyway auto-configuration so a new developer can validate the web foundation without SQL Server.

Business routes are enabled with the `dev`, `qa`, and `test` profiles:

- `/` — application home
- `/certificates` — server-side certificate search and filters
- `/certificates/{id}` — certificate detail
- `/certificates/new` — session-backed Add Certificate wizard

The wizard stores only a serializable form DTO in the HTTP session. It writes no domain data until final confirmation, then clears the session state and redirects to detail using Post/Redirect/Get.

## Local SQL Server configuration

The `dev` and `qa` profiles enable SQL Server persistence and Flyway. Supply credentials through the process environment; never add credentials to a tracked file.

```powershell
$env:CERTTRACK_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=certificate_tracker;encrypt=true;trustServerCertificate=true'
$env:CERTTRACK_DB_USERNAME = '<local-user>'
$env:CERTTRACK_DB_PASSWORD = '<local-password>'
mvn spring-boot:run '-Dspring-boot.run.profiles=dev'
```

Missing variables cause startup to fail at configuration binding/connection time. Use an OS-level secret facility or untracked local shell configuration for developer credentials. Production authentication, authorization, and secret-management decisions remain open.

## Security status

Spring Security is present. A temporary security configuration permits the home, certificate, and static-asset routes. It does not define users, roles, or production-ready authentication and must be replaced when the security model is governed.

## Governance and status

WP-004 delivers the accessible terminal-style inventory, detail, and Add flow over the WP-003 certificate schema. Update/Replace, guidance, follow-ups, support cases, administration, integrations, and automation are not implemented. Start with the [project charter](docs/01-governance/PROJECT_CHARTER.md), [MVP scope](docs/02-requirements/MVP_SCOPE.md), and [architecture decisions](docs/03-architecture/ARCHITECTURE_DECISIONS.md).
