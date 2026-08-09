package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.DataScopeType;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.dto.request.*;
import org.dromara.nova.system.dto.response.RoleRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.entity.RoleDepartmentEntity;
import org.dromara.nova.system.entity.RoleEntity;
import org.dromara.nova.system.entity.RoleMenuEntity;
import org.dromara.nova.system.entity.UserRoleEntity;
import org.dromara.nova.system.mapper.*;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.MenuEntityTableDef.MENU_ENTITY;
import static org.dromara.nova.system.entity.table.RoleDepartmentEntityTableDef.ROLE_DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.RoleEntityTableDef.ROLE_ENTITY;
import static org.dromara.nova.system.entity.table.RoleMenuEntityTableDef.ROLE_MENU_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;

/**
 * Tenant Role、DataScope、Menu 权限维护。
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final Converter converter;
    private final RoleDepartmentMapper roleDepartmentMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final DepartmentMapper departmentMapper;
    private final MenuMapper menuMapper;
    private final OnlineUserService onlineUserService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req RolePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<RoleRespDto> page(RolePageReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.DELETED.eq(false));
        if (req.keyword() != null && !req.keyword().isBlank())
            q.and(ROLE_ENTITY.ROLE_CODE.like(req.keyword()).or(ROLE_ENTITY.ROLE_NAME.like(req.keyword())));
        if (req.status() != null) q.and(ROLE_ENTITY.STATUS.eq(req.status()));
        Page<RoleEntity> p = roleMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(ROLE_ENTITY.SORT.asc()).orderBy(ROLE_ENTITY.ID.asc()));
        return PageResult.of(p.getRecords().stream().map(this::resp).toList(), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param roleId 角色 ID
     * @return 业务响应 DTO。
     */
    @Override
    public RoleRespDto detail(Long roleId) {
        return resp(require(roleId));
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req RoleCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "ROLE", type = "CREATE", description = "新增 Role")
    public RoleRespDto create(RoleCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        if (roleMapper.selectCountByQuery(QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.ROLE_CODE.eq(req.roleCode())).and(ROLE_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "Role 编码已存在");
        RoleEntity r = converter.convert(req, RoleEntity.class);
        r.setTenantId(tenantId);
        r.setDataScope(req.dataScope().name());
        r.setBuiltIn(false);
        AuditEntitySupport.created(r, LoginUserUtils.getUserId());
        roleMapper.insert(r);
        replaceDepartments(r, req.customDepartmentIds());
        return resp(r);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req RoleUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "ROLE", type = "UPDATE", description = "修改 Role")
    public RoleRespDto update(Long id, RoleUpdateReqDto req) {
        RoleEntity r = require(id);
        converter.convert(req, r);
        r.setDataScope(req.dataScope().name());
        AuditEntitySupport.updated(r, LoginUserUtils.getUserId());
        roleMapper.update(r);
        replaceDepartments(r, req.customDepartmentIds());
        invalidateRoleUsers(r);
        return resp(r);
    }

    /**
     * 修改业务状态并刷新相关缓存或会话。
     *
     * @param id  主键 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @CacheEvict(cacheNames = {"rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "ROLE", type = "STATUS", description = "修改 Role 状态")
    public void updateStatus(Long id, StatusUpdateReqDto req) {
        RoleEntity r = require(id);
        r.setStatus(req.status());
        AuditEntitySupport.updated(r, LoginUserUtils.getUserId());
        roleMapper.update(r);
        invalidateRoleUsers(r);
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "ROLE", type = "DELETE", description = "删除 Role")
    public void delete(Long id) {
        RoleEntity r = require(id);
        if (Boolean.TRUE.equals(r.getBuiltIn()))
            throw new BusinessException(CommonResultCode.CONFLICT, "内置 Role 不允许删除");
        if (userRoleMapper.selectCountByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(r.getTenantId())).and(USER_ROLE_ENTITY.ROLE_ID.eq(id))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "Role 已分配用户");
        r.setDeleted(true);
        AuditEntitySupport.updated(r, LoginUserUtils.getUserId());
        roleMapper.update(r);
    }

    /**
     * 查询指定角色已授权的菜单 ID。
     *
     * @param id 主键 ID
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<Long> menuIds(Long id) {
        RoleEntity r = require(id);
        return roleMenuMapper.selectListByQuery(QueryWrapper.create().where(ROLE_MENU_ENTITY.TENANT_ID.eq(r.getTenantId())).and(ROLE_MENU_ENTITY.ROLE_ID.eq(id))).stream().map(RoleMenuEntity::getMenuId).toList();
    }

    /**
     * 以全量替换语义更新角色菜单授权，并刷新受影响权限缓存。
     *
     * @param id  主键 ID
     * @param req RoleMenuUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"rolePermission", "userPermission", "menu"}, allEntries = true)
    @OperationAudit(module = "ROLE", type = "MENU_AUTHORIZE", description = "Role 菜单授权")
    public void replaceMenus(Long id, RoleMenuUpdateReqDto req) {
        RoleEntity r = require(id);
        Set<Long> menuIds = new LinkedHashSet<>(req.menuIds());
        if (!menuIds.isEmpty() && menuMapper.selectCountByQuery(QueryWrapper.create().where(MENU_ENTITY.ID.in(menuIds)).and(MENU_ENTITY.DELETED.eq(false))) != menuIds.size())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "包含无效 Menu");
        for (RoleMenuEntity old : roleMenuMapper.selectListByQuery(QueryWrapper.create().where(ROLE_MENU_ENTITY.TENANT_ID.eq(r.getTenantId())).and(ROLE_MENU_ENTITY.ROLE_ID.eq(id))))
            roleMenuMapper.deleteById(old.getId());
        for (Long menuId : menuIds) {
            RoleMenuEntity link = new RoleMenuEntity();
            link.setTenantId(r.getTenantId());
            link.setRoleId(id);
            link.setMenuId(menuId);
            link.setCreateBy(LoginUserUtils.getUserId());
            link.setCreateTime(LocalDateTime.now());
            roleMenuMapper.insert(link);
        }
        invalidateRoleUsers(r);
    }

    /**
     * 查询当前用户允许切换的 Context 选项。
     *
     * @param keyword 模糊搜索关键字
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<RoleSimpleRespDto> options(String keyword) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.DELETED.eq(false)).and(ROLE_ENTITY.STATUS.eq(1));
        if (keyword != null && !keyword.isBlank())
            q.and(ROLE_ENTITY.ROLE_CODE.like(keyword).or(ROLE_ENTITY.ROLE_NAME.like(keyword)));
        return converter.convert(roleMapper.selectListByQuery(q.orderBy(ROLE_ENTITY.SORT.asc()).limit(100)), RoleSimpleRespDto.class);
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private RoleEntity require(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        RoleEntity r = roleMapper.selectOneByQuery(QueryWrapper.create().where(ROLE_ENTITY.ID.eq(id)).and(ROLE_ENTITY.TENANT_ID.eq(tenantId)).and(ROLE_ENTITY.DELETED.eq(false)));
        if (r == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "Role 不存在");
        return r;
    }

    /**
     * 全量替换角色 CUSTOM DataScope 的部门授权。
     *
     * @param roleEntity 角色实体
     * @param ids        业务主键 ID 集合
     */
    private void replaceDepartments(RoleEntity r, List<Long> ids) {
        for (RoleDepartmentEntity old : roleDepartmentMapper.selectListByQuery(QueryWrapper.create().where(ROLE_DEPARTMENT_ENTITY.TENANT_ID.eq(r.getTenantId())).and(ROLE_DEPARTMENT_ENTITY.ROLE_ID.eq(r.getId()))))
            roleDepartmentMapper.deleteById(old.getId());
        if (!DataScopeType.CUSTOM.name().equals(r.getDataScope())) return;
        Set<Long> unique = new LinkedHashSet<>(ids == null ? List.of() : ids);
        if (!unique.isEmpty() && departmentMapper.selectCountByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.TENANT_ID.eq(r.getTenantId())).and(DEPARTMENT_ENTITY.ID.in(unique)).and(DEPARTMENT_ENTITY.DELETED.eq(false))) != unique.size())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "CUSTOM DataScope 包含无效部门");
        for (Long departmentId : unique) {
            RoleDepartmentEntity link = new RoleDepartmentEntity();
            link.setTenantId(r.getTenantId());
            link.setRoleId(r.getId());
            link.setDepartmentId(departmentId);
            link.setCreateBy(LoginUserUtils.getUserId());
            link.setCreateTime(LocalDateTime.now());
            roleDepartmentMapper.insert(link);
        }
    }

    /**
     * 使分配该角色的在线用户会话失效，确保权限重新计算。
     *
     * @param r 角色实体
     */
    private void invalidateRoleUsers(RoleEntity r) {
        List<Long> userIds = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(r.getTenantId())).and(USER_ROLE_ENTITY.ROLE_ID.eq(r.getId()))).stream().map(UserRoleEntity::getUserId).distinct().toList();
        onlineUserService.invalidateTenantUsers(r.getTenantId(), userIds);
    }

    /**
     * 将实体转换为对外响应 DTO。
     *
     * @param r 角色实体
     * @return 业务响应 DTO。
     */
    private RoleRespDto resp(RoleEntity roleEntity) {
        List<Long> customDepartmentIds = roleDepartmentMapper.selectListByQuery(QueryWrapper.create().where(ROLE_DEPARTMENT_ENTITY.TENANT_ID.eq(roleEntity.getTenantId())).and(ROLE_DEPARTMENT_ENTITY.ROLE_ID.eq(roleEntity.getId()))).stream().map(RoleDepartmentEntity::getDepartmentId).toList();
        RoleRespDto mapped = converter.convert(roleEntity, RoleRespDto.class);
        return new RoleRespDto(mapped.id(), mapped.roleCode(), mapped.roleName(), mapped.dataScope(), mapped.builtIn(), mapped.sort(), mapped.status(), mapped.remark(), customDepartmentIds, mapped.createTime());
    }
}
