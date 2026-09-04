# Data Model Proposal

This is a conceptual proposal, not an implemented schema. Cardinalities, required fields, ownership, lifecycle, and audit/retention rules require validation in a later work package.

## Proposed concepts

- **Customer:** organization associated with certificates and affected sites.
- **Certificate:** the single authoritative operational record of a certificate and its validity window.
- **Certificate Type:** governed classification of certificate purpose or form.
- **Function / Usage:** what the certificate enables or protects.
- **System / Integration:** the technical system, application boundary, or integration consuming the certificate.
- **Environment:** operational context such as development, QA, or production.
- **Hostname / FQDN:** network identity relevant to the certificate or a consumer.
- **Affected Site:** customer or operational site affected by a certificate change.
- **Support Case reference:** link or identifier for externally managed support work.
- **Follow-up Action:** unfinished task, owner/context, and state arising from certificate work.
- **Certificate Update History:** auditable record of material renewal, replacement, or record changes.
- **Guidance Rule:** maintained condition determining when operational guidance applies.
- **Guidance Response:** response or acknowledgement captured when guidance is presented.

## Governing relationship

**A certificate is stored once. Consumers and sites are related to it rather than creating duplicate certificate records per site.** This avoids conflicting validity data while permitting a certificate to affect multiple systems, integrations, environments, hostnames, and sites.

## Decision requiring validation

Product, Application, and Interface are currently being considered for consolidation into two clearer concepts:

- Function / Usage
- System / Integration

Stakeholder vocabulary and representative operational scenarios must validate this choice before physical schema design.
