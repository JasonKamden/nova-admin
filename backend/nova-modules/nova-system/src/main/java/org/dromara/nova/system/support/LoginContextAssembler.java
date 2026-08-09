package org.dromara.nova.system.support;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.ContextType;
import org.dromara.nova.common.core.enums.Status;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.system.entity.*;
import org.dromara.nova.system.mapper.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.MenuEntityTableDef.MENU_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.RoleMenuEntityTableDef.ROLE_MENU_ENTITY;
import static org.dromara.nova.system.entity.table.TenantEntityTableDef.TENANT_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * 登录或 Context 切换后重新装载角色、菜单、按钮权限和 Department。
 */
@Component
@RequiredArgsConstructor
public class LoginContextAssembler {
    private final TenantMapper tenantMapper;
    private final UserTenantMapper userTenantMapper;
    private final DepartmentMapper departmentMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    /**
     * 切换到 PLATFORM Context。
     *
     * @param user 用户名或昵称查询条件
     * @return 方法处理结果。
     */
    public CurrentLoginUser platform(UserEntity user) {
        return new CurrentLoginUser(user.getId(), user.getUsername(), user.getNickname(), avatar(user), true, ContextType.PLATFORM, null, null, null, null, List.of("platform_admin"), List.of("*"));
    }

    /**
     * 切换到指定 Tenant Context。
     *
     * @param user     用户名或昵称查询条件
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @return 方法处理结果。
     */
    public CurrentLoginUser tenant(UserEntity user, Long tenantId) {
        TenantEntity tenant = tenantMapper.selectOneByQuery(QueryWrapper.create().where(TENANT_ENTITY.ID.eq(tenantId)).and(TENANT_ENTITY.DELETED.eq(false)));
        if (tenant == null || !Status.enabled(tenant.getStatus()) || (tenant.getExpireAt() != null && tenant.getExpireAt().isBefore(LocalDate.now())))
            throw new BusinessException(CommonResultCode.TENANT_UNAVAILABLE);
        UserTenantEntity membership = userTenantMapper.selectOneByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.USER_ID.eq(user.getId())).and(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.STATUS.eq(1)));
        if (membership == null && !Boolean.TRUE.equals(user.getPlatformAdmin()))
            throw new BusinessException(CommonResultCode.FORBIDDEN, "当前用户无权访问该 Tenant");
        Long departmentId = membership == null ? null : membership.getDepartmentId();
        String departmentName = null;
        if (departmentId != null) {
            DepartmentEntity d = departmentMapper.selectOneByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(departmentId)).and(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
            departmentName = d == null ? null : d.getDepartmentName();
        }
        if (Boolean.TRUE.equals(user.getPlatformAdmin()))
            return new CurrentLoginUser(user.getId(), user.getUsername(), user.getNickname(), avatar(user), true, ContextType.TENANT, tenantId, tenant.getTenantName(), departmentId, departmentName, List.of("platform_admin"), List.of("*"));
        List<UserRoleEntity> links = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.eq(user.getId())));
        Set<Long> roleIds = new HashSet<>();
        links.forEach(x -> roleIds.add(x.getRoleId()));
        List<RoleEntity> roles = roleIds.isEmpty() ? List.of() : roleMapper.selectListByQuery(QueryWrapper.create().where(ROLE_ENTITY.ID.in(roleIds)).and(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.STATUS.eq(1)).and(ROLE_ENTITY.DELETED.eq(false)));
        List<String> roleCodes = roles.stream().map(RoleEntity::getRoleCode).toList();
        Set<Long> enabledRoleIds = new HashSet<>();
        roles.forEach(r -> enabledRoleIds.add(r.getId()));
        Set<Long> menuIds = new HashSet<>();
        if (!enabledRoleIds.isEmpty())
            roleMenuMapper.selectListByQuery(QueryWrapper.create().where(ROLE_MENU_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_MENU_ENTITY.ROLE_ID.in(enabledRoleIds))).forEach(x -> menuIds.add(x.getMenuId()));
        List<String> permissions = menuIds.isEmpty() ? List.of() : menuMapper.selectListByQuery(QueryWrapper.create().where(MENU_ENTITY.ID.in(menuIds)).and(MENU_ENTITY.STATUS.eq(1)).and(MENU_ENTITY.DELETED.eq(false))).stream().map(MenuEntity::getPermissionCode).filter(Objects::nonNull).filter(s -> !s.isBlank()).distinct().toList();
        return new CurrentLoginUser(user.getId(), user.getUsername(), user.getNickname(), avatar(user), false, ContextType.TENANT, tenantId, tenant.getTenantName(), departmentId, departmentName, roleCodes, permissions);
    }

    /**
     * 根据用户头像存储状态生成对外头像访问地址。
     *
     * @param user 用户名或昵称查询条件
     * @return 方法处理结果。
     */
    private String avatar(UserEntity user) {
        return user.getAvatar() == null || user.getAvatar().isBlank() ? null : "/api/profile/avatar";
    }

    /**
     * 查询指定用户当前有效的 Tenant 成员关系。
     *
     * @param userId 用户 ID
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    public List<UserTenantEntity> memberships(Long userId) {
        return userTenantMapper.selectListByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.USER_ID.eq(userId)).and(USER_TENANT_ENTITY.STATUS.eq(1)).orderBy(USER_TENANT_ENTITY.ID.asc()));
    }
}
