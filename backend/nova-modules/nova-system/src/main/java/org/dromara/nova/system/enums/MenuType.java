package org.dromara.nova.system.enums;

/**
 * 全局菜单定义类型。
 */
public enum MenuType {
    /**
     * 目录节点，仅用于菜单层级组织。
     */
    DIRECTORY,
    /**
     * 可路由业务菜单。
     */
    MENU,
    /**
     * 按钮/接口权限节点，不生成独立页面路由。
     */
    BUTTON
}
