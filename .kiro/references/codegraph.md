# CodeGraph reference

[CodeGraph](https://github.com/colbymchenry/codegraph) is a local, tree-sitter–parsed knowledge graph exposed to agents via MCP. **class-ai-agent** installs Cursor and Kiro MCP wiring plus usage rules, and runs `codegraph init -i` after scaffolding.

**Mandatory for all personas:** `.cursor/agents/*.md` each include **CodeGraph (mandatory)** — use `codegraph_*` for structural questions before grep/read exploration loops.

## Smart index management

| Situation | Smart action |
|-----------|--------------|
| Session start / first structural query | `codegraph_status` (pass `projectPath` if MCP cwd may differ) |
| Healthy index | Use `codegraph_*`; watcher auto-syncs edits in ~1–2s |
| Staleness banner (few files) | Read only listed files; check **Pending sync** — wait, do not `init` |
| Missing index | Ask user; `npx @colbymchenry/codegraph init -i` once at workspace root |
| Large repo first init | Confirm with user before full index |
| Path / wrong project | Absolute workspace root on `projectPath` and OntoSight `[project-path]` |

**Never** re-run `init` after normal edits, failed searches, or partial staleness.

## Kiro (included with class-ai-agent)

| Item | Path |
|------|------|
| MCP config | `.kiro/settings/mcp.json` |
| Usage rules | `.kiro/steering/codegraph.md` |
| Index (generated) | `.codegraph/` (gitignored) |

1. Restart Kiro after install so MCP connects.
2. Confirm **CodeGraph** appears under MCP in Kiro (IDE or CLI).
3. Use `codegraph_*` tools for structural questions; grep/read for literal text.

**Manual index:** `npx @colbymchenry/codegraph init -i`

**Skip auto-index on install:** `CODEGRAPH_SKIP_INIT=1 npx class-ai-agent`

## Kiro (included with class-ai-agent)

| Item | Path |
|------|------|
| MCP config | `.kiro/settings/mcp.json` |
| Usage rules | `.kiro/steering/codegraph.md` |
| Index (generated) | `.codegraph/` (gitignored) |

1. Restart Kiro after install so MCP connects.
2. Confirm **CodeGraph** under MCP in Kiro IDE or CLI settings.
3. Use `codegraph_*` tools for structural questions.

See `.kiro/references/codegraph.md` for full notes.

## Claude Code

Project scaffolding does **not** add Claude MCP config. Install CodeGraph globally:

```bash
npx @colbymchenry/codegraph
codegraph install --target=claude --yes
```

## Cursor

Cursor users get `.cursor/mcp.json` and `.cursor/rules/codegraph.mdc` from the same package. See `.cursor/references/codegraph.md`.

## Requirements

- **Node 20+** required for class-ai-agent CLI and CodeGraph MCP.
- First index can take a minute on large repos; progress prints during `npx class-ai-agent` install.

## Tool parameters

| Tool | Pass | Not |
|------|------|-----|
| `codegraph_search` | `query`, optional `limit` | — |
| `codegraph_context` | **`task`** (natural-language area), optional **`maxNodes`** | `query`, `limit` |

Example — wrong (search-style args on context):

```json
{ "query": "auth flow", "limit": 15 }
```

→ `Error: task must be a non-empty string`

Example — correct:

```json
{ "task": "how authentication flow works", "maxNodes": 15 }
```

**Session handoff** (`/resume`, `.agent/SESSION.md`) is not a CodeGraph call — read those files with the editor Read tool.

## Troubleshooting

| Issue | Action |
|-------|--------|
| `task must be a non-empty string` | Use `task` (not `query`) on `codegraph_context`; use `maxNodes` (not `limit`). For `/resume`, read `.agent/SESSION.md` instead. |
| MCP “not initialized” | Run `npx @colbymchenry/codegraph init -i` in project root |
| MCP wrong project / path mismatch | Pass `projectPath: "<workspace-root>"` on all `codegraph_*` calls; use the same root for OntoSight `[project-path]` |
| MCP not connecting | Reload Cursor; verify `.cursor/mcp.json`; test `npx @colbymchenry/codegraph serve --mcp` |
| Stale symbols after edit | Wait ~2s for watcher sync, or check staleness banner in tool output |
| Init failed during install | Run `npx @colbymchenry/codegraph init -i` manually |
| OntoSight shows wrong project's graph | Pass absolute workspace root to OntoSight (not bare `.`); run `codegraph_status` first — see [`.cursor/references/ontosight.md`](ontosight.md) |

## Visualization (OntoSight)

For interactive call-graph exploration in the browser, use **[OntoSight](https://www.npmjs.com/package/royalsolution-ontosight)** (`royalsolution-ontosight`). Gather facts with `codegraph_*` first; open OntoSight when the user wants a visual UI. Pass the **absolute workspace root** as `[project-path]` (not bare `.`) so the browser loads this project's graph. See [`.cursor/references/ontosight.md`](ontosight.md).

Upstream: [colbymchenry/codegraph](https://github.com/colbymchenry/codegraph)
