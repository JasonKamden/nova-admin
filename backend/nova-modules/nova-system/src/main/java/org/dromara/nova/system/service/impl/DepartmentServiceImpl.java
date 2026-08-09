package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.dto.request.DepartmentCreateReqDto;
import org.dromara.nova.system.dto.request.DepartmentQueryReqDto;
import org.dromara.nova.system.dto.request.DepartmentUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.DepartmentRespDto;
import org.dromara.nova.system.entity.DepartmentEntity;
import org.dromara.nova.system.mapper.DepartmentMapper;
import org.dromara.nova.system.mapper.UserTenantMapper;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.DepartmentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.dromara.nova.system.entity.table.DepartmentEntityTableDef.DEPARTMENT_ENTITY;
import static org.dromara.nova.system.entity.table.UserTenantEntityTableDef.USER_TENANT_ENTITY;

/**
 * Tenant Department 树维护。
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final UserTenantMapper userTenantMapper;
    private final Converter converter;
    private final DataScopeService dataScopeService;

    /**
     * 查询当前 Tenant 和 DataScope 范围内的树形数据。
     *
     * @param req DepartmentQueryReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<DepartmentRespDto> tree(DepartmentQueryReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false));
        if (req.keyword() != null && !req.keyword().isBlank())
            q.and(DEPARTMENT_ENTITY.DEPARTMENT_CODE.like(req.keyword()).or(DEPARTMENT_ENTITY.DEPARTMENT_NAME.like(req.keyword())));
        if (req.status() != null) q.and(DEPARTMENT_ENTITY.STATUS.eq(req.status()));
        DataScopeRule rule = dataScopeService.current();
        if (!rule.allTenant()) {
            Set<Long> ids = new HashSet<>(rule.departmentIds());
            if (ids.isEmpty() && LoginUserUtils.getDepartmentId() != null) ids.add(LoginUserUtils.getDepartmentId());
            if (ids.isEmpty()) return List.of();
            q.and(DEPARTMENT_ENTITY.ID.in(ids));
        }
        List<DepartmentEntity> entities = departmentMapper.selectListByQuery(q.orderBy(DEPARTMENT_ENTITY.SORT.asc()).orderBy(DEPARTMENT_ENTITY.ID.asc()));
        return buildTree(entities);
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public DepartmentRespDto detail(Long id) {
        return toResp(require(id));
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req DepartmentCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = "departmentTree", allEntries = true)
    @OperationAudit(module = "DEPARTMENT", type = "CREATE", description = "新增 Department")
    public DepartmentRespDto create(DepartmentCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        validateParent(tenantId, null, req.parentId());
        if (departmentMapper.selectCountByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DEPARTMENT_CODE.eq(req.departmentCode())).and(DEPARTMENT_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "Department 编码已存在");
        DepartmentEntity e = converter.convert(req, DepartmentEntity.class);
        e.setTenantId(tenantId);
        AuditEntitySupport.created(e, LoginUserUtils.getUserId());
        departmentMapper.insert(e);
        return toResp(e);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req DepartmentUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = "departmentTree", allEntries = true)
    @OperationAudit(module = "DEPARTMENT", type = "UPDATE", description = "修改 Department")
    public DepartmentRespDto update(Long id, DepartmentUpdateReqDto req) {
        DepartmentEntity e = require(id);
        validateParent(e.getTenantId(), id, req.parentId());
        converter.convert(req, e);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        departmentMapper.update(e);
        return toResp(e);
    }

    /**
     * 修改业务状态并刷新相关缓存或会话。
     *
     * @param id  主键 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @CacheEvict(cacheNames = "departmentTree", allEntries = true)
    @OperationAudit(module = "DEPARTMENT", type = "STATUS", description = "修改 Department 状态")
    public void updateStatus(Long id, StatusUpdateReqDto req) {
        DepartmentEntity e = require(id);
        e.setStatus(req.status());
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        departmentMapper.update(e);
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @CacheEvict(cacheNames = "departmentTree", allEntries = true)
    @OperationAudit(module = "DEPARTMENT", type = "DELETE", description = "删除 Department")
    public void delete(Long id) {
        DepartmentEntity e = require(id);
        if (departmentMapper.selectCountByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.TENANT_ID.eq(e.getTenantId())).and(DEPARTMENT_ENTITY.PARENT_ID.eq(id)).and(DEPARTMENT_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "请先删除下级 Department");
        if (userTenantMapper.selectCountByQuery(QueryWrapper.create().where(USER_TENANT_ENTITY.TENANT_ID.eq(e.getTenantId())).and(USER_TENANT_ENTITY.DEPARTMENT_ID.eq(id)).and(USER_TENANT_ENTITY.STATUS.eq(1))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "Department 下仍有有效成员");
        e.setDeleted(true);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        departmentMapper.update(e);
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private DepartmentEntity require(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        DepartmentEntity e = departmentMapper.selectOneByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(id)).and(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "Department 不存在");
        return e;
    }

    /**
     * 校验父节点存在性、Tenant 归属和循环层级。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param selfId   当前节点 ID
     * @param parentId 父 Department/菜单节点 ID；根节点可为空
     */
    private void validateParent(Long tenantId, Long selfId, Long parentId) {
        if (parentId == null) return;
        DepartmentEntity p = departmentMapper.selectOneByQuery(QueryWrapper.create().where(DEPARTMENT_ENTITY.ID.eq(parentId)).and(DEPARTMENT_ENTITY.TENANT_ID.eq(tenantId)).and(DEPARTMENT_ENTITY.DELETED.eq(false)));
        if (p == null) throw new BusinessException(CommonResultCode.BAD_REQUEST, "父 Department 不存在");
        Long cursor = parentId;
        Set<Long> seen = new HashSet<>();
        while (cursor != null && seen.add(cursor)) {
            if (Objects.equals(cursor, selfId))
                throw new BusinessException(CommonResultCode.CONFLICT, "Department 层级形成循环");
            DepartmentEntity current = departmentMapper.selectOneById(cursor);
            cursor = current == null ? null : current.getParentId();
        }
    }

    /**
     * 将 Department 实体集合构建为层级树响应。
     *
     * @param entities Department 实体集合
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<DepartmentRespDto> buildTree(List<DepartmentEntity> entities) {
        Map<Long, DepartmentRespDto> map = new LinkedHashMap<>();
        for (DepartmentEntity e : entities) map.put(e.getId(), toResp(e));
        List<DepartmentRespDto> roots = new ArrayList<>();
        for (DepartmentEntity e : entities) {
            DepartmentRespDto node = map.get(e.getId());
            DepartmentRespDto parent = e.getParentId() == null ? null : map.get(e.getParentId());
            if (parent == null) roots.add(node);
            else parent.getChildren().add(node);
        }
        return roots;
    }

    /**
     * 将实体转换为响应 DTO。
     *
     * @param e Department 实体
     * @return 业务响应 DTO。
     */
    private DepartmentRespDto toResp(DepartmentEntity e) {
        return converter.convert(e, DepartmentRespDto.class);
    }
}
