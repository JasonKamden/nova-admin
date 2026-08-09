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
- Done: login now integrates backend captcha with mount-time loading, click refresh, and automatic refresh after login
  failure.
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

- Done: `platform_tenant` now lands on a real page instead of the placeholder, with backend-driven search, create,
  detail, update, enable/disable, delete, and tenant option lookup wired to `/api/platform/tenants*`.
- Done: `system_department` now lands on a real page instead of the placeholder, with backend-driven tree query,
  create root/child, detail, update, enable/disable, delete, parent selector, and leader selector wired to
  `/api/system/departments*` and `/api/system/users`.
- Done: `system_user` now lands on a real page instead of the placeholder, with backend-driven page query, detail,
  create, update, enable/disable, reset password, role assignment, and delete wired to `/api/system/users*`.
- Done: `system_role` now lands on a real page instead of the placeholder, with backend-driven page query, detail,
  create, update, enable/disable, menu authorization, and delete wired to `/api/system/roles*` and
  `/api/system/menus`.
- Done: `system_menu` now lands on a real read-only tree page instead of the placeholder, matching current Tenant-side
  backend capability exposed by `/api/system/menus`.
- Done: `system_menu` now upgrades to a context-aware real page: it stays read-only in `TENANT` context, and exposes
  real `PLATFORM` create/detail/update/enable-disable/delete flows through `/api/platform/menus*`.
- Done: `system_dictionary` now lands on a real page instead of the placeholder, with backend-driven left type/right
  data management wired to `/api/system/dictionaries/types*` and `/api/system/dictionaries/data*`.
- Done: `system_config` now lands on a real page instead of the placeholder, with backend-driven page query, create,
  update, detail, and delete wired to `/api/system/configs`.
- Done: `system_message` now lands on a real page instead of the placeholder, with backend-driven draft create/update,
  preview recipient count, send, withdraw, detail, and recipient read-status query wired to `/api/system/messages*`.
- Done: dynamic route adapter now preserves multi-segment top-level backend routes such as `/platform/tenant` and
  `/system/department` instead of incorrectly rewriting them to `layout.base$view.*`.
- Done: `home` now uses Nova real dashboard aggregation instead of Soybean mock content, with `/api/dashboard` for
  `TENANT` context and `/api/platform/dashboard` for `PLATFORM` context.
- Done: dynamic route mode now keeps the built-in `home` route available, so `/home` no longer falls into `not-found`
  after backend menu initialization.
- Tenant browser regression is complete for page open plus real data render, and API regression is complete for
  create/detail/update/status/options/delete with cleanup.
- Department browser regression is complete for real page login, real page render, and real list data render; API
  regression is complete for create/detail/update/status/tree/delete with cleanup.
- User browser regression is complete for real page login, real page render, and real list data render; API regression
  is complete for create/detail/update/status/password/roles/delete with cleanup.
- Role browser regression is complete for real page login, real page render, and real list data render; API regression
  is complete for create/detail/update/status/menu grant/delete with cleanup.
- Menu browser regression is complete for real page login and real menu tree render in `TENANT` context; platform menu
  API regression is complete for create/detail/update/status/delete with cleanup, and `PLATFORM` browser regression is
  complete for action visibility plus real CRUD entry render at `/system/menu`.
- Home dashboard browser regression is complete in both `TENANT` and `PLATFORM` contexts, with real aggregate data
  rendered instead of Soybean demo cards and charts.
- Done: `profile` now lands on a real built-in page instead of falling into `not-found`, with backend-driven
  `/api/profile` query, basic-profile update, password change, avatar upload, and Header user-center entry wired in.
- Done: dynamic route mode now preserves the built-in `profile` route alongside `home`, so Header user-center
  navigation works even though `profile` is not part of backend `sys_menu`.
- Done: profile basic-info update now synchronizes Header user info immediately from the profile response instead of
  waiting for `/api/auth/me` to refresh stale login-context snapshot data.
- Dictionary browser regression is complete for real page login and real type/data page render; API regression is
  complete for type create/update/delete and data create/update/delete with cleanup.
- Config browser regression is complete for real page login and real table render; API regression is complete for
  create/update/delete with cleanup.
- Message browser regression is complete for real page login and real table render; API regression is complete for
  draft create/detail/update/delete plus preview/send/recipient query/withdraw with cleanup.
- Done: Header `MessageBell`, `Message Store`, formal `Message Center` route page, and browser-level SSE regression are
  now closed end-to-end on the real backend.
- Done: browser SSE direct-backend preflight in dev proxy mode is fixed by backend global CORS handling plus
  `OPTIONS` auth-bypass for `/api/**`.
- Done: message push events are now published after transaction commit instead of inside an uncommitted transaction,
  which closes the last gap where the event arrived but the page refreshed against stale unread data.
- Done: the formal message center route path is verified at `/message/center`, and the Home quick-entry exclusion now
  matches that real path instead of the stale `/message-center` alias.
- Done: route labels used in this stage (`file`, `monitor`, `system_dictionary`, `system_config`,
  `system_message`, `message_center`) now render through i18n instead of exposing raw route keys.
- Done: footer and remaining inherited branding in this scope now use `Nova`.
