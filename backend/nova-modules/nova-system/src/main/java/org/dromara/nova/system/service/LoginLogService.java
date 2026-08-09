package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.LoginLogPageReqDto;
import org.dromara.nova.system.dto.response.LoginLogRespDto;

/**
 * 登录审计日志记录与只读查询业务契约。
 */
public interface LoginLogService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<LoginLogRespDto> page(LoginLogPageReqDto request);

    /**
     * 记录一次登录成功或失败审计信息，不记录明文密码。
     */
    void record(Long userId, String username, String contextType, Long tenantId, boolean success, String failureReason);
}
