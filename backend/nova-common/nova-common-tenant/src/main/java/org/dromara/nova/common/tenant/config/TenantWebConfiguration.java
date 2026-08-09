package org.dromara.nova.common.tenant.config;

import org.dromara.nova.common.tenant.web.TenantRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 Tenant Context 请求拦截器。
 */
@Configuration
public class TenantWebConfiguration implements WebMvcConfigurer {
    private final TenantRequestInterceptor tenantRequestInterceptor;

    public TenantWebConfiguration(TenantRequestInterceptor tenantRequestInterceptor) {
        this.tenantRequestInterceptor = tenantRequestInterceptor;
    }

    /**
     * 向 Spring MVC 注册当前模块的请求拦截器。
     *
     * @param registry Spring MVC 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantRequestInterceptor).addPathPatterns("/api/**");
    }
}
