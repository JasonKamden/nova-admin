# Backend API Contract

统一响应：`R<T>`；分页：`R<PageResult<T>>`。OpenAPI：`/v3/api-docs`，Swagger UI：`/swagger-ui.html`。

主要接口分组：

- Auth：`/api/auth/**`
- Context：`/api/context/**`
- Platform：`/api/platform/tenants`、`/api/platform/menus`、`/api/platform/dashboard`
- System：`/api/system/departments`、`users`、`roles`、`menus`、`dictionaries`、`configs`
- Monitor：`/api/system/login-logs`、`operation-logs`、`online-users`、`caches`
- Profile：`/api/profile`
- Dashboard：`/api/dashboard`
- Files：`/api/files/**`、`/api/system/files`
- Message Admin：`/api/system/messages`
- Message Center：`/api/message-center/**`

前端对接应以运行后的 Springdoc OpenAPI 为最终 DTO/字段契约，不从 Entity 猜字段。详细接口路径和阶段验收见根目录
`BACKEND-EPICS.md`。
