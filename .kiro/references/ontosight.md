# OntoSight reference

[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight) visualizes [CodeGraph](https://github.com/colbymchenry/codegraph) call subgraphs in an interactive browser UI. **class-ai-agent** installs usage rules across all four agent trees; invocation via **`npx royalsolution-ontosight@0.2.1`** (`npx royalsolution-ontosight@0.2.1` or global install — no npm dependency in class-ai-agent).

**Recommended:** `npx royalsolution-ontosight@0.2.1` (npm wrapper pins PyPI `ontosight-codegraph` 0.2.0).

OntoSight complements CodeGraph MCP — use `codegraph_*` for structural facts in chat; use OntoSight when the user wants visual exploration. See also [`.cursor/references/codegraph.md`](codegraph.md).

## Per-tool wiring

| Tool | Rules | Reference |
|------|-------|-----------|
| Cursor | `.cursor/rules/ontosight.mdc` | `.cursor/references/ontosight.md` |
| Claude Code | `.claude/rules/ontosight.md` | `.claude/references/ontosight.md` |
| Kiro | `.kiro/steering/ontosight.md` | `.kiro/references/ontosight.md` |
| Antigravity | `.agent/rules/ontosight.md` | `.agents/references/ontosight.md` |

## Project graph fidelity

OntoSight loads nodes from `.codegraph/codegraph.db` under **`[project-path]`** (default: shell `cwd`). To ensure the browser shows **this workspace's** graph:

1. Resolve workspace root (absolute path).
2. Run `codegraph_status({ projectPath: "<workspace-root>" })` before OntoSight.
3. Pass **absolute workspace root** as `[project-path]` — never bare `.` when root is known.
4. Seed `--symbol` / `--task` from `codegraph_*` queries using the **same** `projectPath`.
5. After launch, verify terminal output: printed project path and topology symbols belong to this repo.

See **Project graph fidelity** in [`.cursor/rules/ontosight.mdc`](../rules/ontosight.mdc).

## Quick start

Replace `<workspace-root>` with the absolute path to the open workspace:

```bash
# Full project — seeds from highest fan-in symbols
npx royalsolution-ontosight@0.2.1 "<workspace-root>"

# Seed around a symbol (optionally scoped to a path)
# --symbol must exist in this project (confirm via codegraph_search first)
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path src/auth/

# Task-scoped subgraph (keyword FTS seeding)
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --task "auth flow" --hops 2 --max-nodes 200
```

Auto-creates the CodeGraph index when `.codegraph/codegraph.db` is missing (unless `ONTOSIGHT_SKIP_AUTO_INIT=1`).

## Flags

| Flag | Description | Default |
|------|-------------|---------|
| `[project-path]` | Project root containing `.codegraph/` | `.` (cwd) — **prefer absolute workspace root** |
| `--path <dir>` | Limit symbols to files under this path | — |
| `--symbol <name>` | Seed symbol for BFS subgraph expansion | auto (fan-in) |
| `--task <text>` | Natural-language seed (keyword match) | — |
| `--hops <n>` | BFS hop depth | `2` |
| `--max-nodes <n>` | Cap subgraph size | `200` |

`--symbol` and `--task` are mutually exclusive seeds; `--path` narrows either.

## Suggested agent workflows

### Symbol the user named

```text
0. codegraph_status({ projectPath: "<workspace-root>" })
1. codegraph_search({ query: "<name>", projectPath: "<workspace-root>" })  → confirm file + kind
2. Tell user what you found
3. npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir-from-search>
```

### Feature area ("how does auth work?")

```text
0. codegraph_status({ projectPath: "<workspace-root>" })
1. codegraph_context({ task: "authentication flow", maxNodes: 20, projectPath: "<workspace-root>" })
2. Summarize key symbols in chat
3. npx royalsolution-ontosight@0.2.1 "<workspace-root>" --task "authentication flow" --hops 2 --max-nodes 200
```

### Impact analysis demonstration

When the user wants to **know** or **see** impact (blast radius, what breaks, downstream callers):

**Triggers:** "impact", "blast radius", "what breaks", "what would break", "downstream", "affected by", "show impact", "demonstrate impact", "visualize impact"; or user is about to refactor/rename/delete a symbol.

**Workflow:**

```text
0. codegraph_status({ projectPath: "<workspace-root>" })
1. codegraph_search({ query: "<symbol>", projectPath: "<workspace-root>" })  → confirm file + kind
2. codegraph_impact({ query: "<symbol>", projectPath: "<workspace-root>" })  → ranked blast radius
3. Read skills/ui-ux-pro-max/IMPACT-DEMO.md  → UX presentation checklist
4. Optional: ui-ux-pro-max chart/ux searches (see playbook)
5. Summarize in chat (seed, ranked table, what graph shows)
6. npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir> --hops 3 --max-nodes 200
```

**Example** (replace paths and symbol with values from `codegraph_search` in **this** project):

```bash
npx royalsolution-ontosight@0.2.1 "/path/to/workspace" --symbol deleteUser --path src/services/ --hops 3 --max-nodes 200
```

**UX:** Always text summary before opening the browser; include a ranked table of top impacted symbols (accessible alternative to graph-only). See [IMPACT-DEMO.md](../skills/ui-ux-pro-max/IMPACT-DEMO.md).

**Truncated subgraph:** narrow `--path` or lower `--hops` before raising `--max-nodes`.

### Index missing

```bash
cd "<workspace-root>"
npx @colbymchenry/codegraph init -i
# or let the OntoSight wrapper auto-init (default) — only when [project-path] is correct
```

## Environment variables

| Variable | Effect |
|----------|--------|
| `ONTOSIGHT_SKIP_AUTO_INIT=1` | Fail if `.codegraph/codegraph.db` missing instead of running init |
| `ONTOSIGHT_CODEGRAPH_VERSION` | Pin PyPI `ontosight-codegraph` version |

## Requirements

| Requirement | Notes |
|-------------|-------|
| Node 20+ | For `npx royalsolution-ontosight@0.2.1` |
| Python 3.11+ | Used by `ontosight-codegraph` |
| uv (recommended) or pipx | Wrapper tries `uvx` first, then `pipx run` |
| CodeGraph index | `.codegraph/codegraph.db` in workspace root |

## What the user sees

1. Terminal prints project path, seed, hops/max-nodes, and a topology table (critical / hub nodes).
2. OntoSight opens in the browser with the call subgraph, critical-node highlights, and a CodeGraph query panel for live reloads.
3. Process stays running until the user closes the server (Ctrl+C in terminal).

**Verify fidelity:** confirm the printed **project path** matches workspace root and topology symbols are files under that project — not another repo. If mismatch, stop and re-run with the corrected absolute path.

## Troubleshooting

| Issue | Action |
|-------|--------|
| Browser shows symbols from another repo | Re-run with absolute workspace root as `[project-path]`; delete stray `.codegraph/` in wrong cwd if auto-init ran there |
| Empty or generic graph | Run `codegraph init -i` in workspace root; seed with `--symbol` from `codegraph_search` in this project |
| MCP and OntoSight disagree | Align paths: same root for MCP `projectPath` and OntoSight `[project-path]` |
| `CodeGraph index not found` | Run `npx @colbymchenry/codegraph init -i` in workspace root or remove `ONTOSIGHT_SKIP_AUTO_INIT` |
| `Neither uv nor pipx found` | Install [uv](https://docs.astral.sh/uv/getting-started/installation/) (recommended) or pipx |
| Subgraph truncated warning | Add `--symbol`, `--task`, or `--path`; lower scope before raising `--max-nodes` |
| Stale symbols after edits | Wait ~2s for CodeGraph watcher sync, or re-run init |
| Agent needs facts, not UI | Use `codegraph_*` MCP tools instead |

Upstream: [royalsolution-ontosight](https://www.npmjs.com/package/royalsolution-ontosight)
