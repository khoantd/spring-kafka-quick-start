---
inclusion: always
description: "Kiro agent workflow, principles, and where project guidance lives"
---

# Kiro agent — project workflow

This repo mirrors Claude Code’s `.claude/` and Cursor’s `.cursor/` layout under **`.kiro/`** for Kiro (IDE and CLI).

## Development workflow

Use the same phase order as in `.kiro/KIRO.md`:

1. **Define** — prompt from `.kiro/commands/spec.md` (paste, attach, or `#` reference)
2. **Plan** — `.kiro/commands/plan.md`
3. **Build** — `.kiro/commands/build.md` (TDD: `.kiro/skills/tdd/`)
4. **Verify** — `.kiro/commands/test.md`
5. **Review** — `.kiro/commands/review.md` (five-axis: `.kiro/skills/code-review/`)
6. **Ship** — `.kiro/commands/deploy.md`

Supporting prompts: `debug`, `simplify`, `fix-issue`, `handoff`, `resume`, `understand-project` in `.kiro/commands/`. Maintainers: `publish-npm` (say **push to npm repo** to draft README release notes and publish).

**First run:** if **`.agent/onboarding.complete`** is missing, agents automatically run **`/understand`** (`.kiro/commands/understand-project.md`) before other work.

**Agent continuity:** committed **`.agent/SESSION.md`** — read at session start (`/resume`), update at end (`/handoff`). See **`.kiro/steering/agent-continuity.md`**.

## Mandatory standards

- Follow **`.kiro/steering/`** (`*.md` with YAML frontmatter). **`security.md`**, **`codegraph.md`**, **`ontosight.md`**, and **`agent-continuity.md`** use `inclusion: always`; others often use `fileMatch`.
- Prefer **tests first** and **small vertical slices** (see `.kiro/skills/incremental-implementation/`).
- Use **`.kiro/references/`** for checklists (security, testing, performance, accessibility).
- For **structural** code questions, prefer **CodeGraph** MCP tools per **`.kiro/steering/codegraph.md`**.
- When the user wants a **visual call graph**, use **OntoSight CLI** per **`.kiro/steering/ontosight.md`** (`royalsolution-ontosight`).
- For **UI/UX work** (design, build, review, fix, improve — components, pages, layouts, styling, accessibility), read and follow the **ui-ux-pro-max** skill per **`.kiro/steering/ui-ux-pro-max.md`**.

## Agents (personas)

Specialized instructions live in **`.kiro/agents/`**. Reference files in chat (paste or attach).

## Relation to `.claude/`, `.cursor/`, and Antigravity

Keep all four trees aligned when you change workflows or standards. After editing `.cursor/`, run `npm run sync:all`.
