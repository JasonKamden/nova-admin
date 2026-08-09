-- 本地开发基础账号，生产环境首次启动后必须立即修改密码。
-- 初始明文密码：Nova@123456
INSERT INTO sys_user(id, username, password, nickname, platform_admin, status, create_by, create_time, update_by,
                     update_time, deleted, version)
VALUES (1, 'platform-admin', '$2y$10$9jlbWAwJOAyMGNj1v/SyUO0McyaZTXE5dR0pNx1zyzNV.sOPxbWT2', '平台管理员', 1, 1, 1,
        NOW(), 1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE nickname=VALUES(nickname);

INSERT INTO sys_tenant(id, tenant_code, tenant_name, contact_name, contact_phone, contact_email, expire_at, status,
                       remark, create_by, create_time, update_by, update_time, deleted, version)
VALUES (1001, 'default-tenant', '默认 Tenant', '管理员', '13800000000', 'admin@nova.local', '2099-12-31', 1,
        '本地初始化 Tenant', 1, NOW(), 1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE tenant_name=VALUES(tenant_name);

INSERT INTO sys_user(id, username, password, nickname, platform_admin, status, create_by, create_time, update_by,
                     update_time, deleted, version)
VALUES (2, 'tenant-admin', '$2y$10$9jlbWAwJOAyMGNj1v/SyUO0McyaZTXE5dR0pNx1zyzNV.sOPxbWT2', '租户管理员', 0, 1, 1, NOW(),
        1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE nickname=VALUES(nickname);

INSERT INTO sys_user_tenant(id, user_id, tenant_id, department_id, status, join_time, create_by, create_time, update_by,
                            update_time)
VALUES (1, 2, 1001, NULL, 1, NOW(), 1, NOW(), 1, NOW())
ON DUPLICATE KEY UPDATE status=1;

INSERT INTO sys_role(id, tenant_id, role_code, role_name, data_scope, built_in, sort, status, create_by, create_time,
                     update_by, update_time, deleted, version)
VALUES (10001, 1001, 'tenant_admin', '租户管理员', 'TENANT', 1, 10, 1, 1, NOW(), 1, NOW(), 0, 0),
       (10002, 1001, 'department_admin', '部门管理员', 'DEPARTMENT_AND_CHILDREN', 1, 20, 1, 1, NOW(), 1, NOW(),
        0, 0),
       (10003, 1001, 'business_admin', '业务管理员', 'TENANT', 1, 30, 1, 1, NOW(), 1, NOW(), 0, 0),
       (10004, 1001, 'user', '普通用户', 'SELF', 1, 40, 1, 1, NOW(), 1, NOW(), 0, 0),
       (10005, 1001, 'auditor', '审计人员', 'TENANT', 1, 50, 1, 1, NOW(), 1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name),
                        data_scope=VALUES(data_scope);
INSERT INTO sys_user_role(tenant_id, user_id, role_id, create_by, create_time)
VALUES (1001, 2, 10001, 1, NOW())
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id);

-- 全局基础菜单/按钮。PLATFORM 管理员通过 '*' 权限，不依赖 RoleMenu。
INSERT INTO sys_menu(id, menu_type, parent_id, menu_name, route_name, route_path, component_path, permission_code, icon,
                     i18n_key, sort, status, visible, keep_alive, create_by, create_time, update_by, update_time,
                     deleted, version)
VALUES (1, 'DIRECTORY', NULL, '系统管理', 'system', '/system', NULL, NULL, 'carbon:settings', 'route.system', 10, 1, 1,
        0, 1, NOW(), 1, NOW(), 0, 0),
       (5, 'MENU', NULL, '租户管理', 'platform_tenant', '/platform/tenant', 'view.platform_tenant',
        'platform:tenant:list',
        'carbon:tenant', 'route.platform_tenant', 5, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (6, 'BUTTON', 5, '新增Tenant', NULL, NULL, NULL, 'platform:tenant:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (7, 'BUTTON', 5, '查看Tenant详情', NULL, NULL, NULL, 'platform:tenant:detail', NULL, NULL, 2, 1, 0, 0, 1, NOW(),
        1,
        NOW(), 0, 0),
       (8, 'BUTTON', 5, '修改Tenant', NULL, NULL, NULL, 'platform:tenant:update', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (9, 'BUTTON', 5, '删除Tenant', NULL, NULL, NULL, 'platform:tenant:delete', NULL, NULL, 4, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (10, 'MENU', 1, '用户管理', 'system_user', '/system/user', 'view.system_user', 'system:user:list',
        'carbon:user-multiple', 'route.system_user', 10, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (11, 'BUTTON', 10, '新增用户', NULL, NULL, NULL, 'system:user:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (12, 'BUTTON', 10, '修改用户', NULL, NULL, NULL, 'system:user:update', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (13, 'BUTTON', 10, '删除用户', NULL, NULL, NULL, 'system:user:delete', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (14, 'BUTTON', 10, '分配角色', NULL, NULL, NULL, 'system:user:role', NULL, NULL, 4, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (15, 'BUTTON', 10, '重置密码', NULL, NULL, NULL, 'system:user:password', NULL, NULL, 5, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (16, 'BUTTON', 10, '导入用户', NULL, NULL, NULL, 'system:user:import', NULL, NULL, 6, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (17, 'BUTTON', 10, '导出用户', NULL, NULL, NULL, 'system:user:export', NULL, NULL, 7, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (20, 'MENU', 1, '部门管理', 'system_department', '/system/department', 'view.system_department',
        'system:department:list', 'carbon:tree-view-alt', 'route.system_department', 20, 1, 1, 1, 1, NOW(), 1, NOW(), 0,
        0),
       (21, 'BUTTON', 20, '新增Department', NULL, NULL, NULL, 'system:department:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(),
        1, NOW(), 0, 0),
       (22, 'BUTTON', 20, '修改Department', NULL, NULL, NULL, 'system:department:update', NULL, NULL, 2, 1, 0, 0, 1,
        NOW(), 1, NOW(), 0, 0),
       (23, 'BUTTON', 20, '删除Department', NULL, NULL, NULL, 'system:department:delete', NULL, NULL, 3, 1, 0, 0, 1,
        NOW(), 1, NOW(), 0, 0),
       (30, 'MENU', 1, '角色管理', 'system_role', '/system/role', 'view.system_role', 'system:role:list',
        'carbon:user-role', 'route.system_role', 30, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (31, 'BUTTON', 30, '新增角色', NULL, NULL, NULL, 'system:role:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (32, 'BUTTON', 30, '修改角色', NULL, NULL, NULL, 'system:role:update', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (33, 'BUTTON', 30, '删除角色', NULL, NULL, NULL, 'system:role:delete', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (34, 'BUTTON', 30, '菜单授权', NULL, NULL, NULL, 'system:role:menu', NULL, NULL, 4, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (40, 'MENU', 1, '菜单管理', 'system_menu', '/system/menu', 'view.system_menu', 'system:menu:list', 'carbon:menu',
        'route.system_menu', 40, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (50, 'MENU', 1, '字典管理', 'system_dictionary', '/system/dictionary', 'view.system_dictionary',
        'system:dictionary:list', 'carbon:catalog', 'route.system_dictionary', 50, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (51, 'BUTTON', 50, '新增字典', NULL, NULL, NULL, 'system:dictionary:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (52, 'BUTTON', 50, '修改字典', NULL, NULL, NULL, 'system:dictionary:update', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (53, 'BUTTON', 50, '删除字典', NULL, NULL, NULL, 'system:dictionary:delete', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (60, 'MENU', 1, '参数配置', 'system_config', '/system/config', 'view.system_config', 'system:config:list',
        'carbon:settings-adjust', 'route.system_config', 60, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (61, 'BUTTON', 60, '新增参数', NULL, NULL, NULL, 'system:config:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (62, 'BUTTON', 60, '修改参数', NULL, NULL, NULL, 'system:config:update', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (63, 'BUTTON', 60, '删除参数', NULL, NULL, NULL, 'system:config:delete', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (70, 'MENU', 1, '消息管理', 'system_message', '/system/message', 'view.system_message', 'system:message:list',
        'carbon:notification', 'route.system_message', 70, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (71, 'BUTTON', 70, '新增消息', NULL, NULL, NULL, 'system:message:add', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (72, 'BUTTON', 70, '修改消息', NULL, NULL, NULL, 'system:message:update', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (73, 'BUTTON', 70, '删除消息', NULL, NULL, NULL, 'system:message:delete', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (74, 'BUTTON', 70, '发送消息', NULL, NULL, NULL, 'system:message:send', NULL, NULL, 4, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (75, 'BUTTON', 70, '撤回消息', NULL, NULL, NULL, 'system:message:withdraw', NULL, NULL, 5, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (76, 'BUTTON', 70, '查看阅读情况', NULL, NULL, NULL, 'system:message:read-status', NULL, NULL, 6, 1, 0, 0, 1,
        NOW(), 1, NOW(), 0, 0),
       (77, 'BUTTON', 70, '消息详情', NULL, NULL, NULL, 'system:message:detail', NULL, NULL, 7, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (100, 'DIRECTORY', NULL, '系统监控', 'monitor', '/monitor', NULL, NULL, 'carbon:monitor', 'route.monitor', 20, 1,
        1, 0, 1, NOW(), 1, NOW(), 0, 0),
       (110, 'MENU', 100, '在线用户', 'monitor_online', '/monitor/online', 'view.monitor_online', 'monitor:online:list',
        'carbon:user-online', 'route.monitor_online', 10, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (111, 'BUTTON', 110, '强制下线', NULL, NULL, NULL, 'monitor:online:kick', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (120, 'MENU', 100, '登录日志', 'monitor_login_log', '/monitor/login-log', 'view.monitor_login_log',
        'monitor:login-log:list', 'carbon:login', 'route.monitor_login_log', 20, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (130, 'MENU', 100, '操作日志', 'monitor_operation_log', '/monitor/operation-log', 'view.monitor_operation_log',
        'monitor:operation-log:list', 'carbon:document', 'route.monitor_operation_log', 30, 1, 1, 1, 1, NOW(), 1, NOW(),
        0, 0),
       (131, 'BUTTON', 130, '日志详情', NULL, NULL, NULL, 'monitor:operation-log:detail', NULL, NULL, 1, 1, 0, 0, 1,
        NOW(), 1, NOW(), 0, 0),
       (140, 'MENU', 100, '缓存管理', 'monitor_cache', '/monitor/cache', 'view.monitor_cache', 'monitor:cache:list',
        'carbon:data-base', 'route.monitor_cache', 40, 1, 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (141, 'BUTTON', 140, '缓存详情', NULL, NULL, NULL, 'monitor:cache:detail', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (142, 'BUTTON', 140, '刷新缓存', NULL, NULL, NULL, 'monitor:cache:refresh', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (143, 'BUTTON', 140, '清理缓存', NULL, NULL, NULL, 'monitor:cache:clear', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1,
        NOW(), 0, 0),
       (200, 'MENU', NULL, '文件管理', 'file', '/file', 'view.file', 'file:list', 'carbon:folder', 'route.file', 30, 1,
        1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (201, 'BUTTON', 200, '上传文件', NULL, NULL, NULL, 'file:upload', NULL, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW(), 0,
        0),
       (202, 'BUTTON', 200, '预览文件', NULL, NULL, NULL, 'file:preview', NULL, NULL, 2, 1, 0, 0, 1, NOW(), 1, NOW(), 0,
        0),
       (203, 'BUTTON', 200, '下载文件', NULL, NULL, NULL, 'file:download', NULL, NULL, 3, 1, 0, 0, 1, NOW(), 1, NOW(),
        0, 0),
       (204, 'BUTTON', 200, '删除文件', NULL, NULL, NULL, 'file:delete', NULL, NULL, 4, 1, 0, 0, 1, NOW(), 1, NOW(), 0,
        0)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),
                        permission_code=VALUES(permission_code),
                        status=VALUES(status);

INSERT IGNORE INTO sys_role_menu(tenant_id, role_id, menu_id, create_by, create_time)
SELECT 1001, 10001, id, 1, NOW()
FROM sys_menu
WHERE deleted = 0
  AND status = 1;

INSERT INTO sys_dict_type(id, tenant_id, dict_name, dict_code, built_in, status, create_by, create_time, update_by,
                          update_time, deleted, version)
VALUES (1, 1001, '用户性别', 'sys_user_gender', 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (2, 1001, '用户状态', 'sys_user_status', 1, 1, 1, NOW(), 1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE dict_name=VALUES(dict_name);
INSERT INTO sys_dict_data(id, tenant_id, dict_type_id, dict_label, dict_value, tag_type, sort, status, create_by,
                          create_time, update_by, update_time, deleted, version)
VALUES (1, 1001, 1, '男', 'MALE', 'primary', 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (2, 1001, 1, '女', 'FEMALE', 'error', 2, 1, 1, NOW(), 1, NOW(), 0, 0),
       (3, 1001, 1, '未知', 'UNKNOWN', 'default', 3, 1, 1, NOW(), 1, NOW(), 0, 0),
       (4, 1001, 2, '启用', 'ENABLED', 'success', 1, 1, 1, NOW(), 1, NOW(), 0, 0),
       (5, 1001, 2, '禁用', 'DISABLED', 'warning', 2, 1, 1, NOW(), 1, NOW(), 0, 0)
ON DUPLICATE KEY UPDATE dict_label=VALUES(dict_label);
