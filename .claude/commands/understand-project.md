---
name: understand
description: First-run — map project structure, persist findings, then continue the user's request
---

# /understand — Understanding this project structure

> "Read the map before you move."

## Purpose

On the **first agent session** in a project where `class-ai-agent` was installed, automatically map the repository structure, persist findings for future sessions, and then continue the user's original request.

## When to use

- **Automatic:** `.agent/onboarding.complete` is missing (see `.cursor/rules/agent-continuity.mdc`)
- **Manual:** User says `/understand`, "understand this project", or "map the codebase"
- **Re-run:** Delete `.agent/onboarding.complete` (and optionally `.agent/PROJECT.md`) to trigger again

## Prerequisites

- Scaffolding installed (`npx class-ai-agent`)
- CodeGraph index recommended (installer runs `codegraph init -i` by default)

## Workflow

### Phase 1: Preflight

1. Confirm **`.agent/onboarding.complete`** does not exist (if it exists, skip to Phase 7 unless user explicitly asked to re-run)
2. Note the user's **original request** — you will continue it after onboarding
3. Read **`.agent/PROJECT.template.md`** for the output schema

> Do **not** edit application code until Phase 7 unless the user only asked for project understanding.

### Phase 2: Static survey (read-only)

Read in this order (skip missing files):

1. **`README.md`** — purpose, setup, scripts
2. **Root manifests** — `package.json`, `pyproject.toml`, `Cargo.toml`, `go.mod`, etc.
3. **Top-level directories** — list dirs at project root (exclude `.git`, `node_modules`, `.codegraph`)
4. **`.env.example`** — required env vars (never read `.env`)
5. **Hub files** — `AGENTS.md`, `.cursor/CURSOR.md`, `.claude/CLAUDE.md`, `.kiro/KIRO.md`, `GEMINI.md` (whichever exist)
6. **`.agent/SESSION.md`** — if present, note goal/phase/pointers (do not treat as structure map)

### Phase 3: Structural survey

Prefer CodeGraph for structure; fall back to directory listing if unavailable.

1. **`codegraph_status`** — verify index; note pending sync if any
2. **`codegraph_context`** with `{ "task": "project architecture and entry points", "maxNodes": 20 }`
3. Optionally **`codegraph_explore`** on 2–3 key symbols from context (entry points, main modules)

If CodeGraph is missing or fails:

- Use `README` + manifest scripts + top-level tree
- Note in PROJECT.md that CodeGraph should be initialized

### Phase 4: Persist

Write **`.agent/PROJECT.md`** using the schema from **`.agent/PROJECT.template.md`**. Include:

| Section | Content |
|---------|---------|
| **Stack** | Languages, frameworks, major dependencies |
| **Layout** | Folder map with one-line purpose per top-level dir |
| **Entry points** | Main apps, CLIs, route roots, config bootstrap |
| **Key files** | Paths agents should know (routes, services, tests, CI) |
| **Commands** | Install, dev, build, test, lint (from manifests/README) |
| **Code intelligence** | CodeGraph/OntoSight status and workspace root path |
| **Updated** | ISO date and tool name |

Keep it concise — future agents read this before deep exploration.

### Phase 5: Complete

Create **`.agent/onboarding.complete`** with one line:

```
YYYY-MM-DD — onboarded via <cursor|claude|kiro|antigravity>
```

Do **not** store secrets or PII in either file.

### Phase 6: Report to user

Reply with a structured summary:

```markdown
## Project structure

**Stack:** ...
**Layout:** ...
**Entry points:** ...
**Commands:** `npm test`, ...

_Onboarding complete — `.agent/PROJECT.md` saved. Continuing with your request._
```

### Phase 7: Continue

Proceed with the user's **original request** without waiting for confirmation (unless onboarding was the only request).

If they asked to **resume** (`/resume`), run `.cursor/commands/resume.md` after Phase 6.

## Output

- `.agent/PROJECT.md` — committed structure map
- `.agent/onboarding.complete` — first-run marker
- User-facing summary (Phase 6)
- Original task continued (Phase 7)

## Next step

After onboarding, follow normal workflow: `/spec` → `/plan` → `/build`, or execute the user's stated task.
