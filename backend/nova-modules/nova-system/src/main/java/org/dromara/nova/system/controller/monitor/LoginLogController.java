package org.dromara.nova.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.LoginLogPageReqDto;
import org.dromara.nova.system.dto.response.LoginLogRespDto;
import org.dromara.nova.system.service.LoginLogService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志只读查询。
 */
@Tag(name = "登录日志", description = "只读查询当前授权范围内的登录日志。")
@RestController
@RequestMapping("/api/system/login-logs")
@RequiredArgsConstructor
public class LoginLogController {
    private final LoginLogService loginLogService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。。
     *
     * @param req LoginLogPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("monitor:login-log:list")
    @Operation(summary = "分页查询登录日志", description = "分页查询登录日志。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<LoginLogRespDto>> page(@ParameterObject @Valid LoginLogPageReqDto req) {
        return R.ok(loginLogService.page(req));
    }
}
