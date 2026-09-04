# Coding Standards

## Java and Spring

- Target Java 21 and the repository-managed Spring Boot version.
- Follow Spring conventions and favor constructor injection. Do not use field injection.
- Organize production code by domain under `com.viapath.certificatetracker`; use `application`, `security`, and `shared` only for genuinely cross-domain responsibilities.
- Validate untrusted input at system boundaries with Bean Validation. Do not rely on browser validation alone.
- Translate expected failures into consistent, useful responses. Use a centralized handler when multiple endpoints need the same policy; do not catch broad exceptions merely to suppress them.
- Use SLF4J parameterized logging. Never log credentials, private keys, certificate secrets, or unnecessarily sensitive customer data.
- Avoid speculative interfaces, base classes, repositories, services, and generalized frameworks.

## Testing

- Add focused unit tests for business rules and boundary tests for MVC, persistence, and security behavior.
- Keep the default test suite deterministic and independent of external services. Database integration tests must use an explicit, documented test facility.
- Name tests by observable behavior and cover validation and failure paths in proportion to risk.

## Persistence and migrations

- Flyway is the sole mechanism for managed schema changes. Never rely on Hibernate schema creation outside disposable tests.
- Once shared, migration files are immutable; correct them with a new, forward migration.
- Keep migrations small, ordered, SQL Server compatible, and reviewed alongside the code that needs them.
- Do not introduce domain tables before their model is governed.

## Security and secrets

- Keep secrets out of source, fixtures, logs, URLs, and documentation examples. Resolve them from approved environment or secret-management facilities.
- Deny access by default and explicitly authorize endpoints. Temporary security behavior must be isolated and labeled.
- Treat all request data as untrusted and use framework protections for CSRF, output encoding, and session handling.

## Web and CSS

- Use semantic HTML, associated labels, logical headings, keyboard operability, visible focus, adequate contrast, and screen-reader-compatible status text.
- Prefer reusable CSS variables and component classes in focused stylesheets. Do not encode meaning by color alone or add a front-end framework without an architecture decision.

## Documentation

- Update governing documents when a decision, scope boundary, setup step, or operational assumption changes.
- Record meaningful architecture choices in the decision log and keep the README accurate for a new developer.
