package org.dromara.nova.system.controller.platform;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.request.TenantCreateReqDto;
import org.dromara.nova.system.dto.request.TenantPageReqDto;
import org.dromara.nova.system.dto.request.TenantUpdateReqDto;
import org.dromara.nova.system.dto.response.ContextTenantOptionRespDto;
import org.dromara.nova.system.dto.response.TenantRespDto;
import org.dromara.nova.system.service.TenantService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PLATFORM Tenant 管理。
 */
@Tag(name = "租户管理", description = "PLATFORM Context 下维护 Tenant 生命周期和 ContextSwitcher 候选项。")
@RestController
@RequestMapping("/api/platform/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    /**
     * Tenant 分页。
     *
     * @param req TenantPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("platform:tenant:list")
    @Operation(summary = "Tenant 分页", description = "Tenant 分页。接口执行服务端权限、Context 和业务规则校验。")
    public R<PageResult<TenantRespDto>> page(@ParameterObject @Valid TenantPageReqDto req) {
        return R.ok(tenantService.page(req));
    }

    /**
     * Tenant 详情。
     *
     * @param tenantId Tenant ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{tenantId}")
    @SaCheckPermission("platform:tenant:detail")
    @Operation(summary = "Tenant 详情", description = "Tenant 详情。接口执行服务端权限、Context 和业务规则校验。")
    public R<TenantRespDto> detail(@Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId) {
        return R.ok(tenantService.detail(tenantId));
    }

    /**
     * ContextSwitcher Tenant 远程搜索。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/options")
    @Operation(summary = "ContextSwitcher Tenant 远程搜索", description = "ContextSwitcher Tenant 远程搜索。接口执行服务端权限、Context 和业务规则校验。")
    public R<List<ContextTenantOptionRespDto>> options(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(tenantService.searchOptions(keyword));
    }

    /**
     * 创建 Tenant。
     *
     * @param req TenantCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("platform:tenant:add")
    @Operation(summary = "创建 Tenant", description = "创建 Tenant。接口执行服务端权限、Context 和业务规则校验。")
    public R<TenantRespDto> create(@Valid @RequestBody TenantCreateReqDto req) {
        return R.ok(tenantService.create(req));
    }

    /**
     * 修改 Tenant。
     *
     * @param tenantId Tenant ID
     * @param req      TenantUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{tenantId}")
    @SaCheckPermission("platform:tenant:update")
    @Operation(summary = "修改 Tenant", description = "修改 Tenant。接口执行服务端权限、Context 和业务规则校验。")
    public R<TenantRespDto> update(@Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId, @Valid @RequestBody TenantUpdateReqDto req) {
        return R.ok(tenantService.update(tenantId, req));
    }

    /**
     * Tenant 启用/停用。
     *
     * @param tenantId Tenant ID
     * @param req      StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{tenantId}/status")
    @SaCheckPermission("platform:tenant:update")
    @Operation(summary = "Tenant 启用/停用", description = "Tenant 启用/停用。接口执行服务端权限、Context 和业务规则校验。")
    public R<Void> status(@Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId, @Valid @RequestBody StatusUpdateReqDto req) {
        tenantService.updateStatus(tenantId, req);
        return R.ok();
    }

    /**
     * 删除 Tenant。
     *
     * @param tenantId Tenant ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{tenantId}")
    @SaCheckPermission("platform:tenant:delete")
    @Operation(summary = "删除 Tenant", description = "删除 Tenant。接口执行服务端权限、Context 和业务规则校验。")
    public R<Void> delete(@Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId) {
        tenantService.delete(tenantId);
        return R.ok();
    }
}
