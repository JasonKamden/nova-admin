# Backend Architecture

Nova Backend 是 Java 21 + Spring Boot 3.5.x 的 Maven 模块化单体，统一由 `nova-boot` 启动。

```text
nova-boot
  ├─ nova-system
  ├─ nova-file
  └─ nova-message
       ↓
    nova-common
```

`nova-system` 提供认证、Context、Tenant、RBAC、DataScope、字典、参数、日志、在线用户、缓存、Profile、Dashboard 和用户 Excel。
`nova-file` 提供文件业务元数据与 Local/MinIO 存储。`nova-message` 提供消息管理、接收快照、消息中心和 SSE。

平台管理使用 `PLATFORM` Context（tenantId=null）；Tenant 业务使用真实 Tenant ID。平台不是伪 Tenant。Tenant 业务服务只从可信
`TenantContext` 获取租户边界。

数据权限先经过 Tenant Fence，再叠加 Role 的 `ALL/TENANT/DEPARTMENT/DEPARTMENT_AND_CHILDREN/SELF/CUSTOM` 范围。CUSTOM
Department 通过 `sys_role_department` 保存。

普通数据库操作统一 MyBatis-Flex QueryWrapper/TableDef；不使用 Mapper XML。普通对象转换优先 MapStructPlus。Sa-Token 是唯一
Web 登录/角色/权限入口。
