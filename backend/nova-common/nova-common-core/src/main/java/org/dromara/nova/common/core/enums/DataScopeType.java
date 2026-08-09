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
     * 当前用户所在部门数据。
     */
    DEPARTMENT,
    /**
     * 当前部门及全部下级部门数据。
     */
    DEPARTMENT_AND_CHILDREN,
    /**
     * 仅当前用户本人数据。
     */
    SELF,
    /**
     * 自定义部门范围，通过角色与部门关系维护。
     */
    CUSTOM
}
