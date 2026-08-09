package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.*;
import org.dromara.nova.system.dto.response.RoleRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;

import java.util.List;

/**
 * 当前 Tenant 角色、DataScope 和菜单授权业务契约。
 */
public interface RoleService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<RoleRespDto> page(RolePageReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    RoleRespDto detail(Long roleId);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    RoleRespDto create(RoleCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    RoleRespDto update(Long roleId, RoleUpdateReqDto request);

    /**
     * 更新业务状态并刷新相关缓存或会话。
     */
    void updateStatus(Long roleId, StatusUpdateReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long roleId);

    /**
     * 查询角色已授权菜单 ID。
     */
    List<Long> menuIds(Long roleId);

    /**
     * 全量替换角色菜单授权并刷新受影响用户权限。
     */
    void replaceMenus(Long roleId, RoleMenuUpdateReqDto request);

    /**
     * 返回当前用户可切换的 Context。
     */
    List<RoleSimpleRespDto> options(String keyword);
}
