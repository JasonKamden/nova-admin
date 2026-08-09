package org.dromara.nova.common.core.model;

import org.dromara.nova.common.core.enums.CommonResultCode;
import org.slf4j.MDC;

import java.time.LocalDateTime;

/**
 * 统一接口响应；前端业务判断依赖稳定 {@code code}，不得依赖中文 {@code message}。
 *
 * @param code      稳定业务响应码，0 表示成功
 * @param message   面向用户/开发者的提示信息，不作为程序分支依据
 * @param data      业务响应数据
 * @param success   是否成功
 * @param timestamp 服务端响应时间
 * @param traceId   链路追踪标识
 * @param requestId 单次 HTTP 请求标识
 * @param <T>       业务响应类型
 */
public record R<T>(
        int code,
        String message,
        T data,
        boolean success,
        LocalDateTime timestamp,
        String traceId,
        String requestId
) {
    /**
     * 构造统一成功响应。
     *
     * @param data 业务响应数据
     * @return 统一成功响应
     */
    public static <T> R<T> ok(T data) {
        return build(CommonResultCode.SUCCESS, CommonResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 构造统一成功响应。
     *
     * @return 统一成功响应
     */
    public static R<Void> ok() {
        return ok(null);
    }

    /**
     * 构造统一失败响应。
     *
     * @param code 稳定编码或结果码
     * @return 统一失败响应
     */
    public static <T> R<T> fail(CommonResultCode code) {
        return build(code, code.getMessage(), null);
    }

    /**
     * 构造统一失败响应。
     *
     * @param code    稳定编码或结果码
     * @param message 提示信息
     * @return 统一失败响应
     */
    public static <T> R<T> fail(CommonResultCode code, String message) {
        return build(code, message, null);
    }

    /**
     * 根据结果码、提示和数据创建统一响应。
     *
     * @param code    稳定编码或结果码
     * @param message 提示信息
     * @param data    业务响应数据
     * @return 统一响应
     */
    private static <T> R<T> build(CommonResultCode code, String message, T data) {
        return new R<>(code.getCode(), message, data, code == CommonResultCode.SUCCESS, LocalDateTime.now(), MDC.get("traceId"), MDC.get("requestId"));
    }
}
