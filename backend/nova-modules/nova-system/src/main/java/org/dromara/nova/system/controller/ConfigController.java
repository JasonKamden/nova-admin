package org.dromara.nova.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.ConfigCreateReqDto;
import org.dromara.nova.system.dto.request.ConfigUpdateReqDto;
import org.dromara.nova.system.dto.response.ConfigRespDto;
import org.dromara.nova.system.service.ConfigService;
import org.springframework.web.bind.annotation.*;

/**
 * Tenant 参数管理。
 */
@Tag(name = "参数管理", description = "维护当前 Tenant 的业务参数配置；敏感参数值由后端脱敏。")
@RestController
@RequestMapping("/api/system/configs")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigService configService;

    /**
     * 分页查询参数配置。
     *
     * @param pageNum    页码，从 1 开始
     * @param pageSize   每页条数
     * @param keyword    模糊搜索关键字
     * @param configType 参数类型
     * @param status     状态
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:config:list")
    @Operation(summary = "分页查询参数配置", description = "分页查询参数配置。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<ConfigRespDto>> page(@Parameter(description = "页码，从 1 开始", required = false) @RequestParam(defaultValue = "1") long pageNum, @Parameter(description = "每页条数", required = false) @RequestParam(defaultValue = "10") long pageSize, @Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword, @Parameter(description = "参数类型", required = false) @RequestParam(required = false) String configType, @Parameter(description = "状态", required = false) @RequestParam(required = false) Integer status) {
        return R.ok(configService.page(pageNum, pageSize, keyword, configType, status));
    }

    /**
     * 新增参数配置。
     *
     * @param req ConfigCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("system:config:add")
    @Operation(summary = "新增参数配置", description = "新增参数配置。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<ConfigRespDto> create(@Valid @RequestBody ConfigCreateReqDto req) {
        return R.ok(configService.create(req));
    }

    /**
     * 修改参数配置。
     *
     * @param id  参数 ID
     * @param req ConfigUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:config:update")
    @Operation(summary = "修改参数配置", description = "修改参数配置。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<ConfigRespDto> update(@Parameter(description = "参数 ID", required = true) @PathVariable Long id, @Valid @RequestBody ConfigUpdateReqDto req) {
        return R.ok(configService.update(id, req));
    }

    /**
     * 删除参数配置。
     *
     * @param id 参数 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:config:delete")
    @Operation(summary = "删除参数配置", description = "删除参数配置。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "参数 ID", required = true) @PathVariable Long id) {
        configService.delete(id);
        return R.ok();
    }
}
