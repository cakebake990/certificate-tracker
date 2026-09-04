# Known Gaps and Future Scope

The following capabilities are explicitly not implemented or committed for the MVP. Each requires separate product, security, architecture, and operational review.

- ServiceNow integration and automated case creation.
- Centralized Java trust-store distribution or automatic modification of `cacerts`.
- SFTP-based distribution.
- Automatic certificate discovery.
- Automated endpoint validation.
- Certificate deployment.
- Private-key and secrets management.
- Advanced notifications, escalation policies, and delivery channels.
- ML/AI-generated recommendations.

The final identity and role model, production secret storage, database operating model, observability, backup/recovery expectations, and deployment platform also remain to be governed.

## Data and workflow decisions still open

- Customer and site master-data source, identifiers, synchronization, aliases, and inactive-record handling.
- Authentication provider, user identity retention, Support versus Implementation permissions, and limited guidance-administrator roles.
- Expiration thresholds, business timezone, notifications, escalation rules, and delivery channels.
- Certificate fingerprint, serial number, subject/SAN, issuer normalization, and duplicate-certificate detection.
- Whether certificate binaries and attachments are prohibited, externally linked, or stored under a separately approved security model.
- Automated certificate inspection and endpoint validation inputs; neither is MVP scope.
- SharePoint migration mapping, data cleansing, duplicate resolution, reconciliation, and cutover ownership.
- Reporting, exports, retention, audit correction, and operational metrics.
- Support-case mandatory scenarios, number/link validation, and future ServiceNow integration boundaries.
- Wizard draft retention/concurrency and whether relationship edits belong in the Update flow.
- SQL Server integration environment/CI credentials and execution of V2–V5 against supported SQL Server versions.
- Certificate duplicate disposition: warn, block, or permit; fingerprint uniqueness scope is intentionally unresolved.
- Customer/Site/System reference-data administration screens, deactivation effects, and merge/correction behavior.
- Two-transaction SQL Server optimistic-lock verification and database collation/case-sensitivity behavior.
