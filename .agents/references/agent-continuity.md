# Agent continuity — quick reference

**Mandatory for all personas:** `.cursor/agents/*.md` each include **Agent continuity (mandatory)** — read SESSION at start, update before handoff or persona switch.

## Files

| Path | Role |
|------|------|
| `.agent/SESSION.md` | Live handoff (commit to git) |
| `.agent/SESSION.template.md` | SESSION schema reference |
| `.agent/PROJECT.md` | Structure map (written on first run) |
| `.agent/PROJECT.template.md` | PROJECT schema reference |
| `.agent/onboarding.complete` | First-run marker (created after `/understand`) |
| `.agent/README.md` | Human overview |
| `tasks/todo.md` | Task checklist (workflow) |
| `SPEC.md` | Feature spec (workflow) |

## Commands

| Command | When |
|---------|------|
| **`/understand`** | First run — map project structure (auto if `onboarding.complete` missing) |
| **`/resume`** | Start of session — read SESSION, summarize, continue |
| **`/handoff`** | End of session — write SESSION, sync tasks |

## Read order (resume)

1. `.agent/PROJECT.md` (if present)
2. `.agent/SESSION.md`
3. `tasks/todo.md`
4. Linked spec from SESSION Pointers

## First run

If `.agent/onboarding.complete` is missing, agents run **`/understand`** before resume or other work. See `.cursor/commands/understand-project.md`.

## Install

```bash
npx class-ai-agent
```

Creates `.agent/` and seeds `SESSION.md` from template.

## Rules

- **Cursor:** `.cursor/rules/agent-continuity.mdc` (`alwaysApply`)
- **Claude:** `.claude/rules/agent-continuity.md`
- **Kiro:** `.kiro/steering/agent-continuity.md` (`inclusion: always`)

## Skill

`.cursor/skills/agent-continuity/SKILL.md` — full handoff/resume checklists.
