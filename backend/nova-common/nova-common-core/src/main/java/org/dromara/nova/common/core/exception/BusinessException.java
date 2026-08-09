package org.dromara.nova.common.core.exception;

import org.dromara.nova.common.core.enums.CommonResultCode;

/**
 * 可预期业务异常。
 */
public class BusinessException extends RuntimeException {
    private final CommonResultCode resultCode;

    public BusinessException(CommonResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(CommonResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 返回业务异常对应的稳定结果码。
     *
     * @return 业务结果码
     */
    public CommonResultCode getResultCode() {
        return resultCode;
    }
}
