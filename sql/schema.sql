SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password        VARCHAR(255) NOT NULL COMMENT 'BCrypt密码',
    nickname        VARCHAR(64)  NOT NULL COMMENT '姓名/昵称',
    avatar          VARCHAR(512)          DEFAULT NULL COMMENT '头像存储路径',
    gender          VARCHAR(16)           DEFAULT NULL COMMENT '性别字典值',
    phone           VARCHAR(32)           DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
    bio             VARCHAR(500)          DEFAULT NULL COMMENT '简介',
    platform_admin  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否平台管理员',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1启用0禁用',
    last_login_time DATETIME              DEFAULT NULL COMMENT '最近登录时间',
    last_login_ip   VARCHAR(64)           DEFAULT NULL COMMENT '最近登录IP',
    create_by       BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_status (status)
) COMMENT ='平台级用户身份';

CREATE TABLE IF NOT EXISTS sys_tenant
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Tenant ID',
    tenant_code   VARCHAR(64)  NOT NULL COMMENT 'Tenant编码',
    tenant_name   VARCHAR(128) NOT NULL COMMENT 'Tenant名称',
    contact_name  VARCHAR(64)           DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(32)           DEFAULT NULL COMMENT '联系电话',
    contact_email VARCHAR(128)          DEFAULT NULL COMMENT '联系邮箱',
    expire_at     DATE                  DEFAULT NULL COMMENT '有效期截止日',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    remark        VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    create_by     BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version       BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_sys_tenant_code (tenant_code),
    KEY idx_sys_tenant_status (status)
) COMMENT ='Tenant业务单位';

CREATE TABLE IF NOT EXISTS sys_department
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Department ID',
    tenant_id       BIGINT       NOT NULL COMMENT 'Tenant ID',
    parent_id       BIGINT                DEFAULT NULL COMMENT '父Department ID',
    department_code VARCHAR(64)  NOT NULL COMMENT 'Department编码',
    department_name VARCHAR(128) NOT NULL COMMENT 'Department名称',
    leader_user_id  BIGINT                DEFAULT NULL COMMENT '负责人用户ID',
    phone           VARCHAR(32)           DEFAULT NULL COMMENT '电话',
    email           VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
    sort            INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_by       BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_department_code (tenant_id, department_code, deleted),
    KEY idx_department_parent (tenant_id, parent_id),
    KEY idx_department_status (tenant_id, status)
) COMMENT ='Department树';

CREATE TABLE IF NOT EXISTS sys_user_tenant
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    user_id       BIGINT   NOT NULL COMMENT '用户ID',
    tenant_id     BIGINT   NOT NULL COMMENT 'Tenant ID',
    department_id BIGINT            DEFAULT NULL COMMENT 'Department ID',
    status        TINYINT  NOT NULL DEFAULT 1 COMMENT '成员状态',
    join_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    create_by     BIGINT            DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     BIGINT            DEFAULT NULL COMMENT '更新人',
    update_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_tenant (user_id, tenant_id),
    KEY idx_user_tenant_tenant (tenant_id, status),
    KEY idx_user_tenant_department (tenant_id, department_id)
) COMMENT ='用户Tenant成员关系';

CREATE TABLE IF NOT EXISTS sys_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Role ID',
    tenant_id   BIGINT       NOT NULL COMMENT 'Tenant ID',
    role_code   VARCHAR(64)  NOT NULL COMMENT 'Role编码',
    role_name   VARCHAR(128) NOT NULL COMMENT 'Role名称',
    data_scope  VARCHAR(32)  NOT NULL COMMENT 'ALL/TENANT/DEPARTMENT/DEPARTMENT_AND_CHILDREN/SELF/CUSTOM',
    built_in    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否内置',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    remark      VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    create_by   BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version     BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_role_code (tenant_id, role_code, deleted),
    KEY idx_role_tenant (tenant_id, status)
) COMMENT ='Tenant角色';

CREATE TABLE IF NOT EXISTS sys_user_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    tenant_id   BIGINT   NOT NULL COMMENT 'Tenant ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    role_id     BIGINT   NOT NULL COMMENT 'Role ID',
    create_by   BIGINT            DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (tenant_id, user_id, role_id),
    KEY idx_user_role_role (tenant_id, role_id)
) COMMENT ='用户Role关系';

CREATE TABLE IF NOT EXISTS sys_menu
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Menu ID',
    menu_type       VARCHAR(16)  NOT NULL COMMENT 'DIRECTORY/MENU/BUTTON',
    parent_id       BIGINT                DEFAULT NULL COMMENT '父Menu ID',
    menu_name       VARCHAR(128) NOT NULL COMMENT '名称',
    route_name      VARCHAR(128)          DEFAULT NULL COMMENT '前端Route name',
    route_path      VARCHAR(255)          DEFAULT NULL COMMENT 'Route path',
    component_path  VARCHAR(255)          DEFAULT NULL COMMENT '组件路径',
    external_link   VARCHAR(500)          DEFAULT NULL COMMENT '外链地址',
    permission_code VARCHAR(128)          DEFAULT NULL COMMENT '权限编码',
    icon            VARCHAR(128)          DEFAULT NULL COMMENT '图标',
    i18n_key        VARCHAR(128)          DEFAULT NULL COMMENT 'i18n key',
    sort            INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    visible         TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否可见',
    keep_alive      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否KeepAlive',
    create_by       BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    KEY idx_menu_parent (parent_id),
    KEY idx_menu_permission (permission_code)
) COMMENT ='全局Menu与按钮权限定义';

CREATE TABLE IF NOT EXISTS sys_role_menu
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    tenant_id   BIGINT   NOT NULL COMMENT 'Tenant ID',
    role_id     BIGINT   NOT NULL COMMENT 'Role ID',
    menu_id     BIGINT   NOT NULL COMMENT 'Menu ID',
    create_by   BIGINT            DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (tenant_id, role_id, menu_id),
    KEY idx_role_menu_menu (menu_id)
) COMMENT ='Tenant Role Menu授权';

CREATE TABLE IF NOT EXISTS sys_role_department
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    tenant_id     BIGINT   NOT NULL COMMENT 'Tenant ID',
    role_id       BIGINT   NOT NULL COMMENT 'Role ID',
    department_id BIGINT   NOT NULL COMMENT 'CUSTOM DataScope Department ID',
    create_by     BIGINT            DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_department (tenant_id, role_id, department_id)
) COMMENT ='CUSTOM DataScope';

CREATE TABLE IF NOT EXISTS sys_dict_type
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典类型ID',
    tenant_id   BIGINT       NOT NULL COMMENT 'Tenant ID',
    dict_name   VARCHAR(128) NOT NULL COMMENT '字典名称',
    dict_code   VARCHAR(128) NOT NULL COMMENT '稳定字典编码',
    built_in    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '系统内置',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    remark      VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    create_by   BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version     BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_dict_type_code (tenant_id, dict_code, deleted)
) COMMENT ='Tenant字典类型';

CREATE TABLE IF NOT EXISTS sys_dict_data
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典数据ID',
    tenant_id    BIGINT       NOT NULL COMMENT 'Tenant ID',
    dict_type_id BIGINT       NOT NULL COMMENT '字典类型ID',
    dict_label   VARCHAR(128) NOT NULL COMMENT '显示标签',
    dict_value   VARCHAR(128) NOT NULL COMMENT '业务值',
    tag_type     VARCHAR(16)  NOT NULL DEFAULT 'default' COMMENT 'default/primary/info/success/warning/error',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    remark       VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    create_by    BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by    BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version      BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_dict_data_value (tenant_id, dict_type_id, dict_value, deleted),
    KEY idx_dict_data_type (tenant_id, dict_type_id, status)
) COMMENT ='Tenant字典数据';

CREATE TABLE IF NOT EXISTS sys_config
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID',
    tenant_id    BIGINT       NOT NULL COMMENT 'Tenant ID',
    config_name  VARCHAR(128) NOT NULL COMMENT '参数名称',
    config_code  VARCHAR(128) NOT NULL COMMENT '稳定参数编码',
    config_value TEXT         NOT NULL COMMENT '参数值',
    config_type  VARCHAR(16)  NOT NULL COMMENT 'STRING/NUMBER/BOOLEAN/JSON',
    is_sensitive TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否敏感',
    built_in     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '系统内置',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    remark       VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    create_by    BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by    BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version      BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    UNIQUE KEY uk_config_code (tenant_id, config_code, deleted)
) COMMENT ='Tenant参数';

CREATE TABLE IF NOT EXISTS sys_login_log
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id        BIGINT               DEFAULT NULL COMMENT '用户ID',
    username       VARCHAR(64)          DEFAULT NULL COMMENT '账号',
    context_type   VARCHAR(16)          DEFAULT NULL COMMENT 'PLATFORM/TENANT',
    tenant_id      BIGINT               DEFAULT NULL COMMENT 'Tenant ID',
    department_id  BIGINT               DEFAULT NULL COMMENT '登录时Department ID',
    login_type     VARCHAR(32) NOT NULL COMMENT '登录类型',
    login_status   TINYINT     NOT NULL COMMENT '1成功0失败',
    ip             VARCHAR(64)          DEFAULT NULL COMMENT 'IP',
    user_agent     VARCHAR(512)         DEFAULT NULL COMMENT 'User-Agent',
    login_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    failure_reason VARCHAR(500)         DEFAULT NULL COMMENT '失败原因',
    request_id     VARCHAR(64)          DEFAULT NULL COMMENT 'Request ID',
    KEY idx_login_log_time (login_time),
    KEY idx_login_log_tenant (tenant_id, login_time),
    KEY idx_login_log_user (user_id, login_time)
) COMMENT ='登录日志';

CREATE TABLE IF NOT EXISTS sys_operation_log
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    module                VARCHAR(64)  NOT NULL COMMENT '操作模块',
    operation_type        VARCHAR(64)  NOT NULL COMMENT '操作类型',
    operation_description VARCHAR(255) NOT NULL COMMENT '操作描述',
    user_id               BIGINT                DEFAULT NULL COMMENT '用户ID',
    username              VARCHAR(64)           DEFAULT NULL COMMENT '账号',
    context_type          VARCHAR(16)           DEFAULT NULL COMMENT 'Context',
    tenant_id             BIGINT                DEFAULT NULL COMMENT 'Tenant ID',
    department_id         BIGINT                DEFAULT NULL COMMENT 'Department ID',
    request_method        VARCHAR(16)           DEFAULT NULL COMMENT 'HTTP Method',
    request_uri           VARCHAR(500)          DEFAULT NULL COMMENT 'URI',
    request_ip            VARCHAR(64)           DEFAULT NULL COMMENT 'IP',
    user_agent            VARCHAR(512)          DEFAULT NULL COMMENT 'User-Agent',
    content_type          VARCHAR(128)          DEFAULT NULL COMMENT 'Content-Type',
    request_headers       TEXT                  DEFAULT NULL COMMENT '白名单请求Header',
    query_params          MEDIUMTEXT            DEFAULT NULL COMMENT 'Query参数',
    path_params           MEDIUMTEXT            DEFAULT NULL COMMENT 'Path参数',
    request_body          MEDIUMTEXT            DEFAULT NULL COMMENT '脱敏请求体',
    http_status           INT                   DEFAULT NULL COMMENT 'HTTP状态',
    business_code         INT                   DEFAULT NULL COMMENT '业务码',
    operation_status      VARCHAR(16)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS/FAILED',
    response_body         MEDIUMTEXT            DEFAULT NULL COMMENT '脱敏响应体',
    exception_type        VARCHAR(255)          DEFAULT NULL COMMENT '异常类型',
    error_code            VARCHAR(64)           DEFAULT NULL COMMENT '错误码',
    error_message         TEXT                  DEFAULT NULL COMMENT '错误信息',
    exception_location    VARCHAR(500)          DEFAULT NULL COMMENT '异常位置',
    exception_stack       MEDIUMTEXT            DEFAULT NULL COMMENT '异常堆栈',
    duration_ms           BIGINT       NOT NULL DEFAULT 0 COMMENT '耗时ms',
    request_id            VARCHAR(64)           DEFAULT NULL COMMENT 'Request ID',
    trace_id              VARCHAR(64)           DEFAULT NULL COMMENT 'Trace ID',
    operation_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_operation_log_time (operation_time),
    KEY idx_operation_log_tenant (tenant_id, operation_time),
    KEY idx_operation_log_user (user_id, operation_time),
    KEY idx_operation_log_module (module, operation_type)
) COMMENT ='操作审计日志';

CREATE TABLE IF NOT EXISTS sys_file
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    context_type  VARCHAR(16)   NOT NULL COMMENT 'PLATFORM/TENANT',
    tenant_id     BIGINT                 DEFAULT NULL COMMENT 'Tenant ID',
    owner_user_id BIGINT        NOT NULL COMMENT '上传用户',
    original_name VARCHAR(255)  NOT NULL COMMENT '原始文件名',
    extension     VARCHAR(32)            DEFAULT NULL COMMENT '扩展名',
    content_type  VARCHAR(128)           DEFAULT NULL COMMENT 'MIME',
    file_size     BIGINT        NOT NULL COMMENT '大小',
    sha256        CHAR(64)      NOT NULL COMMENT 'SHA-256',
    storage_type  VARCHAR(16)   NOT NULL COMMENT 'LOCAL/MINIO',
    storage_path  VARCHAR(1024) NOT NULL COMMENT '对象路径',
    status        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态',
    create_by     BIGINT                 DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     BIGINT                 DEFAULT NULL COMMENT '更新人',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version       BIGINT        NOT NULL DEFAULT 0 COMMENT '版本',
    KEY idx_file_tenant (tenant_id, create_time),
    KEY idx_file_sha256 (sha256)
) COMMENT ='统一文件元数据';

CREATE TABLE IF NOT EXISTS sys_file_relation
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    tenant_id     BIGINT                DEFAULT NULL COMMENT 'Tenant ID',
    file_id       BIGINT       NOT NULL COMMENT '文件ID',
    business_type VARCHAR(64)  NOT NULL COMMENT '业务类型',
    business_id   VARCHAR(128) NOT NULL COMMENT '业务ID',
    create_by     BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_file_relation (file_id, business_type, business_id),
    KEY idx_file_relation_business (tenant_id, business_type, business_id)
) COMMENT ='文件业务关联';

CREATE TABLE IF NOT EXISTS sys_message
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    tenant_id           BIGINT       NOT NULL COMMENT 'Tenant ID',
    title               VARCHAR(255) NOT NULL COMMENT '标题',
    message_type        VARCHAR(32)  NOT NULL COMMENT 'ANNOUNCEMENT/NOTICE/REMINDER',
    content_html        MEDIUMTEXT   NOT NULL COMMENT '清洗后的富文本',
    recipient_type      VARCHAR(32)  NOT NULL COMMENT 'ALL/DEPARTMENT/ROLE/USER',
    recipient_rule_json TEXT         NOT NULL COMMENT '发送规则快照',
    status              VARCHAR(16)  NOT NULL COMMENT 'DRAFT/SENT/WITHDRAWN',
    recipient_count     INT          NOT NULL DEFAULT 0 COMMENT '接收人数',
    read_count          INT          NOT NULL DEFAULT 0 COMMENT '已读人数',
    send_time           DATETIME              DEFAULT NULL COMMENT '发送时间',
    withdraw_time       DATETIME              DEFAULT NULL COMMENT '撤回时间',
    create_by           BIGINT                DEFAULT NULL COMMENT '创建人',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           BIGINT                DEFAULT NULL COMMENT '更新人',
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version             BIGINT       NOT NULL DEFAULT 0 COMMENT '版本',
    KEY idx_message_tenant_status (tenant_id, status, create_time)
) COMMENT ='消息主体';

CREATE TABLE IF NOT EXISTS sys_message_user
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '接收快照ID',
    tenant_id       BIGINT      NOT NULL COMMENT 'Tenant ID',
    message_id      BIGINT      NOT NULL COMMENT '消息ID',
    user_id         BIGINT      NOT NULL COMMENT '接收用户ID',
    department_id   BIGINT               DEFAULT NULL COMMENT '发送时Department ID',
    username        VARCHAR(64) NOT NULL COMMENT '发送时账号快照',
    nickname        VARCHAR(64) NOT NULL COMMENT '发送时昵称快照',
    department_name VARCHAR(128)         DEFAULT NULL COMMENT '发送时Department快照',
    read_status     TINYINT     NOT NULL DEFAULT 0 COMMENT '0未读1已读',
    receive_time    DATETIME    NOT NULL COMMENT '接收时间',
    read_time       DATETIME             DEFAULT NULL COMMENT '阅读时间',
    UNIQUE KEY uk_message_user (message_id, user_id),
    KEY idx_message_user_user (tenant_id, user_id, read_status, receive_time),
    KEY idx_message_user_message (message_id, read_status)
) COMMENT ='消息接收用户快照';

CREATE TABLE IF NOT EXISTS sys_message_file
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    tenant_id  BIGINT NOT NULL COMMENT 'Tenant ID',
    message_id BIGINT NOT NULL COMMENT '消息ID',
    file_id    BIGINT NOT NULL COMMENT '文件ID',
    UNIQUE KEY uk_message_file (message_id, file_id)
) COMMENT ='消息附件';
