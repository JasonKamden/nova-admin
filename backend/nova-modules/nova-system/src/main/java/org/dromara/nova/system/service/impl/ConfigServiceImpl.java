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
import org.dromara.nova.system.dto.request.ConfigCreateReqDto;
import org.dromara.nova.system.dto.request.ConfigUpdateReqDto;
import org.dromara.nova.system.dto.response.ConfigRespDto;
import org.dromara.nova.system.entity.ConfigEntity;
import org.dromara.nova.system.mapper.ConfigMapper;
import org.dromara.nova.system.service.ConfigService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static org.dromara.nova.system.entity.table.ConfigEntityTableDef.CONFIG_ENTITY;

/**
 * Tenant 参数配置。
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final ConfigMapper configMapper;
    private final Converter converter;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页条数
     * @param keyword  模糊搜索关键字
     * @param type     类型
     * @param status   业务状态
     * @return 分页业务数据。
     */
    @Override
    public PageResult<ConfigRespDto> page(long pageNum, long pageSize, String keyword, String type, Integer status) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(CONFIG_ENTITY.TENANT_ID.eq(tenantId)).and(CONFIG_ENTITY.DELETED.eq(false));
        if (keyword != null && !keyword.isBlank())
            q.and(CONFIG_ENTITY.CONFIG_NAME.like(keyword).or(CONFIG_ENTITY.CONFIG_CODE.like(keyword)));
        if (type != null && !type.isBlank()) q.and(CONFIG_ENTITY.CONFIG_TYPE.eq(type));
        if (status != null) q.and(CONFIG_ENTITY.STATUS.eq(status));
        Page<ConfigEntity> p = configMapper.paginate(pageNum, pageSize, q.orderBy(CONFIG_ENTITY.ID.desc()));
        return PageResult.of(p.getRecords().stream().map(this::resp).toList(), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req ConfigCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @CacheEvict(cacheNames = "config", allEntries = true)
    @OperationAudit(module = "CONFIG", type = "CREATE", description = "新增参数")
    public ConfigRespDto create(ConfigCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        if (configMapper.selectCountByQuery(QueryWrapper.create().where(CONFIG_ENTITY.TENANT_ID.eq(tenantId)).and(CONFIG_ENTITY.CONFIG_CODE.eq(req.configCode())).and(CONFIG_ENTITY.DELETED.eq(false))) > 0)
            throw new BusinessException(CommonResultCode.DUPLICATE, "参数编码已存在");
        ConfigEntity e = converter.convert(req, ConfigEntity.class);
        e.setTenantId(tenantId);
        e.setConfigType(req.configType().name());
        e.setSensitive(Boolean.TRUE.equals(req.sensitive()));
        e.setBuiltIn(false);
        AuditEntitySupport.created(e, LoginUserUtils.getUserId());
        configMapper.insert(e);
        return resp(e);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req ConfigUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "CONFIG", type = "UPDATE", description = "修改参数")
    @CacheEvict(cacheNames = "config", allEntries = true)
    public ConfigRespDto update(Long id, ConfigUpdateReqDto req) {
        ConfigEntity e = require(id);
        converter.convert(req, e);
        e.setConfigType(req.configType().name());
        e.setSensitive(Boolean.TRUE.equals(req.sensitive()));
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        configMapper.update(e);
        return resp(e);
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @OperationAudit(module = "CONFIG", type = "DELETE", description = "删除参数")
    @CacheEvict(cacheNames = "config", allEntries = true)
    public void delete(Long id) {
        ConfigEntity e = require(id);
        if (Boolean.TRUE.equals(e.getBuiltIn()))
            throw new BusinessException(CommonResultCode.CONFLICT, "内置参数不允许删除");
        e.setDeleted(true);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        configMapper.update(e);
    }

    /**
     * 读取当前 Tenant 已启用参数值并使用业务缓存。
     *
     * @param code 编码
     * @return 方法处理结果。
     */
    @Override
    @Cacheable(cacheNames = "config", key = "T(org.dromara.nova.common.tenant.context.TenantContextSupport).requireTenantId() + ':' + #code")
    public String getValue(String code) {
        Long tenantId = TenantContextSupport.requireTenantId();
        ConfigEntity e = configMapper.selectOneByQuery(QueryWrapper.create().where(CONFIG_ENTITY.TENANT_ID.eq(tenantId)).and(CONFIG_ENTITY.CONFIG_CODE.eq(code)).and(CONFIG_ENTITY.STATUS.eq(1)).and(CONFIG_ENTITY.DELETED.eq(false)));
        return e == null ? null : e.getConfigValue();
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private ConfigEntity require(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        ConfigEntity e = configMapper.selectOneByQuery(QueryWrapper.create().where(CONFIG_ENTITY.ID.eq(id)).and(CONFIG_ENTITY.TENANT_ID.eq(tenantId)).and(CONFIG_ENTITY.DELETED.eq(false)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "参数不存在");
        return e;
    }

    /**
     * 将实体转换为对外响应 DTO。
     *
     * @param e 参数配置实体
     * @return 业务响应 DTO。
     */
    private ConfigRespDto resp(ConfigEntity e) {
        ConfigRespDto mapped = converter.convert(e, ConfigRespDto.class);
        String value = Boolean.TRUE.equals(e.getSensitive()) ? "******" : mapped.configValue();
        return new ConfigRespDto(mapped.id(), mapped.configName(), mapped.configCode(), value, mapped.configType(), mapped.sensitive(), mapped.builtIn(), mapped.status(), mapped.remark(), mapped.updateTime());
    }
}
