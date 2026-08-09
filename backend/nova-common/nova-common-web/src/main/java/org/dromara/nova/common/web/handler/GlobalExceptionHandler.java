package org.dromara.nova.common.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import jakarta.validation.ConstraintViolationException;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常转换。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常并转换为统一响应。
     *
     * @param e 异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> business(BusinessException e) {
        return R.fail(e.getResultCode(), e.getMessage());
    }

    /**
     * 处理未登录异常并转换为统一未认证响应。
     *
     * @param e 异常对象
     * @return 统一未认证响应
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> notLogin(NotLoginException e) {
        return R.fail(CommonResultCode.UNAUTHORIZED);
    }

    /**
     * 处理权限不足异常并转换为统一禁止访问响应。
     *
     * @param e 异常对象
     * @return 统一禁止访问响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> forbidden(NotPermissionException e) {
        return R.fail(CommonResultCode.FORBIDDEN);
    }

    /**
     * 处理请求参数校验异常并返回可读提示。
     *
     * @param e 异常对象
     * @return 统一参数错误响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public R<Void> badRequest(Exception e) {
        return R.fail(CommonResultCode.BAD_REQUEST, firstValidationMessage(e));
    }

    /**
     * 记录未处理系统异常并返回统一系统错误响应。
     *
     * @param e 异常对象
     * @return 统一系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public R<Void> system(Exception e) {
        log.error("Unhandled exception", e);
        return R.fail(CommonResultCode.INTERNAL_ERROR);
    }

    /**
     * 从 Spring Validation 异常中提取首个可读校验信息。
     *
     * @param e 异常对象
     * @return 首个参数校验提示
     */
    private String firstValidationMessage(Exception e) {
        if (e instanceof MethodArgumentNotValidException ex && ex.getBindingResult().getFieldError() != null)
            return ex.getBindingResult().getFieldError().getDefaultMessage();
        if (e instanceof BindException ex && ex.getBindingResult().getFieldError() != null)
            return ex.getBindingResult().getFieldError().getDefaultMessage();
        return e.getMessage() == null ? CommonResultCode.BAD_REQUEST.getMessage() : e.getMessage();
    }
}
