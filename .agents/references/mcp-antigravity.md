# Antigravity MCP reference

Antigravity reads MCP server configuration from a **user-level** file (not committed to the project):

| Platform | Path |
|----------|------|
| macOS / Linux | `~/.gemini/antigravity/mcp_config.json` |
| Windows | `C:\Users\<USERNAME>\.gemini\antigravity\mcp_config.json` |

**Open in IDE:** Agent panel → **...** → **MCP Servers** → **Manage MCP Servers** → **View raw config**.

After editing, save and restart Antigravity (or refresh MCP servers in the UI).

## Important differences from Cursor

| Cursor (`.cursor/mcp.json`) | Antigravity (`mcp_config.json`) |
|-------------------------------|-----------------------------------|
| Project-level file | User-level file |
| HTTP servers use `url` | HTTP servers use `serverUrl` |
| `${workspaceFolder}` in args | Use absolute project path or cwd-relative paths |

## Example configuration

Derived from class-ai-agent's bundled Cursor MCP. **Do not commit secrets** — use environment variables.

```json
{
  "mcpServers": {
    "codegraph": {
      "command": "npx",
      "args": [
        "-y",
        "@colbymchenry/codegraph",
        "serve",
        "--mcp",
        "--path",
        "/absolute/path/to/your/project"
      ]
    },
    "supabase": {
      "serverUrl": "https://mcp.supabase.com/mcp?features=docs"
    }
  }
}
```

Replace `/absolute/path/to/your/project` with your workspace root so CodeGraph indexes the correct tree.

**Supabase:** Complete OAuth in the browser the first time you use Supabase MCP tools.

## CodeGraph without MCP

Install globally for CLI use:

```bash
npx @colbymchenry/codegraph
codegraph install --target=claude --yes
```

Per-project index: `npx @colbymchenry/codegraph init -i` (class-ai-agent runs this on install).

## Troubleshooting

| Issue | Action |
|-------|--------|
| MCP tools missing | Restart Antigravity; verify JSON syntax |
| HTTP server fails | Use `serverUrl` not `url` |
| CodeGraph stale | Wait ~2s after edits; run `codegraph init -i` if index missing |
| Too many tools | Antigravity recommends keeping enabled tools under ~50 |

See also `.agents/references/codegraph.md`.
