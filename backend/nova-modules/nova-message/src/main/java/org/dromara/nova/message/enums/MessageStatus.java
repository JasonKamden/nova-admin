package org.dromara.nova.message.enums;

/**
 * 消息生命周期状态。
 */
public enum MessageStatus {
    /**
     * 草稿，可继续编辑、删除或发送。
     */
    DRAFT,
    /**
     * 已发送，正文和接收范围冻结，仅允许查看、追踪或撤回。
     */
    SENT,
    /**
     * 已撤回，保留历史接收和阅读记录。
     */
    WITHDRAWN
}
