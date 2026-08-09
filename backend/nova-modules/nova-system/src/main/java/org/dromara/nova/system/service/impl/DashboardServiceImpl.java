package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.dto.response.DashboardRespDto;
import org.dromara.nova.system.dto.response.PlatformDashboardRespDto;
import org.dromara.nova.system.entity.LoginLogEntity;
import org.dromara.nova.system.entity.UserTenantEntity;
import org.dromara.nova.system.mapper.*;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.DashboardService;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.support.AccessControlSupport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.LoginLogEntityTableDef.LOGIN_LOG_ENTITY;
import static org.dromara.nova.system.entity.table.OperationLogEntityTableDef.OPERATION_LOG_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.TenantEntityTableDef.TENANT_ENTITY;
import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * 工作台真实统计；Tenant 统计同时遵循当前 DataScope。
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserTenantMapper userTenantMapper;
    private final DepartmentMapper departmentMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final LoginLogMapper loginLogMapper;
    private final OperationLogMapper operationLogMapper;
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final OnlineUserService onlineUserService;
    private final AccessControlSupport accessControlSupport;
    private final DataScopeService dataScopeService;

    /**
     * 查询当前 Tenant 和 DataScope 范围内的工作台聚合数据。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public DashboardRespDto tenantDashboard() {
        Long tenantId = TenantContextSupport.requireTenantId();
        var current = LoginUserUtils.getLoginUser();
        DataScopeRule rule = dataScopeService.current();

        QueryWrapper membershipQuery = QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId));
        applyMembershipScope(membershipQuery, rule);
        List<UserTenantEntity> memberships = userTenantMapper.selectListByQuery(membershipQuery);
        long users = memberships.size();
        long enabled = memberships.stream().filter(m -> Integer.valueOf(1).equals(m.getStatus())).count();
        long disabled = Math.max(0, users - enabled);

        long departments;
        if (rule.allTenant()) {
            departments = departmentMapper.selectCountByQuery(QueryWrapper.create()
                    .where(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
        } else {
            Set<Long> departmentIds = new HashSet<>(rule.departmentIds());
            if (departmentIds.isEmpty() && current.departmentId() != null) departmentIds.add(current.departmentId());
            departments = departmentIds.size();
        }

        long roles;
        if (rule.allTenant()) {
            roles = roleMapper.selectCountByQuery(QueryWrapper.create()
                    .where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.DELETED.eq(false)));
        } else {
            Set<Long> userIds = memberships.stream().map(UserTenantEntity::getUserId).collect(java.util.stream.Collectors.toSet());
            if (userIds.isEmpty()) roles = 0;
            else roles = userRoleMapper.selectListByQuery(QueryWrapper.create()
                            .where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.in(userIds)))
                    .stream().map(x -> x.getRoleId()).distinct().count();
        }

        LocalDateTime start = LocalDate.now().minusDays(6).atStartOfDay();
        QueryWrapper loginQuery = QueryWrapper.create().where(LOGIN_LOG_ENTITY.TENANT_ID.eq(tenantId))
                .and(LOGIN_LOG_ENTITY.LOGIN_STATUS.eq(1)).and(LOGIN_LOG_ENTITY.LOGIN_TIME.ge(start));
        applyLogScope(loginQuery, rule, true);
        List<LoginLogEntity> logs = loginLogMapper.selectListByQuery(loginQuery);
        Map<LocalDate, Long> byDay = new HashMap<>();
        logs.forEach(log -> byDay.merge(log.getLoginTime().toLocalDate(), 1L, Long::sum));
        List<DashboardRespDto.TrendPoint> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            trend.add(new DashboardRespDto.TrendPoint(day.format(DateTimeFormatter.ofPattern("MM-dd")), byDay.getOrDefault(day, 0L)));
        }

        QueryWrapper operationQuery = QueryWrapper.create().where(OPERATION_LOG_ENTITY.TENANT_ID.eq(tenantId));
        applyLogScope(operationQuery, rule, false);
        List<DashboardRespDto.RecentOperation> recent = operationLogMapper.selectListByQuery(
                        operationQuery.orderBy(OPERATION_LOG_ENTITY.OPERATION_TIME.desc()).limit(10))
                .stream().map(x -> new DashboardRespDto.RecentOperation(x.getUsername(), x.getOperationDescription(), x.getOperationTime().toString()))
                .toList();

        return new DashboardRespDto(current.tenantName(), current.departmentName(), users, departments, roles,
                onlineUserService.countCurrentTenant(), trend,
                List.of(new DashboardRespDto.StatusPoint("启用", enabled), new DashboardRespDto.StatusPoint("禁用", disabled)), recent);
    }

    /**
     * 查询 PLATFORM Context 下的平台工作台聚合数据。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public PlatformDashboardRespDto platformDashboard() {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        long total = tenantMapper.selectCountByQuery(QueryWrapper.create().where(TENANT_ENTITY.DELETED.eq(false)));
        long enabled = tenantMapper.selectCountByQuery(QueryWrapper.create().where(TENANT_ENTITY.DELETED.eq(false)).and(TENANT_ENTITY.STATUS.eq(1)));
        long platformUsers = userMapper.selectCountByQuery(QueryWrapper.create().where(USER_ENTITY.PLATFORM_ADMIN.eq(true)).and(USER_ENTITY.DELETED.eq(false)));
        long today = loginLogMapper.selectCountByQuery(QueryWrapper.create().where(LOGIN_LOG_ENTITY.LOGIN_STATUS.eq(1)).and(LOGIN_LOG_ENTITY.LOGIN_TIME.ge(LocalDate.now().atStartOfDay())));
        return new PlatformDashboardRespDto(total, enabled, total - enabled, platformUsers, today);
    }

    /**
     * 将当前 DataScope 约束应用到用户 Tenant 成员关系查询。
     *
     * @param query MyBatis-Flex 查询条件
     * @param rule  当前用户解析后的 DataScope 规则
     */
    private void applyMembershipScope(QueryWrapper query, DataScopeRule rule) {
        if (rule.allTenant()) return;
        if (!rule.departmentIds().isEmpty() && rule.selfUserId() != null) {
            query.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()).or(USER_TENANT_ENTITY.USER_ID.eq(rule.selfUserId())));
        } else if (!rule.departmentIds().isEmpty()) {
            query.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()));
        } else if (rule.selfUserId() != null) {
            query.and(USER_TENANT_ENTITY.USER_ID.eq(rule.selfUserId()));
        } else {
            query.and(USER_TENANT_ENTITY.ID.eq(-1));
        }
    }

    /**
     * 将当前 DataScope 约束应用到操作日志查询。
     *
     * @param query    MyBatis-Flex 查询条件
     * @param rule     当前用户解析后的 DataScope 规则
     * @param loginLog 登录日志实体
     */
    private void applyLogScope(QueryWrapper query, DataScopeRule rule, boolean loginLog) {
        if (rule.allTenant()) return;
        if (loginLog) {
            if (!rule.departmentIds().isEmpty() && rule.selfUserId() != null)
                query.and(LOGIN_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()).or(LOGIN_LOG_ENTITY.USER_ID.eq(rule.selfUserId())));
            else if (!rule.departmentIds().isEmpty())
                query.and(LOGIN_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()));
            else if (rule.selfUserId() != null) query.and(LOGIN_LOG_ENTITY.USER_ID.eq(rule.selfUserId()));
            else query.and(LOGIN_LOG_ENTITY.ID.eq(-1));
        } else {
            if (!rule.departmentIds().isEmpty() && rule.selfUserId() != null)
                query.and(OPERATION_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()).or(OPERATION_LOG_ENTITY.USER_ID.eq(rule.selfUserId())));
            else if (!rule.departmentIds().isEmpty())
                query.and(OPERATION_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()));
            else if (rule.selfUserId() != null) query.and(OPERATION_LOG_ENTITY.USER_ID.eq(rule.selfUserId()));
            else query.and(OPERATION_LOG_ENTITY.ID.eq(-1));
        }
    }
}
