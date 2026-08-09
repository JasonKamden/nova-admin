package org.dromara.nova.system.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    /**
     * 允许本地前端开发服务直连后端，以便浏览器原生消费 SSE 长连接。
     *
     * @param registry Spring MVC CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://127.0.0.1:*", "http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
