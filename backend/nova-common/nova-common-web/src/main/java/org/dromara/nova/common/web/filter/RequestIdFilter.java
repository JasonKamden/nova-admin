package org.dromara.nova.common.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 为每次 HTTP 请求绑定 requestId。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {
    public static final String REQUEST_ID = "requestId";
    public static final String TRACE_ID = "traceId";

    /**
     * 为 HTTP 请求建立 requestId/traceId，并写入 MDC 与响应头。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    Servlet 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        String traceId = traceId(request.getHeader("traceparent"), request.getHeader("X-Trace-Id"), requestId);
        MDC.put(REQUEST_ID, requestId);
        MDC.put(TRACE_ID, traceId);
        request.setAttribute(REQUEST_ID, requestId);
        response.setHeader("X-Request-Id", requestId);
        response.setHeader("X-Trace-Id", traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(TRACE_ID);
        }
    }

    /**
     * 从标准 traceparent、Trace Header 或兜底值中解析最终 traceId。
     *
     * @param traceparent   W3C traceparent 请求头
     * @param headerTraceId 业务 Trace ID 请求头
     * @param fallback      兜底 traceId
     * @return 最终 traceId
     */
    private String traceId(String traceparent, String headerTraceId, String fallback) {
        if (headerTraceId != null && headerTraceId.matches("[0-9a-fA-F]{16,64}")) return headerTraceId.toLowerCase();
        if (traceparent != null) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 4 && parts[1].matches("[0-9a-fA-F]{32}")) return parts[1].toLowerCase();
        }
        return fallback;
    }
}
