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
