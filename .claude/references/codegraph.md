# CodeGraph reference

[CodeGraph](https://github.com/colbymchenry/codegraph) is a local, tree-sitter–parsed knowledge graph exposed to agents via MCP.

**Mandatory for all personas:** `.claude/agents/*.md` each include **CodeGraph (mandatory)** and **Index health (smart)**.

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

## Claude Code (included with class-ai-agent)

| Item | Path |
|------|------|
| Usage rules | `.claude/rules/codegraph.md` |
| Index (generated) | `.codegraph/` (gitignored) |

1. Install CodeGraph for Claude Code globally (see below).
2. Confirm CodeGraph MCP is available in Claude Code.
3. Use `codegraph_*` tools for structural questions; grep/read for literal text.

**Global install** (project scaffolding does not add Claude MCP config):

```bash
npx @colbymchenry/codegraph
# or: npm i -g @colbymchenry/codegraph
codegraph install --target=claude --yes
```

**Manual index:** `codegraph init -i` (class-ai-agent may run this on install)

## Cursor (via class-ai-agent)

- `.cursor/mcp.json` — CodeGraph MCP server
- `.cursor/rules/codegraph.mdc` — when to use `codegraph_*` tools

Reload Cursor after install. See `.cursor/references/codegraph.md`.

## Kiro (via class-ai-agent)

- `.kiro/settings/mcp.json` — CodeGraph MCP server
- `.kiro/steering/codegraph.md` — when to use `codegraph_*` tools

Restart Kiro after install. See `.kiro/references/codegraph.md`.

## Requirements

- **Node 20+** recommended for CodeGraph (class-ai-agent CLI itself supports Node 16.7+).
- Index data lives in `.codegraph/` — add to `.gitignore` (class-ai-agent does this automatically).

## Tool parameters

| Tool | Pass | Not |
|------|------|-----|
| `codegraph_search` | `query`, optional `limit` | — |
| `codegraph_context` | **`task`** (natural-language area), optional **`maxNodes`** | `query`, `limit` |

**Session handoff** (`/resume`, `.agent/SESSION.md`) is not a CodeGraph call — read those files with the editor Read tool.

## Troubleshooting

| Issue | Action |
|-------|--------|
| `task must be a non-empty string` | Use `task` (not `query`) on `codegraph_context`; use `maxNodes` (not `limit`). For `/resume`, read `.agent/SESSION.md` instead. |
| MCP "not initialized" | Ask user; run `npx @colbymchenry/codegraph init -i` once at workspace root |
| MCP wrong project / path mismatch | Pass `projectPath: "<workspace-root>"` on all `codegraph_*` calls |
| Stale symbols after edit | Wait ~2s for watcher sync; Read files in staleness banner; check `codegraph_status` **Pending sync** — do not re-init |
| Re-init loop | Never `init` after every edit or failed search; init only when index is missing |

See [CodeGraph README](https://github.com/colbymchenry/codegraph) for full troubleshooting.
