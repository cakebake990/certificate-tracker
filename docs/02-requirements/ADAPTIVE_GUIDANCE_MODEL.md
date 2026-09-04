# Adaptive Guidance Model

Configurable guidance reminds staff of relevant dependencies without deployment. It is a narrow decision table, not a general rules engine.

## Rule definition

| Field | Requirement |
|---|---|
| Stable ID/name | Immutable identity and understandable label. |
| Status | Draft, Active, Retired; only effective Active rules run. |
| Conditions | Supported fact/operator/value conditions. |
| Prompt/explanation | Plain question and optional context. |
| Allowed responses | Ordered controlled options. |
| Follow-up behavior | Optional response-to-prefilled-action mapping. |
| Priority/order | Deterministic presentation. |
| Effective/retired dates | Evaluation time bounds. |
| Audit metadata | Version, timestamps, administrative identity. |

Supported MVP facts are System / Integration, Function / Usage, Environment, Certificate Type, issuer changed, and multiple environments. Operators are limited to equality, selected-set membership, and boolean checks.

MVP supports one condition or multiple conditions combined with **AND**. A set condition may use bounded “contains any.” Arbitrary nested AND/OR, scripts, expressions, and cross-record queries are excluded. Broader OR grouping requires a proven rule that cannot be expressed as separate rules.

## Evaluation and history

Evaluate against replacement facts and current/selected relationships when guidance is entered; re-evaluate after trigger facts change. Each response snapshots stable rule ID/version, prompt, explanation, allowed options, selection, time/user, and follow-up outcome. Editing/retiring a rule creates a new effective version and never changes history.

## OMS example

```text
Condition: System / Integration contains OMS
Question: Did you also check OMS Integration?
Responses:
  Yes — checked / updated as needed
  Needs follow-up -> suggest "Verify OMS Integration"
  Not applicable
```

`Needs follow-up` opens a prefilled editor. The response remains recorded even if the suggestion is not created, and review highlights that outcome.

Only limited administrators may create, test, activate, order, version, or retire rules. Rules cannot deploy, invoke external systems, or mutate certificate facts beyond suggesting a follow-up.

## Open decisions

- Administrative roles and approval/version workflow.
- Final response vocabulary and whether follow-up creation is mandatory.
- Duplicate/conflicting prompt behavior and preview fixtures.
- Effective-date timezone and audit retention/export.
