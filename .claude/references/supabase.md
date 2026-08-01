# Supabase reference

[class-ai-agent](https://github.com/khoantd/class-ai-agent) bundles official [Supabase Agent Skills](https://github.com/supabase/agent-skills) and wires the **Supabase MCP** server for Cursor and Kiro.

## Skills

| Skill | Use when |
|-------|----------|
| `supabase` | Any Supabase product work: Database, Auth, Edge Functions, Realtime, Storage, CLI, MCP, migrations, RLS, `supabase-js`, `@supabase/ssr` |
| `supabase-postgres-best-practices` | SQL, schema design, indexes, pooling, RLS performance, query review |

Paths: `.cursor/skills/supabase/`, `.cursor/skills/supabase-postgres-best-practices/` (and `.claude/skills/`, `.kiro/skills/` after install).

Invoke with **`@`** mention or let the agent load them when the task matches. See each skill’s `SKILL.md` for security checklists and workflow.

**Maintainers:** refresh vendored copies with `npm run sync:supabase-skills` (pin in `scripts/supabase-skills.lock.json`).

## MCP (Cursor & Kiro)

| Tool | MCP config |
|------|------------|
| Cursor | `.cursor/mcp.json` → `mcpServers.supabase` |
| Kiro | `.kiro/settings/mcp.json` → `mcpServers.supabase` |

Server URL: `https://mcp.supabase.com/mcp?features=docs` (HTTP, OAuth 2.1).

### After install

1. **Reload** Cursor or **restart** Kiro so MCP servers connect.
2. On first use, complete **OAuth** in the browser when prompted (Supabase account).
3. Health check (expect `401` without a token — server is up):
   ```bash
   curl -so /dev/null -w "%{http_code}" "https://mcp.supabase.com/mcp"
   ```

Useful MCP tools include `search_docs`, `list_projects`, `list_tables`, `execute_sql`, `get_advisors`, `get_logs`, and migration helpers. Prefer `search_docs` over guessing API behavior.

**Note:** Upstream skill text may refer to a project-root `.mcp.json`. In this scaffold, Supabase MCP lives only under `.cursor/mcp.json` and `.kiro/settings/mcp.json` — do not add a duplicate root `.mcp.json`.

## Claude Code

Skills install to `.claude/skills/`. Claude Code does not get MCP from this package by default. Options:

- [Supabase MCP setup](https://supabase.com/docs/guides/getting-started/mcp)
- [Supabase plugin for Claude Code](https://github.com/supabase/agent-skills)

## Secrets

Never commit service role keys, secret keys, or project tokens. Use environment variables per `.cursor/rules/security.mdc` (and `.claude/rules/security.md`, `.kiro/steering/security.md`).

## Learn more

- [Supabase AI skills docs](https://supabase.com/docs/guides/ai-tools/ai-skills)
- [Upstream repository](https://github.com/supabase/agent-skills)
- [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) — license and pinned version
