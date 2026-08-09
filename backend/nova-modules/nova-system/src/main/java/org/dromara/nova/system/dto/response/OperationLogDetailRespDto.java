package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 对应前端“大尺寸右侧 Drawer + 单页分区”的完整操作日志详情。
 *
 * @param basic     操作日志基本信息
 * @param request   操作日志请求信息分区
 * @param response  操作日志响应信息分区
 * @param exception 异常信息分区
 */
@Schema(description = "操作日志完整详情响应，直接支撑前端单页分区 Drawer")
public record OperationLogDetailRespDto(@Schema(description = "操作日志基本信息") Basic basic,
                                        @Schema(description = "操作日志请求信息分区") Request request,
                                        @Schema(description = "操作日志响应信息分区") Response response,
                                        @Schema(description = "异常信息分区") ExceptionInfo exception) {
    /**
     * 操作日志基本信息。
     *
     * @param id                   主键 ID
     * @param module               操作所属业务模块
     * @param operationType        操作类型
     * @param operationDescription 操作描述
     * @param userId               用户 ID
     * @param username             登录账号
     * @param contextType          当前运行上下文类型：PLATFORM 或 TENANT
     * @param tenantId             Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param departmentId         部门ID
     * @param requestIp            请求客户端 IP
     * @param userAgent            客户端 User-Agent
     * @param durationMs           执行耗时，单位毫秒
     * @param requestId            请求唯一标识
     * @param traceId              链路追踪标识
     * @param operationTime        操作发生时间
     */
    @Schema(description = "操作日志基本信息")
    public record Basic(@Schema(description = "主键 ID") Long id,
                        @Schema(description = "操作所属业务模块") String module,
                        @Schema(description = "操作类型") String operationType,
                        @Schema(description = "操作描述") String operationDescription,
                        @Schema(description = "用户 ID") Long userId, @Schema(description = "登录账号") String username,
                        @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") String contextType,
                        @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId,
                        @Schema(description = "Department ID") Long departmentId,
                        @Schema(description = "请求客户端 IP") String requestIp,
                        @Schema(description = "客户端 User-Agent") String userAgent,
                        @Schema(description = "执行耗时，单位毫秒") Long durationMs,
                        @Schema(description = "请求唯一标识") String requestId,
                        @Schema(description = "链路追踪标识") String traceId,
                        @Schema(description = "操作发生时间") LocalDateTime operationTime) {
    }

    /**
     * 执行耗时，单位毫秒。
     *
     * @param method      HTTP 请求方法
     * @param uri         HTTP 请求 URI
     * @param contentType 文件或请求的 MIME 类型
     * @param headers     脱敏后的白名单请求头
     * @param queryParams 请求 Query 参数
     * @param pathParams  请求路径参数
     * @param body        脱敏后的请求体；超长内容可能按服务端策略截断
     */
    @Schema(description = "操作日志请求信息")
    public record Request(@Schema(description = "HTTP 请求方法") String method,
                          @Schema(description = "HTTP 请求 URI") String uri,
                          @Schema(description = "文件或请求的 MIME 类型") String contentType,
                          @Schema(description = "脱敏后的白名单请求头") String headers,
                          @Schema(description = "请求 Query 参数") String queryParams,
                          @Schema(description = "请求路径参数") String pathParams,
                          @Schema(description = "脱敏后的请求体；超长内容可能按服务端策略截断") String body) {
    }

    /**
     * 脱敏后的白名单请求头。
     *
     * @param httpStatus   HTTP 响应状态码
     * @param businessCode 业务响应码
     * @param body         脱敏后的响应体；超长内容可能按服务端策略截断
     */
    @Schema(description = "操作日志响应信息")
    public record Response(@Schema(description = "HTTP 响应状态码") Integer httpStatus,
                           @Schema(description = "业务响应码") Integer businessCode,
                           @Schema(description = "脱敏后的响应体；超长内容可能按服务端策略截断") String body) {
    }

    /**
     * 操作日志响应信息。
     *
     * @param type      类型
     * @param errorCode 稳定业务错误码
     * @param message   提示或错误消息
     * @param location  异常位置
     * @param stack     异常堆栈
     */
    @Schema(description = "操作日志异常信息")
    public record ExceptionInfo(@Schema(description = "类型") String type,
                                @Schema(description = "稳定业务错误码") String errorCode,
                                @Schema(description = "提示或错误消息") String message,
                                @Schema(description = "异常位置") String location,
                                @Schema(description = "异常堆栈") String stack) {
    }
}
