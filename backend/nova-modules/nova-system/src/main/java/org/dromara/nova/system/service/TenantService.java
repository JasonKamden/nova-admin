package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.request.TenantCreateReqDto;
import org.dromara.nova.system.dto.request.TenantPageReqDto;
import org.dromara.nova.system.dto.request.TenantUpdateReqDto;
import org.dromara.nova.system.dto.response.ContextTenantOptionRespDto;
import org.dromara.nova.system.dto.response.TenantRespDto;

import java.util.List;

/**
 * PLATFORM Tenant 生命周期、初始化和 ContextSwitcher 选项业务契约。
 */
public interface TenantService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<TenantRespDto> page(TenantPageReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    TenantRespDto detail(Long tenantId);

    /**
     * 远程搜索可选项。
     */
    List<ContextTenantOptionRespDto> searchOptions(String keyword);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    TenantRespDto create(TenantCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    TenantRespDto update(Long tenantId, TenantUpdateReqDto request);

    /**
     * 更新业务状态并刷新相关缓存或会话。
     */
    void updateStatus(Long tenantId, StatusUpdateReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long tenantId);
}
