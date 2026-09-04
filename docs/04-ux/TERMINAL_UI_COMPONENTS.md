# Terminal UI Components

WP-004 applies the principle “1992 appearance, 2026 usability” through semantic HTML and reusable rules in `terminal.css`.

| Pattern | Use |
|---|---|
| `.terminal-frame`, `.terminal-panel`, `.terminal-titlebar` | Bounded navy application shell and content regions without ASCII-art markup |
| `.primary-nav`, `.action-footer`, `.command-button` | Standard links/buttons with restrained function-key visual cues |
| `.terminal-table`, `.table-scroll` | Semantic inventory table with contained horizontal scrolling on narrow screens |
| `.status-badge` plus health class | Text and border treatment for health so color is never the only signal |
| `.question-screen`, `.wizard`, `.wizard-actions` | Consistent one-question wizard layout, progress text, Back/Continue/Cancel placement |
| `.choice-grid`, `.choice`, `fieldset`/`legend` | Native keyboard-operable grouped multi-select controls |
| `.message`, `.field-error`, `.invalid` | Announced status/errors and visible field-level validation |
| `.detail-grid`, `.review-grid`, `.value-list` | Responsive detail and confirmation summaries |
| `:focus-visible`, `.visually-hidden` | Strong keyboard focus and screen-reader-only context |

Layouts collapse to one column on small screens and prevent page-level horizontal overflow. Motion is not required for any task. Primary actions remain ordinary links and buttons; the displayed function-key language is decorative guidance, not a mandatory interaction model.
