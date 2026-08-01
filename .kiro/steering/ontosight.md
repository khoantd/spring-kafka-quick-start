---
inclusion: always
description: "OntoSight CLI — visualize CodeGraph call subgraphs in the browser"
---

## OntoSight

[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight) visualizes CodeGraph call subgraphs in an interactive browser UI. It is **not** an MCP server — there are no `ontosight_*` tools. Use **CodeGraph MCP** (`codegraph_*`) to answer structural questions in chat; use **OntoSight CLI** when the user wants a visual, interactive call graph.

See **`.kiro/rules/codegraph.md`** for MCP usage and **`.kiro/references/ontosight.md`** for full flags and troubleshooting.

### CodeGraph MCP vs OntoSight

| Goal | Use |
|------|-----|
| Find symbols, callers, traces, impact (text in chat) | `codegraph_*` MCP tools |
| Visual exploration of a subgraph | `npx royalsolution-ontosight@0.2.1` |
| User says "show me the graph" | OntoSight CLI |
| Impact / blast radius demonstration | `codegraph_impact` → summary → OntoSight CLI |

Rule of thumb: gather facts with CodeGraph MCP; open OntoSight when visualization helps the human explore complexity you already identified.

### Project graph fidelity

OntoSight reads nodes from `.codegraph/codegraph.db` under the **`[project-path]`** argument (default: shell `cwd`). The browser must show **this workspace's** project graph — never a foreign repo or auto-init index from the wrong directory.

**Mandatory preflight** (before any OntoSight run):

1. Resolve **workspace root** (Cursor `${workspaceFolder}` / absolute path from user context).
2. `codegraph_status({ projectPath: "<workspace-root>" })` when MCP may be on wrong cwd; otherwise default.
3. If index missing → `npx @colbymchenry/codegraph init -i` **in workspace root** (ask user on large repos).
4. If MCP returns "wrong project" / path mismatch → pass `projectPath` on all subsequent `codegraph_*` calls.

**Hard rule:** Do not open OntoSight until `.codegraph/codegraph.db` exists under the **target workspace root**.

**Absolute project path** — never rely on bare `.` when workspace root is known:

```bash
# Required when workspace root is known
npx royalsolution-ontosight@0.2.1 "/absolute/path/to/workspace" \
  --symbol <name> --path <dir>
```

Pass workspace root as the **first positional `[project-path]`** argument. Optionally set shell `cwd` to workspace root as well — but always pass the absolute path.

**Seed binding** — seeds must come from the same project's CodeGraph index:

| Seed mode | Requirement |
|-----------|-------------|
| `--symbol` | Name **must** come from `codegraph_search` / `codegraph_context` with the **same** `projectPath`; `--path` must match the directory prefix of the returned file path |
| `--task` | Only after `codegraph_context({ task, maxNodes })`; prefer `--symbol` when user named a symbol |
| Default (no seed) | Full-project exploration only; still require absolute workspace path |

**Anti-patterns (explicit ban):**

- Do not use README/doc example symbols (`view_graph`, `deleteUser`) unless `codegraph_search` confirms they exist in **this** project.
- Do not run OntoSight from a subdirectory with bare `.` — auto-init may index the wrong tree.

**After launch:** confirm OntoSight terminal output shows the correct **project path** and topology symbols under that project. If mismatch → stop and re-run with corrected absolute path.

### When to run OntoSight

Run (or suggest) OntoSight when the user:

- Asks to visualize, show, or explore a call graph or architecture
- Wants an interactive view after you surfaced symbols via `codegraph_search` / `codegraph_context`
- Is doing onboarding or architecture review and benefits from a graph UI
- Asks about **impact**, **blast radius**, or **what breaks** and wants to see or demonstrate scope (refactor, rename, delete)

Do **not** run OntoSight to collect agent context — that duplicates CodeGraph MCP and blocks the terminal while the browser is open.

### Impact analysis demonstration

**Triggers** — open the graph, not only text:

- "impact", "blast radius", "what breaks", "what would break", "downstream", "affected by"
- "show impact", "demonstrate impact", "visualize impact"
- User is about to refactor, rename, or delete a symbol and wants to see scope

**Workflow** (mandatory order):

0. `codegraph_status({ projectPath: "<workspace-root>" })` — verify index at workspace root
1. `codegraph_search({ query: "<symbol>", projectPath: "<workspace-root>" })` — confirm name, file, kind
2. `codegraph_impact({ query: "<symbol>", projectPath: "<workspace-root>" })` — ranked blast radius (chat facts)
3. Read **`.kiro/skills/ui-ux-pro-max/IMPACT-DEMO.md`** — presentation checklist (use your tool's skills path: `.claude/skills/`, `.kiro/skills/`, `.agents/skills/`)
4. Optional: run ui-ux-pro-max searches from the playbook for chart/UX framing
5. Summarize in chat: seed symbol, top impacted callers, risk tier, what the graph will highlight
6. `npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir> --hops 3 --max-nodes 200`

**Hop guidance:** use `--hops 3` (or `4` for deep trees) for impact demos; prefer `--path` over raising `--max-nodes` when truncated.

**UX rule:** always give a **text summary before** opening the browser (progressive disclosure); include a **ranked table** of top impacted symbols as an accessible alternative to the graph alone.

### Suggested workflows

**Symbol the user named:**

0. `codegraph_status({ projectPath: "<workspace-root>" })`
1. `codegraph_search({ query: "<name>", projectPath: "<workspace-root>" })` → confirm file + kind
2. Summarize in chat
3. `npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir-from-search>`

**Feature area ("how does auth work?"):**

0. `codegraph_status({ projectPath: "<workspace-root>" })`
1. `codegraph_context({ task: "authentication flow", maxNodes: 20, projectPath: "<workspace-root>" })`
2. Summarize key symbols in chat
3. `npx royalsolution-ontosight@0.2.1 "<workspace-root>" --task "authentication flow" --hops 2 --max-nodes 200`

**Large repo:** prefer `--path` or `--symbol` before raising `--max-nodes`.

### Commands

Replace `<workspace-root>` with the absolute path to the open workspace (never bare `.` when root is known):

```bash
npx royalsolution-ontosight@0.2.1 "<workspace-root>"
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir>
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --task "auth flow" --hops 2 --max-nodes 200
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir> --hops 3 --max-nodes 200
```

Auto-creates the CodeGraph index when `.codegraph/codegraph.db` is missing (unless `ONTOSIGHT_SKIP_AUTO_INIT=1`). Ask the user before auto-init on very large repos.

### Prerequisites

| Requirement | Notes |
|-------------|-------|
| Node 20+ | For `npx royalsolution-ontosight@0.2.1` (see `classAiAgent.ontosightVersion`) |
| Python 3.11+ | Used by `ontosight-codegraph` |
| uv (recommended) or pipx | Wrapper tries `uvx` first, then `pipx run` |
| CodeGraph index | `.codegraph/codegraph.db` in workspace root |
