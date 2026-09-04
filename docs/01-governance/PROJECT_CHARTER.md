# Certificate Tracker Project Charter

## Problem statement

Certificate information and renewal knowledge are difficult to see and retain consistently. Staff need a reliable operational record of expiration dates, affected consumers, related support work, and the steps that make an update successful.

## Product objective

Provide one internal application that makes certificates easy to register, impending expirations easy to act on, and renewal work guided and auditable.

## Primary users

- Implementation staff adding certificates and impact information.
- Support staff reviewing expirations and coordinating renewal work as a team.
- Staff maintaining operational guidance and reviewing history.

## Business value

The product should reduce missed update steps, improve visibility of operational risk, shorten routine data entry, preserve institutional knowledge, and provide traceability without replacing established case-management processes.

## High-level scope

The MVP covers certificate inventory, search and detail, an expiration work queue, guided add and update/replace flows, contextual guidance, support-case references, follow-up actions, and history/audit.

## Out of scope

The MVP does not deploy certificates; manage private keys or secrets; modify Java trust stores; discover certificates; distribute trust stores over SFTP; replace or automatically create ServiceNow cases; or perform automated endpoint validation. Advanced notifications and AI recommendations are separate future decisions.

## Guiding principles

- Store a certificate once and relate its consumers and sites.
- Make adding fast and updating guided.
- Prefer plain operational language and team ownership over blame.
- Preserve user input through navigation and require review before important changes.
- Keep the architecture server-rendered, secure by design, accessible, and no more complex than current needs.
- Govern schema and behavior through incremental, reviewable work packages.
