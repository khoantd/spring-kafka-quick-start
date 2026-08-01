---
description: "publish-npm"
---
# Publish to npm (maintainers)

## Description

Publish **`class-ai-agent`** to the npm registry: draft release notes from git, get maintainer approval, update README, bump version, verify CLI, publish.

## Triggers

Use when the maintainer says any of:

- **push to npm repo**
- **publish to npm**
- **publish class-ai-agent**

Or **@ mention this file** in Chat/Composer (`.agents/workflows/publish-npm.md` in Cursor; `.agents/workflows/publish-npm.md` in Kiro).

## Prerequisites

- Changes are ready to ship; working tree reflects what you are publishing.
- **`npm login`** completed for the `class-ai-agent` package scope.
- Two-factor auth enabled for npm **writes** if the account requires it (see README *Publishing to npm*).

## Workflow

### 1. Resolve baseline version

```bash
git tag -l 'v*' --sort=-v:refname | head -1
```

If no `v*` tags exist, use the latest `### x.y.z` heading under **## Release notes** in README, or `package.json` version as the last shipped baseline.

Strip a leading `v` from tags when comparing to semver (e.g. `v1.2.4` → `1.2.4`).

### 2. Draft release notes (do not publish yet)

```bash
git log <lastTagOrBaseline>..HEAD --pretty=format:'- %s (%h)'
```

- Group related commits; rewrite subjects for user-facing clarity.
- Drop noise (merge commits, duplicate WIP messages) unless relevant.
- Present the bullet list to the maintainer and **wait for explicit approval or edits**.

### 3. Confirm version bump

Default: **`patch`** (`npm version patch --no-git-tag-version`).

Use **`minor`** or **`major`** only if the maintainer requests it.

### 4. Bump version

```bash
npm version patch --no-git-tag-version   # or minor | major
```

Read the new version from `package.json`.

### 5. Update README

Prefer the helper script after approval (write bullets to a temp file):

```bash
npm run release:readme -- --version NEW_VERSION --date YYYY-MM-DD --notes-file /path/to/notes.md
```

`notes.md` should contain one bullet per line (with or without leading `- `).

If **## Release notes** is missing, add it before **## Contributing** first, then run the script.

Fallback: manually insert at the top of **## Release notes**:

```markdown
### NEW_VERSION — YYYY-MM-DD

- …
```

And sync the version badge: `version-NEW_VERSION` in the shields.io img (line ~21).

### 6. Verify CLI

```bash
npm run test:cli
```

Stop on failure; do not publish.

### 7. Publish

Local publish (provenance requires GitHub Actions OIDC — see README):

```bash
npm publish --access public --provenance=false
```

Or: `npm run publish:local`

If npm prompts for **OTP**, the maintainer enters it in the terminal.

On **403 / cannot publish over previously published versions**: bump with `npm version patch --no-git-tag-version` and retry (each semver can only be published once).

### 8. Report

Tell the maintainer:

- Published version (from `package.json`)
- https://www.npmjs.com/package/class-ai-agent

**Do not** `git commit`, tag, or push unless the maintainer separately asks.

## Maintainer quick reference

| Step | Command / action |
|------|------------------|
| Draft | `git log <tag>..HEAD --pretty=format:'- %s (%h)'` |
| Approve | Maintainer edits bullets in chat |
| Bump | `npm version patch --no-git-tag-version` |
| README | `npm run release:readme -- --version … --notes-file …` |
| Test | `npm run test:cli` |
| Publish | `npm run publish:local` (or `npm publish --access public --provenance=false`) |

See also [README — Publishing to npm](../../README.md#publishing-to-npm-maintainers) and [README — Release notes](../../README.md#release-notes).
