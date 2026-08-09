package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.request.CacheBatchReqDto;
import org.dromara.nova.system.dto.response.CacheRespDto;

import java.util.List;

/**
 * 系统注册逻辑业务缓存查询、清理和刷新业务契约。
 */
public interface CacheManagementService {
    /**
     * 查询当前授权范围内的数据列表。
     */
    List<CacheRespDto> list(String name, String type, String module);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    CacheRespDto detail(String cacheCode);

    /**
     * 清理指定逻辑业务缓存。
     */
    void clear(String cacheCode);

    /**
     * 刷新指定逻辑业务缓存。
     */
    void refresh(String cacheCode);

    /**
     * 批量清理逻辑业务缓存。
     */
    void batchClear(CacheBatchReqDto request);

    /**
     * 检查 Redis 连通状态。
     */
    String redisStatus();
}
