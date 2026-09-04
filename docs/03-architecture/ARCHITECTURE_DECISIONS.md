# Architecture Decision Log

## ADR-001 — Java 21 / Spring Boot server-rendered application

- **Decision:** Build a Java 21 Spring Boot application using Maven and domain-oriented packages.
- **Status:** Accepted.
- **Context:** The product needs a maintainable internal web foundation without a separate client application.
- **Rationale:** Spring Boot provides established web, validation, security, persistence, and testing support while Java 21 is the approved runtime.
- **Consequences:** The team maintains one deployable application; Java and Spring upgrades must be governed; speculative layers are discouraged.

## ADR-002 — Microsoft SQL Server persistence

- **Decision:** Use Microsoft SQL Server through the supported JDBC driver and Spring Data JPA.
- **Status:** Accepted.
- **Context:** Durable relational data and organizational compatibility are required.
- **Rationale:** SQL Server is the approved persistence platform and supports the expected relational model and operational controls.
- **Consequences:** Development and deployed environments require managed SQL Server connectivity; SQL must be SQL Server compatible; integration-test infrastructure remains to be selected.

## ADR-003 — Flyway-managed schema migrations

- **Decision:** Manage database schema evolution exclusively with ordered Flyway migrations.
- **Status:** Accepted.
- **Context:** Schema changes must be repeatable, reviewable, and traceable across environments.
- **Rationale:** Versioned migrations make database change part of the application delivery process.
- **Consequences:** Applied migrations are immutable and Hibernate schema generation is not a production migration mechanism.

## ADR-004 — Thymeleaf server-rendered UX

- **Decision:** Render the web experience with Spring MVC and Thymeleaf.
- **Status:** Accepted.
- **Context:** The MVP does not need a separately deployed SPA or duplicated client/server domain logic.
- **Rationale:** Server rendering fits operational forms and guided flows while keeping the technology footprint small.
- **Consequences:** Progressive enhancement is preferred; React, Angular, Vue, Node.js, and SPA infrastructure require a later decision.

## ADR-005 — Retro terminal visual design with modern accessibility

- **Decision:** Use an original late-1980s/early-1990s terminal-inspired design language while retaining modern web interaction and accessibility.
- **Status:** Accepted.
- **Context:** The product seeks a distinct operational aesthetic, not obsolete behavior or a copyrighted reproduction.
- **Rationale:** Monospace typography, high-contrast surfaces, status colors, box borders, selection states, and action footers can create the visual identity without compromising usability.
- **Consequences:** Semantic HTML, mouse and keyboard support, responsive layout, visible focus, labels, contrast, and assistive-technology behavior are mandatory.

## ADR-006 — No certificate deployment/private-key management in MVP

- **Decision:** The application records and guides operational work but does not deploy certificates or store/manage private keys.
- **Status:** Accepted.
- **Context:** Deployment and key custody create materially different security and operational risk.
- **Rationale:** Excluding them keeps the MVP focused on inventory, coordination, guidance, and audit.
- **Consequences:** Existing approved systems remain responsible for keys and deployment; any future expansion requires a separate security and architecture decision.

## ADR-007 — One certificate with many consumer relationships

- **Decision:** Store one authoritative Certificate and relate zero or many functions, systems, environments, hostnames, and sites through children or junctions.
- **Status:** Accepted.
- **Context:** One certificate may support many customer sites and systems.
- **Rationale:** Duplication per site creates conflicting identity, expiration, and history.
- **Consequences:** Detail/work queues aggregate consumers; junction integrity and cross-customer Site validation are required.

## ADR-008 — Separate historical Certificate Update entity

- **Decision:** Record each completed renewal/replacement as an immutable Certificate Update event while Certificate retains current facts.
- **Status:** Accepted.
- **Context:** Replacing the current expiration must not erase prior operational context.
- **Rationale:** An event can retain before/after facts, environments, user/time, cases, guidance, follow-ups, and notes.
- **Consequences:** Completion updates current facts and appends history atomically; correction and retention policies remain open.

## ADR-009 — Function / Usage and System / Integration classification

- **Decision:** Replace overlapping Product/Application/Interface certificate fields with multi-valued Function / Usage and System / Integration reference dimensions for MVP.
- **Status:** Proposed.
- **Context:** Existing terms overlap and do not clearly distinguish role from named consumer.
- **Rationale:** “What does it do?” and “what named system uses it?” are distinct and understandable.
- **Consequences:** Stakeholders must validate vocabulary/mapping from SharePoint. Product returns only if an independently useful hierarchy is proven.

## ADR-010 — Derived expiration health separate from operational state

- **Decision:** Derive Active/Expiring/Expired from current expiration and separately store the renewal work state.
- **Status:** Accepted.
- **Context:** Time risk and work progress can differ simultaneously.
- **Rationale:** Separate dimensions prevent an overloaded status and support accurate filtering.
- **Consequences:** Threshold/timezone are governed configuration; state transitions and health calculations are tested independently.

## ADR-011 — Narrow configurable guidance model

- **Decision:** Evaluate active rules against bounded certificate/update facts using one condition or simple AND conditions, with set membership as a bounded OR.
- **Status:** Proposed.
- **Context:** Operational reminders must change without deployment, but known use cases do not need an enterprise rules engine.
- **Rationale:** A small decision-table model is understandable, testable, and administrable.
- **Consequences:** No scripts, nested expressions, or external side effects; administrative authorization/version workflow remains open.

## ADR-012 — Snapshot historical guidance responses

- **Decision:** Persist the presented prompt/explanation/options, rule stable ID/version, selected response, identity/time, and follow-up outcome with the Certificate Update.
- **Status:** Accepted.
- **Context:** Configurable rules will change or be retired after updates occur.
- **Rationale:** Historical records must retain what the user actually saw and answered.
- **Consequences:** History duplicates small amounts of display data intentionally; rule edits create new versions and never cascade into responses.
