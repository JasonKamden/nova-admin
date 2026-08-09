package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * ContextSwitcher 全部候选。
 *
 * @param platform 是否允许 PLATFORM Context
 * @param tenants  可切换 Tenant 列表
 */
@Schema(description = "ContextSwitcher 可切换上下文响应")
public record ContextOptionsRespDto(@Schema(description = "是否允许 PLATFORM Context") boolean platform,
                                    @Schema(description = "可切换 Tenant 列表") List<ContextTenantOptionRespDto> tenants) {
}
