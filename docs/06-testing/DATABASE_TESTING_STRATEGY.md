# Database Testing Strategy

## Default automated suite

- Pure domain tests use fixed clocks and no database, including every expiration threshold boundary.
- Service/repository tests use H2 in-memory with MSSQL compatibility mode and Hibernate `create-drop` under the `test` profile.
- Tests cover validation, customer/site ownership, reference lookup, all certificate associations, hostname normalization, detail loading, inventory queries, and optimistic-version increments.
- Tests are transactional and do not rely on generated-ID or insertion ordering.

H2 proves JPA mappings and domain/service behavior. It does **not** prove T-SQL syntax, SQL Server types/indexes, constraint behavior, Flyway execution, collation, or query plans. Flyway is deliberately disabled for H2 rather than modifying production migrations for H2 compatibility.

## SQL Server integration strategy

Use an externally configured disposable SQL Server database in CI or a developer-approved local instance. This avoids committing Docker/Testcontainers licensing and runtime assumptions now. The integration job should:

1. Provision an empty database dedicated to the run.
2. Provide `CERTTRACK_DB_URL`, `CERTTRACK_DB_USERNAME`, and `CERTTRACK_DB_PASSWORD` through CI secrets.
3. Start with the `qa` profile so Flyway applies V1–V5 and Hibernate validates mappings.
4. Run a separately tagged Maven integration suite that verifies seeded codes, FK/unique constraints, optimistic locking, repository queries, and clean migration from empty schema.
5. Destroy or reset only the dedicated database through approved infrastructure.

A dedicated Maven `sqlserver-integration` profile/test source should be added when the CI database endpoint and credential process are approved. Testcontainers SQL Server may be reconsidered after container runtime, image licensing acceptance, startup cost, and CI support are confirmed.

## Current limitations

- V2–V5 have been reviewed as T-SQL but not executed against SQL Server in this workspace.
- No automated migration rollback is promised; Flyway migrations are forward-only.
- Collation/case behavior, `NVARCHAR(MAX)`, cascade behavior, and index usefulness require SQL Server validation.
- Concurrency coverage currently verifies `@Version` increments; a two-transaction stale-write integration test belongs in the SQL Server suite.
