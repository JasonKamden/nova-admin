package org.dromara.nova.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.system.dto.response.ContextOptionsRespDto;
import org.dromara.nova.system.dto.response.CurrentContextRespDto;
import org.dromara.nova.system.service.ContextService;
import org.springframework.web.bind.annotation.*;

/**
 * PLATFORM/TENANT ContextSwitcher API。
 */
@Tag(name = "运行上下文", description = "查询和切换 PLATFORM/TENANT Context。")
@RestController
@RequestMapping("/api/context")
@RequiredArgsConstructor
public class ContextController {
    private final ContextService contextService;

    /**
     * 当前 Context。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/current")
    @Operation(summary = "当前 Context", description = "当前 Context。接口执行服务端权限、Context 和业务规则校验。")
    public R<CurrentContextRespDto> current() {
        return R.ok(contextService.current());
    }

    /**
     * 可切换 Context。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/options")
    @Operation(summary = "可切换 Context", description = "可切换 Context。接口执行服务端权限、Context 和业务规则校验。")
    public R<ContextOptionsRespDto> options() {
        return R.ok(contextService.options());
    }

    /**
     * 切换到 PLATFORM。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/platform")
    @Operation(summary = "切换到 PLATFORM", description = "切换到 PLATFORM。接口执行服务端权限、Context 和业务规则校验。")
    @OperationAudit(module = "CONTEXT", type = "SWITCH", description = "切换到 PLATFORM")
    public R<CurrentContextRespDto> platform() {
        return R.ok(contextService.switchToPlatform());
    }

    /**
     * 切换到 Tenant。
     *
     * @param tenantId Tenant ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/tenant/{tenantId}")
    @Operation(summary = "切换到 Tenant", description = "切换到 Tenant。接口执行服务端权限、Context 和业务规则校验。")
    @OperationAudit(module = "CONTEXT", type = "SWITCH", description = "切换 Tenant")
    public R<CurrentContextRespDto> tenant(@Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId) {
        return R.ok(contextService.switchToTenant(tenantId));
    }
}
