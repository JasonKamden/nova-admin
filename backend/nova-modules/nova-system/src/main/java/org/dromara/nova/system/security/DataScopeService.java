package org.dromara.nova.system.security;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.DataScopeType;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.system.entity.DepartmentEntity;
import org.dromara.nova.system.entity.RoleEntity;
import org.dromara.nova.system.entity.UserRoleEntity;
import org.dromara.nova.system.mapper.DepartmentMapper;
import org.dromara.nova.system.mapper.RoleDepartmentMapper;
import org.dromara.nova.system.mapper.RoleMapper;
import org.dromara.nova.system.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.RoleDepartmentEntityTableDef.ROLE_DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;

/**
 * 将 Role.dataScope 转换成真实可执行的部门/本人范围。
 */
@Service
@RequiredArgsConstructor
public class DataScopeService {
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RoleDepartmentMapper roleDepartmentMapper;
    private final DepartmentMapper departmentMapper;

    /**
     * 查询当前 PLATFORM/TENANT 运行 Context。
     *
     * @return 方法处理结果。
     */
    public DataScopeRule current() {
        var user = LoginUserUtils.getLoginUser();
        if (user.platformAdmin()) return new DataScopeRule(true, Set.of(), null);
        Long tenantId = user.tenantId();
        if (tenantId == null) return new DataScopeRule(false, Set.of(), user.userId());
        List<UserRoleEntity> links = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.eq(user.userId())));
        if (links.isEmpty())
            return new DataScopeRule(false, user.departmentId() == null ? Set.of() : Set.of(user.departmentId()), user.userId());
        Set<Long> roleIds = new HashSet<>();
        links.forEach(x -> roleIds.add(x.getRoleId()));
        List<RoleEntity> roles = roleMapper.selectListByQuery(QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.ID.in(roleIds)).and(ROLE_ENTITY.STATUS.eq(1)).and(ROLE_ENTITY.DELETED.eq(false)));
        boolean all = false;
        boolean includeSelf = false;
        Set<Long> departments = new HashSet<>();
        for (RoleEntity role : roles) {
            DataScopeType scope = DataScopeType.valueOf(role.getDataScope());
            switch (scope) {
                case ALL, TENANT -> all = true;
                case SELF -> includeSelf = true;
                case DEPARTMENT -> {
                    if (user.departmentId() != null) departments.add(user.departmentId());
                }
                case DEPARTMENT_AND_CHILDREN -> {
                    if (user.departmentId() != null) {
                        departments.add(user.departmentId());
                        departments.addAll(descendants(tenantId, user.departmentId()));
                    }
                }
                case CUSTOM ->
                        roleDepartmentMapper.selectListByQuery(QueryWrapper.create().where(ROLE_DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_DEPARTMENT_ENTITY.ROLE_ID.eq(role.getId()))).forEach(x -> departments.add(x.getDepartmentId()));
            }
        }
        return new DataScopeRule(all, departments, includeSelf ? user.userId() : null);
    }

    /**
     * 递归解析指定 Department 的全部下级 Department ID。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param parentId 父 Department/菜单节点 ID；根节点可为空
     * @return 方法处理结果。
     */
    public Set<Long> descendants(Long tenantId, Long parentId) {
        List<DepartmentEntity> all = departmentMapper.selectListByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
        Map<Long, List<Long>> children = new HashMap<>();
        for (DepartmentEntity d : all) children.computeIfAbsent(d.getParentId(), k -> new ArrayList<>()).add(d.getId());
        Set<Long> result = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(parentId);
        while (!queue.isEmpty()) {
            Long id = queue.removeFirst();
            for (Long child : children.getOrDefault(id, List.of())) if (result.add(child)) queue.addLast(child);
        }
        return result;
    }
}
