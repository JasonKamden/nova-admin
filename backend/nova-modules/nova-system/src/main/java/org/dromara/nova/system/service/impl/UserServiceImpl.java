package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.dto.request.*;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.dto.response.UserRespDto;
import org.dromara.nova.system.entity.DepartmentEntity;
import org.dromara.nova.system.entity.UserEntity;
import org.dromara.nova.system.entity.UserRoleEntity;
import org.dromara.nova.system.entity.UserTenantEntity;
import org.dromara.nova.system.mapper.*;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * Tenant 用户/成员维护。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final Converter converter;
    private final UserTenantMapper userTenantMapper;
    private final DepartmentMapper departmentMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final DataScopeService dataScopeService;
    private final OnlineUserService onlineUserService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req UserPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<UserRespDto> page(UserPageReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId));
        if (req.departmentId() != null) q.and(USER_TENANT_ENTITY.DEPARTMENT_ID.eq(req.departmentId()));
        if (req.status() != null) q.and(USER_TENANT_ENTITY.STATUS.eq(req.status()));
        else q.and(USER_TENANT_ENTITY.STATUS.eq(1));
        if (hasGlobalFilter(req)) {
            QueryWrapper userQuery = QueryWrapper.create().select(USER_ENTITY.ID).where(USER_ENTITY.DELETED.eq(false));
            if (req.username() != null && !req.username().isBlank())
                userQuery.and(USER_ENTITY.USERNAME.like(req.username()));
            if (req.nickname() != null && !req.nickname().isBlank())
                userQuery.and(USER_ENTITY.NICKNAME.like(req.nickname()));
            if (req.phone() != null && !req.phone().isBlank()) userQuery.and(USER_ENTITY.PHONE.like(req.phone()));
            if (req.email() != null && !req.email().isBlank()) userQuery.and(USER_ENTITY.EMAIL.like(req.email()));
            List<Long> userIds = userMapper.selectListByQuery(userQuery).stream().map(UserEntity::getId).toList();
            if (userIds.isEmpty()) return PageResult.of(List.of(), 0, req.pageNum(), req.pageSize());
            q.and(USER_TENANT_ENTITY.USER_ID.in(userIds));
        }
        applyScope(q, dataScopeService.current());
        Page<UserTenantEntity> p = userTenantMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(USER_TENANT_ENTITY.ID.desc()));
        List<UserRespDto> rows = p.getRecords().stream().map(m -> assemble(m, null)).toList();
        return PageResult.of(rows, p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 判断用户查询是否包含需要关联全局用户字段的过滤条件。
     *
     * @param req UserPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务校验或处理结果。
     */
    private boolean hasGlobalFilter(UserPageReqDto req) {
        return (req.username() != null && !req.username().isBlank()) || (req.nickname() != null && !req.nickname().isBlank()) || (req.phone() != null && !req.phone().isBlank()) || (req.email() != null && !req.email().isBlank());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param userId 用户 ID
     * @return 业务响应 DTO。
     */
    @Override
    public UserRespDto detail(Long userId) {
        return assemble(requireMembership(userId), null);
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req UserCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "userPermission", allEntries = true)
    @OperationAudit(module = "USER", type = "CREATE", description = "新增 Tenant 用户")
    public UserRespDto create(UserCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        Long operator = LoginUserUtils.getUserId();
        validateDepartment(tenantId, req.departmentId());
        UserEntity user = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.USERNAME.eq(req.username())).and(USER_ENTITY.DELETED.eq(false)));
        if (user == null) {
            if (req.initialPassword() == null || req.initialPassword().isBlank())
                throw new BusinessException(CommonResultCode.BAD_REQUEST, "新账号必须设置初始密码");
            user = converter.convert(req, UserEntity.class);
            user.setPassword(passwordEncoder.encode(req.initialPassword()));
            user.setPlatformAdmin(false);
            user.setStatus(1);
            AuditEntitySupport.created(user, operator);
            userMapper.insert(user);
        }
        if (userTenantMapper.selectCountByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.USER_ID.eq(user.getId())).and(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "用户已属于当前 Tenant");
        UserTenantEntity m = new UserTenantEntity();
        m.setUserId(user.getId());
        m.setTenantId(tenantId);
        m.setDepartmentId(req.departmentId());
        m.setStatus(1);
        m.setJoinTime(LocalDateTime.now());
        m.setCreateBy(operator);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateBy(operator);
        m.setUpdateTime(LocalDateTime.now());
        userTenantMapper.insert(m);
        if (req.roleIds() != null) replaceRoleIds(user.getId(), req.roleIds());
        return assemble(m, null);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param userId 用户 ID
     * @param req    UserUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "USER", type = "UPDATE", description = "修改 Tenant 用户")
    public UserRespDto update(Long userId, UserUpdateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        UserTenantEntity m = requireMembership(userId);
        validateDepartment(tenantId, req.departmentId());
        UserEntity u = requireUser(userId);
        converter.convert(req, u);
        AuditEntitySupport.updated(u, LoginUserUtils.getUserId());
        userMapper.update(u);
        m.setDepartmentId(req.departmentId());
        m.setUpdateBy(LoginUserUtils.getUserId());
        m.setUpdateTime(LocalDateTime.now());
        userTenantMapper.update(m);
        onlineUserService.invalidateTenantUsers(tenantId, List.of(userId));
        return assemble(m, null);
    }

    /**
     * 修改业务状态并刷新相关缓存或会话。
     *
     * @param userId 用户 ID
     * @param req    StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @OperationAudit(module = "USER", type = "STATUS", description = "修改 Tenant 用户状态")
    public void updateStatus(Long userId, StatusUpdateReqDto req) {
        UserTenantEntity m = requireMembership(userId);
        m.setStatus(req.status());
        m.setUpdateBy(LoginUserUtils.getUserId());
        m.setUpdateTime(LocalDateTime.now());
        userTenantMapper.update(m);
        onlineUserService.invalidateTenantUsers(m.getTenantId(), List.of(userId));
    }

    /**
     * 重置指定 Tenant 用户密码并使其在线会话失效。
     *
     * @param userId 用户 ID
     * @param req    PasswordResetReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @OperationAudit(module = "USER", type = "PASSWORD", description = "重置用户密码")
    public void resetPassword(Long userId, PasswordResetReqDto req) {
        requireMembership(userId);
        UserEntity u = requireUser(userId);
        u.setPassword(passwordEncoder.encode(req.newPassword()));
        AuditEntitySupport.updated(u, LoginUserUtils.getUserId());
        userMapper.update(u);
        onlineUserService.invalidateUsers(List.of(userId));
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param userId 用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "userPermission", allEntries = true)
    @OperationAudit(module = "USER", type = "DELETE", description = "移除 Tenant 用户")
    public void delete(Long userId) {
        Long tenantId = TenantContextSupport.requireTenantId();
        UserTenantEntity m = requireMembership(userId);
        m.setStatus(0);
        m.setUpdateBy(LoginUserUtils.getUserId());
        m.setUpdateTime(LocalDateTime.now());
        userTenantMapper.update(m);
        for (UserRoleEntity link : userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.eq(userId))))
            userRoleMapper.deleteById(link.getId());
        onlineUserService.invalidateTenantUsers(tenantId, List.of(userId));
    }

    /**
     * 查询指定用户在当前 Tenant 已分配的角色。
     *
     * @param userId 用户 ID
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RoleSimpleRespDto> roles(Long userId) {
        Long tenantId = TenantContextSupport.requireTenantId();
        requireMembership(userId);
        List<UserRoleEntity> links = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.eq(userId)));
        if (links.isEmpty()) return List.of();
        Set<Long> ids = links.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toSet());
        return converter.convert(roleMapper.selectListByQuery(QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.ID.in(ids)).and(ROLE_ENTITY.DELETED.eq(false))), RoleSimpleRespDto.class);
    }

    /**
     * 以全量替换语义更新用户角色，并刷新权限缓存。
     *
     * @param userId 用户 ID
     * @param req    UserRoleUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "userPermission", allEntries = true)
    @OperationAudit(module = "USER", type = "AUTHORIZE", description = "分配用户角色")
    public void replaceRoles(Long userId, UserRoleUpdateReqDto req) {
        UserTenantEntity membership = requireMembership(userId);
        replaceRoleIds(userId, req.roleIds());
        onlineUserService.invalidateTenantUsers(membership.getTenantId(), List.of(userId));
    }

    /**
     * 全量替换用户角色关系。
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 集合，全量替换提交
     */
    private void replaceRoleIds(Long userId, List<Long> roleIds) {
        Long tenantId = TenantContextSupport.requireTenantId();
        Set<Long> ids = new LinkedHashSet<>(roleIds == null ? List.of() : roleIds);
        if (!ids.isEmpty() && roleMapper.selectCountByQuery(QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.ID.in(ids)).and(ROLE_ENTITY.DELETED.eq(false))) != ids.size())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "包含无效或跨 Tenant Role");
        for (UserRoleEntity old : userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(USER_ROLE_ENTITY.USER_ID.eq(userId))))
            userRoleMapper.deleteById(old.getId());
        for (Long roleId : ids) {
            UserRoleEntity link = new UserRoleEntity();
            link.setTenantId(tenantId);
            link.setUserId(userId);
            link.setRoleId(roleId);
            link.setCreateBy(LoginUserUtils.getUserId());
            link.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(link);
        }
    }

    /**
     * 聚合用户、成员关系、Department 和角色数据形成用户响应。
     *
     * @param m      当前 Tenant 用户成员关系
     * @param filter 用户查询过滤条件
     * @return 业务响应 DTO。
     */
    private UserRespDto assemble(UserTenantEntity m, UserPageReqDto filter) {
        UserEntity u = requireUser(m.getUserId());
        if (filter != null) {
            if (filter.username() != null && !filter.username().isBlank() && !u.getUsername().contains(filter.username()))
                return null;
            if (filter.nickname() != null && !filter.nickname().isBlank() && !u.getNickname().contains(filter.nickname()))
                return null;
            if (filter.phone() != null && !filter.phone().isBlank() && (u.getPhone() == null || !u.getPhone().contains(filter.phone())))
                return null;
            if (filter.email() != null && !filter.email().isBlank() && (u.getEmail() == null || !u.getEmail().contains(filter.email())))
                return null;
            if (filter.status() != null && !Objects.equals(m.getStatus(), filter.status())) return null;
        }
        String departmentCode = null;
        String departmentName = null;
        if (m.getDepartmentId() != null) {
            DepartmentEntity d = departmentMapper.selectOneByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(m.getDepartmentId())).and(DEPARTMENT_ENTITY.TENANT_ID.eq(m.getTenantId())).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
            if (d != null) {
                departmentCode = d.getDepartmentCode();
                departmentName = d.getDepartmentName();
            }
        }
        UserRespDto mapped = converter.convert(u, UserRespDto.class);
        return new UserRespDto(mapped.id(), mapped.username(), mapped.nickname(), mapped.avatar(), mapped.gender(), mapped.phone(), mapped.email(), mapped.bio(), m.getStatus(), m.getDepartmentId(), departmentCode, departmentName, roles(u.getId()), mapped.lastLoginTime(), mapped.lastLoginIp(), mapped.createTime());
    }

    /**
     * 加载有效用户，不存在时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private UserEntity requireUser(Long id) {
        UserEntity u = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.ID.eq(id)).and(USER_ENTITY.DELETED.eq(false)));
        if (u == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "用户不存在");
        return u;
    }

    /**
     * 加载当前 Tenant 用户成员关系。
     *
     * @param userId 用户 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private UserTenantEntity requireMembership(Long userId) {
        Long tenantId = TenantContextSupport.requireTenantId();
        UserTenantEntity m = userTenantMapper.selectOneByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.USER_ID.eq(userId)).and(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.STATUS.ge(0)));
        if (m == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "当前 Tenant 成员不存在");
        return m;
    }

    /**
     * 校验 Department 是否属于当前 Tenant。
     *
     * @param tenantId     Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param departmentId Department ID
     */
    private void validateDepartment(Long tenantId, Long departmentId) {
        if (departmentId == null) return;
        if (departmentMapper.selectCountByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(departmentId)).and(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false))) == 0)
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "Department 不存在");
    }

    /**
     * 将当前 DataScope 规则应用到查询条件。
     *
     * @param q    MyBatis-Flex 查询条件
     * @param rule 当前用户解析后的 DataScope 规则
     */
    private void applyScope(QueryWrapper q, DataScopeRule rule) {
        if (rule.allTenant()) return;
        if (!rule.departmentIds().isEmpty() && rule.selfUserId() != null)
            q.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()).or(USER_TENANT_ENTITY.USER_ID.eq(rule.selfUserId())));
        else if (!rule.departmentIds().isEmpty()) q.and(USER_TENANT_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()));
        else if (rule.selfUserId() != null) q.and(USER_TENANT_ENTITY.USER_ID.eq(rule.selfUserId()));
        else q.and(USER_TENANT_ENTITY.ID.eq(-1));
    }
}
