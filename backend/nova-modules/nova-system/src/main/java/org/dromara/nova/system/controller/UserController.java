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
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.dto.response.UserRespDto;
import org.dromara.nova.system.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tenant 用户管理。
 */
@Tag(name = "用户管理", description = "维护当前 Tenant 成员、状态、密码、角色以及用户导入导出。")
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final org.dromara.nova.system.service.UserExcelService userExcelService;

    /**
     * 分页查询 Tenant 用户。
     *
     * @param req UserPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:user:list")
    @Operation(summary = "分页查询 Tenant 用户", description = "分页查询 Tenant 用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<UserRespDto>> page(@ParameterObject @Valid UserPageReqDto req) {
        return R.ok(userService.page(req));
    }

    /**
     * 查询 Tenant 用户详情。
     *
     * @param id 用户 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:user:list")
    @Operation(summary = "查询 Tenant 用户详情", description = "查询 Tenant 用户详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<UserRespDto> detail(@Parameter(description = "用户 ID", required = true) @PathVariable Long id) {
        return R.ok(userService.detail(id));
    }

    /**
     * 新增 Tenant 用户。
     *
     * @param req UserCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("system:user:add")
    @Operation(summary = "新增 Tenant 用户", description = "新增 Tenant 用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<UserRespDto> create(@Valid @RequestBody UserCreateReqDto req) {
        return R.ok(userService.create(req));
    }

    /**
     * 修改 Tenant 用户。
     *
     * @param id  用户 ID
     * @param req UserUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:user:update")
    @Operation(summary = "修改 Tenant 用户", description = "修改 Tenant 用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<UserRespDto> update(@Parameter(description = "用户 ID", required = true) @PathVariable Long id, @Valid @RequestBody UserUpdateReqDto req) {
        return R.ok(userService.update(id, req));
    }

    /**
     * 修改 Tenant 用户状态。
     *
     * @param id  用户 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("system:user:update")
    @Operation(summary = "修改 Tenant 用户状态", description = "修改 Tenant 用户状态。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> status(@Parameter(description = "用户 ID", required = true) @PathVariable Long id, @Valid @RequestBody StatusUpdateReqDto req) {
        userService.updateStatus(id, req);
        return R.ok();
    }

    /**
     * 重置 Tenant 用户密码。
     *
     * @param id  用户 ID
     * @param req PasswordResetReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/password")
    @SaCheckPermission("system:user:password")
    @Operation(summary = "重置 Tenant 用户密码", description = "重置 Tenant 用户密码。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> password(@Parameter(description = "用户 ID", required = true) @PathVariable Long id, @Valid @RequestBody PasswordResetReqDto req) {
        userService.resetPassword(id, req);
        return R.ok();
    }

    /**
     * 移除 Tenant 用户。
     *
     * @param id 用户 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:user:delete")
    @Operation(summary = "移除 Tenant 用户", description = "移除 Tenant 用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "用户 ID", required = true) @PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }

    /**
     * 查询或更新用户角色。
     *
     * @param id 用户 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}/roles")
    @SaCheckPermission("system:user:list")
    @Operation(summary = "查询或更新用户角色", description = "查询或更新用户角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<RoleSimpleRespDto>> roles(@Parameter(description = "用户 ID", required = true) @PathVariable Long id) {
        return R.ok(userService.roles(id));
    }

    /**
     * 查询或更新用户角色。
     *
     * @param id  用户 ID
     * @param req UserRoleUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/roles")
    @SaCheckPermission("system:user:role")
    @Operation(summary = "查询或更新用户角色", description = "查询或更新用户角色。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> roles(@Parameter(description = "用户 ID", required = true) @PathVariable Long id, @Valid @RequestBody UserRoleUpdateReqDto req) {
        userService.replaceRoles(id, req);
        return R.ok();
    }

    /**
     * 下载用户导入模板。
     *
     * @param response HTTP 响应对象，用于输出文件或导出数据
     */
    @GetMapping("/import-template")
    @SaCheckPermission("system:user:import")
    @Operation(summary = "下载用户导入模板", description = "下载用户导入模板。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public void template(jakarta.servlet.http.HttpServletResponse response) {
        userExcelService.template(response);
    }

    /**
     * 导入用户。
     *
     * @param file 上传文件
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/import")
    @SaCheckPermission("system:user:import")
    @Operation(summary = "导入用户", description = "导入用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<org.dromara.nova.system.dto.response.ImportResultRespDto> importUsers(@Parameter(description = "上传文件", required = true) @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        return R.ok(userExcelService.importUsers(file));
    }

    /**
     * 导出用户。
     *
     * @param req      UserPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @param response HTTP 响应对象，用于输出文件或导出数据
     */
    @GetMapping("/export")
    @SaCheckPermission("system:user:export")
    @Operation(summary = "导出用户", description = "导出用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public void export(@ParameterObject @Valid UserPageReqDto req, jakarta.servlet.http.HttpServletResponse response) {
        userExcelService.export(req, response);
    }
}

