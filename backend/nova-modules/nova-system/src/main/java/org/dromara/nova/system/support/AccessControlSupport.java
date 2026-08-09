package org.dromara.nova.system.support;

import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.springframework.stereotype.Component;

/**
 * 平台/Tenant 边界校验。
 */
@Component
public class AccessControlSupport {
    /**
     * 校验当前登录用户必须具有平台管理员身份。
     *
     * @return 方法处理结果。
     */
    public CurrentLoginUser requirePlatformAdmin() {
        var u = LoginUserUtils.getLoginUser();
        if (!u.platformAdmin()) throw new BusinessException(CommonResultCode.FORBIDDEN, "仅平台管理员可执行");
        return u;
    }

    /**
     * 校验当前请求必须处于 PLATFORM Context。
     *
     * @return 方法处理结果。
     */
    public CurrentLoginUser requirePlatformContext() {
        TenantContextSupport.requirePlatform();
        return LoginUserUtils.getLoginUser();
    }

    /**
     * 获取当前可信 Tenant ID；非 TENANT Context 时拒绝访问。
     *
     * @return 业务计算结果。
     */
    public Long requireTenantId() {
        return TenantContextSupport.requireTenantId();
    }
}
