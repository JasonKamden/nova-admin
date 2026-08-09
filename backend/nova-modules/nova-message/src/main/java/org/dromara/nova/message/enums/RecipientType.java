package org.dromara.nova.message.enums;

/**
 * 消息接收范围类型。
 */
public enum RecipientType {
    /**
     * 当前合法 Context 下的全部有效用户；TENANT 中只代表当前 Tenant 用户。
     */
    ALL,
    /**
     * 按部门发送，可配合 includeChildren 包含下级 Department。
     */
    DEPARTMENT,
    /**
     * 按当前 Tenant Role 发送。
     */
    ROLE,
    /**
     * 按明确选择的用户发送。
     */
    USER
}
