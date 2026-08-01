
# UI/UX — ui-ux-pro-max skill

When UI files are in context **or** the task involves UI/UX (components, pages, layouts, styling, accessibility, design systems, landing pages), follow this rule and read **`.claude/skills/ui-ux-pro-max/SKILL.md`** before acting.

**Skill paths by tool:** `.claude/skills/ui-ux-pro-max/`, `.claude/skills/ui-ux-pro-max/`, `.kiro/skills/ui-ux-pro-max/`, `.agents/skills/ui-ux-pro-max/`

Run search commands from the project root; adjust the path prefix to match your tool.

## Tiered workflow

| Task type | Required action |
|-----------|-----------------|
| **New UI** — pages, components, layouts, design systems, landing pages | Read SKILL → run `--design-system` search → supplement with `--domain` / `--stack` as needed |
| **Fixes / reviews** — styling bugs, a11y, hover states, responsive tweaks | Read SKILL → run targeted `--domain ux` or `--stack` searches; skip full design-system unless scope is large |
| **Trivial mechanical edits** — typo in className, rename prop, import fix | Apply SKILL pre-delivery checklist; no Python search required |

## New UI — design-system search (required)

```bash
python3 .claude/skills/ui-ux-pro-max/scripts/search.py "<product_type> <industry> <keywords>" --design-system [-p "Project Name"]
```

For multi-page projects, use `--persist` to save `design-system/MASTER.md` and page overrides (see SKILL Step 2b).

## Fixes / reviews — targeted searches

```bash
python3 .claude/skills/ui-ux-pro-max/scripts/search.py "<keyword>" --domain ux
python3 .claude/skills/ui-ux-pro-max/scripts/search.py "<keyword>" --stack html-tailwind
```

Default stack: `html-tailwind`. Also available: `react`, `nextjs`, `astro`, `vue`, `nuxtjs`, `nuxt-ui`, `svelte`, `shadcn`, `swiftui`, `react-native`, `flutter`, `jetpack-compose`, `angular`, `laravel`, `javafx`, `threejs`.

## Before delivery

Verify the SKILL **Pre-Delivery Checklist**: SVG icons (no emoji icons), contrast, `cursor-pointer` on interactive elements, hover/focus states, responsive breakpoints (375/768/1024/1440), no horizontal scroll on mobile, form labels, `prefers-reduced-motion`.
