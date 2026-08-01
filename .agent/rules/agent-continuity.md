---
trigger: always_on
description: "Agent session continuity — cross-tool handoff via .agent/SESSION.md"
---

# Agent continuity

Cross-tool handoff lives in **`.agent/SESSION.md`** (committed). Cursor, Claude Code, and Kiro agents share this file.

**All agent personas** (`.agents/agents/*.md`) must read SESSION at session start and update it before ending or switching roles — see the **Agent continuity (mandatory)** section in each persona file.

## First run — project understanding

Before **`/resume`**, planning, or editing code, check **`.agent/onboarding.complete`**.

If missing:

1. Follow **`.agents/workflows/understand-project.md`** ("Understanding this project structure")
2. Present the structure summary to the user
3. Then continue the user's request (or **`/resume`** if they asked to continue)

Persistent map: **`.agent/PROJECT.md`** (schema: **`.agent/PROJECT.template.md`**).

## Session start

1. If **`.agent/SESSION.md`** exists, read it **before** planning or editing code.
2. When the user says **continue**, **resume**, or **pick up**, use **`.agents/workflows/resume.md`** (or equivalent in `.agents/` / `.agents/`).
3. Then read **`tasks/todo.md`** and linked **SPEC** paths from SESSION **Pointers**.

**Do not** call `codegraph_context` with `query` / `limit` for session resume — that tool requires **`task`** and is for code symbols, not handoff state. For continuity, **Read** `.agent/SESSION.md` (and `tasks/todo.md`); use `codegraph_context` only when you need structural code context for the work described in SESSION.

## Session end and phase changes

1. Update **`.agent/SESSION.md`** before ending a session or switching tools — use **`.agents/workflows/handoff.md`** when possible.
2. Keep **Done**, **In progress**, and **Next** accurate; do not leave stale **In progress** items.
3. Sync **`tasks/todo.md`** checkboxes when tasks change.

## Security (SESSION.md)

**Never** store in `.agent/SESSION.md`:

- API keys, passwords, tokens, credentials
- PII or customer data

Use issue links, commit SHAs, and file paths instead.

## Workflow integration

| Phase | SESSION `phase` value |
|-------|------------------------|
| Spec | `spec` |
| Plan | `plan` |
| Build | `build` |
| Test | `test` |
| Review | `review` |
| Debug | `debug` |

Set **Meta → Tool** to `cursor`, `claude`, or `kiro` as appropriate.
