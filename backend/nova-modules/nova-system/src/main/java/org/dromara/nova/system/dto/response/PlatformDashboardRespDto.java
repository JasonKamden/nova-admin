package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PLATFORM 工作台统计。
 *
 * @param tenantCount         Tenant 总数
 * @param enabledTenantCount  启用 Tenant 数量
 * @param disabledTenantCount 停用 Tenant 数量
 * @param platformUserCount   平台用户数量
 * @param todayLoginCount     今日登录次数
 */
@Schema(description = "PLATFORM 工作台聚合响应")
public record PlatformDashboardRespDto(@Schema(description = "Tenant 总数") long tenantCount,
                                       @Schema(description = "启用 Tenant 数量") long enabledTenantCount,
                                       @Schema(description = "停用 Tenant 数量") long disabledTenantCount,
                                       @Schema(description = "平台用户数量") long platformUserCount,
                                       @Schema(description = "今日登录次数") long todayLoginCount) {
}
