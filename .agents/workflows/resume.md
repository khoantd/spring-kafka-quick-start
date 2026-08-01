---
description: "Start-of-session — load .agent/SESSION.md and continue prior work"
---

# /resume — Continue prior work

> "Read the map before you move."

## Purpose

Load cross-tool handoff state and continue work from a previous agent session without re-discovering context.

## When to use

- Starting a new chat on the same feature
- Switching tools (Cursor ↔ Claude Code ↔ Kiro)
- User says "continue", "pick up where we left off", or "resume"
- After pulling a branch that includes an updated `.agent/SESSION.md`

## Prerequisites

- **`.agent/SESSION.md`** exists in the project root
- If missing: run `npx class-ai-agent` or copy `.agent/SESSION.template.md` → `.agent/SESSION.md`

## First run

If **`.agent/onboarding.complete`** is missing, run **`.agents/workflows/understand-project.md`** first (present summary, write `.agent/PROJECT.md`), then continue with this resume workflow.

## Workflow

### Phase 1: Load handoff (read-only first)

Read in this order:

1. **`.agent/PROJECT.md`** — if present (structure map from `/understand`)
2. **`.agent/SESSION.md`** — goal, done, in progress, next, decisions, gotchas, pointers
3. **`tasks/todo.md`** — if referenced in Pointers
4. **`SPEC.md`** or path from Pointers — if in spec/plan/build phase

> Do **not** edit code until Phase 3 plan is stated to the user.

### Phase 2: Sanity check

From SESSION **Gotchas** and **Pointers**, run quick checks when noted:

- `git status` — uncommitted work matches SESSION?
- Build/test commands listed in Gotchas — run if stale or uncertain
- Branch matches Pointers

If SESSION reports **blockers** or broken build, surface them before implementing.

### Phase 3: State plan to user

Reply with a short structured summary:

```markdown
## Resuming

**Goal:** [from SESSION]
**Phase:** [spec | plan | build | test | review | debug]
**Last updated:** [Meta date] via [tool/persona]

### Already done
- ...

### In progress
- ...

### Next (this session)
1. ...

### Risks / blockers
- ...
```

Ask for confirmation only if SESSION is ambiguous or blockers need a decision.

### Phase 4: Continue workflow

| Phase in SESSION | Command to follow |
|------------------|-------------------|
| spec | `/spec` (refine) or `/plan` if spec is done |
| plan | `/plan` or `/build` if plan exists |
| build | `/build` |
| test | `/test` |
| review | `/review` |
| debug | `/debug` |

Update **Meta** in `.agent/SESSION.md` when you change phase or tool.

### Phase 5: During work

- After meaningful progress, update SESSION **Done** / **In progress** / **Next**
- End session with **`/handoff`**

## If SESSION is empty or stale

1. Survey repo: `git log`, `tasks/todo.md`, open PRs
2. Rebuild SESSION from evidence
3. Ask user to confirm goal and next steps
4. Run `/handoff` when aligned

## Output

- Resumption summary (Phase 3)
- Explicit next action (first item from **Next**)
- No code changes until plan is stated (unless user asked for a specific fix)

## Next step

Execute the first **Next** item using the appropriate workflow command (`/build`, etc.).
