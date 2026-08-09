package org.dromara.nova.common.log.model;

import java.time.LocalDateTime;

/**
 * 模块无关的操作审计事件。
 *
 * @param module            操作所属业务模块
 * @param type              类型
 * @param description       说明
 * @param userId            用户 ID
 * @param username          登录账号
 * @param contextType       运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId          Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param departmentId      Department ID
 * @param requestMethod     HTTP 请求方法
 * @param requestUri        请求 URI
 * @param requestIp         请求客户端 IP
 * @param userAgent         客户端 User-Agent
 * @param contentType       MIME 类型
 * @param requestHeaders    脱敏后的白名单请求头
 * @param queryParams       请求 Query 参数
 * @param pathParams        请求路径参数
 * @param requestBody       脱敏后的请求体
 * @param httpStatus        HTTP 响应状态码
 * @param businessCode      业务响应码
 * @param responseBody      脱敏后的响应体
 * @param exceptionType     异常 Java 类型
 * @param errorCode         稳定业务错误码
 * @param errorMessage      异常信息
 * @param exceptionLocation 异常首个堆栈位置
 * @param exceptionStack    异常堆栈，按日志策略截断
 * @param durationMs        执行耗时，单位毫秒
 * @param requestId         请求唯一标识
 * @param traceId           链路追踪标识
 * @param operationTime     操作发生时间
 */
public record OperationAuditEvent(
        String module, String type, String description, Long userId, String username,
        String contextType, Long tenantId, Long departmentId, String requestMethod,
        String requestUri, String requestIp, String userAgent, String contentType,
        String requestHeaders, String queryParams, String pathParams, String requestBody, Integer httpStatus,
        Integer businessCode, String responseBody, String exceptionType, String errorCode,
        String errorMessage, String exceptionLocation, String exceptionStack,
        long durationMs, String requestId, String traceId, LocalDateTime operationTime
) {
}
