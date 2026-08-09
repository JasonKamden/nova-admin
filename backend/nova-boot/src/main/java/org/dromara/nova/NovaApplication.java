package org.dromara.nova;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.nova.common.core.util.JsonUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Nova 后端统一启动入口。
 */
@SpringBootApplication
@ConfigurationPropertiesScan("org.dromara.nova")
@MapperScan({
        "org.dromara.nova.system.mapper",
        "org.dromara.nova.file.mapper",
        "org.dromara.nova.message.mapper"
})
@EnableScheduling
public class NovaApplication {
    /**
     * 启动 Nova Backend Spring Boot 应用。
     *
     * @param args JVM 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(NovaApplication.class, args);
    }

    /**
     * 将 Spring 管理的 ObjectMapper 注册到公共 JSON 基础设施，保证全项目序列化配置一致。
     *
     * @param objectMapper Spring Boot 自动配置的 ObjectMapper
     * @return 应用启动后的初始化任务
     */
    @Bean
    ApplicationRunner configureJson(ObjectMapper objectMapper) {
        return args -> JsonUtils.configure(objectMapper);
    }
}
