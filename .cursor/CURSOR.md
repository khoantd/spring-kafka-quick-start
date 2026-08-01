# Cursor AI agent configuration

## Overview

This project uses **Cursor** with the same structured workflows, specialized agent personas, and coding standards as **`.claude/`** and **`.kiro/`**. Cursor-specific files live under **`.cursor/`**.

---

## Development workflow

Follow this workflow for feature development:

```
/spec  →  /plan  →  /build  →  /test  →  /review  →  Ship
```

| Phase | Prompt source | Purpose |
|-------|----------------|--------|
| **Define** | `.cursor/commands/spec.md` | PRD: objectives, scope, boundaries |
| **Plan** | `.cursor/commands/plan.md` | Vertical slices, acceptance criteria |
| **Build** | `.cursor/commands/build.md` | Incremental implementation, TDD |
| **Verify** | `.cursor/commands/test.md` | Tests and verification |
| **Review** | `.cursor/commands/review.md` | Five-axis review before merge |
| **Ship** | `.cursor/commands/deploy.md` | Build, test, deploy |

### Supporting prompts

| File | Purpose |
|------|---------|
| `commands/debug.md` | Systematic diagnosis |
| `commands/simplify.md` | Reduce complexity, same behavior |
| `commands/fix-issue.md` | Analyze and fix reported issues |
| `commands/handoff.md` | End session — update `.agent/SESSION.md` for cross-tool continuity |
| `commands/resume.md` | Start session — load `.agent/SESSION.md` and continue prior work |
| `commands/understand-project.md` | First run — map project structure to `.agent/PROJECT.md` (auto if `onboarding.complete` missing) |
| `commands/publish-npm.md` | **Maintainers:** draft release notes, bump version, update README, publish to npm |

**How to use:** Open the markdown file, copy the section you need, or **@ mention** the file in Chat/Composer so the model loads it.

---

## Core principles

- **TDD** — Failing tests first, then implementation (`.cursor/skills/tdd/`)
- **Incremental implementation** — Small vertical slices (`.cursor/skills/incremental-implementation/`)
- **Five-axis review** — Correctness, readability, architecture, security, performance (`.cursor/skills/code-review/`)

---

## Mandatory rules (Cursor)

Project rules are **`.cursor/rules/*.mdc`**. They use YAML frontmatter:

- **`alwaysApply: true`** — Included in every relevant session (`cursor-overview.mdc`, `security.mdc`)
- **`alwaysApply: false`** + **`globs`** — Applied when files matching the pattern are in context

| Topic | Rule file |
|-------|-----------|
| Clean code, style, errors | `clean-code`, `code-style`, `error-handling` |
| Stack, structure, APIs | `tech-stack`, `project-structure`, `api-conventions` |
| Data & naming | `naming-conventions`, `database` |
| Ops & quality | `security`, `monitoring`, `testing`, `git-workflow`, `system-design` |
| Code intelligence | `codegraph` (MCP usage; see below) |
| Code visualization | `ontosight` (CLI; see below) |
| UI/UX | `ui-ux-pro-max` (design-system search, UX checklist; see below) |
| Loop workflows | `loop-library` (find/adapt/design bounded agent loops; see below) |

---

## Code intelligence (CodeGraph)

This project includes **[CodeGraph](https://github.com/colbymchenry/codegraph)** for local, structural code search via MCP.

| Item | Location |
|------|----------|
| MCP server config | `.cursor/mcp.json` |
| Usage rules | `.cursor/rules/codegraph.mdc` |
| Symbol index (generated) | `.codegraph/` (gitignored) |
| Setup reference | `.cursor/references/codegraph.md` |

After installing scaffolding, **reload the Cursor window** (or restart Cursor) so the CodeGraph MCP server connects. Use `codegraph_*` tools for structural questions; use grep/read for literal text in comments or strings.

**Smart indexing:** Run `codegraph_status` before first use — init only when the index is missing; trust the watcher for edits; Read stale-listed files instead of re-initing. See **Index health (smart)** in each persona and `.cursor/rules/codegraph.mdc`.

If the index is missing, run `npx @colbymchenry/codegraph init -i` in the project root (ask on large repos).

---

## Code visualization (OntoSight)

This project includes **[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight)** for interactive CodeGraph call subgraphs in the browser.

| Item | Location |
|------|----------|
| Usage rules | `.cursor/rules/ontosight.mdc` |
| Setup reference | `.cursor/references/ontosight.md` |
| Shared index | `.codegraph/` (same as CodeGraph) |

Use `codegraph_*` MCP tools to gather structural facts in chat; run `npx royalsolution-ontosight@0.2.1 "<workspace-root>"` when the user wants a visual call graph. For **impact analysis demos**, follow `skills/ui-ux-pro-max/IMPACT-DEMO.md` (search → `codegraph_impact` → summary → graph). Requires Node 20+, Python 3.11+, and uv or pipx.

---

## Agent personas

Instructions live in **`.cursor/agents/`**. Invoke by **@ mentioning** the file (e.g. `@.cursor/agents/backend.md`).

**Mandatory:** Every persona **must** read **`.agent/SESSION.md`** at session start and update it before ending or switching roles. Every persona **must** use **CodeGraph** (`codegraph_*` MCP) for structural code questions before grep/read loops — see **Agent continuity (mandatory)** and **CodeGraph (mandatory)** in each persona file.

| Area | File |
|------|------|
| Frontend, backend, architecture | `frontend.md`, `backend.md`, `systems-architect.md` |
| Quality | `code-reviewer.md`, `test-engineer.md`, `qa.md`, `security-auditor.md` |
| Product & content | `business-analyst.md`, `project-manager.md`, `ui-ux-designer.md`, `copywriter-seo.md` |

---

## Skills

Reusable playbooks: **`.cursor/skills/*/SKILL.md`** (and related `.md` files where present).

| Skill | Use for |
|-------|---------|
| `tdd` | Red–green–refactor |
| `code-review` | Five-axis review |
| `incremental-implementation` | Vertical slices |
| `deploy` | Deployment pipeline |
| `security-review` | Security audit |
| `agent-continuity` | Cross-tool session handoff via `.agent/SESSION.md` |
| `supabase` | Supabase products, Auth, CLI, MCP, migrations, RLS |
| `supabase-postgres-best-practices` | Postgres performance, indexes, RLS tuning |
| `ui-ux-pro-max` | UI/UX design systems, styling, accessibility, pre-delivery checklist |
| `loop-library` | Find, adapt, or design bounded repeatable agent workflows (loops) |

---

## Reference checklists

**`.cursor/references/`**

| File | Use for |
|------|---------|
| `security-checklist.md` | Pre-deploy security |
| `testing-patterns.md` | Test structure |
| `performance-checklist.md` | Performance |
| `accessibility-checklist.md` | WCAG-oriented checks |
| `codegraph.md` | CodeGraph install and Claude Code setup |
| `ontosight.md` | OntoSight CLI for visual call graphs |
| `agent-continuity.md` | Session handoff and `/resume` / `/handoff` |
| `supabase.md` | Supabase skills, MCP OAuth, secrets |
| `loop-library.md` | Loop Library skill, npm CLI, web catalog |

---

## Config parity

**`.cursor/settings.json`** lists directories (mirrors `.claude/settings.json` for Claude Code). Cursor natively loads **`.cursor/rules/*.mdc`** and **`.cursor/mcp.json`** for MCP servers; other paths are documentation for humans and for `@` includes.

---

## Agent continuity

Cross-tool handoff lives in **`.agent/SESSION.md`** (committed). Use **`/resume`** at session start and **`/handoff`** at session end when switching chats or tools (Cursor, Claude Code, Kiro). See **`.cursor/references/agent-continuity.md`** and **`.cursor/rules/agent-continuity.mdc`**.

---

## Agent behavior

1. Follow the workflow and use the command prompts when starting a phase.
2. If **`.agent/SESSION.md`** exists, read it before planning or coding; run **`/resume`** when continuing prior work.
3. Apply **`.cursor/rules/`**; treat **`security.mdc`** as non-negotiable.
4. Prefer tests first and small, buildable changes.
5. **@ mention** the right **`.cursor/agents/`** file when the task matches that role.
6. Update **`.agent/SESSION.md`** (or **`/handoff`**) before ending a session.
