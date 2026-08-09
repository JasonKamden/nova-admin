package org.dromara.nova.common.tenant.context;

import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;

/**
 * 业务层读取可信 Tenant Context。
 */
public final class TenantContextSupport {
    private TenantContextSupport() {
    }

    /**
     * 获取当前线程已绑定的 Tenant Context。
     *
     * @return 当前 Tenant Context
     */
    public static TenantContext current() {
        return TenantContextHolder.get();
    }

    /**
     * 获取当前 Tenant ID；当前不是 TENANT Context 时拒绝访问。
     *
     * @return 当前 Tenant ID
     */
    public static Long requireTenantId() {
        TenantContext context = current();
        if (!context.isTenant() || context.tenantId() == null)
            throw new BusinessException(CommonResultCode.TENANT_REQUIRED);
        return context.tenantId();
    }

    /**
     * 校验当前请求必须处于 PLATFORM Context。
     */
    public static void requirePlatform() {
        if (!current().isPlatform())
            throw new BusinessException(CommonResultCode.FORBIDDEN, "当前操作仅允许平台上下文");
    }
}
