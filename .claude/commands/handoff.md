---
name: handoff
description: End-of-session — update .agent/SESSION.md for the next agent or tool
---

# /handoff — Session handoff

> "Leave the next agent a map, not a maze."

## Purpose

Capture current work in **`.agent/SESSION.md`** so another chat, persona, or tool (Cursor, Claude Code, Kiro) can continue without re-discovering context.

## When to use

- End of a work session (before closing chat)
- Switching tools (Cursor → Claude Code → Kiro)
- Switching persona (e.g. architect → backend)
- After completing a workflow phase (spec, plan, build, test, review)
- Before opening a PR (document what reviewers should know)

## Prerequisites

- `.agent/SESSION.md` exists (created by `npx class-ai-agent` or copy from `.agent/SESSION.template.md`)
- You have context on what was done this session

## Workflow

### Phase 1: Gather state

1. **Review git** — branch name, uncommitted files, last commits
2. **Review tasks** — open `tasks/todo.md`; sync checkboxes with reality
3. **Review spec** — note linked `SPEC.md` or `docs/specs/...` path
4. **Scan decisions** — what did we choose that is not obvious from code alone?
5. **Scan gotchas** — what failed, env quirks, commands that matter

### Phase 2: Update `.agent/SESSION.md`

Refresh every section (use `.agent/SESSION.template.md` as schema):

| Section | Content |
|---------|---------|
| **Meta** | `Updated` (today), `Phase`, `Tool` (cursor/claude/kiro), optional `Persona` |
| **Goal** | One paragraph — still accurate? |
| **Done** | Bullets with file paths or commit refs |
| **In progress** | Current task; **Blockers** (none or describe) |
| **Next** | Numbered steps for the *next* agent |
| **Decisions** | Non-obvious choices made this session |
| **Gotchas** | Failed attempts, test commands, env notes |
| **Pointers** | Spec path, `tasks/todo.md`, branch, key files |

### Phase 3: Sync `tasks/todo.md`

- Mark completed items `[x]`
- Add new tasks discovered during work
- Remove or defer items that are out of scope

### Phase 4: Risk note (if applicable)

If work is **not** safe to pick up blindly, add under **Gotchas** or **In progress**:

- Uncommitted changes and why
- Failing tests or broken build
- External blockers (API, review, dependency)

### Phase 5: Optional milestone archive

For major milestones, copy `SESSION.md` to:

```
.agent/history/YYYY-MM-DD-short-slug.md
```

Commit both `SESSION.md` and the history file when ready.

## Security

**Never** write to `SESSION.md`:

- API keys, passwords, tokens, credentials
- PII or customer data
- Full stack traces with secrets

Use issue links or commit SHAs instead.

## Output

- Updated **`.agent/SESSION.md`**
- Updated **`tasks/todo.md`** (if it exists)
- Short summary for the user: phase, next steps, blockers

## Next step

Tell the user to run **`/resume`** in the next session or tool, or commit and share the branch.
