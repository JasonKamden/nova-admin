package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.OperationLogPageReqDto;
import org.dromara.nova.system.dto.response.OperationLogDetailRespDto;
import org.dromara.nova.system.dto.response.OperationLogRespDto;

/**
 * 核心业务操作审计持久化、分页和完整详情查询业务契约。
 */
public interface OperationLogService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<OperationLogRespDto> page(OperationLogPageReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    OperationLogDetailRespDto detail(Long id);
}
