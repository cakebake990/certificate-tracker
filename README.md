# Certificate Tracker

Certificate Tracker is an internal, server-rendered application for maintaining a certificate inventory, understanding operational impact, and guiding certificate renewal work. The current repository contains the project foundation only; certificate business functionality is not yet implemented.

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

Spring Security is present. A narrowly scoped, temporary security configuration permits the foundation landing page and static assets only. It does not define users, roles, or production-ready authentication and must be replaced when the security model is governed.

## Governance and status

The foundation includes a minimal accessible terminal-style landing screen and an infrastructure-only Flyway migration. No domain CRUD, certificate schema, integrations, or automation exists yet. Start with the [project charter](docs/01-governance/PROJECT_CHARTER.md), [MVP scope](docs/02-requirements/MVP_SCOPE.md), and [architecture decisions](docs/03-architecture/ARCHITECTURE_DECISIONS.md).
