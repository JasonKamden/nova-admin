# Nova Backend 纯净重实现 Epic

> 实现基线：根目录 `backend.md` + 已确定的前端 API/页面契约。  
> 目标：一次建立可供 SoybeanAdmin 前端稳定对接的 Backend 契约，不再按“建表/建类”算完成，而按业务闭环验收。

## 1. 总体完成链路

每个 Epic 都必须完成：

```text
需求/API 契约
→ SQL
→ Entity
→ MyBatis-Flex Mapper + TableDef
→ ReqDto/RespDto
→ MapStructPlus
→ Service
→ Controller/OpenAPI
→ Sa-Token Permission
→ Tenant Fence + DataScope
→ Cache / Audit
→ Unit Test / Integration Test
→ Maven compile/test/package
→ Boot Smoke Test
```

## 2. 最终模块

```text
backend/
├── nova-common/
│   ├── nova-common-core
│   ├── nova-common-web
│   ├── nova-common-security
│   ├── nova-common-tenant
│   ├── nova-common-mybatis
│   ├── nova-common-cache
│   ├── nova-common-log
│   ├── nova-common-excel
│   └── nova-common-storage
├── nova-modules/
│   ├── nova-system
│   ├── nova-file
│   └── nova-message
└── nova-boot
```

## 3. Epic 总览

| Epic  | 主题                      | 关键交付                                                                         |
|-------|-------------------------|------------------------------------------------------------------------------|
| BE-00 | Clean Foundation        | Maven、统一响应/异常、APT、Sa-Token、TenantContext、Redis/Caffeine、Storage、日志基础         |
| BE-01 | Auth / Context / Tenant | 登录退出、PLATFORM/TENANT、Context 切换、Tenant 生命周期和初始化                              |
| BE-02 | RBAC / DataScope        | Department、User/UserTenant、Role/UserRole、Menu/RoleMenu、按钮权限、CUSTOM DataScope |
| BE-03 | Dictionary / Config     | 字典类型/数据、参数、缓存失效                                                              |
| BE-04 | Audit / Monitor         | 登录日志、完整操作日志详情、在线用户、强制下线                                                      |
| BE-05 | Logical Cache           | 逻辑缓存注册、详情、清理、刷新、批量清理、Redis 状态                                                |
| BE-06 | Profile / Dashboard     | 个人中心、密码/头像、Tenant Dashboard、Platform Dashboard                               |
| BE-07 | File / Storage          | File、业务附件关系、Local、MinIO、上传/预览/下载/删除、安全校验                                     |
| BE-08 | Message / SSE           | 消息管理、接收快照、阅读跟踪、消息中心、未读数、SSE、多节点推送                                            |
| BE-09 | FastExcel               | 用户模板、导入、导出、错误明细、行数保护                                                         |
| BE-10 | Security / Release      | 空库初始化、Testcontainers、安全回归、Maven/Boot/Docker 完整验收                             |

推荐里程碑：

```text
M1 Core      = BE-00 + BE-01 + BE-02
M2 System    = BE-03 + BE-04 + BE-05 + BE-06
M3 Shared    = BE-07 + BE-08 + BE-09
M4 Release   = BE-10
```

---

## BE-00 Clean Foundation

### 目标

建立后续所有模块唯一技术基线。

### 实现

- Java 21 / Spring Boot 3.5.x Maven 多模块。
- `R<T>`、`PageResult<T>`、稳定错误码、统一异常处理。
- MyBatis-Flex + APT TableDef；禁止 Mapper XML。
- MapStructPlus Processor/Converter；普通转换优先编译期映射。
- Sa-Token 为唯一 Web 认证入口；Spring Security 仅使用 crypto/BCrypt 能力。
- `CurrentLoginUser`、`LoginUserUtils`、`TenantContext`、请求期 Tenant Context 绑定。
- Redis/Redisson/Spring Cache/Caffeine 基础设施。
- `RequestIdFilter`、操作审计注解/AOP、日志脱敏与截断。
- Storage 抽象：Local/MinIO。
- FastExcel 基础能力。
- `JsonUtils`、`TreeUtils`、`DigestUtils`、`ServletUtils`、`IpUtils`、`RedisUtils` 只在真实调用场景使用。

### 验收

- 不存在 BeanUtils/BeanCopier/Mapper XML/伪 Tenant。
- APT 配置集中在父 POM。
- `mvn clean compile/test/package` 可通过时必须通过。

---

## BE-01 Auth / Context / Tenant

### API

```http
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
GET  /api/auth/menus
GET  /api/auth/permissions

GET /api/context/current
GET /api/context/options
PUT /api/context/platform
PUT /api/context/tenant/{tenantId}

GET    /api/platform/tenants
GET    /api/platform/tenants/{tenantId}
GET    /api/platform/tenants/options?keyword=
POST   /api/platform/tenants
PUT    /api/platform/tenants/{tenantId}
PUT    /api/platform/tenants/{tenantId}/status
DELETE /api/platform/tenants/{tenantId}
```

### 规则

- 平台管理员登录默认 PLATFORM；普通用户进入第一个有效 Tenant。
- 平台管理员可切换有效 Tenant，不要求 `sys_user_tenant` 逐租户绑定。
- Tenant 切换校验存在、启用、未过期、访问资格。
- 切换后重建角色、菜单、按钮权限、Department 与 DataScope 相关登录快照。
- 创建 Tenant 在事务中初始化默认 Role、tenant_admin、RoleMenu、UserTenant/UserRole；创建成功仍处 PLATFORM。
- Context/Tenant 关键操作进入操作日志。

---

## BE-02 RBAC / DataScope

### Department

```http
GET    /api/system/departments
GET    /api/system/departments/tree
GET    /api/system/departments/{id}
POST   /api/system/departments
PUT    /api/system/departments/{id}
PUT    /api/system/departments/{id}/status
DELETE /api/system/departments/{id}
```

支持 Tree、父子校验、循环校验、负责人校验、子节点/成员删除保护。

### User/UserTenant

```http
GET    /api/system/users
GET    /api/system/users/{id}
POST   /api/system/users
PUT    /api/system/users/{id}
PUT    /api/system/users/{id}/status
PUT    /api/system/users/{id}/password
DELETE /api/system/users/{id}
GET    /api/system/users/{id}/roles
PUT    /api/system/users/{id}/roles
```

Tenant 新增用户 = 创建/复用全局身份 + 建立 Tenant membership + Role 关系；租户管理员不得枚举平台敏感身份。

### Role/UserRole

```http
GET    /api/system/roles
GET    /api/system/roles/options
GET    /api/system/roles/{id}
POST   /api/system/roles
PUT    /api/system/roles/{id}
PUT    /api/system/roles/{id}/status
DELETE /api/system/roles/{id}
GET    /api/system/roles/{id}/menus
PUT    /api/system/roles/{id}/menus
```

角色与 DataScope 解耦；用户角色和角色菜单都采用全量替换语义。

### Menu/RoleMenu

- 全局菜单定义由 PLATFORM 管理。
- Tenant 读取当前授权菜单树。
- Menu 类型：DIRECTORY/MENU/BUTTON。
- 权限由 `permission_code` 提供给 Sa-Token。

### DataScope

真正实现：`ALL/TENANT/DEPARTMENT/DEPARTMENT_AND_CHILDREN/SELF/CUSTOM`。
CUSTOM 通过 `sys_role_department` 保存授权 Department；Tenant Fence 与 DataScope 必须同时生效。

---

## BE-03 Dictionary / Config

### Dictionary

- `sys_dict_type` + `sys_dict_data` Tenant 隔离。
- 类型：搜索、新增、编辑、删除；code 创建后不可修改。
- 类型存在数据时禁止删除。
- 数据：分页、新增、编辑、删除、排序、状态。
- tagType 只允许：`default/primary/info/success/warning/error`。
- 字典变更自动失效 `system.dictionary` 缓存。

### Config

- Tenant 配置分页、新增、修改、删除。
- 敏感配置列表/详情脱敏，不把 Secret 明文返回前端。
- 变更自动失效 `system.config` 缓存。

---

## BE-04 Audit / Monitor

### Login Log

记录成功/失败登录：userId、username、Context、Tenant、IP、User-Agent、failureReason、requestId、时间。

### Operation Log

列表只读；详情专门支撑“大尺寸右侧 Drawer + 单页分区”：

```text
Basic
Request(method/uri/ip/userAgent/contentType/headers/query/path/body)
Response(httpStatus/businessCode/body)
Exception(type/errorCode/message/location/stack)
```

请求/响应 JSON 脱敏并限制长度；文件内容不入日志；password/token/cookie/secret/accessKey 永不记录明文。

### Online User

- 当前合法范围在线会话列表。
- Tenant 管理员只能看当前 Tenant。
- 强制下线使用服务端 sessionId，不向前端返回真实 Token。

---

## BE-05 Logical Cache

缓存管理不是 Redis Desktop Manager。

```http
GET  /api/system/caches
GET  /api/system/caches/{cacheCode}
POST /api/system/caches/{cacheCode}/clear
POST /api/system/caches/{cacheCode}/refresh
POST /api/system/caches/batch-clear
GET  /api/system/caches/redis/status
```

逻辑缓存示例：
`system.dictionary`、`system.config`、`system.menu`、`security.userPermission`、`system.departmentTree`。
禁止 Key/Value 任意浏览、`KEYS *`、FLUSHDB/FLUSHALL、任意 Redis 命令。

---

## BE-06 Profile / Dashboard

### Profile

```http
GET /api/profile
PUT /api/profile
PUT /api/profile/password
POST /api/profile/avatar
```

可修改头像/昵称/手机号/邮箱/性别/bio；Tenant、Department、Role、platformAdmin、status 只读。改密码后退出当前会话。

### Dashboard

```http
GET /api/dashboard
GET /api/platform/dashboard
```

Tenant 工作台：用户/Department/Role/在线用户、7 天登录趋势、用户状态、最近操作。  
Platform 工作台：Tenant 总数/启用/停用、平台用户、今日登录等。通知公告由 Message Center 接口组合，避免
`nova-system -> nova-message` 反向依赖。

---

## BE-07 File / Storage

### 数据

`sys_file` + `sys_file_relation`。

### API

```http
POST   /api/files/upload
GET    /api/system/files
GET    /api/files/{id}
GET    /api/files/{id}/preview
GET    /api/files/{id}/download
DELETE /api/system/files/{id}
DELETE /api/system/files/batch
```

### 安全

Local/MinIO 统一 `StorageService`；校验大小、扩展名/MIME、路径、Tenant、访问权限；预览只允许安全白名单；业务模块通过
fileId/关系表绑定，不直接依赖 MinIO SDK。

---

## BE-08 Message / Message Center / SSE

### Message Management

```http
GET    /api/system/messages
GET    /api/system/messages/{id}
POST   /api/system/messages
PUT    /api/system/messages/{id}
DELETE /api/system/messages/{id}
POST   /api/system/messages/{id}/recipient-preview
POST   /api/system/messages/{id}/send
POST   /api/system/messages/{id}/withdraw
GET    /api/system/messages/{id}/recipients
```

状态：DRAFT/SENT/WITHDRAWN。发送后生成 `sys_message_user` 接收快照，历史接收人不随 Role/Department 后续变化漂移；SENT
不允许直接修改/删除；撤回保留审计/阅读历史。

接收范围：ALL/DEPARTMENT/ROLE/USER；TENANT 的 ALL 只表示当前 Tenant 有效成员。富文本写入前服务端清洗；附件通过
`sys_message_file` 绑定已有 fileId。

### Message Center

```http
GET /api/message-center/unread-count
GET /api/message-center/recent?limit=10
GET /api/message-center/messages
GET /api/message-center/messages/{id}
PUT /api/message-center/messages/{id}/read
PUT /api/message-center/read-all
GET /api/message-center/sse
```

点击具体消息时幂等标记已读；打开铃铛本身不自动全部已读。后端返回真实 unreadCount，前端自行将 >=100 显示为 `99+`。

### SSE

REST 首次同步 + SSE 增量；事件：CONNECTED/MESSAGE_CREATED/MESSAGE_WITHDRAWN/UNREAD_COUNT_CHANGED/HEARTBEAT。连接按
userId+Context+tenantId 隔离；多节点通过 Redisson Topic 广播到本机连接。

---

## BE-09 FastExcel

用户管理实现：

```http
GET  /api/system/users/import-template
POST /api/system/users/import
GET  /api/system/users/export
```

模板字段：账号、姓名、性别、手机、邮箱、DepartmentCode、RoleCodes、初始密码。导入限制最大行数、逐行校验并返回成功/失败/错误明细；导出按当前查询和
DataScope，禁止绕过权限导出全 Tenant 数据。

---

## BE-10 Security / Release

### 测试

- JUnit5/Mockito：核心 Service、树、权限、消息、文件、日志脱敏。
- Testcontainers MySQL：`schema.sql`/`data.sql`、Mapper、Tenant Fence、关系更新。
- Redis/SSE/Storage 做可重复的集成验证。

### 安全回归

SQL 注入、XSS/富文本 XSS、越权、跨 Tenant、密码/Token 日志泄漏、路径穿越、MIME 欺骗、超大文件、强制下线、SSE 跨 Tenant、批量接口越权。

### 发布门槛

```bash
cd backend
mvn clean compile
mvn test
mvn package
mvn dependency:tree
```

随后空库初始化、Boot 启动、OpenAPI、登录/Context/RBAC/字典/日志/缓存/Profile/Dashboard/File/Message/SSE/Excel Smoke
Test。任何失败都必须修复并重新验证。

---

## 4. 本纯净源码包当前实现映射

本包已按以上 Epic 建立对应源代码、SQL 与 API 契约。后续 Codex 开发必须以当前源码为基线增量修改，不得再次生成另一套平行架构。每次提交都应同步
`docs/backend-progress.md`，区分“源码已实现”和“环境已验证”。
