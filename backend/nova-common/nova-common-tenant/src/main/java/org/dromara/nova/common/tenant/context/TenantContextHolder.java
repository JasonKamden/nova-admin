package org.dromara.nova.common.tenant.context;

import org.dromara.nova.common.core.enums.ContextType;

/**
 * 请求线程 TenantContext Holder。
 */
public final class TenantContextHolder {
    private static final ThreadLocal<TenantContext> HOLDER = ThreadLocal.withInitial(() -> new TenantContext(ContextType.PLATFORM, null));

    private TenantContextHolder() {
    }

    /**
     * 读取当前上下文、缓存或注册项。
     *
     * @return 查询结果
     */
    public static TenantContext get() {
        return HOLDER.get();
    }

    /**
     * 写入指定上下文或缓存值。
     *
     * @param context Tenant Context
     */
    public static void set(TenantContext context) {
        HOLDER.set(context);
    }

    /**
     * 清理当前线程上下文。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
