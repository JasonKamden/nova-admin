# Backend 注释、OpenAPI 与核心业务日志规范落地说明

## 1. 本轮目标

本轮在不改变既有业务边界和 API 路径的前提下，统一完善 Backend 的源码备注、Springdoc OpenAPI 契约说明和核心业务日志。

主要覆盖：

- Controller 接口分组、接口说明、Path/Query/File 参数说明。
- Request/Response DTO 类型和字段说明。
- Query/Page DTO 的 Springdoc `@ParameterObject` 展开。
- Entity 业务语义和数据库字段说明。
- Service 业务接口及方法职责说明。
- Enum、Config Properties、统一分页/登录上下文等重要公共模型说明。
- 核心写操作运行日志与持久化操作审计。

## 2. OpenAPI

所有正式 Controller 使用：

- `@Tag`：接口业务分组和模块说明。
- `@Operation`：每个 HTTP API 的业务含义。
- `@Parameter`：PathVariable、RequestParam、文件上传参数说明。
- `@ParameterObject`：GET 分页/查询 DTO 展开为可读查询参数。

所有业务 Request/Response DTO 使用 `@Schema` 描述模型与字段。

密码类输入字段标记为 `WRITE_ONLY`，登录 Token 响应字段标记为 `READ_ONLY`。

前端对接仍以运行后的 `/v3/api-docs` 为最终契约，不从 Entity 猜字段。

## 3. Entity 与 Service 注释

- Entity 类注释描述领域职责和多租户边界。
- Entity 字段全部补充 JavaDoc，明确主键、Tenant、状态、审计、逻辑删除、乐观锁等含义。
- Service 接口的所有方法均补充职责说明，复杂业务强调 Tenant、DataScope、缓存、权限或状态约束。
- Enum 常量补充业务语义，避免依赖枚举名称猜用途。
- Storage/Excel 等 Config Properties 补充字段用途，并特别说明敏感配置不得打印日志。

## 4. 核心业务日志

核心写操作继续通过 `@OperationAudit` 持久化审计，同时 `OperationAuditAspect` 统一输出结构化运行日志。

成功日志至少包含：

```text
module
type
description
userId
contextType
tenantId
requestId
traceId
durationMs
```

失败日志额外包含：

```text
exceptionType
```

不在普通运行日志中输出：

```text
password
oldPassword
newPassword
Token
Authorization
Cookie
SecretKey
AccessKey/SecretKey
文件二进制
```

登录业务额外输出成功/失败原因编码，但不打印密码；Context 切换记录目标 Context 和 tenantId。

本轮补齐审计覆盖的关键操作包括：

- Tenant 创建/修改/状态/删除。
- Department 新增/修改/状态/删除。
- User 新增/修改/状态/密码重置/移除/角色授权。
- Role 新增/修改/状态/删除/菜单授权。
- Menu 新增/修改/状态/删除。
- Dictionary 类型和数据新增/修改/删除。
- Config 新增/修改/删除。
- Cache 清理/刷新/批量清理。
- OnlineUser 强制下线。
- Profile 资料/密码/头像。
- File 上传/删除/批量删除。
- Message 草稿新增/修改/删除/发送/撤回。
- Excel 导入/导出。

## 5. 静态验收指标

本轮静态检查结果：

```text
Controller: 20
HTTP Endpoint: 104
@Tag 覆盖: 20 / 20
@Operation 覆盖: 104 / 104
@ParameterObject: 11
@Parameter: 75

DTO 文件: 67
带 @Schema DTO: 67 / 67
@Schema 总数: 574

Entity: 20
Entity 字段 JavaDoc: 206 / 206

Service 接口: 20
Service 接口方法 JavaDoc: 117 / 117

@OperationAudit: 52
```

`python scripts/validate_source.py` 已通过。

由于当前执行环境未安装 Maven/Docker，本轮没有把 `mvn clean compile/test/package` 和真实启动验证标记为通过；在正常开发环境仍需继续执行完整
Maven 与运行时验收。
