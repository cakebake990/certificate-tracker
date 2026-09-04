# Requirements Traceability Map

| Story | Primary entities | UX/screens | Lifecycle and requirement links |
|---|---|---|---|
| CT-US-001 | Certificate, Customer, Environment, Follow-up, Support Case | Expiration Work Queue, Home | [Lifecycle](CERTIFICATE_LIFECYCLE.md) |
| CT-US-002 | Certificate and all consumer junctions, Hostname, Site | Search, Certificate Detail | [Logical model](../05-data/DATA_MODEL_PROPOSAL.md) |
| CT-US-003 | Customer, Certificate, Type, Function, System, Environment, Hostname, Site | [Add flow](../04-ux/ADD_CERTIFICATE_FLOW.md) | Single-certificate principle; Add validation |
| CT-US-004 | Certificate, Certificate Update, Update Environment, Guidance Response | [Update flow](../04-ux/UPDATE_CERTIFICATE_FLOW.md) | [Lifecycle completion](CERTIFICATE_LIFECYCLE.md) |
| CT-US-005 | Guidance Rule/Condition/Response, Certificate Update | Update guidance step | [Adaptive guidance](ADAPTIVE_GUIDANCE_MODEL.md) |
| CT-US-006 | Follow-up Action, Certificate Update, optional Support Case | Update follow-up/cleanup/completeness, Follow-up Detail | [Follow-ups](FOLLOW_UP_ACTIONS.md); Follow-up Required state |
| CT-US-007 | Support Case Reference, Certificate Update, Follow-up | Update case step, Detail, History | [Support case model](SUPPORT_CASE_REFERENCE.md) |
| CT-US-008 | Guidance Rule, Rule Condition | Guidance list/add/edit | [Adaptive guidance](ADAPTIVE_GUIDANCE_MODEL.md) |
| CT-US-009 | Certificate Update, Guidance Response, Follow-up, Support Case | Certificate History/Detail | Immutable history and correction open decision |

The complete planned screen set is governed in [Screen Inventory](../04-ux/SCREEN_INVENTORY.md).

## WP-004 implementation evidence

| Requirement | Implementation | Automated evidence |
|---|---|---|
| CT-US-002 inventory discovery | Server-side partial-name, customer, health, and optional system filters; detail projection includes all inventory relationships | `CertificateWebControllerTests.inventoryLoadsAndAppliesSearchFilters`; `detailLoadsAndUnknownCertificateReturnsOperational404` |
| CT-US-003 short Add flow | Five server-rendered steps, dedicated session DTO, active lookup values, customer-scoped sites, review, transactional create, PRG | `CertificateWebControllerTests.wizardValidatesCurrentStepAndPreservesStateWhenNavigatingBack`; `completionCreatesOnceAndCancelClearsWizard` |
| CT-US-003 relationship integrity | Service resolves active references, checks customer/site ownership, normalizes metadata/hostnames, and uses set-backed selections | `CertificateInventoryServiceTests.createsAndRetrievesCertificateWithAllOptionalAssociations`; `rejectsInvalidHostnameAndCrossCustomerSite`; `rejectsInactiveReferencesAndDeduplicatesJunctionSelections` |
| Accessibility and error handling | Semantic headings, labels, fieldsets/legends, visible focus, text health labels, same-step errors, safe 404/error view | Controller rendering tests plus reusable patterns in `TERMINAL_UI_COMPONENTS.md` |
