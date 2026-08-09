package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.model.OperationAuditEvent;
import org.dromara.nova.common.log.service.OperationAuditSink;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.system.dto.request.OperationLogPageReqDto;
import org.dromara.nova.system.dto.response.OperationLogDetailRespDto;
import org.dromara.nova.system.dto.response.OperationLogRespDto;
import org.dromara.nova.system.entity.OperationLogEntity;
import org.dromara.nova.system.mapper.OperationLogMapper;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.OperationLogService;
import org.springframework.stereotype.Service;

import static org.dromara.nova.system.entity.table.OperationLogEntityTableDef.OPERATION_LOG_ENTITY;

/**
 * 操作日志持久化、分页与详情。
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService, OperationAuditSink {
    private final OperationLogMapper operationLogMapper;
    private final DataScopeService dataScopeService;
    private final Converter converter;

    /**
     * 持久化一次核心业务操作审计事件，并计算最终操作状态。
     *
     * @param a 已完成脱敏和截断处理的操作审计事件
     */
    @Override
    public void save(OperationAuditEvent a) {
        OperationLogEntity e = new OperationLogEntity();
        e.setModule(a.module());
        e.setOperationType(a.type());
        e.setOperationDescription(a.description());
        e.setUserId(a.userId());
        e.setUsername(a.username());
        e.setContextType(a.contextType());
        e.setTenantId(a.tenantId());
        e.setDepartmentId(a.departmentId());
        e.setRequestMethod(a.requestMethod());
        e.setRequestUri(a.requestUri());
        e.setRequestIp(a.requestIp());
        e.setUserAgent(a.userAgent());
        e.setContentType(a.contentType());
        e.setRequestHeaders(a.requestHeaders());
        e.setQueryParams(a.queryParams());
        e.setPathParams(a.pathParams());
        e.setRequestBody(a.requestBody());
        e.setHttpStatus(a.httpStatus());
        e.setBusinessCode(a.businessCode());
        e.setResponseBody(a.responseBody());
        e.setExceptionType(a.exceptionType());
        e.setErrorCode(a.errorCode());
        e.setErrorMessage(a.errorMessage());
        e.setExceptionLocation(a.exceptionLocation());
        e.setExceptionStack(a.exceptionStack());
        e.setOperationStatus(a.exceptionType() == null ? "SUCCESS" : "FAILED");
        e.setDurationMs(a.durationMs());
        e.setRequestId(a.requestId());
        e.setTraceId(a.traceId());
        e.setOperationTime(a.operationTime());
        operationLogMapper.insert(e);
    }

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req OperationLogPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<OperationLogRespDto> page(OperationLogPageReqDto req) {
        QueryWrapper q = QueryWrapper.create();
        if (TenantContextSupport.current().isTenant()) {
            q.where(OPERATION_LOG_ENTITY.TENANT_ID.eq(TenantContextSupport.requireTenantId()));
            DataScopeRule r = dataScopeService.current();
            if (!r.allTenant()) {
                if (!r.departmentIds().isEmpty() && r.selfUserId() != null)
                    q.and(OPERATION_LOG_ENTITY.DEPARTMENT_ID.in(r.departmentIds()).or(OPERATION_LOG_ENTITY.USER_ID.eq(r.selfUserId())));
                else if (!r.departmentIds().isEmpty()) q.and(OPERATION_LOG_ENTITY.DEPARTMENT_ID.in(r.departmentIds()));
                else if (r.selfUserId() != null) q.and(OPERATION_LOG_ENTITY.USER_ID.eq(r.selfUserId()));
            }
        } else q.where(OPERATION_LOG_ENTITY.CONTEXT_TYPE.eq("PLATFORM"));
        if (req.module() != null && !req.module().isBlank()) q.and(OPERATION_LOG_ENTITY.MODULE.eq(req.module()));
        if (req.operationType() != null && !req.operationType().isBlank())
            q.and(OPERATION_LOG_ENTITY.OPERATION_TYPE.eq(req.operationType()));
        if (req.operator() != null && !req.operator().isBlank())
            q.and(OPERATION_LOG_ENTITY.USERNAME.like(req.operator()));
        if (req.requestMethod() != null && !req.requestMethod().isBlank())
            q.and(OPERATION_LOG_ENTITY.REQUEST_METHOD.eq(req.requestMethod()));
        if (req.status() != null && !req.status().isBlank())
            q.and(OPERATION_LOG_ENTITY.OPERATION_STATUS.eq(req.status()));
        if (req.requestIp() != null && !req.requestIp().isBlank())
            q.and(OPERATION_LOG_ENTITY.REQUEST_IP.like(req.requestIp()));
        if (req.startTime() != null) q.and(OPERATION_LOG_ENTITY.OPERATION_TIME.ge(req.startTime()));
        if (req.endTime() != null) q.and(OPERATION_LOG_ENTITY.OPERATION_TIME.le(req.endTime()));
        Page<OperationLogEntity> p = operationLogMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(OPERATION_LOG_ENTITY.OPERATION_TIME.desc()));
        return PageResult.of(converter.convert(p.getRecords(), OperationLogRespDto.class), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public OperationLogDetailRespDto detail(Long id) {
        OperationLogEntity e = operationLogMapper.selectOneById(id);
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "操作日志不存在");
        if (TenantContextSupport.current().isTenant()) {
            if (!java.util.Objects.equals(e.getTenantId(), TenantContextSupport.requireTenantId()))
                throw new BusinessException(CommonResultCode.FORBIDDEN);
            DataScopeRule rule = dataScopeService.current();
            if (!rule.allTenant()) {
                boolean allowed = (rule.selfUserId() != null && java.util.Objects.equals(rule.selfUserId(), e.getUserId())) || (!rule.departmentIds().isEmpty() && e.getDepartmentId() != null && rule.departmentIds().contains(e.getDepartmentId()));
                if (!allowed) throw new BusinessException(CommonResultCode.FORBIDDEN);
            }
        }
        return new OperationLogDetailRespDto(new OperationLogDetailRespDto.Basic(e.getId(), e.getModule(), e.getOperationType(), e.getOperationDescription(), e.getUserId(), e.getUsername(), e.getContextType(), e.getTenantId(), e.getDepartmentId(), e.getRequestIp(), e.getUserAgent(), e.getDurationMs(), e.getRequestId(), e.getTraceId(), e.getOperationTime()), new OperationLogDetailRespDto.Request(e.getRequestMethod(), e.getRequestUri(), e.getContentType(), e.getRequestHeaders(), e.getQueryParams(), e.getPathParams(), e.getRequestBody()), new OperationLogDetailRespDto.Response(e.getHttpStatus(), e.getBusinessCode(), e.getResponseBody()), new OperationLogDetailRespDto.ExceptionInfo(e.getExceptionType(), e.getErrorCode(), e.getErrorMessage(), e.getExceptionLocation(), e.getExceptionStack()));
    }
}
