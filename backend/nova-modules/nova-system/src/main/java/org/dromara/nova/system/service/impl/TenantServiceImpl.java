package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.DataScopeType;
import org.dromara.nova.common.core.enums.Status;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.request.TenantCreateReqDto;
import org.dromara.nova.system.dto.request.TenantPageReqDto;
import org.dromara.nova.system.dto.request.TenantUpdateReqDto;
import org.dromara.nova.system.dto.response.ContextTenantOptionRespDto;
import org.dromara.nova.system.dto.response.TenantRespDto;
import org.dromara.nova.system.entity.*;
import org.dromara.nova.system.mapper.*;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.service.TenantService;
import org.dromara.nova.system.support.AccessControlSupport;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.dromara.nova.system.entity.table.MenuEntityTableDef.MENU_ENTITY;
import static org.dromara.nova.system.entity.table.TenantEntityTableDef.TENANT_ENTITY;
import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * PLATFORM Tenant 生命周期服务。
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final UserTenantMapper userTenantMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlSupport accessControlSupport;
    private final Converter converter;
    private final OnlineUserService onlineUserService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req TenantPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<TenantRespDto> page(TenantPageReqDto req) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        QueryWrapper q = QueryWrapper.create().where(TENANT_ENTITY.DELETED.eq(false));
        if (req.keyword() != null && !req.keyword().isBlank())
            q.and(TENANT_ENTITY.TENANT_CODE.like(req.keyword()).or(TENANT_ENTITY.TENANT_NAME.like(req.keyword())));
        if (req.status() != null) q.and(TENANT_ENTITY.STATUS.eq(req.status()));
        q.orderBy(TENANT_ENTITY.ID.desc());
        Page<TenantEntity> p = tenantMapper.paginate(req.pageNum(), req.pageSize(), q);
        return PageResult.of(converter.convert(p.getRecords(), TenantRespDto.class), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public TenantRespDto detail(Long id) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        return resp(requireTenant(id));
    }

    /**
     * 远程搜索可用 Tenant 选项。
     *
     * @param keyword 模糊搜索关键字
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<ContextTenantOptionRespDto> searchOptions(String keyword) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        QueryWrapper q = QueryWrapper.create().where(TENANT_ENTITY.DELETED.eq(false)).and(TENANT_ENTITY.STATUS.eq(1));
        if (keyword != null && !keyword.isBlank())
            q.and(TENANT_ENTITY.TENANT_CODE.like(keyword).or(TENANT_ENTITY.TENANT_NAME.like(keyword)));
        return converter.convert(tenantMapper.selectListByQuery(q.orderBy(TENANT_ENTITY.ID.desc()).limit(50)), ContextTenantOptionRespDto.class);
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req TenantCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "TENANT", type = "CREATE", description = "创建 Tenant")
    public TenantRespDto create(TenantCreateReqDto req) {
        var operator = accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        if (tenantMapper.selectCountByQuery(QueryWrapper.create().where(TENANT_ENTITY.TENANT_CODE.eq(req.tenantCode())).and(TENANT_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "Tenant 编码已存在");
        TenantEntity tenant = converter.convert(req, TenantEntity.class);
        tenant.setStatus(1);
        AuditEntitySupport.created(tenant, operator.userId());
        tenantMapper.insert(tenant);
        UserEntity admin = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.USERNAME.eq(req.adminUsername())).and(USER_ENTITY.DELETED.eq(false)));
        if (admin == null) {
            admin = new UserEntity();
            admin.setUsername(req.adminUsername());
            admin.setNickname(req.adminNickname());
            admin.setPassword(passwordEncoder.encode(req.adminPassword()));
            admin.setPlatformAdmin(false);
            admin.setStatus(1);
            AuditEntitySupport.created(admin, operator.userId());
            userMapper.insert(admin);
        }
        UserTenantEntity membership = userTenantMapper.selectOneByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.USER_ID.eq(admin.getId())).and(USER_TENANT_ENTITY.TENANT_ID.eq(tenant.getId())));
        if (membership == null) {
            membership = new UserTenantEntity();
            membership.setUserId(admin.getId());
            membership.setTenantId(tenant.getId());
            membership.setDepartmentId(null);
            membership.setStatus(1);
            membership.setJoinTime(LocalDateTime.now());
            membership.setCreateBy(operator.userId());
            membership.setCreateTime(LocalDateTime.now());
            membership.setUpdateBy(operator.userId());
            membership.setUpdateTime(LocalDateTime.now());
            userTenantMapper.insert(membership);
        }
        Map<String, RoleEntity> roles = initializeRoles(tenant.getId(), operator.userId());
        RoleEntity tenantAdmin = roles.get("tenant_admin");
        UserRoleEntity ur = new UserRoleEntity();
        ur.setTenantId(tenant.getId());
        ur.setUserId(admin.getId());
        ur.setRoleId(tenantAdmin.getId());
        ur.setCreateBy(operator.userId());
        ur.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(ur);
        List<MenuEntity> menus = menuMapper.selectListByQuery(QueryWrapper.create().where(MENU_ENTITY.DELETED.eq(false)).and(MENU_ENTITY.STATUS.eq(1)));
        for (MenuEntity m : menus) {
            RoleMenuEntity rm = new RoleMenuEntity();
            rm.setTenantId(tenant.getId());
            rm.setRoleId(tenantAdmin.getId());
            rm.setMenuId(m.getId());
            rm.setCreateBy(operator.userId());
            rm.setCreateTime(LocalDateTime.now());
            roleMenuMapper.insert(rm);
        }
        return resp(tenant);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req TenantUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "TENANT", type = "UPDATE", description = "修改 Tenant")
    public TenantRespDto update(Long id, TenantUpdateReqDto req) {
        var u = accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        TenantEntity t = requireTenant(id);
        converter.convert(req, t);
        AuditEntitySupport.updated(t, u.userId());
        tenantMapper.update(t);
        if (t.getExpireAt() != null && t.getExpireAt().isBefore(LocalDate.now())) invalidateTenantUsers(t.getId());
        return resp(t);
    }

    /**
     * 修改业务状态并刷新相关缓存或会话。
     *
     * @param id  主键 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @OperationAudit(module = "TENANT", type = "STATUS", description = "修改 Tenant 状态")
    public void updateStatus(Long id, StatusUpdateReqDto req) {
        var u = accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        TenantEntity t = requireTenant(id);
        t.setStatus(req.status());
        AuditEntitySupport.updated(t, u.userId());
        tenantMapper.update(t);
        if (!Status.enabled(t.getStatus())) invalidateTenantUsers(t.getId());
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @OperationAudit(module = "TENANT", type = "DELETE", description = "删除 Tenant")
    public void delete(Long id) {
        var u = accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        TenantEntity t = requireTenant(id);
        t.setDeleted(true);
        t.setStatus(0);
        AuditEntitySupport.updated(t, u.userId());
        tenantMapper.update(t);
        invalidateTenantUsers(t.getId());
    }

    /**
     * 使指定 Tenant 下的一组用户会话失效。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     */
    private void invalidateTenantUsers(Long tenantId) {
        List<Long> userIds = userTenantMapper.selectListByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(tenantId)).and(USER_TENANT_ENTITY.STATUS.eq(1))).stream().map(UserTenantEntity::getUserId).distinct().toList();
        onlineUserService.invalidateTenantUsers(tenantId, userIds);
    }

    /**
     * 初始化新 Tenant 的默认角色。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param userId   用户 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private Map<String, RoleEntity> initializeRoles(Long tenantId, Long userId) {
        LinkedHashMap<String, RoleEntity> result = new LinkedHashMap<>();
        createRole(result, tenantId, "tenant_admin", "租户管理员", DataScopeType.TENANT, true, 10, userId);
        createRole(result, tenantId, "department_admin", "Department 管理员", DataScopeType.DEPARTMENT_AND_CHILDREN, true, 20, userId);
        createRole(result, tenantId, "business_admin", "业务管理员", DataScopeType.TENANT, true, 30, userId);
        createRole(result, tenantId, "user", "普通用户", DataScopeType.SELF, true, 40, userId);
        createRole(result, tenantId, "auditor", "审计人员", DataScopeType.TENANT, true, 50, userId);
        return result;
    }

    /**
     * 创建新 Tenant 的内置角色。
     *
     * @param map      默认角色结果映射
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param code     编码
     * @param name     名称
     * @param scope    作用域
     * @param builtIn  是否系统内置数据
     * @param sort     排序值，数值越小越靠前
     * @param userId   用户 ID
     */
    private void createRole(Map<String, RoleEntity> map, Long tenantId, String code, String name, DataScopeType scope, boolean builtIn, int sort, Long userId) {
        RoleEntity r = new RoleEntity();
        r.setTenantId(tenantId);
        r.setRoleCode(code);
        r.setRoleName(name);
        r.setDataScope(scope.name());
        r.setBuiltIn(builtIn);
        r.setSort(sort);
        r.setStatus(1);
        AuditEntitySupport.created(r, userId);
        roleMapper.insert(r);
        map.put(code, r);
    }

    /**
     * 加载有效 Tenant，不存在时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private TenantEntity requireTenant(Long id) {
        TenantEntity t = tenantMapper.selectOneByQuery(QueryWrapper.create().where(TENANT_ENTITY.ID.eq(id)).and(TENANT_ENTITY.DELETED.eq(false)));
        if (t == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "Tenant 不存在");
        return t;
    }

    /**
     * 将实体转换为对外响应 DTO。
     *
     * @param t Tenant 实体
     * @return 业务响应 DTO。
     */
    private TenantRespDto resp(TenantEntity t) {
        return converter.convert(t, TenantRespDto.class);
    }
}
