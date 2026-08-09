package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.ConfigCreateReqDto;
import org.dromara.nova.system.dto.request.ConfigUpdateReqDto;
import org.dromara.nova.system.dto.response.ConfigRespDto;

/**
 * 当前 Tenant 参数配置维护和缓存读取业务契约。
 */
public interface ConfigService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<ConfigRespDto> page(long pageNum, long pageSize, String keyword, String configType, Integer status);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    ConfigRespDto create(ConfigCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    ConfigRespDto update(Long id, ConfigUpdateReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long id);

    /**
     * 读取启用的参数值并使用业务缓存。
     */
    String getValue(String code);
}
