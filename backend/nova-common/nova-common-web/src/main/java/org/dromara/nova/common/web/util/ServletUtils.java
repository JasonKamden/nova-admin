package org.dromara.nova.common.web.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前 Servlet 请求读取工具。
 */
public final class ServletUtils {
    private ServletUtils() {
    }

    /**
     * 获取当前线程绑定的 HttpServletRequest；非 Web 线程返回 null。
     *
     * @return 当前请求或 null
     */
    public static HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        return attrs instanceof ServletRequestAttributes servlet ? servlet.getRequest() : null;
    }

    /**
     * 读取当前 HTTP 请求的 User-Agent。
     *
     * @return User-Agent
     */
    public static String userAgent() {
        var request = currentRequest();
        return request == null ? null : request.getHeader("User-Agent");
    }

    /**
     * 读取当前请求的 requestId。
     *
     * @return 请求 ID
     */
    public static String requestId() {
        var request = currentRequest();
        Object value = request == null ? null : request.getAttribute("requestId");
        return value == null ? null : value.toString();
    }
}
