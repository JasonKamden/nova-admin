# Multi Tenant

Nova 使用 `PLATFORM` / `TENANT` 双 Context，而不是“平台也是一个特殊 Tenant”。

```text
PLATFORM -> tenantId = null
TENANT   -> tenantId = 当前真实 Tenant ID
```

平台管理员由 `sys_user.platform_admin` 表示，不要求在 `sys_user_tenant` 中绑定所有 Tenant；普通用户的 Tenant 可访问关系来自
`sys_user_tenant`。

Tenant 本身就是业务单位，组织结构只有：

```text
Tenant
└── Department
    └── Child Department
```

不创建 Organization/Post。

Tenant 业务接口只从可信 `TenantContext` 获取 tenantId；请求体/Query 中出现的 tenantId 不能作为隔离依据。查询同时执行
Tenant Fence 与 DataScope。

Context 切换前校验 Tenant 存在、启用、未过期以及当前用户访问资格；切换成功后重新装载角色、菜单、按钮权限、Department
和权限快照。平台管理员切入 Tenant 后身份仍是平台管理员，但 Tenant 业务仍被当前 tenantId 限定。

Tenant 创建在事务中初始化：Tenant、默认角色、初始 Tenant 管理员、UserTenant、UserRole 以及默认 RoleMenu；完成后仍保持 PLATFORM
Context。
