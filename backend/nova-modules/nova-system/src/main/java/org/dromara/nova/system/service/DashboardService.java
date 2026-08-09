package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.response.DashboardRespDto;
import org.dromara.nova.system.dto.response.PlatformDashboardRespDto;

/**
 * TENANT/PLATFORM 工作台聚合统计业务契约。
 */
public interface DashboardService {
    /**
     * 查询当前 Tenant 工作台聚合统计。
     */
    DashboardRespDto tenantDashboard();

    /**
     * 查询 PLATFORM 工作台聚合统计。
     */
    PlatformDashboardRespDto platformDashboard();
}
