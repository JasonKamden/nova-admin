package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.request.DepartmentCreateReqDto;
import org.dromara.nova.system.dto.request.DepartmentQueryReqDto;
import org.dromara.nova.system.dto.request.DepartmentUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.DepartmentRespDto;

import java.util.List;

/**
 * 当前 Tenant部门树维护业务契约。
 */
public interface DepartmentService {
    /**
     * 返回符合当前权限范围的树形数据。
     */
    List<DepartmentRespDto> tree(DepartmentQueryReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    DepartmentRespDto detail(Long id);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    DepartmentRespDto create(DepartmentCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    DepartmentRespDto update(Long id, DepartmentUpdateReqDto request);

    /**
     * 更新业务状态并刷新相关缓存或会话。
     */
    void updateStatus(Long id, StatusUpdateReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long id);
}
