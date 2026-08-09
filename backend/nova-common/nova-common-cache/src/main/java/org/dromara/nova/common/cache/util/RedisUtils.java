package org.dromara.nova.common.cache.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Redis 共享状态窄职责封装；扫描使用 SCAN，禁止 KEYS *。
 */
@Component
public class RedisUtils {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 写入指定上下文或缓存值。
     *
     * @param key   Redis Key
     * @param value 待存储或处理的值
     * @param ttl   缓存过期时间
     */
    public void set(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 读取当前上下文、缓存或注册项。
     *
     * @param key Redis Key
     * @return 查询结果
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 原子读取并删除指定 Redis Key。
     *
     * @param key Redis Key
     * @return 删除前的值
     */
    public String getAndDelete(String key) {
        return stringRedisTemplate.opsForValue().getAndDelete(key);
    }

    /**
     * 删除指定存储路径对应的物理对象。
     *
     * @param key Redis Key
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 对指定 Redis Key 执行原子自增。
     *
     * @param key Redis Key
     * @return 自增后的值
     */
    public long increment(String key) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        return value == null ? 0L : value;
    }

    /**
     * 设置指定 Redis Key 的过期时间。
     *
     * @param key Redis Key
     * @param ttl 缓存过期时间
     * @return 是否设置成功
     */
    public boolean expire(String key, Duration ttl) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, ttl));
    }

    /**
     * 查询指定 Redis Key 的剩余 TTL。
     *
     * @param key Redis Key
     * @return 剩余 TTL
     */
    public Duration ttl(String key) {
        Long seconds = stringRedisTemplate.getExpire(key);
        return seconds == null || seconds < 0 ? Duration.ZERO : Duration.ofSeconds(seconds);
    }

    /**
     * 使用 Redis SCAN 按前缀有限扫描 Key，避免使用 KEYS *。
     *
     * @param prefix Redis Key 前缀
     * @param limit  最大扫描数量
     * @return 扫描到的 Key 集合
     */
    public Set<String> scanKeys(String prefix, int limit) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        var factory = stringRedisTemplate.getConnectionFactory();
        if (factory == null) return keys;
        try (var connection = factory.getConnection()) {
            var options = org.springframework.data.redis.core.ScanOptions.scanOptions().match(prefix + "*").count(Math.min(Math.max(limit, 10), 1000)).build();
            try (var cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext() && keys.size() < limit)
                    keys.add(new String(cursor.next(), java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        return keys;
    }

    /**
     * 删除指定前缀匹配到的 Redis Key。
     *
     * @param prefix Redis Key 前缀
     * @return 删除的 Key 数量
     */
    public long deleteByPrefix(String prefix) {
        Set<String> keys = scanKeys(prefix, 100000);
        return keys.isEmpty() ? 0 : Optional.ofNullable(stringRedisTemplate.delete(keys)).orElse(0L);
    }

    /**
     * 检测当前 Redis 连接是否可用。
     *
     * @return Redis PING 结果
     */
    public String ping() {
        var factory = stringRedisTemplate.getConnectionFactory();
        if (factory == null) return null;
        try (var c = factory.getConnection()) {
            return c.ping();
        }
    }
}
