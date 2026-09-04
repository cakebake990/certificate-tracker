# Governed User Stories

These criteria govern MVP intent. Thresholds, controlled values, permissions, and policies identified as open decisions are refined before implementation. No story authorizes deployment, key handling, discovery, distribution, or ServiceNow case creation.

## CT-US-001 — View upcoming expirations

**As Support, I want to view certificates approaching or past expiration so the team can prioritize work.**

Acceptance criteria:

1. The work queue shows one row per certificate, never one duplicate row per affected site.
2. Each row shows certificate/customer identity, expiration date, derived health, operational renewal state, and open-follow-up/case indicators.
3. Users can filter by health, renewal state, customer, environment, and relevant date window and sort by expiration.
4. Active, Expiring, and Expired are derived from expiration date and governed threshold/timezone.
5. The queue is keyboard operable, has semantic table/list structure, and does not communicate status by color alone.

## CT-US-002 — Understand certificate impact

**As Implementation or Support, I want one certificate detail view showing all consumers so I can assess impact.**

Acceptance criteria:

1. Detail shows current identity, customer, type, issuer, expiration/health, operational state, and notes.
2. It lists all related Function/Usage values, Systems/Integrations, environments, hostnames, and sites without duplicating the certificate.
3. Sites are limited to the certificate customer; duplicate relationships are not shown.
4. Current open follow-ups, latest case context, and update-history entry points are visible.
5. Empty groups are labeled as none recorded rather than omitted ambiguously.

## CT-US-003 — Add a new certificate

**As Implementation, I want a short guided add flow so I can establish an accurate inventory record quickly.**

Acceptance criteria:

1. The flow follows Customer, Identity/details, Function/Usage, Where used, and Review/create.
2. Each screen has one primary question, validates required data, and preserves values through Back/Edit navigation.
3. Users may relate multiple systems, functions, environments, hostnames, and customer sites to one certificate.
4. Review displays all current entries and allows editing before an idempotent create action.
5. Invalid dates/hostnames, stale references, cross-customer sites, and duplicate relationships are rejected with accessible errors.
6. No certificate binary or private key is requested.

## CT-US-004 — Update an expiring certificate

**As Support, I want a guided replacement flow so current facts change without losing renewal history.**

Acceptance criteria:

1. The flow captures replacement facts, environment/system outcomes, applicable guidance, cleanup, follow-ups, case reference, and review.
2. Previous/new expiration, issuer-change fact, environment outcomes, time/user, notes, case references, guidance snapshots/responses, and created follow-ups are recorded in one completed Certificate Update event.
3. Required replacement facts, guidance, and expected context outcomes block completion when unanswered/invalid.
4. Explicit open follow-ups and a documented no-case response do not inherently block completion.
5. Completing atomically updates current certificate facts and writes immutable event history; duplicate submission does not create duplicate events.
6. Back/Edit navigation preserves draft values and applicable responses.

## CT-US-005 — Receive relevant operational reminders

**As Support, I want only applicable guidance during an update so I remember operational dependencies.**

Acceptance criteria:

1. Only active, effective rules whose bounded conditions match selected certificate/update facts are presented in deterministic order.
2. Each prompt presents its configured controlled responses and optional explanation accessibly.
3. For a certificate related to OMS, a configured rule can ask “Did you also check OMS Integration?”
4. Selecting Needs follow-up can prefill `Verify OMS Integration` for user review.
5. Changing trigger facts re-evaluates guidance and warns before discarding a draft response.
6. Presented content and response are snapshotted so later rule edits do not alter history.

## CT-US-006 — Capture unfinished work

**As Support, I want to record multiple follow-ups so an update can complete while outstanding work remains visible.**

Acceptance criteria:

1. Multiple Open actions may be created from guidance, system checks, cleanup, completeness, or review.
2. Each action has a description, originating update/certificate, status, audit metadata, optional due date, and optional originating case.
3. Individual assignment is not required; Open actions appear in the shared Support work queue and certificate detail.
4. An update may complete with valid Open actions and then has operational state Follow-up Required.
5. Support can complete or cancel an Open action; terminal actions remain historical and the last resolution permits state Complete.
6. Staging cleanup can propose `Remove previous certificate from Staging`.

## CT-US-007 — Link the support case

**As Support, I want to link external cases to a certificate update so case context is visible without recreating ServiceNow.**

Acceptance criteria:

1. Each update records whether a case exists and may contain zero or more case references.
2. A reference stores external system/type, case number, optional safe URL, and linked audit time.
3. Certificate detail/history identifies which update each case belongs to and whether current work has no case.
4. Case comments, assignee, SLA, incident state, and ServiceNow workflow are not stored.
5. Missing case data blocks adding that reference, but a recorded no-case outcome does not inherently block update completion.

## CT-US-008 — Maintain update guidance

**As a limited administrator, I want to configure bounded guidance so operational knowledge changes without application deployment.**

Acceptance criteria:

1. An administrator can create, preview/test, activate, order, version, and retire a rule subject to final authorization policy.
2. Rules support governed facts/operators and either one condition or simple AND conditions; arbitrary expressions/code are rejected.
3. Required stable identity, status, prompt, responses, priority, effective dates, and audit metadata are validated.
4. Follow-up templates map only configured responses to a proposed action; rules cannot deploy or call external systems.
5. Editing an effective rule creates a new version and does not mutate prior Guidance Responses.

## CT-US-009 — Review certificate history

**As Support or an authorized reviewer, I want to inspect update history so I can understand what changed and what guidance was followed.**

Acceptance criteria:

1. History lists completed update events chronologically with update type, previous/new expiration, time, and user identity.
2. Event detail shows environment/system outcomes, cases, notes, presented guidance and selected responses, and originating follow-ups including terminal state.
3. Rule changes or reference-data renaming do not rewrite snapshotted historical meaning.
4. History is read-only except through a separately governed correction mechanism.
5. Authorization and retention/export policy remain open decisions and must not be inferred from general access.
