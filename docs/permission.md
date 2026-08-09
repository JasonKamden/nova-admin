# Permission

权限链路：

```text
sys_user
→ sys_user_tenant
→ sys_user_role
→ sys_role
→ sys_role_menu
→ sys_menu.permission_code
→ CurrentLoginUser.permissions
→ Sa-Token @SaCheckPermission
```

平台管理员是 `sys_user.platform_admin` 身份，不是租户普通 Role。平台管理员在 PLATFORM/TENANT Context 中保持真实平台身份。

DataScope 独立于功能权限：`ALL/TENANT/DEPARTMENT/DEPARTMENT_AND_CHILDREN/SELF/CUSTOM`。Tenant 查询必须同时满足租户边界和
DataScope。前端按钮权限只用于交互，Controller 仍必须鉴权。

角色菜单、用户角色使用全量替换语义；修改授权后必须清除/刷新权限相关缓存并在下一次 Context 装载时获得新快照。
