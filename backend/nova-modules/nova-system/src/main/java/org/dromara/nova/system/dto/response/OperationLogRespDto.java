package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 操作日志列表项。
 *
 * @param id                   主键 ID
 * @param module               操作所属业务模块
 * @param operationType        操作类型
 * @param operationDescription 操作描述
 * @param userId               用户 ID
 * @param username             登录账号
 * @param contextType          当前运行上下文类型：PLATFORM 或 TENANT
 * @param requestMethod        HTTP 请求方法
 * @param requestUri           请求 URI
 * @param requestIp            请求客户端 IP
 * @param status               状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param durationMs           执行耗时，单位毫秒
 * @param operationTime        操作发生时间
 */
@Schema(description = "操作日志列表项响应")
public record OperationLogRespDto(@Schema(description = "主键 ID") Long id,
                                  @Schema(description = "操作所属业务模块") String module,
                                  @Schema(description = "操作类型") String operationType,
                                  @Schema(description = "操作描述") String operationDescription,
                                  @Schema(description = "用户 ID") Long userId,
                                  @Schema(description = "登录账号") String username,
                                  @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") String contextType,
                                  @Schema(description = "HTTP 请求方法") String requestMethod,
                                  @Schema(description = "请求 URI") String requestUri,
                                  @Schema(description = "请求客户端 IP") String requestIp,
                                  @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") String status,
                                  @Schema(description = "执行耗时，单位毫秒") Long durationMs,
                                  @Schema(description = "操作发生时间") LocalDateTime operationTime) {
}
