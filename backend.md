# Nova 企业级统一后台管理系统 Backend 开发规范

## 1. 文档定位

本文件是 Nova 企业级统一后台管理系统后端开发的核心规范。

适用于：

- Codex / AI Agent 后端开发
- Java 后端开发
- 数据库设计
- 多租户设计
- 权限设计
- 缓存设计
- 文件与 Excel 能力
- 日志与审计
- 测试
- Maven 构建
- 后端启动验证
- Docker 部署

本文件必须结合项目根目录：

```text
AGENTS.md
```

共同执行。

规则冲突时遵循：

```text
用户当前最新明确要求
>
当前真实源码、依赖和脚本
>
AGENTS.md
>
backend.md
>
历史文档
>
通用经验
```

当前阶段如果 backend 目录为空，应从 0 创建完整后端。

禁止只创建工程骨架、空方法、TODO、伪代码或不可运行实现。

---

# 2. 项目基本信息

项目名称：

```text
Nova 企业级统一后台管理系统
```

统一基础信息：

```text
根包名：org.dromara.nova
Maven GroupId：org.dromara.nova
Maven 制品前缀：nova-
统一启动类：NovaApplication
默认配置文件：application.yml
生产配置文件：application-prod.yml
默认编码：UTF-8
默认时区：Asia/Shanghai
项目作者：kamden
```

除：

```text
NovaApplication
```

外，项目内部 Java 类型不得无意义增加 `Nova` 前缀。

例如禁止：

```text
NovaTenantContext
NovaCacheConfiguration
NovaStorageService
NovaSecurityProperties
```

应使用：

```text
TenantContext
CacheConfiguration
StorageService
SecurityProperties
```

---

# 3. 系统定位

项目采用：

```text
前后端分离
模块化单体
Maven 多模块
统一 Spring Boot 启动入口
统一后端部署 Jar
MySQL + Redis + MinIO
Docker Compose
```

后端需要完整支持：

```text
统一登录认证
平台管理
多租户
Context 切换
Department
用户
角色
菜单
按钮权限
数据权限
字典
参数
登录日志
操作日志
在线用户
缓存管理
消息中心
通知公告
文件附件
Local 存储
MinIO 存储
Excel 导入导出
工作台
个人中心
OpenAPI
安全控制
测试
部署
```

当前明确不实现：

```text
Organization
Post
Liquibase
Mapper XML
```

---

# 4. 后端技术栈

统一使用：

```text
Java 21 LTS
Spring Boot 3.5.x
Maven 3.9.16
MyBatis-Flex
MapStructPlus
Sa-Token
Spring Validation
Spring Cache
Redis
Redisson
Caffeine
Springdoc OpenAPI
Lombok
Jackson
HikariCP
MySQL 8.4
Logback
FastExcel
MinIO Java SDK
JUnit 5
Mockito
Testcontainers
Docker Compose
```

所有依赖版本必须由父 POM 统一管理。

子模块不得随意覆盖版本。

必须确保依赖与：

```text
Java 21
Spring Boot 3.5.x
MySQL 8.4
```

兼容。

---

# 5. Java 21 开发规范

项目以 Java 21 为基线。

在可读性和稳定性不受影响的前提下，优先使用 Java 21 已稳定的新语言特性和现代标准库能力。

推荐：

```text
record
switch expression
pattern matching for instanceof
pattern matching for switch
sealed class
sealed interface
var
Text Blocks
Stream.toList()
List.of()
Set.of()
Map.of()
java.time
Optional
try-with-resources
NIO
```

## 5.1 record

优先用于：

```text
不可变 Command
Query
Event
Result
配置快照
简单值对象
内部不可变上下文
```

不强制用于：

```text
Entity
需要 ORM 可变字段的模型
复杂继承结构
```

## 5.2 时间 API

统一：

```text
LocalDate
LocalTime
LocalDateTime
Instant
Duration
ZoneId
```

禁止新增：

```text
Date
Calendar
SimpleDateFormat
```

除非兼容第三方旧 API。

## 5.3 Optional

适合：

```text
Service 内部明确表达“可能不存在”
查询结果组合
```

不建议用于：

```text
Entity 字段
ReqDto 字段
RespDto 字段
数据库字段映射
```

## 5.4 Preview Feature

默认禁止使用 Java Preview Feature。

不得为了使用新特性增加构建参数或破坏稳定性。

---

# 6. Virtual Threads Ready

项目架构必须保持：

```text
Virtual Threads Ready
```

允许后续通过 Spring Boot 配置启用虚拟线程，例如：

```yaml
spring:
  threads:
    virtual:
      enabled: ${VIRTUAL_THREADS_ENABLED:false}
  main:
    keep-alive: true
```

第一阶段默认可以关闭。

启用前必须验证：

```text
Spring MVC
Servlet
Sa-Token
TenantContext
登录上下文
TraceId
RequestId
JDBC
HikariCP
Redis
Redisson
HTTP Client
MinIO
文件 IO
异步任务
```

不得在业务代码中随意：

```java
new Thread(...)
```

也不得到处自行创建：

```text
Executors.newFixedThreadPool(...)
Executors.newCachedThreadPool(...)
```

线程执行能力统一管理。

虚拟线程增加并发能力不代表：

```text
数据库连接
Redis 连接
MinIO 连接
第三方 HTTP 服务
```

可以无限并发。

资源池仍必须合理限流和配置。

---

# 7. Maven 工程结构

正式后端目录：

```text
backend/
├── pom.xml
├── nova-common/
│   ├── pom.xml
│   ├── nova-common-core/
│   ├── nova-common-web/
│   ├── nova-common-security/
│   ├── nova-common-tenant/
│   ├── nova-common-mybatis/
│   ├── nova-common-cache/
│   ├── nova-common-log/
│   ├── nova-common-excel/
│   └── nova-common-storage/
├── nova-modules/
│   ├── pom.xml
│   ├── nova-system/
│   ├── nova-message/
│   ├── nova-file/
│   └── 实际业务模块/
└── nova-boot/
    ├── pom.xml
    └── src/
```

最终运行包：

```text
backend/nova-boot/target/nova-boot.jar
```

依赖方向：

```text
nova-boot
    ↓
nova-modules
    ↓
nova-common
```

必须遵守：

1. `nova-boot` 只负责装配和启动。
2. common 不依赖业务模块。
3. modules 不依赖 boot。
4. system 不依赖具体业务模块实现。
5. 模块之间禁止直接调用对方 Mapper。
6. 模块之间禁止调用对方 Controller。
7. 禁止模块循环依赖。
8. 模块间业务调用优先通过 Service / Facade / Spring Event 等明确边界完成。
9. common 不承载具体业务查询。
10. 不因为未来可能扩展而创建大量空模块。

---

# 8. 业务模块目录

每个业务 Maven 模块内部只保留一套统一技术分层。

例如：

```text
nova-system/
└── src/main/java/org/dromara/nova/system/
    ├── controller/
    ├── service/
    │   └── impl/
    ├── mapper/
    ├── entity/
    ├── dto/
    │   ├── request/
    │   └── response/
    ├── bo/
    ├── facade/
    ├── enums/
    ├── constant/
    ├── event/
    ├── listener/
    ├── support/
    └── table/
```

`facade/event/listener/support` 等目录只有实际有类时才创建。

禁止为了目录“好看”创建空目录或空类。

---

# 9. 系统业务角色定义

系统角色必须区分：

```text
平台级身份
租户级角色
```

不能混为同一个概念。

## 9.1 平台管理员

层级：

```text
PLATFORM
```

定义：

平台管理员是整个平台的最高管理身份。

职责：

```text
登录后默认进入 PLATFORM
管理租户
创建租户
修改租户
启停租户
管理平台级配置
查看平台级统计
查看平台级必要日志
切换到全部允许管理的有效租户
在租户 Context 中进行平台管理操作
```

平台管理员：

```text
不要求通过 sys_user_tenant 关联全部租户
不要求通过普通租户角色获得平台管理能力
```

平台管理员身份属于平台级安全身份。

不得使用：

```text
tenant_id = 0
tenant_id = -1
特殊 sys_tenant
```

表示平台管理员。

## 9.2 租户管理员

层级：

```text
TENANT
```

职责：

```text
管理当前租户 Department
管理当前租户用户
管理当前租户角色
管理当前租户菜单授权
管理当前租户字典
管理当前租户参数
查看当前租户日志
管理当前租户文件
管理当前租户消息
维护当前租户基础配置
```

默认数据范围：

```text
当前租户全部数据
```

## 9.3 部门管理员

层级：

```text
TENANT
```

职责：

```text
管理授权 Department 范围内的数据
根据权限管理 Department 成员
执行部门级业务管理
查看部门级统计
```

默认推荐数据范围：

```text
当前 Department + 下级 Department
```

实际数据范围仍由 DataScope 配置决定。

## 9.4 业务管理员

层级：

```text
TENANT
```

职责：

```text
管理指定业务模块
使用业务级新增、修改、删除、导入、导出等权限
```

不天然拥有：

```text
用户管理
角色管理
系统参数管理
全部租户数据
```

## 9.5 普通用户

层级：

```text
TENANT
```

职责：

```text
使用被授权菜单
使用被授权按钮
访问被授权数据
维护个人信息
接收消息
使用业务功能
```

## 9.6 审计人员

层级：

```text
TENANT
```

主要职责：

```text
登录日志查询
操作日志查询
审计数据查询
业务统计查看
异常操作检查
```

通常以只读权限为主。

---

# 10. Role 与 DataScope 必须解耦

Role 表示：

```text
用户可以访问哪些菜单
用户可以执行哪些按钮
用户具备哪些功能权限
```

DataScope 表示：

```text
用户可以访问哪些数据
```

不得把：

```text
tenant_admin
department_admin
business_admin
```

直接写死为唯一的数据范围逻辑。

角色可以配置不同 DataScope。

推荐数据范围类型至少考虑：

```text
ALL
TENANT
DEPARTMENT
DEPARTMENT_AND_CHILDREN
SELF
CUSTOM
```

具体实现以 backend 实际数据权限设计为准。

---

# 11. PLATFORM / TENANT Context

系统存在两种业务上下文：

```text
PLATFORM
TENANT
```

## 11.1 PLATFORM

```text
contextType = PLATFORM
currentTenantId = null
```

用于：

```text
平台管理
租户管理
平台统计
平台级配置
```

## 11.2 TENANT

```text
contextType = TENANT
currentTenantId = 实际租户 ID
```

用于：

```text
Department
User
Role
Menu permission
Dictionary
Parameter
File
Message
Tenant business data
```

## 11.3 禁止伪平台租户

禁止创建：

```text
sys_tenant:
tenant_id = 0
tenant_name = 平台管理
```

平台管理是 Context，不是 Tenant。

---

# 12. 登录与 Context 默认规则

## 12.1 平台管理员

登录成功：

```text
默认进入 PLATFORM
```

可切换：

```text
PLATFORM
+
全部允许管理的有效租户
```

平台管理员不需要在：

```text
sys_user_tenant
```

中增加全部租户记录。

## 12.2 普通用户

可访问租户来源：

```text
sys_user_tenant
```

如果只有一个有效租户：

```text
自动进入该 TENANT
```

如果有多个：

```text
按产品交互选择或进入默认租户
```

普通用户不得进入 PLATFORM。

---

# 13. Context 切换接口

建议提供：

```http
GET /api/context/current
GET /api/context/options
PUT /api/context/platform
PUT /api/context/tenant/{tenantId}
```

平台管理员：

```text
GET /api/context/options
```

可返回：

```json
{
  "platform": true,
  "tenants": []
}
```

普通用户：

```json
{
  "platform": false,
  "tenants": []
}
```

租户很多时，不要求一次返回几千条。

允许提供：

```http
GET /api/platform/tenants/options?keyword=
```

用于远程搜索。

---

# 14. Context 切换安全规则

切换 TENANT 前必须验证：

```text
租户存在
租户启用
租户未过期
当前用户有访问权限
平台管理员有平台授权
```

后端不得信任前端提交的 tenantId 作为最终数据隔离依据。

切换后应重新计算或刷新：

```text
当前 Context
角色
菜单
按钮权限
数据权限
字典
参数
登录用户上下文
必要缓存
```

前端应重新加载：

```text
动态菜单
动态路由
权限
标签页
页面缓存
业务 Store
```

后端必须防止：

```text
Context 串租户
缓存串租户
权限串租户
数据串租户
```

---

# 15. 创建租户

只有 PLATFORM 上下文允许执行平台租户创建。

平台管理员创建租户后：

```text
仍保持 PLATFORM
```

不得自动切换到新租户。

创建完成后需要：

```text
刷新平台租户列表
刷新 ContextSwitcher 数据
刷新租户统计
```

新租户应立即可以被平台管理员搜索和切换。

创建租户时建议完成：

```text
创建 sys_tenant
初始化租户默认角色
初始化默认菜单授权
初始化必要参数
创建或绑定租户管理员
创建用户租户关系
创建用户角色关系
```

创建租户不自动创建：

```text
虚假 Department
普通用户
演示数据
其他租户数据副本
```

---

# 16. Tenant 即业务单位

当前 Nova 设计中：

```text
Tenant
```

本身就是业务单位。

例如：

```text
医院
公司
学校
客户机构
单位
```

因此不增加：

```text
Organization
```

当前组织关系：

```text
Tenant
└── Department
    └── Child Department
```

明确禁止：

```text
sys_organization
OrganizationEntity
OrganizationController
OrganizationService
OrganizationMapper
organization_id
```

未来如果出现真实集团型组织需求，再重新评估。

---

# 17. Department 命名规范

部门领域正式统一使用：

```text
Department
department
```

禁止正式使用：

```text
Dept
dept
```

包括：

```text
Java 类
变量
DTO
BO
Entity
Mapper
Service
Controller
数据库表
数据库字段
API
权限编码
前端领域名称
```

例如：

```text
DepartmentController
DepartmentService
DepartmentMapper
DepartmentEntity
DepartmentCreateReqDto
DepartmentUpdateReqDto
DepartmentRespDto

sys_department
department_id
department_code
department_name

/api/system/departments

system:department:list
system:department:add
system:department:update
system:department:delete
```

---

# 18. Department 数据模型

建议：

```text
sys_department
```

至少包含：

```text
id
tenant_id
parent_id
department_code
department_name
leader_user_id
phone
email
sort
status
create_by
create_time
update_by
update_time
deleted
version
```

Department 支持树形结构。

业务查询必须确保：

```text
tenant_id = 当前 TENANT
```

前端新增 Department 时不要求提交可信 tenantId。

后端从 Context 获取。

---

# 19. 不实现岗位 Post

当前 Nova 基础后台不设计岗位。

禁止新增：

```text
sys_post
sys_user_post
PostEntity
PostMapper
PostService
PostController
岗位管理菜单
岗位分配
岗位权限
```

用户组织归属由：

```text
Tenant + Department
```

表达。

权限由：

```text
Role
```

表达。

只有未来出现明确：

```text
人事管理
编制
岗位级别
岗位审批
一人多岗
岗位统计
```

需求时再设计 Post。

---

# 20. User 核心模型

`sys_user` 表示平台级用户身份。

不得将用户永久绑定到单个租户。

例如同一用户可以：

```text
租户 A → Department A → Role A
租户 B → Department B → Role B
```

因此：

```text
sys_user
```

不直接承载当前租户业务归属。

---

# 21. sys_user_tenant

用户与租户成员关系使用：

```text
sys_user_tenant
```

建议字段：

```text
id
user_id
tenant_id
department_id
status
join_time
create_by
create_time
update_by
update_time
```

职责：

```text
这个用户属于哪个 Tenant
这个用户在该 Tenant 属于哪个 Department
这个用户在该 Tenant 的成员状态
```

如果未来出现一人在一个 Tenant 多 Department，再新增专门关系表。

第一版不提前复杂化。

---

# 22. sys_user_role

用户与角色：

```text
sys_user_role
```

建议：

```text
id
tenant_id
user_id
role_id
create_by
create_time
```

职责：

```text
这个用户在这个 Tenant 能做什么
```

与：

```text
sys_user_tenant
```

分开。

核心原则：

```text
sys_user_tenant
=
这个人在哪里

sys_user_role
=
这个人能做什么
```

---

# 23. User 创建规则

在某个 TENANT Context 下新增用户，本质是：

```text
新增租户成员
```

逻辑：

```text
根据唯一账号/手机号/邮箱等规则查找全局 sys_user
↓
不存在：创建 sys_user
存在：复用 sys_user
↓
创建 sys_user_tenant
↓
按需要创建 sys_user_role
```

禁止同一个真实账号因为加入不同租户而重复创建多个全局用户身份。

用户搜索和成员添加必须注意隐私与越权，不允许普通租户管理员枚举平台全部用户敏感信息。

---

# 24. Role

角色属于租户权限体系。

建议：

```text
sys_role
```

至少：

```text
id
tenant_id
role_code
role_name
data_scope
sort
status
remark
create_by
create_time
update_by
update_time
deleted
version
```

租户默认角色可以初始化：

```text
tenant_admin
department_admin
business_admin
user
auditor
```

平台管理员不是普通租户角色。

---

# 25. Menu

菜单定义用于：

```text
目录
菜单
按钮
动态路由
权限标识
```

建议：

```text
sys_menu
```

菜单可以由平台维护统一定义。

角色菜单关系：

```text
sys_role_menu
```

负责当前租户角色菜单授权。

菜单类型建议支持：

```text
DIRECTORY
MENU
BUTTON
```

前端国际化优先使用：

```text
i18nKey
```

而不是把 UI 中文写死为唯一标识。

---

# 26. 用户分配角色

用户角色分配不得使用简单多选下拉作为唯一复杂管理方式。

后端建议提供：

```http
GET /api/system/users/{userId}/roles
PUT /api/system/users/{userId}/roles
```

写接口采用全量替换语义：

```json
{
  "roleIds": [
    1,
    2,
    3
  ]
}
```

更新时必须：

```text
验证用户属于当前 Tenant
验证 Role 属于当前 Tenant
禁止跨租户 Role
事务更新
清除用户权限缓存
```

---

# 27. Role 分配 Menu

建议：

```http
GET /api/system/roles/{roleId}/menus
PUT /api/system/roles/{roleId}/menus
```

请求：

```json
{
  "menuIds": [
    1,
    2,
    3
  ]
}
```

必须：

```text
验证 Role 属于当前 Tenant
验证 Menu 合法
事务更新
刷新角色权限缓存
刷新受影响用户权限
```

---

# 28. 多租户 API 边界

建议平台接口：

```text
/api/platform/**
```

只允许 PLATFORM Context。

例如：

```http
GET    /api/platform/tenants
POST   /api/platform/tenants
GET    /api/platform/tenants/{id}
PUT    /api/platform/tenants/{id}
PUT    /api/platform/tenants/{id}/status
DELETE /api/platform/tenants/{id}
```

租户业务接口：

```text
/api/system/**
/api/business/**
```

原则上需要 TENANT Context。

例如：

```http
/api/system/departments
/api/system/users
/api/system/roles
/api/system/menus
```

跨租户平台统计必须设计专门：

```text
/api/platform/reports/**
```

禁止简单关闭租户拦截后直接复用 Tenant API 做全租户查询。

---

# 29. MyBatis-Flex

数据库访问统一使用：

```text
MyBatis-Flex
```

优先用于：

```text
按主键查询
普通查询
分页
新增
更新
删除
状态修改
统计
存在性判断
JOIN
聚合
子查询
```

普通单表 CRUD 禁止原生 SQL。

多表查询先使用：

```text
QueryWrapper
TableDef
join
groupBy
having
exists
```

实现。

---

# 30. Mapper SQL

禁止：

```text
Mapper XML
```

只有 MyBatis-Flex 确实无法清晰、安全、高效完成时，允许：

```java
@Select
@Update
@Insert
@Delete
```

适用：

```text
FOR UPDATE
递归 CTE
特殊数据库函数
复杂原子更新
特殊高性能批量 SQL
特殊统计
```

所有动态参数必须使用安全参数绑定。

禁止不受控：

```text
${parameter}
```

特殊 SQL 必须通过 JavaDoc 说明保留原因。

---

# 31. TableDef

TableDef 必须由 MyBatis-Flex APT 自动生成。

父 POM 统一配置必要 Annotation Processor。

至少考虑：

```text
Lombok Processor
MyBatis-Flex Processor
MapStructPlus Processor
Lombok MapStruct Binding
```

生成目录：

```text
target/generated-sources/annotations
```

禁止：

```text
手工维护 TableDef
提交 target/generated-sources
依赖历史生成文件
```

必须真实执行：

```bash
mvn clean compile
```

验证。

---

# 32. MapStructPlus

Entity、ReqDto、RespDto、BO、VO 普通对象转换优先：

```text
MapStructPlus
```

禁止：

```text
BeanUtils.copyProperties
BeanCopier
反射复制
JSON 序列化再反序列化作为对象转换
大量普通字段手工复制
```

只有动态结构等不适合编译期映射的场景例外。

不要为普通转换再创建大量：

```text
XxxConvert
XxxConverter
```

与 MapStructPlus 重复。

---

# 33. DTO 规范

请求：

```text
XxxCreateReqDto
XxxUpdateReqDto
XxxPageReqDto
XxxQueryReqDto
```

响应：

```text
XxxRespDto
XxxDetailRespDto
XxxPageRespDto
```

字段必须具有清晰注释。

Controller 不返回 Entity。

前端 TypeScript 类型应与后端 DTO / OpenAPI 对齐。

---

# 34. 统一响应

Controller 统一使用：

```text
R<T>
```

例如：

```java
R<Void>
R<Long>
R<Boolean>
R<UserRespDto>
R<Page<UserRespDto>>
```

不得不同 Controller 自行定义不同响应结构。

---

# 35. 结果码与异常

必须建立统一：

```text
CommonResultCode
```

以及模块业务结果码。

错误码必须：

```text
稳定
唯一
可被前端判断
```

不得依赖 message 文案作为业务判断。

异常至少区分：

```text
参数异常
认证异常
权限异常
业务异常
资源不存在
状态冲突
重复数据
Tenant 异常
文件异常
第三方异常
系统异常
```

禁止：

```text
所有异常统一 ServiceException
catch Exception 后全部重新包装
异常返回 null
吞异常
```

---

# 36. 参数校验

Controller 类：

```java
@Validated
```

Request DTO 使用：

```text
@NotNull
@NotBlank
@Size
@Length
@Min
@Max
@Pattern
@Email
@Valid
```

新增、修改规则不同可以使用：

```text
CreateGroup
UpdateGroup
QueryGroup
```

Service 必须继续进行：

```text
数据库状态校验
唯一性校验
权限校验
Tenant 校验
业务规则校验
```

不得只依赖前端和 Bean Validation。

---

# 37. Controller

Controller 只负责：

```text
请求接收
基础校验
权限注解
调用 Service
返回 R<T>
```

禁止：

```text
Controller 直接调用 Mapper
Controller 大量业务逻辑
Controller 直接返回 Entity
Controller 自己做复杂事务
```

Controller 应补充必要 OpenAPI 说明。

---

# 38. Service

Service 默认使用：

```java
@Service
@RequiredArgsConstructor
@Slf4j
```

接口实现必须：

```java
@Override
```

Service 负责：

```text
业务校验
唯一性校验
租户隔离
数据权限
状态流转
事务
并发
缓存
业务日志
事件
文件补偿
异常转换
幂等控制
```

多个数据库写操作需要强一致性时：

```java
@Transactional(rollbackFor = Exception.class)
```

事务方法禁止吞异常。

不得将所有复杂业务塞到一个巨大 `ServiceImpl`。

同样不得为了“架构漂亮”过度拆分。

---

# 39. 工具类原则

工具类必须：

```text
少而精
真实使用
无状态
职责单一
跨模块复用
不承载具体业务规则
```

只有同时满足以下条件才允许定义通用 Utils：

1. 无状态。
2. 多处实际复用。
3. 不属于具体业务。
4. JDK / Spring / 当前框架没有直接等价能力。
5. 职责明确。
6. 有实际调用场景。
7. 可以编写单元测试。

---

# 40. 第一阶段允许的常用工具类

原则上只保留真正常用的：

```text
nova-common-core
├── StringUtils
├── DateTimeUtils
├── JsonUtils
├── TreeUtils
├── EnumUtils
├── DigestUtils
└── RandomUtils

nova-common-web
├── ServletUtils
└── IpUtils

nova-common-security
└── LoginUserUtils

nova-common-cache
└── RedisUtils
```

多租户使用：

```text
TenantContext
```

但 `TenantContext` 是基础设施 Context，不是普通 Utils。

如果某个工具类没有实际调用，不要为了规范机械创建。

---

# 41. 禁止无意义 Utils

没有真实需求时禁止创建：

```text
CommonUtils
BusinessUtils
SecurityUtils
PermissionUtils
TenantUtils
ContextUtils
PageUtils
QueryUtils
DataScopeUtils
LogUtils
AuditUtils
MaskUtils
ExcelUtils
ExcelTemplateUtils
FileNameUtils
MimeTypeUtils
StoragePathUtils
FileSignatureUtils
RedissonUtils
LockUtils
```

应该优先使用：

```text
JDK
Spring
MyBatis-Flex
Sa-Token
Spring Cache
Redisson
FastExcel
Service
Component
Handler
Interceptor
Context
```

已有框架能力不得简单再包一层 Utils。

---

# 42. LoginUserUtils

`LoginUserUtils` 可以统一封装业务层常用登录用户信息获取。

例如：

```text
getUserId()
getUsername()
getLoginUser()
isPlatformAdmin()
```

业务代码尽量不要散落大量 Sa-Token 登录模型解析细节。

但权限校验仍优先使用 Sa-Token 标准能力和 PermissionService。

---

# 43. TenantContext

`TenantContext` 是多租户基础设施核心。

建议提供明确能力：

```text
getContextType()
getTenantId()
isPlatform()
isTenant()
```

PLATFORM 时：

```text
tenantId = null
```

TENANT 时：

```text
tenantId = 当前租户 ID
```

复杂 Context 切换使用 Service 完成。

禁止：

```text
ContextUtils
TenantUtils
```

重复承担 Context 职责。

---

# 44. Redis 与缓存

职责划分：

```text
Spring Cache
→ 字典、参数、菜单、权限、系统配置等业务缓存

Redis
→ 分布式共享状态、验证码、Session 辅助、限流等

Redisson
→ 分布式锁、分布式同步
```

业务模块不要到处直接操作：

```text
StringRedisTemplate
RedisTemplate
RedissonClient
```

如果已有明确公共封装，应复用。

但 `RedisUtils` 不得变成万能业务缓存入口。

---

# 45. Caffeine

Caffeine 用于：

```text
本地热点缓存
二级缓存中的本地层
短生命周期本地数据
```

业务代码不要直接到处操作原生 Caffeine。

应通过：

```text
Spring Cache
或明确 Cache Component
```

统一管理。

---

# 46. DataScope

数据权限属于安全和 MyBatis 基础设施。

不使用：

```text
DataScopeUtils
```

到处手工拼接条件。

建议通过：

```text
DataScopeContext
DataScopeProvider
DataScopeHandler
MyBatis Interceptor
```

等方式实现。

最终查询必须同时满足：

```text
Tenant 隔离
+
DataScope
```

两层约束。

---

# 47. Sa-Token

统一承担：

```text
登录
Token
会话
角色
权限
接口权限
在线用户基础能力
```

Controller 权限优先使用标准注解，例如：

```text
@SaCheckPermission
@SaCheckRole
```

平台权限和租户权限必须明确区分。

不得只靠前端隐藏按钮实现权限。

---

# 48. 操作日志

普通运行日志直接：

```java
@Slf4j
```

禁止创建：

```text
LogUtils
```

核心操作必须有业务日志或操作日志，例如：

```text
登录
退出
Context 切换
租户新增
租户修改
租户状态变化
Department 新增/修改/删除
用户新增/修改/状态变化
角色授权
菜单授权
文件上传/删除
导入
导出
```

日志禁止输出：

```text
密码
Token
SecretKey
完整敏感凭证
```

---

# 49. 登录日志

至少记录：

```text
user_id
username
login_type
login_status
ip
user_agent
login_time
failure_reason
request_id
```

平台 / Tenant Context 情况按实际记录。

---

# 50. 文件存储

支持：

```text
Local
MinIO
```

统一抽象存储服务。

业务模块不得直接依赖具体 MinIO SDK。

必须支持：

```text
上传
下载
删除
预览
文件元数据
访问控制
业务关联
```

必须防止：

```text
路径穿越
伪造文件扩展名
MIME 欺骗
危险文件
超大文件
越权下载
越权删除
```

不要求创建一堆 `FileXxxUtils`。

复杂能力使用明确职责 Component。

---

# 51. Excel

统一：

```text
FastExcel
```

用于：

```text
导入
导出
模板下载
批量数据处理
```

不创建：

```text
ExcelUtils
ExcelTemplateUtils
```

仅仅再次包装 FastExcel。

如果存在公共导入导出流程，使用明确职责的：

```text
Importer
Exporter
Reader
Writer
Service
Component
```

---

# 52. i18n

系统 UI 国际化主要由 frontend 实现。

后端必须保证：

```text
业务错误码稳定
```

后端可以预留：

```text
MessageSource
LocaleContext
Accept-Language
```

但第一阶段不强制所有后端业务 message 全量国际化。

禁止根据中文 message 判断业务逻辑。

菜单可以提供：

```text
i18n_key
```

供前端翻译。

---

# 53. 字典与参数

字典和参数属于 Tenant 业务配置时必须包含：

```text
tenant_id
```

如果存在平台级公共配置，应设计明确平台配置能力。

不得用：

```text
tenant_id = 0
```

模拟平台数据。

公共固定枚举优先代码枚举，不要所有东西都塞数据库字典。

---

# 54. 在线用户

在线用户至少支持：

```text
当前登录用户列表
账号
IP
登录时间
Context
Tenant
强制下线
```

必须遵循权限范围。

Tenant 管理员不得看到无关 Tenant 登录用户。

平台管理员平台页面可根据平台权限查看相应范围。

---

# 55. 工作台

工作台数据必须来自真实接口。

TENANT 工作台至少可根据实际功能提供：

```text
当前租户
当前 Department
用户统计
角色统计
消息统计
文件统计
最近操作
```

PLATFORM 工作台：

```text
租户总数
启用租户
禁用租户
必要平台统计
```

禁止使用静态演示数字冒充业务数据。

---

# 56. 个人中心

至少：

```text
个人信息
修改头像
修改密码
查看当前 Context
查看当前 Tenant
查看当前 Department
查看当前角色
```

普通用户不得通过个人中心越权修改：

```text
Tenant
Department
Role
平台身份
```

---

# 57. SQL 目录

数据库 SQL 统一维护在项目：

```text
sql/
```

至少建议：

```text
sql/
├── init.sql
├── schema.sql
├── data.sql
└── increment/
```

根据项目实际组织。

当前明确：

```text
不使用 Liquibase
```

SQL 必须可以从空 MySQL 8.4 数据库初始化。

---

# 58. 核心表建议

至少包含：

```text
sys_user
sys_tenant
sys_user_tenant
sys_department

sys_role
sys_user_role

sys_menu
sys_role_menu

sys_dict_type
sys_dict_data

sys_config

sys_login_log
sys_operation_log

sys_message
sys_message_user

sys_file
```

根据实际功能继续完善。

明确禁止：

```text
sys_organization
sys_post
sys_user_post
```

---

# 59. 数据库基础字段

业务表根据需要统一：

```text
id
tenant_id
status
sort
remark
create_by
create_time
update_by
update_time
deleted
version
```

不是每张表机械全部加入。

平台级表不应为了统一强行增加 tenant_id。

所有字段：

```text
命名清晰
类型合理
长度合理
默认值合理
有字段 COMMENT
```

---

# 60. 主键

主键策略统一，不允许各表随意不同。

如果使用 Long ID：

```text
统一雪花 ID 或当前框架确定的分布式 ID
```

不得同时混用大量：

```text
自增 ID
UUID String
雪花 Long
```

除非有明确业务理由。

---

# 61. 逻辑删除

使用逻辑删除的表必须：

```text
查询默认排除 deleted
唯一约束设计考虑逻辑删除
恢复数据时考虑唯一性
```

日志等只追加型表可以根据业务决定是否使用逻辑删除。

---

# 62. 乐观锁

存在明显并发更新风险的核心业务表使用：

```text
version
```

结合 MyBatis-Flex 乐观锁能力。

不要所有表机械加乐观锁。

---

# 63. 索引

必须为：

```text
唯一账号
tenant_id
department parent
角色关系
用户租户关系
用户角色关系
角色菜单关系
查询条件
排序条件
日志时间
```

等高频查询设计合理索引。

禁止无意义给所有字段创建索引。

---

# 64. 多租户唯一约束

租户级唯一字段必须考虑 tenant_id。

例如 Department：

```text
tenant_id + department_code
```

Role：

```text
tenant_id + role_code
```

不得错误地做平台全局唯一，除非业务明确要求。

---

# 65. API 规范

统一：

```text
REST 风格
/api
```

资源优先使用复数：

```text
/api/system/users
/api/system/roles
/api/system/menus
/api/system/departments
```

平台：

```text
/api/platform/tenants
```

Context：

```text
/api/context
```

---

# 66. 分页

统一分页请求和响应。

分页使用 MyBatis-Flex 原生能力。

不创建无意义：

```text
PageUtils
```

必须限制：

```text
最大 pageSize
```

避免一次请求无限数据。

---

# 67. OpenAPI

Springdoc OpenAPI 必须真实可访问。

Controller、DTO、重要字段应提供清晰说明。

OpenAPI 是前后端契约之一。

必须确保：

```text
请求字段
响应字段
枚举
日期格式
错误码
```

清晰。

---

# 68. 日期时间格式

API 统一：

```text
yyyy-MM-dd HH:mm:ss
yyyy-MM-dd
yyyy-MM
HH:mm:ss
```

前端显示对应：

```text
YYYY-MM-DD HH:mm:ss
YYYY-MM-DD
YYYY-MM
HH:mm:ss
```

不得不同模块自行定义不同时间格式。

---

# 69. 安全规范

必须重点防范：

```text
SQL 注入
XSS
CSRF
水平越权
垂直越权
多租户串扰
文件上传攻击
文件下载越权
路径穿越
敏感信息泄露
重复提交
接口限流
登录暴力破解
Token 泄漏
导出越权
弱密钥
日志注入
SSRF
开放重定向
反序列化风险
```

生产环境：

```text
CORS 不允许宽泛 *
默认密钥不得可工作
敏感响应可设置 no-store
```

---

# 70. 幂等与重复提交

重要写接口根据业务设计：

```text
RequestId
业务唯一键
载荷摘要
Redis 幂等
数据库唯一约束
```

不要所有接口统一使用一个粗暴锁。

---

# 71. 限流

至少考虑：

```text
登录
验证码
文件上传
文件下载
Excel 导入
Excel 导出
消息发送
高成本查询
```

限流策略按接口实际成本配置。

---

# 72. 密码与凭证

密码必须使用安全 PasswordEncoder。

禁止：

```text
MD5 密码
SHA-256 直接存密码
明文密码
日志打印密码
```

SecretKey、Token 等禁止明文输出日志。

---

# 73. 测试体系

必须使用：

```text
JUnit 5
Mockito
Testcontainers
```

测试至少覆盖：

```text
工具类
Service
权限核心逻辑
Context 切换
Tenant 创建
Department 树
用户租户关系
角色授权
菜单授权
数据权限
异常场景
```

重要数据库功能使用集成测试。

---

# 74. 工具类测试

实际创建的公共 Utils 必须有测试。

例如：

```text
DateTimeUtilsTest
JsonUtilsTest
TreeUtilsTest
EnumUtilsTest
DigestUtilsTest
```

没有实际能力的 Utils 不允许为了满足测试数量创建。

---

# 75. 后端真实构建

每个主要阶段必须执行：

```bash
cd backend
mvn clean compile
mvn test
mvn package
mvn dependency:tree
```

必须验证：

```text
Lombok Processor
MyBatis-Flex Processor
MapStructPlus Processor
TableDef
generated-sources
所有模块
测试
Jar
依赖冲突
```

禁止：

```text
静态扫描通过
=
Maven 构建通过
```

---

# 76. 启动验证

环境允许时必须真实启动：

```text
nova-boot
```

验证：

```text
MySQL
Redis
Redisson
Spring Cache
Caffeine
Sa-Token
MyBatis-Flex
MapStructPlus
Tenant Context
Springdoc
Local Storage
MinIO
全局异常
```

启动失败必须：

```text
看日志
定位
修复
重新启动
```

---

# 77. 业务验证

至少验证：

```text
登录
退出
PLATFORM 默认 Context
平台管理员租户列表
租户创建
租户修改
Tenant Context 切换
切回 PLATFORM
普通用户租户范围

Department CRUD
Department Tree

User CRUD
UserTenant
UserRole

Role CRUD
RoleMenu

Menu CRUD
按钮权限
数据权限

字典
参数

登录日志
操作日志
在线用户

消息

文件上传
文件下载
文件删除
Local
MinIO

Excel 导入
Excel 导出

个人中心
工作台
```

---

# 78. 多租户重点测试

必须至少测试：

```text
Tenant A 用户不能读取 Tenant B 数据
Tenant A 用户不能修改 Tenant B 数据
Tenant A Role 不能授权 Tenant B 用户
Tenant A Department 不能挂到 Tenant B
普通用户不能进入 PLATFORM
平台管理员进入 Tenant 后仍记录真实平台管理员身份
切换 Tenant 后旧权限失效
切换 Tenant 后旧缓存不能继续命中
禁用 Tenant 无法继续正常访问
```

---

# 79. 角色权限测试

必须覆盖：

```text
平台管理员
租户管理员
部门管理员
业务管理员
普通用户
审计人员
```

验证：

```text
菜单
按钮
API
DataScope
日志范围
文件范围
租户范围
```

不能只测试菜单是否显示。

---

# 80. Docker

部署目录：

```text
deploy/
```

提供实际可用 Docker / Docker Compose 配置。

至少考虑：

```text
MySQL
Redis
MinIO
backend
```

不要将开发密钥硬编码进生产部署文件。

---

# 81. 文档输出

后端完成后至少提供：

```text
docs/backend-architecture.md
docs/database.md
docs/api.md
docs/permission.md
docs/multi-tenant.md
docs/deployment.md
docs/testing.md
```

具体文件可按项目实际调整。

文档必须和真实代码一致。

---

# 82. 不创建 .editorconfig

本项目明确不需要：

```text
.editorconfig
```

如果仓库中不存在：

```text
不得主动创建
```

也不要因为所谓“企业规范”擅自增加无明确需要的 IDE 风格配置文件。

---

# 83. 不强制设计模式

当前 backend 规范不将任何设计模式作为强制要求。

开发优先考虑：

```text
业务正确
代码简单
职责清晰
可读性
可测试性
可维护性
```

实际业务自然需要时可以使用合适抽象。

不得因为“企业架构”而创建：

```text
无意义 Factory
无意义 Strategy
空 Handler
空 Provider
空 Adapter
```

---

# 84. 禁止空实现

禁止：

```text
空 Controller
空 Service
空 ServiceImpl
空 Mapper
空 Component
空 Listener
空测试
TODO
伪代码
UnsupportedOperationException 占位
Mock 业务接口
静态业务假数据
```

创建功能就完成真实最小业务闭环。

---

# 85. 禁止重复造轮子

开发前先搜索：

```text
Utils
Service
Mapper
异常
结果码
枚举
配置
缓存
DTO
Component
```

是否已有实现。

已有：

```text
DateTimeUtils
```

就不要再创建：

```text
DateUtils
TimeUtils
```

除非职责确实不同。

---

# 86. 不主动新增依赖

新增依赖前先确认：

```text
JDK 是否已支持
Spring 是否已支持
现有依赖是否已支持
common 是否已有能力
```

新增依赖必须确认：

```text
Java 21 兼容
Spring Boot 3.5.x 兼容
License 合适
无严重依赖冲突
```

---

# 87. 注释规范

需要完整说明：

```text
Entity
DTO
Enum
Config Properties
Controller API
核心 Service
重要公共接口
```

复杂业务注释解释：

```text
为什么这样做
业务约束
特殊边界
```

禁止无意义重复代码的注释。

---

# 88. 日志规范

核心业务添加必要日志。

推荐格式包含：

```text
requestId
userId
contextType
tenantId
业务主键
操作
结果
耗时（必要时）
```

禁止日志泄露敏感数据。

---

# 89. 包和变量命名

必须表达完整业务含义。

推荐：

```java
private final UserService userService;
private final DepartmentService departmentService;
private final TenantService tenantService;
```

禁止：

```java
private final UserService s;
private final UserService service;
private final UserMapper mapper;
```

多个同类依赖时名称必须明确。

---

# 90. 当前禁止项汇总

明确禁止：

```text
Liquibase

Organization
sys_organization

Post
sys_post
sys_user_post

Dept / dept 正式领域命名

Mapper XML

BeanUtils.copyProperties
BeanCopier
反射式属性复制

手工 TableDef

无意义 XxxUtils

LogUtils
AuditUtils
SecurityUtils
PermissionUtils
TenantUtils
ContextUtils
PageUtils
QueryUtils
DataScopeUtils
ExcelUtils
ExcelTemplateUtils

空实现
TODO
伪代码
Mock 业务数据

.editorconfig

平台伪租户
tenant_id = 0 表示平台
tenant_id = -1 表示平台

普通用户进入 PLATFORM

业务 Tenant API 信任前端 tenantId
```

---

# 91. 开发执行顺序

## 第一阶段：工程基础

完成：

```text
backend Maven 父工程
nova-common
nova-modules
nova-boot

Java 21
Spring Boot
配置

R<T>
结果码
异常

MyBatis-Flex
TableDef APT
MapStructPlus APT
Lombok

MySQL
Redis
Redisson
Spring Cache
Caffeine

Sa-Token
TenantContext
LoginUserUtils

基础工具类

SQL 初始化
```

执行：

```text
clean compile
test
package
```

---

## 第二阶段：认证和多租户

完成：

```text
登录
退出

平台管理员身份

PLATFORM
TENANT

Context current
Context options
Context switch

Tenant CRUD
Tenant 初始化
Tenant 管理员绑定

多租户拦截
租户隔离
```

真实测试后进入下一阶段。

---

## 第三阶段：系统权限

完成：

```text
Department

User
UserTenant

Role
UserRole

Menu
RoleMenu

按钮权限
DataScope
```

完成完整 RBAC 闭环。

---

## 第四阶段：系统基础能力

完成：

```text
字典
参数
登录日志
操作日志
在线用户
缓存管理
个人中心
工作台
```

---

## 第五阶段：通用业务能力

完成：

```text
消息中心
通知公告
文件附件
Local
MinIO
Excel 导入
Excel 导出
```

---

## 第六阶段：验证和整改

执行：

```text
Maven clean compile
Maven test
Maven package
dependency:tree

SQL 空库初始化
后端启动
认证测试
Tenant 测试
权限测试
数据权限测试
文件测试
Excel 测试
异常测试
安全测试
```

发现问题直接修复并重新验证。

---

# 92. 最终交付

最终必须提供完整累计结果，包括：

```text
完整 backend 源码
完整 SQL
Docker 部署文件
后端架构说明
数据库说明
API 文档
权限说明
多租户说明
部署手册
测试说明
测试用例
真实构建结果
真实启动结果
问题修复结果
```

不得只提供：

```text
差异代码
Patch
问题清单
实现建议
TODO
```

---

# 93. Codex 开发要求

Codex 开始后端任务时必须：

1. 先阅读根目录 `AGENTS.md`。
2. 完整阅读 `backend.md`。
3. 扫描当前 `backend/`。
4. 扫描当前 `sql/`。
5. 检查已有 Maven 和 Java 文件。
6. 确认当前真实技术基线。
7. 输出简洁执行计划。
8. 直接开始开发。
9. 不需要每个阶段等待人工确认。
10. 每完成主要阶段执行真实 Maven 验证。
11. 构建失败必须修复。
12. 启动失败必须修复。
13. 遇到外部环境阻塞时明确说明。
14. 禁止假装构建成功。
15. 禁止修改 frontend 业务代码，除非当前任务明确要求联调。

---

# 94. 最终后端领域模型

核心关系固定为：

```text
Platform
│
├── Platform Administrator
│
└── Tenant
     │
     ├── Department
     │    └── Child Department
     │
     ├── UserTenant
     │    └── User
     │
     └── Role
          │
          ├── UserRole
          └── RoleMenu
               └── Menu
```

职责：

```text
Tenant
=
独立业务空间

Department
=
Tenant 内组织层级

User
=
平台级账号身份

UserTenant
=
用户在哪个 Tenant、哪个 Department

Role
=
Tenant 内功能权限集合

UserRole
=
用户在 Tenant 内拥有哪些 Role

Menu
=
菜单 / 路由 / 按钮定义

RoleMenu
=
Role 拥有哪些菜单和按钮
```

平台管理员：

```text
平台级身份
默认 PLATFORM
可切换全部允许管理的 Tenant
不要求成为每个 Tenant 的 UserTenant 成员
```

---

# 95. 最终原则

始终遵循：

```text
先读 AGENTS.md
再读 backend.md
再读真实源码

不猜测
不重复造轮子
不创建无意义文件
不创建 .editorconfig
不创建无意义 Utils
不引入 Organization
不引入 Post
Department 不缩写 Dept
平台管理不是 Tenant
不信任前端 tenantId
不使用 Mapper XML
不使用 BeanUtils
优先 MapStructPlus
优先 MyBatis-Flex
优先 Java 21 稳定新特性
保持 Virtual Threads Ready
不过度设计
不创建空实现

真实 compile
真实 test
真实 package
环境允许时真实 run
发现问题继续修复
```
