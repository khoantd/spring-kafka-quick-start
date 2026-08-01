# Agent session

> Cross-tool handoff state for Cursor, Claude Code, and Kiro. Update at session end (`/handoff`) or phase changes; read at session start (`/resume`).

## Meta

| Field | Value |
|-------|-------|
| **Updated** | YYYY-MM-DD |
| **Phase** | spec \| plan \| build \| test \| review \| debug |
| **Tool** | cursor \| claude \| kiro |
| **Persona** | _(optional, e.g. backend, architect)_ |

## Goal

_One paragraph: what we are building and why._

## Done

- _(completed work; include file paths or PR refs when useful)_

## In progress

- _(current task)_
- **Blockers:** _(none \| describe)_

## Next

1. _(ordered steps for the next agent)_

## Decisions

- _(non-obvious choices: API shape, libraries, approach)_

## Gotchas

- _(failed attempts, env quirks, test commands that matter)_

## Pointers

| Item | Location |
|------|----------|
| Spec | `SPEC.md` or `docs/specs/...` |
| Tasks | `tasks/todo.md` |
| Branch | `feature/...` |
| Key files | _(paths)_ |
