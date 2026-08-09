package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.request.MenuCreateReqDto;
import org.dromara.nova.system.dto.request.MenuUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.MenuRespDto;

import java.util.List;

/**
 * 全局菜单定义维护及当前用户动态菜单查询业务契约。
 */
public interface MenuService {
    /**
     * 返回符合当前权限范围的树形数据。
     */
    List<MenuRespDto> tree(String keyword);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    MenuRespDto detail(Long menuId);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    MenuRespDto create(MenuCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    MenuRespDto update(Long menuId, MenuUpdateReqDto request);

    /**
     * 更新业务状态并刷新相关缓存或会话。
     */
    void updateStatus(Long menuId, StatusUpdateReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long menuId);

    /**
     * 返回当前登录用户在当前 Context 下可访问的动态菜单树。
     */
    List<MenuRespDto> currentUserTree();
}
