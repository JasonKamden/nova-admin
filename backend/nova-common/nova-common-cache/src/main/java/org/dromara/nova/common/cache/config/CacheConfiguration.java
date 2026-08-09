package org.dromara.nova.common.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Spring Cache 本地热点缓存。
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
    /**
     * 创建项目统一的 Caffeine Spring CacheManager。
     *
     * @return Spring CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("dictionary", "config", "menu", "rolePermission", "userPermission", "departmentTree");
        manager.setCaffeine(Caffeine.newBuilder().maximumSize(5000).expireAfterWrite(Duration.ofMinutes(15)));
        return manager;
    }
}
