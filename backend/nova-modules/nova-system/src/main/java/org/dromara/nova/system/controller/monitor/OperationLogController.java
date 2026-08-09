package org.dromara.nova.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.OperationLogPageReqDto;
import org.dromara.nova.system.dto.response.OperationLogDetailRespDto;
import org.dromara.nova.system.dto.response.OperationLogRespDto;
import org.dromara.nova.system.service.OperationLogService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志列表和完整详情。
 */
@Tag(name = "操作日志", description = "分页查询操作日志并查看脱敏请求、响应和异常详情。")
@RestController
@RequestMapping("/api/system/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {
    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志。
     *
     * @param req OperationLogPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("monitor:operation-log:list")
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<OperationLogRespDto>> page(@ParameterObject @Valid OperationLogPageReqDto req) {
        return R.ok(operationLogService.page(req));
    }

    /**
     * 查询操作日志完整详情。
     *
     * @param id 操作日志 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("monitor:operation-log:detail")
    @Operation(summary = "查询操作日志完整详情", description = "查询操作日志完整详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<OperationLogDetailRespDto> detail(@Parameter(description = "操作日志 ID", required = true) @PathVariable Long id) {
        return R.ok(operationLogService.detail(id));
    }
}
