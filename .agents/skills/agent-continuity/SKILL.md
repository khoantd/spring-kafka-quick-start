---
name: Agent Continuity
description: Cross-tool session handoff and resume via .agent/SESSION.md
---

# Agent Continuity Skill

## Purpose

Keep **Cursor**, **Claude Code**, and **Kiro** aligned on the same in-flight work using committed **`.agent/SESSION.md`**.

**Applies to every agent persona** (`.agents/agents/*.md` and synced copies) — not only the default agent. When you @ mention or adopt a persona, SESSION read/update rules are still mandatory.

---

## When to apply

| Situation | Action |
|-----------|--------|
| First agent session (`onboarding.complete` missing) | **Understand** — map structure, then continue user request |
| New chat, same feature | **Resume** — read SESSION first |
| End of session | **Handoff** — update SESSION |
| Switch IDE/tool | **Handoff** then **Resume** in new tool |
| Switch persona | Update Meta `persona`; handoff notes for next role |
| Phase change (plan → build) | Update Meta `phase` |

---

## Handoff checklist

- [ ] Meta: date, phase, tool, persona
- [ ] Goal still accurate (one paragraph)
- [ ] Done: bullets with paths/commits
- [ ] In progress + blockers
- [ ] Next: numbered for next agent
- [ ] Decisions: non-obvious choices
- [ ] Gotchas: failures, test commands, env
- [ ] Pointers: spec, tasks, branch, key files
- [ ] `tasks/todo.md` synced
- [ ] No secrets or PII in SESSION

---

## First-run checklist

- [ ] Confirm `.agent/onboarding.complete` is missing
- [ ] Follow `.agents/workflows/understand-project.md`
- [ ] Write `.agent/PROJECT.md` from template
- [ ] Create `.agent/onboarding.complete`
- [ ] Present structure summary to user
- [ ] Continue user's original request

## Resume checklist

- [ ] Read `.agent/PROJECT.md` if present
- [ ] Read `.agent/SESSION.md`
- [ ] Read `tasks/todo.md` if linked
- [ ] Read SPEC if linked
- [ ] `git status` vs SESSION expectations
- [ ] Run sanity build/test if Gotchas say so
- [ ] Post resumption summary to user
- [ ] Execute first **Next** step via workflow command

---

## SESSION schema

See **`.agent/SESSION.template.md`** for the canonical sections.

---

## Commands

| Command | File |
|---------|------|
| `/understand` | `.agents/workflows/understand-project.md` |
| `/handoff` | `.agents/workflows/handoff.md` (`.agents/`, `.agents/`) |
| `/resume` | `.agents/workflows/resume.md` |

---

## Optional history

Copy SESSION to `.agent/history/YYYY-MM-DD-slug.md` at milestones; commit for audit trail.
