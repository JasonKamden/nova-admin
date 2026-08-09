package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 登录日志。
 *
 * @param id            主键 ID
 * @param userId        用户 ID
 * @param username      登录账号
 * @param contextType   当前运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId      Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param departmentId  部门ID
 * @param loginType     登录类型
 * @param loginStatus   登录结果状态
 * @param ip            客户端 IP 地址
 * @param userAgent     客户端 User-Agent
 * @param loginTime     登录时间
 * @param failureReason 失败原因
 * @param requestId     请求唯一标识
 */
@Schema(description = "登录日志响应")
public record LoginLogRespDto(
        @Schema(description = "主键 ID") Long id, @Schema(description = "用户 ID") Long userId,
        @Schema(description = "登录账号") String username,
        @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") String contextType,
        @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId,
        @Schema(description = "Department ID") Long departmentId, @Schema(description = "登录类型") String loginType,
        @Schema(description = "登录结果状态") Integer loginStatus,
        @Schema(description = "客户端 IP 地址") String ip, @Schema(description = "客户端 User-Agent") String userAgent,
        @Schema(description = "登录时间") LocalDateTime loginTime,
        @Schema(description = "失败原因") String failureReason, @Schema(description = "请求唯一标识") String requestId
) {
}
