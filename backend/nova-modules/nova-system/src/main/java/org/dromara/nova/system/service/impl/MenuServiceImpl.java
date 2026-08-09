package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.ContextType;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.system.dto.request.MenuCreateReqDto;
import org.dromara.nova.system.dto.request.MenuUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.MenuRespDto;
import org.dromara.nova.system.entity.MenuEntity;
import org.dromara.nova.system.entity.RoleMenuEntity;
import org.dromara.nova.system.entity.UserRoleEntity;
import org.dromara.nova.system.mapper.MenuMapper;
import org.dromara.nova.system.mapper.RoleMenuMapper;
import org.dromara.nova.system.mapper.UserRoleMapper;
import org.dromara.nova.system.service.MenuService;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.support.AccessControlSupport;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.dromara.nova.system.entity.table.MenuEntityTableDef.MENU_ENTITY;
import static org.dromara.nova.system.entity.table.RoleMenuEntityTableDef.ROLE_MENU_ENTITY;
import static org.dromara.nova.system.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;

/**
 * 全局 Menu 定义和当前用户 Menu Tree。
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuMapper menuMapper;
    private final Converter converter;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final AccessControlSupport accessControlSupport;
    private final OnlineUserService onlineUserService;

    /**
     * 查询当前 Tenant 和 DataScope 范围内的树形数据。
     *
     * @param keyword 模糊搜索关键字
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<MenuRespDto> tree(String keyword) {
        QueryWrapper q = QueryWrapper.create().where(MENU_ENTITY.DELETED.eq(false));
        if (keyword != null && !keyword.isBlank())
            q.and(MENU_ENTITY.MENU_NAME.like(keyword).or(MENU_ENTITY.PERMISSION_CODE.like(keyword)));
        return build(menuMapper.selectListByQuery(q.orderBy(MENU_ENTITY.SORT.asc()).orderBy(MENU_ENTITY.ID.asc())));
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public MenuRespDto detail(Long id) {
        return toResp(require(id));
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req MenuCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = {"menu", "rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "MENU", type = "CREATE", description = "创建全局 Menu")
    public MenuRespDto create(MenuCreateReqDto req) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        validateParent(null, req.parentId());
        MenuEntity m = converter.convert(req, MenuEntity.class);
        m.setMenuType(req.menuType().name());
        AuditEntitySupport.created(m, LoginUserUtils.getUserId());
        menuMapper.insert(m);
        return toResp(m);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req MenuUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = {"menu", "rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "MENU", type = "UPDATE", description = "修改全局 Menu")
    public MenuRespDto update(Long id, MenuUpdateReqDto req) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        MenuEntity m = require(id);
        validateParent(id, req.parentId());
        fill(m, req.menuType().name(), req.parentId(), req.menuName(), req.routeName(), req.routePath(), req.componentPath(), req.externalLink(), req.permissionCode(), req.icon(), req.i18nKey(), req.sort(), req.status(), req.visible(), req.keepAlive());
        AuditEntitySupport.updated(m, LoginUserUtils.getUserId());
        menuMapper.update(m);
        invalidateMenuUsers(id);
        return toResp(m);
    }

    /**
     * 修改业务状态并刷新相关缓存或会话。
     *
     * @param id  主键 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @CacheEvict(cacheNames = {"menu", "rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "MENU", type = "STATUS", description = "修改全局 Menu 状态")
    public void updateStatus(Long id, StatusUpdateReqDto req) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        MenuEntity m = require(id);
        m.setStatus(req.status());
        AuditEntitySupport.updated(m, LoginUserUtils.getUserId());
        menuMapper.update(m);
        invalidateMenuUsers(id);
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @CacheEvict(cacheNames = {"menu", "rolePermission", "userPermission"}, allEntries = true)
    @OperationAudit(module = "MENU", type = "DELETE", description = "删除全局 Menu")
    public void delete(Long id) {
        accessControlSupport.requirePlatformAdmin();
        accessControlSupport.requirePlatformContext();
        MenuEntity m = require(id);
        if (menuMapper.selectCountByQuery(QueryWrapper.create().where(MENU_ENTITY.PARENT_ID.eq(id)).and(MENU_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "请先删除子 Menu");
        m.setDeleted(true);
        AuditEntitySupport.updated(m, LoginUserUtils.getUserId());
        menuMapper.update(m);
        invalidateMenuUsers(id);
    }

    /**
     * 根据当前登录用户角色授权构建可访问的动态菜单树。
     *
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<MenuRespDto> currentUserTree() {
        var u = LoginUserUtils.getLoginUser();
        if (u.contextType() == ContextType.PLATFORM && u.platformAdmin())
            return build(menuMapper.selectListByQuery(QueryWrapper.create().where(MENU_ENTITY.DELETED.eq(false)).and(MENU_ENTITY.STATUS.eq(1)).and(MENU_ENTITY.MENU_TYPE.ne("BUTTON")).orderBy(MENU_ENTITY.SORT.asc())));
        if (u.tenantId() == null) return List.of();
        if (u.platformAdmin())
            return build(menuMapper.selectListByQuery(QueryWrapper.create().where(MENU_ENTITY.DELETED.eq(false)).and(MENU_ENTITY.STATUS.eq(1)).and(MENU_ENTITY.MENU_TYPE.ne("BUTTON")).orderBy(MENU_ENTITY.SORT.asc())));
        List<UserRoleEntity> roles = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(u.tenantId())).and(USER_ROLE_ENTITY.USER_ID.eq(u.userId())));
        if (roles.isEmpty()) return List.of();
        Set<Long> roleIds = new HashSet<>();
        roles.forEach(x -> roleIds.add(x.getRoleId()));
        Set<Long> menuIds = new HashSet<>();
        roleMenuMapper.selectListByQuery(QueryWrapper.create().where(ROLE_MENU_ENTITY.TENANT_ID.eq(u.tenantId())).and(ROLE_MENU_ENTITY.ROLE_ID.in(roleIds))).forEach(x -> menuIds.add(x.getMenuId()));
        if (menuIds.isEmpty()) return List.of();
        return build(menuMapper.selectListByQuery(QueryWrapper.create().where(MENU_ENTITY.ID.in(menuIds)).and(MENU_ENTITY.DELETED.eq(false)).and(MENU_ENTITY.STATUS.eq(1)).and(MENU_ENTITY.MENU_TYPE.ne("BUTTON")).orderBy(MENU_ENTITY.SORT.asc())));
    }

    /**
     * 使受菜单权限变化影响的在线用户会话失效。
     *
     * @param menuId 菜单 ID
     */
    private void invalidateMenuUsers(Long menuId) {
        List<RoleMenuEntity> links = roleMenuMapper.selectListByQuery(QueryWrapper.create().where(ROLE_MENU_ENTITY.MENU_ID.eq(menuId)));
        Map<Long, Set<Long>> rolesByTenant = new HashMap<>();
        for (RoleMenuEntity link : links)
            rolesByTenant.computeIfAbsent(link.getTenantId(), k -> new HashSet<>()).add(link.getRoleId());
        for (var entry : rolesByTenant.entrySet()) {
            List<Long> userIds = userRoleMapper.selectListByQuery(QueryWrapper.create().where(USER_ROLE_ENTITY.TENANT_ID.eq(entry.getKey())).and(USER_ROLE_ENTITY.ROLE_ID.in(entry.getValue()))).stream().map(UserRoleEntity::getUserId).distinct().toList();
            onlineUserService.invalidateTenantUsers(entry.getKey(), userIds);
        }
    }

    /**
     * 将已校验的业务字段写入目标实体。
     *
     * @param m            菜单实体
     * @param type         类型
     * @param parent       父节点
     * @param name         名称
     * @param rn           路由名称
     * @param rp           路由路径
     * @param cp           组件路径
     * @param externalLink 外链地址或外链标识
     * @param perm         权限编码
     * @param icon         菜单图标
     * @param i18n         国际化 Key
     * @param sort         排序值，数值越小越靠前
     * @param status       业务状态
     * @param visible      菜单是否可见
     * @param keep         是否启用 KeepAlive
     */
    private void fill(MenuEntity m, String type, Long parent, String name, String rn, String rp, String cp, String externalLink, String perm, String icon, String i18n, Integer sort, Integer status, Boolean visible, Boolean keep) {
        m.setMenuType(type);
        m.setParentId(parent);
        m.setMenuName(name);
        m.setRouteName(rn);
        m.setRoutePath(rp);
        m.setComponentPath(cp);
        m.setExternalLink(externalLink);
        m.setPermissionCode(perm);
        m.setIcon(icon);
        m.setI18nKey(i18n);
        m.setSort(sort);
        m.setStatus(status);
        m.setVisible(visible == null || visible);
        m.setKeepAlive(keep != null && keep);
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private MenuEntity require(Long id) {
        MenuEntity m = menuMapper.selectOneByQuery(QueryWrapper.create().where(MENU_ENTITY.ID.eq(id)).and(MENU_ENTITY.DELETED.eq(false)));
        if (m == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "Menu 不存在");
        return m;
    }

    /**
     * 校验父节点存在性、Tenant 归属和循环层级。
     *
     * @param self   当前菜单 ID
     * @param parent 父节点
     */
    private void validateParent(Long self, Long parent) {
        if (parent == null) return;
        MenuEntity p = require(parent);
        Long cursor = p.getId();
        Set<Long> seen = new HashSet<>();
        while (cursor != null && seen.add(cursor)) {
            if (Objects.equals(cursor, self))
                throw new BusinessException(CommonResultCode.CONFLICT, "Menu 层级形成循环");
            MenuEntity c = menuMapper.selectOneById(cursor);
            cursor = c == null ? null : c.getParentId();
        }
    }

    /**
     * 构建业务响应对象。
     *
     * @param list 待构建的菜单实体集合
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<MenuRespDto> build(List<MenuEntity> list) {
        Map<Long, MenuRespDto> map = new LinkedHashMap<>();
        for (MenuEntity e : list) map.put(e.getId(), toResp(e));
        List<MenuRespDto> roots = new ArrayList<>();
        for (MenuEntity e : list) {
            MenuRespDto n = map.get(e.getId()), p = e.getParentId() == null ? null : map.get(e.getParentId());
            if (p == null) roots.add(n);
            else p.children.add(n);
        }
        return roots;
    }

    /**
     * 将实体转换为响应 DTO。
     *
     * @param e 菜单实体
     * @return 业务响应 DTO。
     */
    private MenuRespDto toResp(MenuEntity e) {
        return converter.convert(e, MenuRespDto.class);
    }
}
