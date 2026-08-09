package org.dromara.nova.common.tenant.web;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.nova.common.core.enums.ContextType;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContext;
import org.dromara.nova.common.tenant.context.TenantContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 将 Token Session 中可信 Context 绑定到当前请求线程。
 */
@Component
public class TenantRequestInterceptor implements HandlerInterceptor {
    /**
     * 在请求进入业务 Controller 前解析并绑定可信 Tenant Context。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  Spring MVC 处理器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!StpUtil.isLogin()) {
            TenantContextHolder.set(new TenantContext(ContextType.PLATFORM, null));
            return true;
        }
        var user = LoginUserUtils.getLoginUser();
        TenantContextHolder.set(new TenantContext(user.contextType(), user.tenantId()));
        return true;
    }

    /**
     * 请求完成后清理线程级 Tenant Context，避免线程复用导致上下文泄漏。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  Spring MVC 处理器
     * @param ex       请求处理异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}
