package org.dromara.nova.system.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 系统模块 Web 拦截器。
 */
@Configuration
@RequiredArgsConstructor
public class SystemWebConfiguration implements WebMvcConfigurer {
    private final OnlineActivityInterceptor onlineActivityInterceptor;

    /**
     * 向 Spring MVC 注册在线会话活动时间拦截器。
     *
     * @param registry Spring MVC 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(onlineActivityInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/auth/login", "/api/auth/captcha");
    }
}
