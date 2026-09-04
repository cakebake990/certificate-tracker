# Support Case Reference

Certificate Tracker records enough external case context to answer:

- Has Support opened a case for this update?
- What is the case number and optional link?
- Which Certificate Update owns the reference?
- Does current work explicitly have no case?

## Recommended MVP model

Each Certificate Update records a case outcome and zero or more lightweight references. `No case` is an explicit update answer, not inferred from a null value. Multiple references preserve replacement/reopened cases without recreating case management. The latest operational context is derived from current renewal work and its update/draft; Certificate has no mutable “current case” field.

| Data | Requirement |
|---|---|
| Case outcome | Case exists / No case; required during update review. |
| External system/type | Required for a reference; controlled/default value may be ServiceNow. |
| Case number | Required for a reference and unique only under rules of the external system. |
| URL | Optional, validated safe external link. |
| Linked time/identity | Required audit metadata. |
| Update | Required parent relationship. |

A Follow-up may optionally refer to one case belonging to its originating update. References remain visible in certificate history after external closure.

## Explicit exclusions

Do not copy ticket comments, ownership, assignment groups, priority, state transitions, SLAs, incident workflow, or case attachments. Do not create or update ServiceNow cases in MVP.

## Completion and open decisions

Recording `No case` does not inherently block certificate update completion. Product ownership must decide whether specific environment/update scenarios require a case, how links are constructed/validated, and whether case numbers need format or duplicate validation.
