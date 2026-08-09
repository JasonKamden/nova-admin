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
import org.dromara.nova.system.dto.request.DictDataCreateReqDto;
import org.dromara.nova.system.dto.request.DictDataUpdateReqDto;
import org.dromara.nova.system.dto.request.DictTypeCreateReqDto;
import org.dromara.nova.system.dto.request.DictTypeUpdateReqDto;
import org.dromara.nova.system.dto.response.DictDataRespDto;
import org.dromara.nova.system.dto.response.DictTypeRespDto;
import org.dromara.nova.system.entity.DictDataEntity;
import org.dromara.nova.system.entity.DictTypeEntity;
import org.dromara.nova.system.mapper.DictDataMapper;
import org.dromara.nova.system.mapper.DictTypeMapper;
import org.dromara.nova.system.service.DictionaryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.dromara.nova.system.entity.table.DictDataEntityTableDef.DICT_DATA_ENTITY;
import static org.dromara.nova.system.entity.table.DictTypeEntityTableDef.DICT_TYPE_ENTITY;

/**
 * Tenant 字典类型 + 字典数据维护。
 */
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {
    private static final Set<String> TAG_TYPES = Set.of("default", "primary", "info", "success", "warning", "error");
    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;
    private final Converter converter;

    /**
     * 查询当前 Tenant 字典类型列表。
     *
     * @param keyword 模糊搜索关键字
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<DictTypeRespDto> types(String keyword) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(DICT_TYPE_ENTITY.TENANT_ID.eq(tenantId)).and(DICT_TYPE_ENTITY.DELETED.eq(false));
        if (keyword != null && !keyword.isBlank())
            q.and(DICT_TYPE_ENTITY.DICT_NAME.like(keyword).or(DICT_TYPE_ENTITY.DICT_CODE.like(keyword)));
        return dictTypeMapper.selectListByQuery(q.orderBy(DICT_TYPE_ENTITY.ID.asc())).stream().map(this::typeResp).toList();
    }

    /**
     * 创建字典类型并清理相关字典缓存。
     *
     * @param req DictTypeCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    @OperationAudit(module = "DICTIONARY", type = "CREATE_TYPE", description = "新增字典类型")
    public DictTypeRespDto createType(DictTypeCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        if (dictTypeMapper.selectCountByQuery(QueryWrapper.create().where(DICT_TYPE_ENTITY.TENANT_ID.eq(tenantId)).and(DICT_TYPE_ENTITY.DICT_CODE.eq(req.dictCode())).and(DICT_TYPE_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "字典编码已存在");
        DictTypeEntity e = converter.convert(req, DictTypeEntity.class);
        e.setTenantId(tenantId);
        e.setBuiltIn(false);
        AuditEntitySupport.created(e, LoginUserUtils.getUserId());
        dictTypeMapper.insert(e);
        return typeResp(e);
    }

    /**
     * 修改字典类型并清理相关字典缓存。
     *
     * @param id  主键 ID
     * @param req DictTypeUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "DICTIONARY", type = "UPDATE_TYPE", description = "修改字典类型")
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    public DictTypeRespDto updateType(Long id, DictTypeUpdateReqDto req) {
        DictTypeEntity e = requireType(id);
        converter.convert(req, e);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        dictTypeMapper.update(e);
        return typeResp(e);
    }

    /**
     * 删除字典类型；存在字典数据或内置类型时拒绝删除。
     *
     * @param id 主键 ID
     */
    @Override
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    @OperationAudit(module = "DICTIONARY", type = "DELETE_TYPE", description = "删除字典类型")
    public void deleteType(Long id) {
        DictTypeEntity e = requireType(id);
        if (Boolean.TRUE.equals(e.getBuiltIn()))
            throw new BusinessException(CommonResultCode.CONFLICT, "系统内置字典不允许删除");
        if (dictDataMapper.selectCountByQuery(QueryWrapper.create().where(DICT_DATA_ENTITY.TENANT_ID.eq(e.getTenantId())).and(DICT_DATA_ENTITY.DICT_TYPE_ID.eq(id)).and(DICT_DATA_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "请先删除该类型下字典数据");
        e.setDeleted(true);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        dictTypeMapper.update(e);
    }

    /**
     * 分页查询指定字典类型下的字典数据。
     *
     * @param typeId   字典类型 ID
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页条数
     * @param label    字典标签
     * @param value    字典值
     * @param status   业务状态
     * @return 分页业务数据。
     */
    @Override
    public PageResult<DictDataRespDto> data(Long typeId, long pageNum, long pageSize, String label, String value, Integer status) {
        DictTypeEntity type = requireType(typeId);
        QueryWrapper q = QueryWrapper.create().where(DICT_DATA_ENTITY.TENANT_ID.eq(type.getTenantId())).and(DICT_DATA_ENTITY.DICT_TYPE_ID.eq(typeId)).and(DICT_DATA_ENTITY.DELETED.eq(false));
        if (label != null && !label.isBlank()) q.and(DICT_DATA_ENTITY.DICT_LABEL.like(label));
        if (value != null && !value.isBlank()) q.and(DICT_DATA_ENTITY.DICT_VALUE.like(value));
        if (status != null) q.and(DICT_DATA_ENTITY.STATUS.eq(status));
        Page<DictDataEntity> p = dictDataMapper.paginate(pageNum, pageSize, q.orderBy(DICT_DATA_ENTITY.SORT.asc()).orderBy(DICT_DATA_ENTITY.ID.asc()));
        return PageResult.of(converter.convert(p.getRecords(), DictDataRespDto.class), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 创建字典数据并清理相关字典缓存。
     *
     * @param typeId 字典类型 ID
     * @param req    DictDataCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "DICTIONARY", type = "CREATE_DATA", description = "新增字典数据")
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    public DictDataRespDto createData(Long typeId, DictDataCreateReqDto req) {
        DictTypeEntity type = requireType(typeId);
        validateTag(req.tagType());
        if (dictDataMapper.selectCountByQuery(QueryWrapper.create().where(DICT_DATA_ENTITY.TENANT_ID.eq(type.getTenantId())).and(DICT_DATA_ENTITY.DICT_TYPE_ID.eq(typeId)).and(DICT_DATA_ENTITY.DICT_VALUE.eq(req.dictValue())).and(DICT_DATA_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "字典值已存在");
        DictDataEntity e = converter.convert(req, DictDataEntity.class);
        e.setTenantId(type.getTenantId());
        e.setDictTypeId(typeId);
        AuditEntitySupport.created(e, LoginUserUtils.getUserId());
        dictDataMapper.insert(e);
        return dataResp(e);
    }

    /**
     * 修改字典数据并清理相关字典缓存。
     *
     * @param id  主键 ID
     * @param req DictDataUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "DICTIONARY", type = "UPDATE_DATA", description = "修改字典数据")
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    public DictDataRespDto updateData(Long id, DictDataUpdateReqDto req) {
        DictDataEntity e = requireData(id);
        validateTag(req.tagType());
        fill(e, req.dictLabel(), req.dictValue(), req.tagType(), req.sort(), req.status(), req.remark());
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        dictDataMapper.update(e);
        return dataResp(e);
    }

    /**
     * 删除字典数据并清理相关字典缓存。
     *
     * @param id 主键 ID
     */
    @Override
    @OperationAudit(module = "DICTIONARY", type = "DELETE_DATA", description = "删除字典数据")
    @CacheEvict(cacheNames = "dictionary", allEntries = true)
    public void deleteData(Long id) {
        DictDataEntity e = requireData(id);
        e.setDeleted(true);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        dictDataMapper.update(e);
    }

    /**
     * 加载当前 Tenant 字典类型。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private DictTypeEntity requireType(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        DictTypeEntity e = dictTypeMapper.selectOneByQuery(QueryWrapper.create().where(DICT_TYPE_ENTITY.ID.eq(id)).and(DICT_TYPE_ENTITY.TENANT_ID.eq(tenantId)).and(DICT_TYPE_ENTITY.DELETED.eq(false)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "字典类型不存在");
        return e;
    }

    /**
     * 加载当前 Tenant 字典数据。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private DictDataEntity requireData(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        DictDataEntity e = dictDataMapper.selectOneByQuery(QueryWrapper.create().where(DICT_DATA_ENTITY.ID.eq(id)).and(DICT_DATA_ENTITY.TENANT_ID.eq(tenantId)).and(DICT_DATA_ENTITY.DELETED.eq(false)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "字典数据不存在");
        return e;
    }

    /**
     * 将字典类型实体组装为响应 DTO。
     *
     * @param dictTypeEntity 字典类型实体
     * @return 业务响应 DTO。
     */
    private DictTypeRespDto typeResp(DictTypeEntity dictTypeEntity) {
        long dataCount = dictDataMapper.selectCountByQuery(QueryWrapper.create().where(DICT_DATA_ENTITY.TENANT_ID.eq(dictTypeEntity.getTenantId())).and(DICT_DATA_ENTITY.DICT_TYPE_ID.eq(dictTypeEntity.getId())).and(DICT_DATA_ENTITY.DELETED.eq(false)));
        DictTypeRespDto mapped = converter.convert(dictTypeEntity, DictTypeRespDto.class);
        return new DictTypeRespDto(mapped.id(), mapped.dictName(), mapped.dictCode(), mapped.builtIn(), mapped.status(), mapped.remark(), dataCount);
    }

    /**
     * 将字典数据实体转换为响应 DTO。
     *
     * @param e 字典实体
     * @return 业务响应 DTO。
     */
    private DictDataRespDto dataResp(DictDataEntity e) {
        return converter.convert(e, DictDataRespDto.class);
    }

    /**
     * 将已校验的业务字段写入目标实体。
     *
     * @param e  字典实体
     * @param l  字典标签
     * @param v  字典值
     * @param t  标签语义样式
     * @param s  排序值
     * @param st 状态值
     * @param r  备注
     */
    private void fill(DictDataEntity e, String l, String v, String t, Integer s, Integer st, String r) {
        e.setDictLabel(l);
        e.setDictValue(v);
        e.setTagType(t == null ? "default" : t);
        e.setSort(s == null ? 0 : s);
        e.setStatus(st);
        e.setRemark(r);
    }

    /**
     * 校验字典标签语义样式。
     *
     * @param t 标签语义样式
     */
    private void validateTag(String t) {
        if (t != null && !TAG_TYPES.contains(t))
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "不支持的标签样式");
    }
}
