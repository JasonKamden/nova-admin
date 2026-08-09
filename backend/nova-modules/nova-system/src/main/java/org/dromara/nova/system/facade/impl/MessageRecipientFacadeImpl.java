package org.dromara.nova.system.facade.impl;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.entity.DepartmentEntity;
import org.dromara.nova.system.entity.UserEntity;
import org.dromara.nova.system.entity.UserRoleEntity;
import org.dromara.nova.system.entity.UserTenantEntity;
import org.dromara.nova.system.facade.MessageRecipientFacade;
import org.dromara.nova.system.mapper.*;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * 基于当前 Tenant + DataScope 解析消息收件人。
 */
@Component
@RequiredArgsConstructor
public class MessageRecipientFacadeImpl implements MessageRecipientFacade {
    private final UserTenantMapper userTenantMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final DataScopeService dataScopeService;

    /**
     * 查询当前 Tenant 全部有效消息接收用户。
     *
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RecipientUser> all() {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.STATUS.eq(1));
        applyScope(q, dataScopeService.current());
        return assemble(userTenantMapper.selectListByQuery(q));
    }

    /**
     * 根据部门范围解析有效消息接收用户。
     *
     * @param ids             业务主键 ID 集合
     * @param includeChildren 选择部门时是否包含下级 Department
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RecipientUser> departments(List<Long> ids, boolean includeChildren) {
        Long tenantId = TenantContextSupport.requireTenantId();
        Set<Long> target = new LinkedHashSet<>(ids == null ? List.of() : ids);
        for (Long id : new ArrayList<>(target)) {
            validateDepartment(tenantId, id);
            if (includeChildren) target.addAll(dataScopeService.descendants(tenantId, id));
        }
        DataScopeRule scope = dataScopeService.current();
        if (!scope.allTenant() && !scope.departmentIds().containsAll(target))
            throw new BusinessException(CommonResultCode.FORBIDDEN, "包含超出 DataScope 的部门");
        if (target.isEmpty()) return List.of();
        return assemble(userTenantMapper.selectListByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(target)).and(USER_TENANT_ENTITY.STATUS.eq(1))));
    }

    /**
     * 查询指定用户在当前 Tenant 已分配的角色。
     *
     * @param roleIds 角色 ID 集合，全量替换提交
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RecipientUser> roles(List<Long> roleIds) {
        Long tenantId = TenantContextSupport.requireTenantId();
        Set<Long> roles = new LinkedHashSet<>(roleIds == null ? List.of() : roleIds);
        if (roles.isEmpty()) return List.of();
        long valid = roleMapper.selectCountByQuery(QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.ID.in(roles)).and(ROLE_ENTITY.STATUS.eq(1)).and(ROLE_ENTITY.DELETED.eq(false)));
        if (valid != roles.size())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "包含无效或跨 Tenant Role");
        Set<Long> userIds = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.ROLE_ID.in(roles))).stream().map(UserRoleEntity::getUserId).collect(Collectors.toSet());
        return users(new ArrayList<>(userIds));
    }

    /**
     * 根据指定用户 ID 集合解析有效消息接收用户。
     *
     * @param ids 业务主键 ID 集合
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RecipientUser> users(List<Long> ids) {
        Long tenantId = TenantContextSupport.requireTenantId();
        Set<Long> target = new LinkedHashSet<>(ids == null ? List.of() : ids);
        if (target.isEmpty()) return List.of();
        QueryWrapper q = QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.USER_ID.in(target)).and(USER_TENANT_ENTITY.STATUS.eq(1));
        applyScope(q, dataScopeService.current());
        List<UserTenantEntity> found = userTenantMapper.selectListByQuery(q);
        if (found.size() != target.size())
            throw new BusinessException(CommonResultCode.FORBIDDEN, "包含无效或无权选择的用户");
        return assemble(found);
    }

    /**
     * 将当前 DataScope 规则应用到查询条件。
     *
     * @param q MyBatis-Flex 查询条件
     * @param r 当前用户 DataScope 规则
     */
    private void applyScope(QueryWrapper q, DataScopeRule r) {
        if (r.allTenant()) return;
        if (!r.departmentIds().isEmpty() && r.selfUserId() != null)
            q.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(r.departmentIds()).or(USER_TENANT_ENTITY.USER_ID.eq(r.selfUserId())));
        else if (!r.departmentIds().isEmpty()) q.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(r.departmentIds()));
        else if (r.selfUserId() != null) q.and(USER_TENANT_ENTITY.USER_ID.eq(r.selfUserId()));
        else q.and(USER_TENANT_ENTITY.ID.eq(-1));
    }

    /**
     * 聚合用户、成员关系、Department 和角色数据形成用户响应。
     *
     * @param memberships Tenant 成员关系列表
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<RecipientUser> assemble(List<UserTenantEntity> memberships) {
        Set<Long> userIds = memberships.stream().map(UserTenantEntity::getUserId).collect(Collectors.toSet());
        Set<Long> departmentIds = memberships.stream().map(UserTenantEntity::getDepartmentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, UserEntity> users = userIds.isEmpty() ? Map.of() : userMapper.selectListByQuery(QueryWrapper.create().where(USER_ENTITY.ID.in(userIds)).and(USER_ENTITY.DELETED.eq(false)).and(USER_ENTITY.STATUS.eq(1))).stream().collect(Collectors.toMap(UserEntity::getId, x -> x));
        Map<Long, DepartmentEntity> deps = departmentIds.isEmpty() ? Map.of() : departmentMapper.selectListByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.in(departmentIds)).and(DEPARTMENT_ENTITY.DELETED.eq(false))).stream().collect(Collectors.toMap(DepartmentEntity::getId, x -> x));
        List<RecipientUser> result = new ArrayList<>();
        for (UserTenantEntity m : memberships) {
            UserEntity u = users.get(m.getUserId());
            if (u == null) continue;
            DepartmentEntity d = m.getDepartmentId() == null ? null : deps.get(m.getDepartmentId());
            result.add(new RecipientUser(u.getId(), m.getDepartmentId(), u.getUsername(), u.getNickname(), d == null ? null : d.getDepartmentName()));
        }
        return result;
    }

    /**
     * 校验部门是否属于当前 Tenant。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param id       主键 ID
     */
    private void validateDepartment(Long tenantId, Long id) {
        if (departmentMapper.selectCountByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(id)).and(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false))) == 0)
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "Department 不存在");
    }
}
