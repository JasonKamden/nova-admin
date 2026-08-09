package org.dromara.nova.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.DepartmentCreateReqDto;
import org.dromara.nova.system.dto.request.DepartmentQueryReqDto;
import org.dromara.nova.system.dto.request.DepartmentUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.DepartmentRespDto;
import org.dromara.nova.system.service.DepartmentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门TreeTable 后端接口。
 */
@Tag(name = "部门管理", description = "维护当前 Tenant 的部门树、状态和层级关系。")
@RestController
@RequestMapping("/api/system/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    /**
     * 查询部门TreeTable。
     *
     * @param req DepartmentQueryReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:department:list")
    @Operation(summary = "查询部门TreeTable", description = "查询部门TreeTable。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<DepartmentRespDto>> tree(@ParameterObject @Valid DepartmentQueryReqDto req) {
        return R.ok(departmentService.tree(req));
    }

    /**
     * 查询部门选择树。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/tree")
    @SaCheckPermission("system:department:list")
    @Operation(summary = "查询部门选择树", description = "查询部门选择树。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<DepartmentRespDto>> selector() {
        return R.ok(departmentService.tree(new DepartmentQueryReqDto(null, 1)));
    }

    /**
     * 查询部门详情。
     *
     * @param id 部门ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:department:list")
    @Operation(summary = "查询部门详情", description = "查询部门详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DepartmentRespDto> detail(@Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        return R.ok(departmentService.detail(id));
    }

    /**
     * 新增 Department。
     *
     * @param req DepartmentCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("system:department:add")
    @Operation(summary = "新增 Department", description = "新增 Department。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DepartmentRespDto> create(@Valid @RequestBody DepartmentCreateReqDto req) {
        return R.ok(departmentService.create(req));
    }

    /**
     * 修改 Department。
     *
     * @param id  部门ID
     * @param req DepartmentUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:department:update")
    @Operation(summary = "修改 Department", description = "修改 Department。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DepartmentRespDto> update(@Parameter(description = "Department ID", required = true) @PathVariable Long id, @Valid @RequestBody DepartmentUpdateReqDto req) {
        return R.ok(departmentService.update(id, req));
    }

    /**
     * 修改部门状态。
     *
     * @param id  部门ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("system:department:update")
    @Operation(summary = "修改部门状态", description = "修改部门状态。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> status(@Parameter(description = "Department ID", required = true) @PathVariable Long id, @Valid @RequestBody StatusUpdateReqDto req) {
        departmentService.updateStatus(id, req);
        return R.ok();
    }

    /**
     * 删除 Department。
     *
     * @param id 部门ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:department:delete")
    @Operation(summary = "删除 Department", description = "删除 Department。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        departmentService.delete(id);
        return R.ok();
    }
}
