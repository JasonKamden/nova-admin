package org.dromara.nova.common.core.exception;

import org.dromara.nova.common.core.enums.CommonResultCode;

/**
 * 资源不存在异常。
 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(CommonResultCode.NOT_FOUND, message);
    }
}
