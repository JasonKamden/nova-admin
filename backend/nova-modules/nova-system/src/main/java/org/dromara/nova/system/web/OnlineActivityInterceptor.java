package org.dromara.nova.system.web;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.system.service.OnlineUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 更新在线用户最近活动时间。
 */
@Component
@RequiredArgsConstructor
public class OnlineActivityInterceptor implements HandlerInterceptor {
    private final OnlineUserService onlineUserService;

    /**
     * 在业务请求进入 Controller 前刷新当前在线会话最近活动时间。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应对象，用于输出文件或导出数据
     * @param handler  Spring MVC 处理器
     * @return 业务校验或处理结果。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (StpUtil.isLogin() && StpUtil.getTokenValue() != null)
            onlineUserService.activity(StpUtil.getTokenValue(), LoginUserUtils.getLoginUser());
        return true;
    }
}
