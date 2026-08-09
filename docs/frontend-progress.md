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

## 2026-08-09

### P0 Real Login Integration

- Done: backend `java -jar` instance is running locally on `http://127.0.0.1:8080` for frontend real-login regression.
- Done: frontend dev server continues to target local `8080` in test mode and real browser login is verified.
- Done: login page now keeps only password login and removes demo login/register/other-account entry points.
- Done: login now integrates backend captcha with mount-time loading, click refresh, and automatic refresh after login failure.
- Done: request layer no longer sends the `apifoxToken` demo header.
- Done: header `ContextSwitcher` completed real backend switch regression against `/api/context/platform` and
  `/api/context/tenant/{tenantId}`.
- Done: logout flow regression issue was fixed by sending `/api/auth/logout` before clearing local auth state and by
  adding a reset reentry guard.

### Verification

- Backend `http://127.0.0.1:8080/actuator/health`: PASS
- Frontend `http://127.0.0.1:9527/login/pwd-login`: PASS
- `pnpm typecheck`: PASS
- `pnpm lint`: PASS
- Real browser login: PASS
- Captcha one-time use: PASS
- Captcha expiry: PASS
- Context switch `PLATFORM -> TENANT -> PLATFORM`: PASS
- Browser refresh keeps current session: PASS
- UI logout returns to login page and hits backend logout once: PASS

### Remaining notes

- Phase 2+ business pages still render the explicit placeholder page; this round only covered login/auth/context regression.
- Backend menu i18n keys like `route.system_user` still appear in the UI because localized route labels are not finished yet.
- Footer still contains inherited `Soybean` copyright text and is outside the P0 login/auth acceptance scope.
