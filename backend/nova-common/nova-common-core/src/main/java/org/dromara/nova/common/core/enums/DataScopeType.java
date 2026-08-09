package org.dromara.nova.common.core.enums;

/**
 * 角色数据权限范围；与菜单/按钮功能权限分离。
 */
public enum DataScopeType {
    /**
     * 全部可管理数据；仅在服务端明确允许的角色/身份下生效。
     */
    ALL,
    /**
     * 当前 Tenant 全部数据。
     */
    TENANT,
    /**
     * 当前用户所在 Department 数据。
     */
    DEPARTMENT,
    /**
     * 当前 Department 及全部下级 Department 数据。
     */
    DEPARTMENT_AND_CHILDREN,
    /**
     * 仅当前用户本人数据。
     */
    SELF,
    /**
     * 自定义 Department 范围，通过角色与 Department 关系维护。
     */
    CUSTOM
}
