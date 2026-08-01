# Loop Library reference

[class-ai-agent](https://github.com/khoantd/class-ai-agent) bundles the [Loop Library agent skill](https://github.com/khoantd/loop-library/tree/main/skills/loop-library) and documents the companion **npm CLI** for fast loop shortlisting.

## Skill

| Skill | Use when |
|-------|----------|
| `loop-library` | Find, adapt, or design repeatable agent workflows (loops) with explicit triggers, verification, stopping conditions, guardrails, and handoffs |

Paths: `.cursor/skills/loop-library/` (and `.claude/skills/`, `.kiro/skills/`, `.agents/skills/` after `npm run sync:all`).

Invoke with **`@`** mention or let the agent load it when the task matches. See **`SKILL.md`** for interview flow, grounding rules, and delivery format.

**Maintainers:** refresh vendored copies with `npm run sync:loop-library-skill` (pin in `scripts/loop-library-skills.lock.json`).

## CLI (pairs with the skill)

Published on npm as [`loop-library`](https://www.npmjs.com/package/loop-library). Use the CLI for a fast JSON shortlist; apply the skill for verification fit, authority, and stopping-condition judgment.

```bash
npx loop-library@latest recommend "keep documentation current" --json
npx loop-library@latest search documentation --json
npx loop-library@latest show overnight-docs-sweep --json
npx loop-library@latest list --category engineering --json
npx loop-library@latest adapt overnight-docs-sweep --json
```

| Flag | Purpose |
|------|---------|
| `--json` | Machine-readable output (default for agents) |
| `--text` | Human-readable output |
| `--online` | Fetch live catalog (bundled fallback if unavailable) |

Requires Node 20+.

## Web catalog

- [Browse published loops](https://signals.forwardfuture.ai/loop-library/)
- [Guided loop builder](https://signals.forwardfuture.ai/loop-library/create/)

## Workflow summary

1. **Find:** `recommend --json` → skill ranks top candidates → recommend at most three with links.
2. **Adapt:** `show` / `adapt --json` → customize thresholds, tools, cadence without weakening the feedback cycle.
3. **Design:** skill interview when no published loop fits.
4. **One-shot:** skip loop-library when a single action suffices.

## Links

- [npm package](https://www.npmjs.com/package/loop-library)
- [GitHub repository](https://github.com/khoantd/loop-library)
- [Forward Future Loop Library](https://signals.forwardfuture.ai/loop-library/)
