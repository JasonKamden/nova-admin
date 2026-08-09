package org.dromara.nova.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.common.web.util.IpUtils;
import org.dromara.nova.common.web.util.ServletUtils;
import org.dromara.nova.system.dto.request.LoginLogPageReqDto;
import org.dromara.nova.system.dto.response.LoginLogRespDto;
import org.dromara.nova.system.entity.LoginLogEntity;
import org.dromara.nova.system.mapper.LoginLogMapper;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.LoginLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.dromara.nova.system.entity.table.LoginLogEntityTableDef.LOGIN_LOG_ENTITY;

/**
 * 登录日志只追加、只读查询。
 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {
    private final LoginLogMapper loginLogMapper;
    private final Converter converter;
    private final DataScopeService dataScopeService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req LoginLogPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<LoginLogRespDto> page(LoginLogPageReqDto req) {
        QueryWrapper query = QueryWrapper.create();
        if (TenantContextSupport.current().isTenant()) {
            query.where(LOGIN_LOG_ENTITY.TENANT_ID.eq(TenantContextSupport.requireTenantId()));
            applyScope(query, dataScopeService.current());
        } else {
            query.where(LOGIN_LOG_ENTITY.CONTEXT_TYPE.eq("PLATFORM").or(LOGIN_LOG_ENTITY.CONTEXT_TYPE.isNull()));
        }
        if (req.username() != null && !req.username().isBlank())
            query.and(LOGIN_LOG_ENTITY.USERNAME.like(req.username()));
        if (req.loginStatus() != null) query.and(LOGIN_LOG_ENTITY.LOGIN_STATUS.eq(req.loginStatus()));
        if (req.ip() != null && !req.ip().isBlank()) query.and(LOGIN_LOG_ENTITY.IP.like(req.ip()));
        if (req.startTime() != null) query.and(LOGIN_LOG_ENTITY.LOGIN_TIME.ge(req.startTime()));
        if (req.endTime() != null) query.and(LOGIN_LOG_ENTITY.LOGIN_TIME.le(req.endTime()));
        Page<LoginLogEntity> page = loginLogMapper.paginate(req.pageNum(), req.pageSize(), query.orderBy(LOGIN_LOG_ENTITY.LOGIN_TIME.desc()));
        return PageResult.of(converter.convert(page.getRecords(), LoginLogRespDto.class), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

    /**
     * 写入登录审计记录。
     *
     * @param userId      用户 ID
     * @param username    登录账号
     * @param contextType 运行上下文类型：PLATFORM 或 TENANT
     * @param tenantId    Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param success     成功数量
     * @param reason      登录失败原因
     */
    @Override
    public void record(Long userId, String username, String contextType, Long tenantId, boolean success, String reason) {
        LoginLogEntity entity = new LoginLogEntity();
        entity.setUserId(userId);
        entity.setUsername(username);
        entity.setContextType(contextType);
        entity.setTenantId(tenantId);
        try {
            entity.setDepartmentId(LoginUserUtils.getLoginUser().departmentId());
        } catch (Exception ignored) {
        }
        entity.setLoginType("PASSWORD");
        entity.setLoginStatus(success ? 1 : 0);
        entity.setIp(IpUtils.currentIp());
        entity.setUserAgent(ServletUtils.userAgent());
        entity.setLoginTime(LocalDateTime.now());
        entity.setFailureReason(reason);
        entity.setRequestId(ServletUtils.requestId());
        loginLogMapper.insert(entity);
    }

    /**
     * 将当前 DataScope 规则应用到查询条件。
     *
     * @param query MyBatis-Flex 查询条件
     * @param rule  当前用户解析后的 DataScope 规则
     */
    private void applyScope(QueryWrapper query, DataScopeRule rule) {
        if (rule.allTenant()) return;
        if (!rule.departmentIds().isEmpty() && rule.selfUserId() != null)
            query.and(LOGIN_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()).or(LOGIN_LOG_ENTITY.USER_ID.eq(rule.selfUserId())));
        else if (!rule.departmentIds().isEmpty()) query.and(LOGIN_LOG_ENTITY.DEPARTMENT_ID.in(rule.departmentIds()));
        else if (rule.selfUserId() != null) query.and(LOGIN_LOG_ENTITY.USER_ID.eq(rule.selfUserId()));
        else query.and(LOGIN_LOG_ENTITY.ID.eq(-1));
    }

}
