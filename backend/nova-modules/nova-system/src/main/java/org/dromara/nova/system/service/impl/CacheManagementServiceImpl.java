package org.dromara.nova.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.cache.model.CacheDefinition;
import org.dromara.nova.common.cache.service.LogicalCacheRegistry;
import org.dromara.nova.common.cache.util.RedisUtils;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.system.dto.request.CacheBatchReqDto;
import org.dromara.nova.system.dto.response.CacheRespDto;
import org.dromara.nova.system.service.CacheManagementService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 逻辑业务缓存管理，不提供 Redis 任意 Key/Value 操作。
 */
@Service
@RequiredArgsConstructor
public class CacheManagementServiceImpl implements CacheManagementService {
    private final LogicalCacheRegistry logicalCacheRegistry;
    private final CacheManager cacheManager;
    private final RedisUtils redisUtils;

    /**
     * 查询当前权限范围内的业务数据列表。
     *
     * @param name   名称
     * @param type   类型
     * @param module 操作所属业务模块
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<CacheRespDto> list(String name, String type, String module) {
        return logicalCacheRegistry.list().stream().filter(d -> name == null || name.isBlank() || d.name().contains(name)).filter(d -> type == null || type.isBlank() || d.type().equalsIgnoreCase(type)).filter(d -> module == null || module.isBlank() || d.module().equalsIgnoreCase(module)).map(this::resp).toList();
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param code 编码
     * @return 业务响应 DTO。
     */
    @Override
    public CacheRespDto detail(String code) {
        return resp(require(code));
    }

    /**
     * 清理指定逻辑业务缓存。
     *
     * @param code 编码
     */
    @Override
    @OperationAudit(module = "CACHE", type = "CLEAR", description = "清理逻辑缓存")
    public void clear(String code) {
        CacheDefinition d = require(code);
        if (!d.clearable()) throw new BusinessException(CommonResultCode.FORBIDDEN, "该缓存不允许手工清理");
        var cache = cacheManager.getCache(cacheName(code));
        if (cache != null) cache.clear();
        if ("REDIS".equalsIgnoreCase(d.type())) redisUtils.deleteByPrefix("cache:" + code + ":");
    }

    /**
     * 刷新或重建指定逻辑业务缓存。
     *
     * @param code 编码
     */
    @Override
    @OperationAudit(module = "CACHE", type = "REFRESH", description = "刷新逻辑缓存")
    public void refresh(String code) {
        CacheDefinition d = require(code);
        if (!d.refreshable()) throw new BusinessException(CommonResultCode.FORBIDDEN, "该缓存不支持刷新");
        clear(code);
    }

    /**
     * 批量清理逻辑业务缓存。
     *
     * @param req CacheBatchReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @OperationAudit(module = "CACHE", type = "BATCH_CLEAR", description = "批量清理逻辑缓存")
    public void batchClear(CacheBatchReqDto req) {
        req.cacheCodes().stream().distinct().forEach(this::clear);
    }

    /**
     * 检查 Redis 连接状态。
     *
     * @return 方法处理结果。
     */
    @Override
    public String redisStatus() {
        try {
            return "PONG".equalsIgnoreCase(redisUtils.ping()) ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param code 编码
     * @return 方法处理结果。
     */
    private CacheDefinition require(String code) {
        CacheDefinition d = logicalCacheRegistry.get(code);
        if (d == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "逻辑缓存不存在");
        return d;
    }

    /**
     * 将逻辑缓存编码解析为 Spring Cache 缓存区域名称。
     *
     * @param code 编码
     * @return 方法处理结果。
     */
    private String cacheName(String code) {
        return switch (code) {
            case "system.dictionary" -> "dictionary";
            case "system.config" -> "config";
            case "system.menu" -> "menu";
            case "security.rolePermission" -> "rolePermission";
            case "security.userPermission" -> "userPermission";
            case "system.departmentTree" -> "departmentTree";
            default -> code;
        };
    }

    /**
     * 将实体转换为对外响应 DTO。
     *
     * @param d 逻辑缓存定义
     * @return 业务响应 DTO。
     */
    private CacheRespDto resp(CacheDefinition d) {
        return new CacheRespDto(d.code(), d.name(), d.type(), d.module(), d.scope(), d.defaultTtlSeconds(), d.clearable(), d.refreshable(), d.description(), "UP");
    }
}
