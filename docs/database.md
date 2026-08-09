# Database

SQL 位于根目录 `sql/`，目标 MySQL 8.4，可从空库执行 `schema.sql` 后执行 `data.sql`。

核心表：

- 身份/租户：`sys_user`、`sys_tenant`、`sys_user_tenant`
- 组织权限：`sys_department`、`sys_role`、`sys_user_role`、`sys_menu`、`sys_role_menu`、`sys_role_department`
- 配置：`sys_dict_type`、`sys_dict_data`、`sys_config`
- 审计：`sys_login_log`、`sys_operation_log`
- 文件：`sys_file`、`sys_file_relation`
- 消息：`sys_message`、`sys_message_user`、`sys_message_file`

不创建 `sys_organization`、`sys_post`、`sys_user_post`。PLATFORM 不用 tenant_id=0/-1 模拟。

`data.sql` 初始化 PLATFORM 管理员、示例 Tenant、Tenant 管理员、默认 Role、菜单/按钮权限和内置字典。初始化密码仅用于本地首次启动，生产环境必须通过安全流程立即修改。
