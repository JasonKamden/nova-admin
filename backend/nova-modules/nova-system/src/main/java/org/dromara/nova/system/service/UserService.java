package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.*;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.dto.response.UserRespDto;

import java.util.List;

/**
 * 当前 Tenant 用户成员、状态、密码和角色关系维护业务契约。
 */
public interface UserService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<UserRespDto> page(UserPageReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    UserRespDto detail(Long userId);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    UserRespDto create(UserCreateReqDto request);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    UserRespDto update(Long userId, UserUpdateReqDto request);

    /**
     * 更新业务状态并刷新相关缓存或会话。
     */
    void updateStatus(Long userId, StatusUpdateReqDto request);

    /**
     * 管理员重置当前 Tenant 用户密码，并使受影响会话按安全策略失效。
     */
    void resetPassword(Long userId, PasswordResetReqDto request);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long userId);

    /**
     * 查询用户在当前 Tenant 已分配的角色。
     */
    List<RoleSimpleRespDto> roles(Long userId);

    /**
     * 全量替换用户角色并刷新权限缓存。
     */
    void replaceRoles(Long userId, UserRoleUpdateReqDto request);
}
