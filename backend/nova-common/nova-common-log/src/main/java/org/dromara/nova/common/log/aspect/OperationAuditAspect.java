package org.dromara.nova.common.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.log.model.OperationAuditEvent;
import org.dromara.nova.common.log.service.OperationAuditSink;
import org.dromara.nova.common.log.support.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 环绕核心操作，记录脱敏请求、响应、异常与耗时。
 */
@Aspect
@Component
public class OperationAuditAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationAuditAspect.class);
    private static final List<String> HEADER_WHITELIST = List.of(
            "Content-Type", "User-Agent", "Accept-Language", "X-Request-Id", "traceparent"
    );

    private final ObjectMapper objectMapper;
    private final ObjectProvider<OperationAuditSink> sinkProvider;

    public OperationAuditAspect(ObjectMapper objectMapper, ObjectProvider<OperationAuditSink> sinkProvider) {
        this.objectMapper = objectMapper;
        this.sinkProvider = sinkProvider;
    }

    /**
     * 环绕执行标记 OperationAudit 的业务方法并收集结果、异常和耗时。
     *
     * @param joinPoint AOP 连接点
     * @param audit     操作审计注解元数据
     * @return 原业务方法返回结果
     */
    @Around("@annotation(audit)")
    public Object around(ProceedingJoinPoint joinPoint, OperationAudit audit) throws Throwable {
        long start = System.nanoTime();
        Actor actor = actor();
        Throwable error = null;
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            error = throwable;
            throw throwable;
        } finally {
            publish(joinPoint, audit, result, error, (System.nanoTime() - start) / 1_000_000L, actor);
        }
    }

    /**
     * 构造并发布一次脱敏后的 OperationAuditEvent。
     *
     * @param point    AOP 连接点
     * @param audit    操作审计注解元数据
     * @param result   业务方法返回结果
     * @param error    业务方法抛出的异常
     * @param duration 执行耗时，单位毫秒
     * @param actor    操作人审计快照
     */
    private void publish(ProceedingJoinPoint point, OperationAudit audit, Object result, Throwable error, long duration, Actor actor) {
        if (error == null) {
            log.info("核心业务操作完成 module={} type={} description={} userId={} contextType={} tenantId={} requestId={} traceId={} durationMs={}",
                    audit.module(), audit.type(), audit.description(), actor.userId(), actor.contextType(), actor.tenantId(),
                    MDC.get("requestId"), MDC.get("traceId"), duration);
        } else {
            log.warn("核心业务操作失败 module={} type={} description={} userId={} contextType={} tenantId={} requestId={} traceId={} durationMs={} exceptionType={}",
                    audit.module(), audit.type(), audit.description(), actor.userId(), actor.contextType(), actor.tenantId(),
                    MDC.get("requestId"), MDC.get("traceId"), duration, error.getClass().getName());
        }
        OperationAuditSink sink = sinkProvider.getIfAvailable();
        if (sink == null) {
            return;
        }
        LogSanitizer sanitizer = new LogSanitizer(objectMapper, 131072);
        ServletRequestAttributes attributes = servletAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();
        HttpServletResponse response = attributes == null ? null : attributes.getResponse();

        String stack = null;
        String location = null;
        if (error != null) {
            StringWriter writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            stack = sanitizer.truncate(writer.toString());
            if (error.getStackTrace().length > 0) {
                location = error.getStackTrace()[0].toString();
            }
        }

        Integer businessCode = result instanceof org.dromara.nova.common.core.model.R<?> r ? r.code() : error instanceof org.dromara.nova.common.core.exception.BusinessException be ? be.getResultCode().getCode() : error == null ? org.dromara.nova.common.core.enums.CommonResultCode.SUCCESS.getCode() : org.dromara.nova.common.core.enums.CommonResultCode.INTERNAL_ERROR.getCode();
        sink.save(new OperationAuditEvent(
                audit.module(), audit.type(), audit.description(), actor.userId(), actor.username(), actor.contextType(), actor.tenantId(), actor.departmentId(),
                request == null ? null : request.getMethod(),
                request == null ? null : request.getRequestURI(),
                request == null ? null : org.dromara.nova.common.web.util.IpUtils.getClientIp(request),
                request == null ? null : request.getHeader("User-Agent"),
                request == null ? null : request.getContentType(),
                sanitizer.json(headers(request)),
                sanitizer.json(request == null ? null : request.getParameterMap()),
                sanitizer.json(pathVariables(request)),
                sanitizer.json(safeArguments(point.getArgs())),
                response == null ? null : response.getStatus(),
                businessCode,
                sanitizer.json(result),
                error == null ? null : error.getClass().getName(),
                error instanceof org.dromara.nova.common.core.exception.BusinessException be ? be.getResultCode().name() : null,
                error == null ? null : sanitizer.truncate(error.getMessage()),
                location, stack, duration,
                request == null ? null : String.valueOf(request.getAttribute("requestId")),
                MDC.get("traceId"),
                LocalDateTime.now()
        ));
    }

    /**
     * 提取允许写入操作日志的请求头白名单。
     *
     * @param request HTTP 请求
     * @return 可审计请求头
     */
    private Map<String, String> headers(HttpServletRequest request) {
        if (request == null) {
            return Map.of();
        }
        Map<String, String> result = new LinkedHashMap<>();
        for (String name : HEADER_WHITELIST) {
            String value = request.getHeader(name);
            if (value != null && !value.isBlank()) {
                result.put(name, value);
            }
        }
        return result;
    }

    /**
     * 提取 Spring MVC 已解析的 URI 路径变量。
     *
     * @param request HTTP 请求
     * @return 路径变量
     */
    @SuppressWarnings("unchecked")
    private Map<String, ?> pathVariables(HttpServletRequest request) {
        if (request == null) {
            return Map.of();
        }
        Object value = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return value instanceof Map<?, ?> map ? (Map<String, ?>) map : Map.of();
    }

    /**
     * 过滤 Servlet、文件流等不适合持久化的调用参数。
     *
     * @param arguments 业务方法原始参数
     * @return 过滤后的审计参数
     */
    private List<Object> safeArguments(Object[] arguments) {
        List<Object> result = new ArrayList<>();
        if (arguments == null) {
            return result;
        }
        for (Object argument : arguments) {
            if (argument == null) {
                result.add(null);
            } else if (argument instanceof MultipartFile file) {
                result.add(Map.of(
                        "fileName", file.getOriginalFilename() == null ? "" : file.getOriginalFilename(),
                        "contentType", file.getContentType() == null ? "" : file.getContentType(),
                        "size", file.getSize()
                ));
            } else if (argument instanceof ServletRequest || argument instanceof ServletResponse
                    || argument instanceof InputStream || argument instanceof OutputStream) {
                // HTTP/stream framework arguments are represented elsewhere; never serialize bodies/binary streams.
            } else {
                result.add(argument);
            }
        }
        return result;
    }

    /**
     * 获取当前操作人的用户、Context、Tenant 和 Department 审计快照。
     *
     * @return 操作人快照
     */
    private Actor actor() {
        try {
            var user = org.dromara.nova.common.security.util.LoginUserUtils.getLoginUser();
            return new Actor(user.userId(), user.username(), user.contextType().name(), user.tenantId(), user.departmentId());
        } catch (Exception ignored) {
            return new Actor(null, null, null, null, null);
        }
    }

    /**
     * 获取当前 Web 请求的 ServletRequestAttributes。
     *
     * @return 当前请求属性或 null
     */
    private ServletRequestAttributes servletAttributes() {
        var attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servlet ? servlet : null;
    }

    /**
     * Actor 数据模型。
     *
     * @param userId       用户 ID
     * @param username     登录账号
     * @param contextType  运行上下文类型：PLATFORM 或 TENANT
     * @param tenantId     Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param departmentId Department ID
     */
    private record Actor(Long userId, String username, String contextType, Long tenantId, Long departmentId) {
    }
}
