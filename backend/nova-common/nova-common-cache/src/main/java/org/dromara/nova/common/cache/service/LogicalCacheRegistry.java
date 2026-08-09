package org.dromara.nova.common.cache.service;

import org.dromara.nova.common.cache.model.CacheDefinition;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 只暴露受控业务缓存，不暴露原始 Redis Key/Value。
 */
@Component
public class LogicalCacheRegistry {
    private final Map<String, CacheDefinition> definitions = new LinkedHashMap<>();

    public LogicalCacheRegistry() {
        register(new CacheDefinition("system.dictionary", "系统字典", "CAFFEINE", "system", "TENANT", 900, true, true, "字典查询热点缓存"));
        register(new CacheDefinition("system.config", "系统参数", "CAFFEINE", "system", "TENANT", 900, true, true, "参数配置缓存"));
        register(new CacheDefinition("system.menu", "菜单", "CAFFEINE", "system", "TENANT", 900, true, true, "菜单树缓存"));
        register(new CacheDefinition("security.rolePermission", "角色权限", "CAFFEINE", "security", "TENANT", 900, true, true, "角色菜单权限缓存"));
        register(new CacheDefinition("security.userPermission", "用户权限", "CAFFEINE", "security", "USER", 900, true, true, "用户权限缓存"));
        register(new CacheDefinition("system.departmentTree", "Department 树", "CAFFEINE", "system", "TENANT", 900, true, true, "Department 树缓存"));
    }

    /**
     * 注册一项逻辑业务缓存定义。
     *
     * @param definition 逻辑缓存定义
     */
    public void register(CacheDefinition definition) {
        definitions.put(definition.code(), definition);
    }

    /**
     * 返回全部已注册的逻辑业务缓存定义。
     *
     * @return 逻辑缓存定义列表
     */
    public List<CacheDefinition> list() {
        return List.copyOf(definitions.values());
    }

    /**
     * 读取当前上下文、缓存或注册项。
     *
     * @param code 稳定编码或结果码
     * @return 查询结果
     */
    public CacheDefinition get(String code) {
        return definitions.get(code);
    }
}
