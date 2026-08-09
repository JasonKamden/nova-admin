package org.dromara.nova.common.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 元信息。
 */
@Configuration
public class OpenApiConfiguration {
    /**
     * 创建 Nova Admin 的 Springdoc OpenAPI 基础元数据。
     *
     * @return OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Nova Admin API").version("1.0.0").contact(new Contact().name("kamden")));
    }
}
