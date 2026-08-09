package org.dromara.nova.system.security;

import java.util.Set;

/**
 * 解析后的数据权限规则，departmentIds 与 selfUserId 采用并集。
 *
 * @param allTenant     是否拥有当前 Tenant 全范围数据权限
 * @param departmentIds Department ID 集合
 * @param selfUserId    SELF 数据范围对应用户 ID
 */
public record DataScopeRule(boolean allTenant, Set<Long> departmentIds, Long selfUserId) {
    public DataScopeRule {
        departmentIds = departmentIds == null ? Set.of() : Set.copyOf(departmentIds);
    }
}
