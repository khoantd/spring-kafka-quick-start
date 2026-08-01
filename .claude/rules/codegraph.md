 
## CodeGraph

This project has a CodeGraph MCP server (`codegraph_*` tools) configured. CodeGraph is a tree-sitter-parsed knowledge graph of every symbol, edge, and file. Reads are sub-millisecond and return structural information grep cannot.

**All agent personas** (`.claude/agents/*.md`) must use CodeGraph for structural questions — see the **CodeGraph (mandatory)** section in each persona file.

### When to prefer codegraph over native search

Use codegraph for **structural** questions — what calls what, what would break, where is X defined, what is X's signature. Use native grep/read only for **literal text** queries (string contents, comments, log messages) or after you already have a specific file open.

| Question | Tool |
|---|---|
| "Where is X defined?" / "Find symbol named X" | `codegraph_search` |
| "What calls function Y?" | `codegraph_callers` |
| "What does Y call?" | `codegraph_callees` |
| "How does X reach/become Y? / trace the flow from X to Y" | `codegraph_trace` (one call = the whole path, incl. callback/React/JSX dynamic hops) |
| "What would break if I changed Z?" | `codegraph_impact` |
| "Show me Y's signature / source / docstring" | `codegraph_node` |
| "Give me focused context for a task/area" | `codegraph_context` |
| "See several related symbols' source at once" | `codegraph_explore` |
| "What files exist under path/" | `codegraph_files` |
| "Is the index healthy?" | `codegraph_status` |
| Visual exploration of a subgraph in browser | `royalsolution-ontosight` (see **`.claude/rules/ontosight.md`**) |
| "Show/demonstrate impact of changing Z" | `codegraph_impact` → then OntoSight `--symbol Z` (see **`.claude/rules/ontosight.md`**) |

### Visualization

For interactive call-graph UI (user asks to "show the graph" or explore architecture visually), use **OntoSight CLI** after gathering context with `codegraph_*` tools. See **`.claude/rules/ontosight.md`**.

For **impact demonstration** requests ("show impact", "blast radius", "what breaks if I change X"), run `codegraph_impact` first, present a text summary, then open OntoSight — never stop at MCP text alone when the user wants to see or show impact.

**Project graph fidelity:** OntoSight is a separate CLI — MCP `${workspaceFolder}` binding does not apply automatically. Pass the **same workspace root** to OntoSight's `[project-path]` as you use for `projectPath` on `codegraph_*` calls when MCP cwd detection is wrong. Run `codegraph_status` before opening OntoSight; seeds (`--symbol`, `--task`) must come from queries against that same index. See **Project graph fidelity** in **`.claude/rules/ontosight.md`**.

### Tool parameters (do not mix)

| Tool | Required arg | Optional cap | Wrong args → error |
|------|--------------|--------------|-------------------|
| `codegraph_search` | **`query`** (symbol name) | `limit` (default 10) | — |
| `codegraph_context` | **`task`** (feature/bug description) | `maxNodes` (default 20) | `query` / `limit` → **`task must be a non-empty string`** |

`codegraph_context` is for **code structure** around a task — not for loading **`.agent/SESSION.md`** or `/resume` handoff (use **Read** + `.claude/commands/resume.md`).

### Rules of thumb

- **Answer directly — don't delegate exploration.** For "how does X work" / architecture questions, answer with 2-3 codegraph calls: `codegraph_context` first, then ONE `codegraph_explore` for the source of the symbols it surfaces. For a specific **flow** ("how does X reach Y") start with `codegraph_trace` from→to — one call returns the whole path with dynamic hops bridged — then ONE `codegraph_explore` for the bodies; don't rebuild the path with `codegraph_search` + `codegraph_callers`. Codegraph IS the pre-built index, so spawning a separate file-reading sub-task/agent — or running a grep + read loop — repeats work codegraph already did and costs more for the same answer.
- **Trust codegraph results.** They come from a full AST parse. Do NOT re-verify them with grep — that's slower, less accurate, and wastes context.
- **Don't grep first** when looking up a symbol by name. `codegraph_search` is faster and returns kind + location + signature in one call.
- **Don't chain `codegraph_search` + `codegraph_node`** when you just want context — `codegraph_context` is one call.
- **Don't loop `codegraph_node` over many symbols** — one `codegraph_explore` call returns several symbols' source grouped in a single capped call, while each separate node/Read call re-reads the whole context and costs far more.
- **Index lag — check the staleness banner, don't guess a wait.** When a codegraph response starts with "⚠️ Some files referenced below were edited since the last index sync…", the listed files are pending re-index — Read those specific files for accurate content. Files NOT in that banner are fresh and codegraph is authoritative for them. `codegraph_status` also lists pending files under "Pending sync".

### Smart index management

**Check before you init — never re-index by default.**

| Situation | Smart action |
|-----------|--------------|
| Session start / first structural query | `codegraph_status` (with `projectPath` if cwd may be wrong) |
| Index healthy, queries succeed | Use `codegraph_*`; trust watcher auto-sync (~1–2s) |
| Staleness banner on a few files | **Read** only listed files; wait for **Pending sync** — no `init` |
| MCP "not initialized" / no `.codegraph/codegraph.db` | Ask user; run `npx @colbymchenry/codegraph init -i` once at workspace root |
| Large repo, first init | Confirm with user before full index |
| Wrong project / path mismatch | Pass absolute `projectPath` on all `codegraph_*` calls |

**Never:** re-run `init` after every edit, failed search, or partial staleness; init from a subdirectory; init when the watcher will catch up.

### If `.codegraph/` doesn't exist

The MCP server returns "not initialized." Ask the user: *"I notice this project doesn't have CodeGraph initialized. Want me to run `codegraph init -i` to build the index?"*
