package org.dromara.nova.common.cache.model;

/**
 * 前端缓存管理页可见的逻辑缓存定义。
 *
 * @param code              编码
 * @param name              名称
 * @param type              类型
 * @param module            操作所属业务模块
 * @param scope             作用域
 * @param defaultTtlSeconds 默认缓存 TTL，单位秒
 * @param clearable         是否允许清理缓存
 * @param refreshable       是否支持刷新缓存
 * @param description       说明
 */
public record CacheDefinition(String code, String name, String type, String module, String scope,
                              long defaultTtlSeconds, boolean clearable, boolean refreshable, String description) {
}
