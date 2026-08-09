package org.dromara.nova.common.tenant.context;

import org.dromara.nova.common.core.enums.ContextType;

/**
 * 当前请求的可信 Context。
 *
 * @param contextType 运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId    Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 */
public record TenantContext(ContextType contextType, Long tenantId) {
    /**
     * 判断当前 Context 是否为 PLATFORM。
     *
     * @return 是否为 PLATFORM
     */
    public boolean isPlatform() {
        return contextType == ContextType.PLATFORM;
    }

    /**
     * 判断当前 Context 是否为 TENANT。
     *
     * @return 是否为 TENANT
     */
    public boolean isTenant() {
        return contextType == ContextType.TENANT;
    }
}
