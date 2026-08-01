# Claude AI Agent Configuration

## Overview

This project uses Claude AI as an intelligent development agent with structured workflows, specialized sub-agents, and mandatory coding standards.

---

## Development Workflow

Follow this workflow for all feature development:

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   /spec  →  /plan  →  /build  →  /test  →  /review  →  Ship│
│                                                             │
│   Define    Plan     Build     Verify    Review     Deploy  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

| Phase | Command | Purpose |
|-------|---------|---------|
| **Define** | `/spec` | Create PRD with objectives, scope, boundaries |
| **Plan** | `/plan` | Decompose into vertical slices with acceptance criteria |
| **Build** | `/build` | Implement incrementally using TDD (RED-GREEN-REFACTOR) |
| **Verify** | `/test` | Write and verify tests; use Prove-It for bug fixes |
| **Review** | `/review` | Five-axis code review before merge |
| **Ship** | `/deploy` | Build, test, deploy with staged rollout |

### Supporting Commands

| Command | Purpose |
|---------|---------|
| `/debug` | Systematic error diagnosis and root cause analysis |
| `/simplify` | Reduce complexity without changing behavior |
| `/fix-issue` | Analyze and fix reported issues |
| `/handoff` | End session — update `.agent/SESSION.md` for cross-tool continuity |
| `/resume` | Start session — load `.agent/SESSION.md` and continue prior work |
| `publish-npm` | **Maintainers:** draft release notes, bump version, update README, publish to npm |

---

## Agent continuity

Cross-tool handoff lives in **`.agent/SESSION.md`** (committed). Use **`/resume`** at session start and **`/handoff`** at session end when switching chats or tools. See **`.claude/references/agent-continuity.md`** and **`.claude/rules/agent-continuity.md`**.

---

## Core Principles

### Code Quality
- **Test-Driven Development** — Write failing tests first, then implement
- **Incremental Implementation** — Small vertical slices, always buildable
- **Five-Axis Review** — Correctness, Readability, Architecture, Security, Performance

### Philosophy
- Progress over perfection
- Fix root causes, not symptoms
- The simplest thing that could work
- Tests are proof, not afterthought

---

## Mandatory Rules

All rules in `.claude/rules/` are **mandatory** and must be followed:

### Code Quality
| Rule | Description |
|------|-------------|
| `clean-code.md` | Variables, functions, SOLID, async/await |
| `code-style.md` | Formatting, naming conventions |
| `error-handling.md` | AppError class, global handler patterns |

### Architecture & Design
| Rule | Description |
|------|-------------|
| `tech-stack.md` | Approved technologies (Next.js, PG, Redis, Prisma) |
| `system-design.md` | CAP theorem, caching, scaling, queues |
| `project-structure.md` | Layered architecture, folder organization |
| `api-conventions.md` | REST standards, response envelopes |

### Data & Naming
| Rule | Description |
|------|-------------|
| `naming-conventions.md` | Cache keys, DB, queues, env vars |
| `database.md` | Prisma patterns, transactions, N+1 prevention |

### Operations
| Rule | Description |
|------|-------------|
| `security.md` | **CRITICAL** — Never violate security rules |
| `monitoring.md` | Prometheus, Grafana, logging, alerting |
| `testing.md` | Coverage thresholds, test patterns |
| `git-workflow.md` | Branching strategy, conventional commits |
| `agent-continuity.md` | Session handoff via `.agent/SESSION.md` |
| `codegraph.md` | CodeGraph MCP usage; when to use `codegraph_*` tools |
| `ontosight.md` | OntoSight CLI for visual call graphs |
| `ui-ux-pro-max.md` | UI/UX design systems, styling, accessibility checklist |

---

## Code intelligence (CodeGraph)

This project includes **[CodeGraph](https://github.com/colbymchenry/codegraph)** for local, structural code search via MCP.

| Item | Location |
|------|----------|
| Usage rules | `.claude/rules/codegraph.md` |
| Symbol index (generated) | `.codegraph/` (gitignored) |
| Setup reference | `.claude/references/codegraph.md` |

Install CodeGraph for Claude Code globally (project scaffolding does not add Claude MCP config):

```bash
npx @colbymchenry/codegraph
codegraph install --target=claude --yes
```

Then in each project: `codegraph init -i` (class-ai-agent may run this on install). Use `codegraph_*` tools for structural questions (callers, callees, traces, impact); use grep/read for literal text in comments or strings.

---

## Code visualization (OntoSight)

This project includes **[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight)** for interactive CodeGraph call subgraphs in the browser.

| Item | Location |
|------|----------|
| Usage rules | `.claude/rules/ontosight.md` |
| Setup reference | `.claude/references/ontosight.md` |
| Shared index | `.codegraph/` (same as CodeGraph) |

Use `codegraph_*` MCP tools to gather structural facts in chat; run `royalsolution-ontosight "<workspace-root>"` when the user wants a visual call graph. For **impact analysis demos**, follow `skills/ui-ux-pro-max/IMPACT-DEMO.md` (search → `codegraph_impact` → summary → graph). Requires Node 20+, Python 3.11+, and uv or pipx.

---

## Available Agents

Invoke the right agent for each task type:

### Development Agents
| Agent | When to Invoke |
|-------|---------------|
| 🖥️ **Frontend Developer** | Components, pages, routing, state, UI performance |
| 🔧 **Backend Developer** | APIs, services, DB queries, background jobs |
| 🏗️ **Systems Architect** | Architecture decisions, ADRs, system design |

### Quality Agents
| Agent | When to Invoke |
|-------|---------------|
| 👀 **Code Reviewer** | Five-axis PR review, code quality assessment |
| 🧪 **Test Engineer** | Test strategy, TDD, coverage, bug reproduction |
| 🔒 **Security Auditor** | Vulnerability assessment, threat modeling |
| ✅ **QA Engineer** | Test plans, E2E tests, bug reports |

### Product Agents
| Agent | When to Invoke |
|-------|---------------|
| 📊 **Business Analyst** | Requirements elicitation, BABOK v3, process modeling, gap analysis |
| 📋 **Project Manager** | User stories, sprint planning, status reports |
| 🎨 **UI/UX Designer** | Design system, wireframes, accessibility |
| ✍️ **Copywriter/SEO** | Page copy, meta tags, SEO optimization |

---

## Available Skills

Specialized skills for complex operations:

| Skill | Description |
|-------|-------------|
| `tdd` | Test-Driven Development patterns |
| `code-review` | Five-axis review framework |
| `incremental-implementation` | Vertical slice development |
| `deploy` | Full deployment pipeline |
| `security-review` | Security audit checklist |
| `agent-continuity` | Cross-tool session handoff via `.agent/SESSION.md` |
| `supabase` | Supabase products, Auth, CLI, MCP, migrations, RLS |
| `supabase-postgres-best-practices` | Postgres performance, indexes, RLS tuning |
| `ui-ux-pro-max` | UI/UX design systems, styling, accessibility, pre-delivery checklist |

---

## Reference Checklists

Quick references in `.claude/references/`:

| Reference | Use For |
|-----------|---------|
| `security-checklist.md` | Pre-deploy security verification |
| `testing-patterns.md` | Test structure and anti-patterns |
| `performance-checklist.md` | Core Web Vitals, optimization |
| `accessibility-checklist.md` | WCAG 2.1 AA compliance |
| `codegraph.md` | CodeGraph install (Claude Code) and Cursor MCP notes |
| `ontosight.md` | OntoSight CLI for visual call graphs |
| `agent-continuity.md` | Session handoff and `/resume` / `/handoff` |
| `supabase.md` | Supabase skills, MCP OAuth, secrets |

---

## Agent Behavior Guidelines

1. **Follow the workflow** — Use `/spec` → `/plan` → `/build` → `/review`
2. **Read `.agent/SESSION.md`** before planning or coding when present; use **`/resume`** to continue prior work
3. **Apply mandatory rules** — All rules in `.claude/rules/` are non-negotiable
4. **Test first** — Write failing tests before implementing
5. **Incremental changes** — Small commits, always buildable
6. **Explain before acting** — Describe changes before making them
7. **Fix root causes** — Don't patch symptoms
8. **Use the right agent** — Invoke specialized agents for their domains
9. **Hand off** — Update `.agent/SESSION.md` with **`/handoff`** before ending a session
