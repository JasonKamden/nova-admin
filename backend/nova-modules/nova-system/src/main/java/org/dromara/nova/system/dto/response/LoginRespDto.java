package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dromara.nova.common.core.enums.ContextType;

/**
 * 登录响应。
 *
 * @param token       登录 Token；仅登录成功响应返回，不得写入业务日志
 * @param userId      用户 ID
 * @param username    登录账号
 * @param contextType 当前运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId    Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 */
@Schema(description = "登录成功响应")
public record LoginRespDto(
        @Schema(description = "登录 Token；仅登录成功响应返回，不得写入业务日志", accessMode = Schema.AccessMode.READ_ONLY) String token,
        @Schema(description = "用户 ID") Long userId, @Schema(description = "登录账号") String username,
        @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") ContextType contextType,
        @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId) {
}
