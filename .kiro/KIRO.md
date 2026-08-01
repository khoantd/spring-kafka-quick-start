# Kiro AI agent configuration

## Overview

This project uses **Kiro** with the same structured workflows, specialized agent personas, and coding standards as **`.claude/`** and **`.cursor/`**. Kiro-specific files live under **`.kiro/`**.

---

## Development workflow

Follow this workflow for feature development:

```
/spec  →  /plan  →  /build  →  /test  →  /review  →  Ship
```

| Phase | Prompt source | Purpose |
|-------|----------------|--------|
| **Define** | `.kiro/commands/spec.md` | PRD: objectives, scope, boundaries |
| **Plan** | `.kiro/commands/plan.md` | Vertical slices, acceptance criteria |
| **Build** | `.kiro/commands/build.md` | Incremental implementation, TDD |
| **Verify** | `.kiro/commands/test.md` | Tests and verification |
| **Review** | `.kiro/commands/review.md` | Five-axis review before merge |
| **Ship** | `.kiro/commands/deploy.md` | Build, test, deploy |

### Supporting prompts

| File | Purpose |
|------|---------|
| `commands/debug.md` | Systematic diagnosis |
| `commands/simplify.md` | Reduce complexity, same behavior |
| `commands/fix-issue.md` | Analyze and fix reported issues |
| `commands/handoff.md` | End session — update `.agent/SESSION.md` for cross-tool continuity |
| `commands/resume.md` | Start session — load `.agent/SESSION.md` and continue prior work |
| `commands/publish-npm.md` | **Maintainers:** draft release notes, bump version, update README, publish to npm |

**How to use:** Open the markdown file, copy the section you need, or reference it in chat (paste or attach).

---

## Core principles

- **TDD** — Failing tests first, then implementation (`.kiro/skills/tdd/`)
- **Incremental implementation** — Small vertical slices (`.kiro/skills/incremental-implementation/`)
- **Five-axis review** — Correctness, readability, architecture, security, performance (`.kiro/skills/code-review/`)

---

## Mandatory standards (steering)

Project standards are **`.kiro/steering/*.md`**. They use YAML frontmatter:

- **`inclusion: always`** — Loaded every session (`kiro-overview.md`, `security.md`, `codegraph.md`, `ontosight.md`, `agent-continuity.md`)
- **`inclusion: fileMatch`** — Loaded when edited files match `fileMatchPattern`
- **`inclusion: manual`** — Reference with `#filename` in chat or `/` slash commands

| Topic | Rule file |
|-------|-----------|
| Clean code, style, errors | `clean-code`, `code-style`, `error-handling` |
| Stack, structure, APIs | `tech-stack`, `project-structure`, `api-conventions` |
| Data & naming | `naming-conventions`, `database` |
| Ops & quality | `security`, `monitoring`, `testing`, `git-workflow`, `system-design` |
| Code intelligence | `codegraph` (MCP usage; see below) |
| Code visualization | `ontosight` (CLI; see below) |
| UI/UX | `ui-ux-pro-max` (design-system search, UX checklist; see below) |
| Agent continuity | `agent-continuity` (`.agent/SESSION.md` handoff) |

---

## Agent continuity

Cross-tool handoff lives in **`.agent/SESSION.md`** (committed). Use **`/resume`** at session start and **`/handoff`** at session end when switching chats or tools. See **`.kiro/references/agent-continuity.md`** and **`.kiro/steering/agent-continuity.md`**.

---

## Code intelligence (CodeGraph)

This project includes **[CodeGraph](https://github.com/colbymchenry/codegraph)** for local, structural code search via MCP.

| Item | Location |
|------|----------|
| MCP server config | `.kiro/settings/mcp.json` |
| Usage rules | `.kiro/steering/codegraph.md` |
| Symbol index (generated) | `.codegraph/` (gitignored) |
| Setup reference | `.kiro/references/codegraph.md` |

After installing scaffolding, **restart Kiro** so the CodeGraph MCP server connects. Use `codegraph_*` tools for structural questions (callers, callees, traces, impact); use grep/read for literal text in comments or strings.

If the index is missing, run `npx @colbymchenry/codegraph init -i` in the project root.

---

## Code visualization (OntoSight)

This project includes **[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight)** for interactive CodeGraph call subgraphs in the browser.

| Item | Location |
|------|----------|
| Usage rules | `.kiro/steering/ontosight.md` |
| Setup reference | `.kiro/references/ontosight.md` |
| Shared index | `.codegraph/` (same as CodeGraph) |

Use `codegraph_*` MCP tools to gather structural facts in chat; run `royalsolution-ontosight "<workspace-root>"` when the user wants a visual call graph. For **impact analysis demos**, follow `skills/ui-ux-pro-max/IMPACT-DEMO.md` (search → `codegraph_impact` → summary → graph). Requires Node 20+, Python 3.11+, and uv or pipx.

---

## Agent personas

Instructions live in **`.kiro/agents/`**. Invoke by **referencing** the file (e.g. `@.kiro/agents/backend.md`).

| Area | File |
|------|------|
| Frontend, backend, architecture | `frontend.md`, `backend.md`, `systems-architect.md` |
| Quality | `code-reviewer.md`, `test-engineer.md`, `qa.md`, `security-auditor.md` |
| Product & content | `business-analyst.md`, `project-manager.md`, `ui-ux-designer.md`, `copywriter-seo.md` |

---

## Skills

Reusable playbooks: **`.kiro/skills/*/SKILL.md`** (and related `.md` files where present).

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

---

## Reference checklists

**`.kiro/references/`**

| File | Use for |
|------|---------|
| `security-checklist.md` | Pre-deploy security |
| `testing-patterns.md` | Test structure |
| `performance-checklist.md` | Performance |
| `accessibility-checklist.md` | WCAG-oriented checks |
| `codegraph.md` | CodeGraph setup (Kiro, Cursor, Claude Code) |
| `agent-continuity.md` | Session handoff and `/resume` / `/handoff` |
| `supabase.md` | Supabase skills, MCP OAuth, secrets |

---

## Config parity

**`.kiro/settings.json`** lists directories (mirrors `.cursor/settings.json`). Kiro loads **`.kiro/steering/*.md`**, root **`AGENTS.md`**, and **`.kiro/settings/mcp.json`**.

---

## Agent behavior

1. Follow the workflow and use the command prompts when starting a phase.
2. If **`.agent/SESSION.md`** exists, read it before planning or coding; run **`/resume`** when continuing prior work.
3. Apply **`.kiro/steering/`**; treat **`security.md`** as non-negotiable.
4. Prefer tests first and small, buildable changes.
5. **Reference** the right **`.kiro/agents/`** file when the task matches that role (paste or attach).
6. Update **`.agent/SESSION.md`** (or **`/handoff`**) before ending a session.
