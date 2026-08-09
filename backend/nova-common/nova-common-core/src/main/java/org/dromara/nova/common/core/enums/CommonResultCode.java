package org.dromara.nova.common.core.enums;

/**
 * 全局稳定业务错误码。
 */
public enum CommonResultCode {
    SUCCESS(0, "操作成功"),
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未登录或登录已失效"),
    FORBIDDEN(40300, "无权限执行该操作"),
    NOT_FOUND(40400, "资源不存在"),
    CONFLICT(40900, "资源状态冲突"),
    DUPLICATE(40901, "数据已存在"),
    TENANT_REQUIRED(42001, "当前操作需要租户上下文"),
    TENANT_UNAVAILABLE(42002, "当前租户不可用"),
    FILE_ERROR(43001, "文件处理失败"),
    MESSAGE_ERROR(44001, "消息处理失败"),
    TOO_MANY_REQUESTS(42900, "请求过于频繁"),
    INTERNAL_ERROR(50000, "系统内部错误");
    private final int code;
    private final String message;

    CommonResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 返回枚举对应的稳定编码。
     *
     * @return 稳定编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 返回结果码默认提示信息。
     *
     * @return 默认提示
     */
    public String getMessage() {
        return message;
    }
}
