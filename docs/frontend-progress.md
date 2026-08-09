# Frontend Progress

## 2026-08-08

### Phase 1

- Done: replaced Soybean mock auth/route contract with Nova `/api/auth` and `/api/context` APIs.
- Done: auth store now targets current user, context, permissions, and backend menu loading instead of static
  `roles/buttons`.
- Done: added header `ContextSwitcher` and backend-driven dynamic route adapter.
- Done: added explicit fallback route view for backend menu targets that are not implemented yet, so Phase 1 can
  complete without pretending later CRUD pages are finished.

### Verification

- Node: `v24.9.0`
- pnpm: `10.14.0`
- `pnpm install`: PASS
- `pnpm typecheck`: PASS
- `pnpm lint`: PASS
- `pnpm build`: PASS

### Known blockers

- Backend service was not reachable at `http://127.0.0.1:8080` during this round, so real browser联调 is not yet
  verified.
- Phase 2+ business pages are not marked complete in this round; unresolved backend menu targets currently fall back to
  an explicit placeholder view instead of fake CRUD pages.
- `pnpm lint --fix` applied broad formatting/style fixes across the frontend workspace, so the final diff is larger than
  the Phase 1 logic-only surface.
