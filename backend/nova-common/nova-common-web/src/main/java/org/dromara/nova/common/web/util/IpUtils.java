package org.dromara.nova.common.web.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 解析可信代理链后的客户端 IP；生产环境应同步配置可信反向代理。
 */
public final class IpUtils {
    private IpUtils() {
    }

    /**
     * 按照受信代理头优先级解析客户端 IP。
     *
     * @param request HTTP 请求
     * @return 客户端 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        for (String name : List.of("X-Forwarded-For", "X-Real-IP")) {
            String value = request.getHeader(name);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value))
                return value.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 解析当前 HTTP 请求的客户端 IP。
     *
     * @return 当前客户端 IP
     */
    public static String currentIp() {
        return getClientIp(ServletUtils.currentRequest());
    }
}
