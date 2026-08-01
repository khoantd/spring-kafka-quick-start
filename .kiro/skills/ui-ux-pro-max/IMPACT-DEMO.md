# Impact analysis demonstration — UX playbook

Use this playbook when presenting **CodeGraph impact analysis** with an **OntoSight** graph demo. It extends the [ui-ux-pro-max SKILL](SKILL.md) for a specific agent workflow — not a full design-system pass.

**Skill path by tool:** `.cursor/skills/ui-ux-pro-max/`, `.claude/skills/ui-ux-pro-max/`, `.kiro/skills/ui-ux-pro-max/`, `.agents/skills/ui-ux-pro-max/`

---

## When to use

User asks about blast radius, what breaks, downstream impact, or wants to **see** / **show** / **demonstrate** impact before a refactor, rename, or delete.

Pair with **`.cursor/rules/ontosight.mdc`** (or synced `ontosight` rule in your tool tree).

---

## ui-ux-pro-max searches (optional, recommended)

Run from project root. Adjust the skills path to match your tool.

### Network graph UX

```bash
python3 .cursor/skills/ui-ux-pro-max/scripts/search.py "relationship connection network graph" --domain chart
```

Use for: drilldown + hover expectations; provide an adjacency-list or ranked table alternative (network graphs are poor for screen-reader-only users).

### Impact tier framing

```bash
python3 .cursor/skills/ui-ux-pro-max/scripts/search.py "root cause decomposition hierarchy drill-down" --domain chart
```

Use for: grouping impact into tiers (direct callers vs transitive) in chat before opening the graph.

### Progressive disclosure

```bash
python3 .cursor/skills/ui-ux-pro-max/scripts/search.py "progressive disclosure loading feedback" --domain ux
```

Use for: summary-before-graph; tell the user what OntoSight is loading and what they will see.

---

## Chat summary template

Present this **before** running `npx royalsolution-ontosight@0.2.1` (with absolute workspace root as `[project-path]`):

### 1. Seed

- Symbol name, kind (function, class, …), file path

### 2. Blast radius

Top 5–10 from `codegraph_impact`, in a **ranked table**:

| Rank | Symbol | File | Relationship |
|------|--------|------|--------------|
| 1 | … | … | direct caller |
| 2 | … | … | transitive |

Group by risk: **direct callers** (highest) vs **transitive** (lower).

### 3. What the graph shows

- Seed node and `--hops` depth (typically 3 for impact demos)
- Note that OntoSight highlights critical / hub nodes in the topology table
- Mention interactive pan, zoom, and live CodeGraph re-query in the browser

### 4. Action

Short line: *"Opening interactive impact graph…"* then run (replace `<workspace-root>` with absolute workspace path):

```bash
npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir-from-search> --hops 3 --max-nodes 200
```

---

## Accessibility notes

- **Always** include the ranked table — graph-only answers fail accessibility guidance for network visualizations.
- Keep table columns sortable by impact rank when possible in prose.
- If subgraph truncates, say so and offer a narrower `--path` before raising `--max-nodes`.

---

## Full agent workflow

```text
0. codegraph_status({ projectPath: "<workspace-root>" })  → verify index at workspace root
1. codegraph_search({ query: "<symbol>", projectPath: "<workspace-root>" })
2. codegraph_impact({ query: "<symbol>", projectPath: "<workspace-root>" })
3. Read this file (IMPACT-DEMO.md)
4. Optional: ui-ux-pro-max chart/ux searches above
5. Chat summary (template sections 1–3)
6. npx royalsolution-ontosight@0.2.1 "<workspace-root>" --symbol <name> --path <dir> --hops 3 --max-nodes 200
```

See **`.cursor/references/ontosight.md`** for flags and troubleshooting.
