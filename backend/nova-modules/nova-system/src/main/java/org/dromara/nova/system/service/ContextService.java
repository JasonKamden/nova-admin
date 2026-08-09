package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.response.ContextOptionsRespDto;
import org.dromara.nova.system.dto.response.CurrentContextRespDto;

/**
 * PLATFORM/TENANT Context 查询与切换业务契约。
 */
public interface ContextService {
    /**
     * 返回当前运行 Context。
     */
    CurrentContextRespDto current();

    /**
     * 返回当前用户可切换的 Context。
     */
    ContextOptionsRespDto options();

    /**
     * 校验平台管理员身份后切换到 PLATFORM Context。
     */
    CurrentContextRespDto switchToPlatform();

    /**
     * 校验 Tenant 可用性和访问资格后切换到目标 TENANT Context。
     */
    CurrentContextRespDto switchToTenant(Long tenantId);
}
