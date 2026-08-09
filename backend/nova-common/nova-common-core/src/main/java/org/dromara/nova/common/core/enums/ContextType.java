package org.dromara.nova.common.core.enums;

/**
 * 系统运行上下文类型。PLATFORM 表示平台管理空间，TENANT 表示具体业务 Tenant。
 */
public enum ContextType {
    /**
     * 平台管理上下文，不对应 sys_tenant 数据，不存在伪 tenantId。
     */
    PLATFORM,
    /**
     * Tenant 业务上下文，tenantId 必须为真实有效 Tenant ID。
     */
    TENANT
}
