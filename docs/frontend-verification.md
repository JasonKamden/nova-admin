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

### Logout Regression

- Header logout returns browser to `/login/pwd-login?redirect=/system/user`: PASS
- `/api/auth/logout` is sent once and returns success: PASS

### Remaining Gaps

- Phase 2+ business pages still use the explicit placeholder view and were not part of this round's acceptance.
- Some backend route i18n keys are still displayed literally, for example `route.system_user`.
- Layout footer still carries inherited `Soybean` branding text.
