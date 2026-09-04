# Physical Schema

## Conventions

- SQL Server names use lowercase `snake_case`; constraints/indexes have explicit descriptive names.
- Primary keys use `BIGINT IDENTITY(1,1)`. This is compact, index-friendly, and sufficient for a single internal database; externally portable/offline-generated IDs are not required.
- Business dates use `DATE`; UTC audit instants use `DATETIME2(3)`; flags use `BIT`; user-facing text uses `NVARCHAR`; normalized codes/serial/fingerprints/hostnames use bounded `VARCHAR`.
- Mutable aggregate/reference masters use a non-null `BIGINT version` mapped with JPA `@Version`.
- JPA entity equality remains Java identity. Mutable or database-generated fields do not participate in `equals`/`hashCode`; IDs are used explicitly at service/repository boundaries.

## Tables

| Table | Primary/key columns | Foreign keys | Important constraints/indexes |
|---|---|---|---|
| `customer` | `id`, `customer_code`, `customer_name`, active/audit/version | — | Unique normalized `customer_code` |
| `site` | `id`, `site_code`, `display_name`, active/audit/version | `customer_id → customer` | Unique `(customer_id, site_code)`; customer/active index |
| `certificate_type` | `id`, `type_code`, `display_name`, active | — | Unique stable code |
| `function_usage` | `id`, `usage_code`, `display_name`, active | — | Unique stable code |
| `deployment_environment` | `id`, `environment_code`, `display_name`, active | — | Unique stable code; avoids reserved `environment` ambiguity |
| `system_integration` | `id`, `system_code`, `display_name`, active/audit/version | — | Unique normalized code; active/name index |
| `certificate` | `id`, name, issuer, expiration, serial, fingerprint, subject CN, notes, active/audit/version | Customer and Certificate Type | Customer index; expiration/active index; name index |
| `certificate_hostname` | `id`, normalized hostname, created time | Certificate with cascade delete | Unique `(certificate_id, hostname)` |
| `certificate_function_usage` | `id`, created time | Certificate and Function Usage | Unique pair; certificate cascade delete |
| `certificate_system_integration` | `id`, created time | Certificate and System Integration | Unique pair; reverse lookup index |
| `certificate_environment` | `id`, created time | Certificate and Environment | Unique pair |
| `certificate_site` | `id`, created time | Certificate and Site | Unique pair; reverse lookup index |

Junctions are explicit entities rather than implicit `@ManyToMany`. Their generated IDs and timestamps provide stable extension points for future relationship audit/effective dating while preserving clear SQL constraints and controlled cascade only from Certificate to owned links. Reference/master rows never cascade-delete.

## Validation and optionality

- Customer code/name required; code normalized uppercase and unique globally.
- Site customer, code, and display name required; code unique within customer.
- System code/display name required; code normalized uppercase and unique globally.
- Certificate customer, name, active Certificate Type, expiration, and at least one active Function / Usage required.
- Systems, environments, hostnames, and sites are optional because approved requirements favor incomplete-but-useful initial inventory over unsupported restrictions.
- Hostnames accept DNS names, wildcard DNS names, and simple host labels; whitespace, empty labels, and malformed values are rejected. SAN metadata is represented by zero or more owned hostname rows.
- Serial, normalized fingerprint, issuer, and subject/CN are optional metadata. No binaries or private keys are stored.
- A Site association must belong to the same Customer as its Certificate.

## Duplicate policy

Only governed business-key duplicates (customer code, site code within customer, reference/system codes, relationship pairs, hostname within certificate) are rejected. Candidate certificate signals are customer + name, serial, normalized fingerprint, subject/CN, and hostname overlap. Fingerprint is normalized by removing spaces/colons and uppercasing but is not unique globally or per customer pending real data analysis.

## Reference data

Flyway seeds stable codes and display labels for five certificate types, nine Function / Usage values, and five environments. Customer-specific systems are not seeded. Runtime code resolves active references by stable code.

## Migration sequence

1. `V1__create_application_metadata.sql` — unchanged WP-001 infrastructure.
2. `V2__create_customer_and_site.sql` — local customer/site masters.
3. `V3__create_certificate_reference_data.sql` — controlled reference and system master tables.
4. `V4__create_certificate_inventory.sql` — certificate, hostname, links, constraints, and indexes.
5. `V5__seed_certificate_reference_data.sql` — stable initial reference rows.

SQL Server migrations are authoritative. Hibernate uses `validate` in database profiles and `create-drop` only for H2-backed tests.
