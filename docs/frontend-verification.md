# Frontend Verification

## 2026-08-09

### Environment

- Backend: `java -jar backend/nova-boot/target/nova-boot-1.0.0-SNAPSHOT.jar`
- Backend URL: `http://127.0.0.1:8080`
- Frontend URL: `http://127.0.0.1:9527`
- Browser regression: local Chrome driven by Playwright

### Build Verification

- `pnpm typecheck`: PASS
- `pnpm lint`: PASS
- `pnpm build`: PASS

### Backend Connectivity

- `GET /actuator/health`: PASS
- Frontend login page reachable at `/login/pwd-login`: PASS

### Real Login Regression

- Login page branding shows `Nova 管理系统`: PASS
- Login page removed demo buttons and non-password login entries: PASS
- `GET /api/auth/captcha` returns real captcha payload: PASS
- Wrong captcha shows backend error and refreshes captcha automatically: PASS
- Wrong password shows backend error and refreshes captcha automatically: PASS
- Correct credentials `platform-admin / Nova@123456` can log in through the real page: PASS
- Login success triggers `/api/auth/me`, `/api/context/options`, and `/api/auth/menus`: PASS

### Captcha Semantics

- Same captcha can be used successfully once and is rejected on second use: PASS
- Forced expiry in Redis causes login rejection with `验证码已失效，请重新获取`: PASS

### Context Regression

- Header `ContextSwitcher` shows `平台管理` after platform login: PASS
- Switching to tenant `1001 / 默认 Tenant` succeeds through `/api/context/tenant/1001`: PASS
- Switching back to platform succeeds through `/api/context/platform`: PASS
- Page refresh after login keeps the active authenticated session: PASS
- `GET /api/dashboard` real Tenant dashboard aggregation: PASS
- `GET /api/platform/dashboard` real Platform dashboard aggregation: PASS

### Logout Regression

- Header logout returns browser to `/login/pwd-login?redirect=/system/user`: PASS
- `/api/auth/logout` is sent once and returns success: PASS

### Remaining Gaps

- `platform_tenant` real page regression:
  `GET /api/platform/tenants`, `GET /api/platform/tenants/{id}`, `POST /api/platform/tenants`,
  `PUT /api/platform/tenants/{id}`, `PUT /api/platform/tenants/{id}/status`,
  `DELETE /api/platform/tenants/{id}`, `GET /api/platform/tenants/options`: PASS
- `platform_tenant` real browser page open and data render at `/platform/tenant`: PASS
- `system_department` real page regression:
  `GET /api/system/departments`, `GET /api/system/departments/tree`, `GET /api/system/departments/{id}`,
  `POST /api/system/departments`, `PUT /api/system/departments/{id}`,
  `PUT /api/system/departments/{id}/status`, `DELETE /api/system/departments/{id}`: PASS
- `system_department` real browser login, page open, and row render at `/system/department`: PASS
- `system_user` real page regression:
  `GET /api/system/users/{id}`, `POST /api/system/users`, `PUT /api/system/users/{id}`,
  `PUT /api/system/users/{id}/status`, `PUT /api/system/users/{id}/password`,
  `GET /api/system/users/{id}/roles`, `PUT /api/system/users/{id}/roles`, `DELETE /api/system/users/{id}`: PASS
- `system_user` real browser login, page open, and row render at `/system/user`: PASS
- `system_role` real page regression:
  `GET /api/system/roles`, `GET /api/system/roles/{id}`, `POST /api/system/roles`,
  `PUT /api/system/roles/{id}`, `PUT /api/system/roles/{id}/status`,
  `GET /api/system/roles/{id}/menus`, `PUT /api/system/roles/{id}/menus`, `DELETE /api/system/roles/{id}`: PASS
- `system_role` real browser login, page open, and row render at `/system/role`: PASS
- `system_menu` Tenant-side real page regression: `GET /api/system/menus`: PASS
- `system_menu` Platform-side real page regression:
  `GET /api/platform/menus`, `GET /api/platform/menus/{id}`, `POST /api/platform/menus`,
  `PUT /api/platform/menus/{id}`, `PUT /api/platform/menus/{id}/status`,
  `DELETE /api/platform/menus/{id}`: PASS
- `system_menu` real browser tree render at `/system/menu` in `TENANT` context: PASS
- `system_menu` real browser CRUD action render at `/system/menu` in `PLATFORM` context: PASS
- `home` real browser dashboard render at `/home` in `TENANT` context: PASS
- `home` real browser dashboard render at `/home` in `PLATFORM` context: PASS
- `profile` built-in route render at `/profile`: PASS
- Header avatar dropdown `个人中心 -> /profile` navigation: PASS
- `profile` real browser data render in `PLATFORM` context: PASS
- `profile` basic-info update regression:
  nickname change reflects on page and Header immediately, and restored to `平台管理员`: PASS
- `message_center` formal route page render at `/message/center`: PASS
- Browser direct SSE connection preflight `OPTIONS /api/message-center/sse`: PASS
- Browser real SSE push refresh on `/message/center` after sending a new message in current Tenant Context: PASS
- Browser real SSE no longer reports CORS policy errors in dev proxy mode: PASS
- `dashboard` Tenant announcements list refreshes with newly delivered messages after SSE/message-center fix: PASS
- `system_dictionary` real page regression:
  `GET /api/system/dictionaries/types`, `POST /api/system/dictionaries/types`,
  `PUT /api/system/dictionaries/types/{id}`, `DELETE /api/system/dictionaries/types/{id}`,
  `GET /api/system/dictionaries/types/{id}/data`, `POST /api/system/dictionaries/types/{id}/data`,
  `PUT /api/system/dictionaries/data/{id}`, `DELETE /api/system/dictionaries/data/{id}`: PASS
- `system_dictionary` real browser login and page render at `/system/dictionary`: PASS
- `system_config` real page regression:
  `GET /api/system/configs`, `POST /api/system/configs`, `PUT /api/system/configs/{id}`,
  `DELETE /api/system/configs/{id}`: PASS
- `system_config` real browser login and page render at `/system/config`: PASS
- `system_message` real page regression:
  `GET /api/system/messages`, `GET /api/system/messages/{id}`, `POST /api/system/messages`,
  `PUT /api/system/messages/{id}`, `DELETE /api/system/messages/{id}`,
  `POST /api/system/messages/{id}/recipient-preview`, `POST /api/system/messages/{id}/send`,
  `GET /api/system/messages/{id}/recipients`, `POST /api/system/messages/{id}/withdraw`: PASS
- `system_message` real browser login and page render at `/system/message`: PASS
- Home quick-entry exclusion now uses the real hidden route path `/message/center` instead of the stale `/message-center`
  alias: PASS
- Route i18n keys used in this round (`route.file`, `route.monitor`, `route.system_dictionary`,
  `route.system_config`, `route.system_message`) are rendered as localized labels instead of raw keys: PASS
- Layout footer and branding text now use `Nova`: PASS
