package org.dromara.nova.common.core.enums;

/**
 * 通用启停状态。
 */
public enum Status {
    DISABLED(0), ENABLED(1);
    private final int code;

    Status(int code) {
        this.code = code;
    }

    /**
     * 判断状态值是否表示启用。
     *
     * @param value 待存储或处理的值
     * @return 是否启用
     */
    public static boolean enabled(Integer value) {
        return value != null && value == ENABLED.code;
    }

    /**
     * 返回枚举对应的稳定编码。
     *
     * @return 稳定编码
     */
    public int getCode() {
        return code;
    }
}
