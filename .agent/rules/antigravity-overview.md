---
trigger: always_on
description: "Antigravity agent workflow, principles, and where project guidance lives"
---

# Antigravity agent — project workflow

This repo mirrors Cursor's `.cursor/` layout into Antigravity-native paths: **`.agents/`** (workflows, skills, agents) and **`.agent/rules/`** (supplement rules).

## Development workflow

Use the same phase order as in `GEMINI.md`:

1. **Define** — `/spec` workflow (`.agents/workflows/spec.md`)
2. **Plan** — `/plan` workflow
3. **Build** — `/build` workflow (TDD: `.agents/skills/tdd/`)
4. **Verify** — `/test` workflow
5. **Review** — `/review` workflow (five-axis: `.agents/skills/code-review/`)
6. **Ship** — `/deploy` workflow

Supporting workflows: `debug`, `simplify`, `fix-issue`, `handoff`, `resume`, `understand-project` in `.agents/workflows/`. Maintainers: `publish-npm` (say **push to npm repo** to draft README release notes and publish).

**First run:** if **`.agent/onboarding.complete`** is missing, agents automatically run **`/understand`** (`.agents/workflows/understand-project.md`) before other work.

**Agent continuity:** committed **`.agent/SESSION.md`** — read at session start (`/resume`), update at end (`/handoff`). See **`.agent/rules/agent-continuity.md`**.

## Mandatory standards

- Follow **`.agent/rules/`** (`*.md` with YAML frontmatter). **`security.md`**, **`codegraph.md`**, **`ontosight.md`**, and **`agent-continuity.md`** use `trigger: always_on`; others often use `trigger: glob`.
- Prefer **tests first** and **small vertical slices** (see `.agents/skills/incremental-implementation/`).
- Use **`.agents/references/`** for checklists (security, testing, performance, accessibility).
- For **structural** code questions, prefer **CodeGraph** MCP tools per **`.agent/rules/codegraph.md`**.
- When the user wants a **visual call graph**, use **OntoSight CLI** per **`.agent/rules/ontosight.md`** (`royalsolution-ontosight`).
- For **UI/UX work** (design, build, review, fix, improve — components, pages, layouts, styling, accessibility), read and follow the **ui-ux-pro-max** skill per **`.agent/rules/ui-ux-pro-max.md`**.

## Agents (personas)

Specialized instructions live in **`.agents/agents/`**. Reference files in chat (paste or attach).

## Relation to `.cursor/`, `.claude/`, and `.kiro/`

Keep all four trees aligned when you change workflows or standards. After editing `.cursor/`, run `npm run sync:all`.
