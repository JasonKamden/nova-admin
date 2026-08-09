package org.dromara.nova.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.*;
import org.dromara.nova.system.dto.response.RoleRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.service.RoleService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tenant Role、DataScope、Menu 授权。
 */
@Tag(name = "角色管理", description = "维护当前 Tenant 的角色、DataScope 和菜单授权。")
@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    /**
     * 分页查询角色。
     *
     * @param req RolePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:role:list")
    @Operation(summary = "分页查询角色", description = "分页查询角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<RoleRespDto>> page(@ParameterObject @Valid RolePageReqDto req) {
        return R.ok(roleService.page(req));
    }

    /**
     * 查询角色选择项。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/options")
    @SaCheckPermission("system:role:list")
    @Operation(summary = "查询角色选择项", description = "查询角色选择项。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<RoleSimpleRespDto>> options(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(roleService.options(keyword));
    }

    /**
     * 查询角色详情。
     *
     * @param id 角色 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:role:list")
    @Operation(summary = "查询角色详情", description = "查询角色详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<RoleRespDto> detail(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.detail(id));
    }

    /**
     * 新增角色。
     *
     * @param req RoleCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("system:role:add")
    @Operation(summary = "新增角色", description = "新增角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<RoleRespDto> create(@Valid @RequestBody RoleCreateReqDto req) {
        return R.ok(roleService.create(req));
    }

    /**
     * 修改角色。
     *
     * @param id  角色 ID
     * @param req RoleUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:role:update")
    @Operation(summary = "修改角色", description = "修改角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<RoleRespDto> update(@Parameter(description = "角色 ID", required = true) @PathVariable Long id, @Valid @RequestBody RoleUpdateReqDto req) {
        return R.ok(roleService.update(id, req));
    }

    /**
     * 修改角色状态。
     *
     * @param id  角色 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("system:role:update")
    @Operation(summary = "修改角色状态", description = "修改角色状态。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> status(@Parameter(description = "角色 ID", required = true) @PathVariable Long id, @Valid @RequestBody StatusUpdateReqDto req) {
        roleService.updateStatus(id, req);
        return R.ok();
    }

    /**
     * 删除角色。
     *
     * @param id 角色 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:role:delete")
    @Operation(summary = "删除角色", description = "删除角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        roleService.delete(id);
        return R.ok();
    }

    /**
     * 查询角色已授权菜单。
     *
     * @param id 角色 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}/menus")
    @SaCheckPermission("system:role:list")
    @Operation(summary = "查询角色已授权菜单", description = "查询角色已授权菜单。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<Long>> menuIds(@Parameter(description = "角色 ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.menuIds(id));
    }

    /**
     * 全量替换角色菜单授权。
     *
     * @param id  角色 ID
     * @param req RoleMenuUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/menus")
    @SaCheckPermission("system:role:menu")
    @Operation(summary = "全量替换角色菜单授权", description = "全量替换角色菜单授权。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> menus(@Parameter(description = "角色 ID", required = true) @PathVariable Long id, @Valid @RequestBody RoleMenuUpdateReqDto req) {
        roleService.replaceMenus(id, req);
        return R.ok();
    }
}
