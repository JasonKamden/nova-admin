package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 在线会话列表，不暴露真实 Token。
 *
 * @param sessionId        服务端在线会话标识，不是原始 Token
 * @param userId           用户 ID
 * @param username         登录账号
 * @param contextType      当前运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId         Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param tenantName       Tenant 名称
 * @param departmentId     部门ID
 * @param departmentName   部门名称
 * @param ip               客户端 IP 地址
 * @param userAgent        客户端 User-Agent
 * @param loginTime        登录时间
 * @param lastActivityTime 最近活动时间
 */
@Schema(description = "在线用户响应，sessionId 为服务端会话标识，不返回原始 Token")
public record OnlineUserRespDto(@Schema(description = "服务端在线会话标识，不是原始 Token") String sessionId,
                                @Schema(description = "用户 ID") Long userId,
                                @Schema(description = "登录账号") String username,
                                @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") String contextType,
                                @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId,
                                @Schema(description = "Tenant 名称") String tenantName,
                                @Schema(description = "Department ID") Long departmentId,
                                @Schema(description = "Department 名称") String departmentName,
                                @Schema(description = "客户端 IP 地址") String ip,
                                @Schema(description = "客户端 User-Agent") String userAgent,
                                @Schema(description = "登录时间") LocalDateTime loginTime,
                                @Schema(description = "最近活动时间") LocalDateTime lastActivityTime) {
}
