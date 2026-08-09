package org.dromara.nova.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.DictDataCreateReqDto;
import org.dromara.nova.system.dto.request.DictDataUpdateReqDto;
import org.dromara.nova.system.dto.request.DictTypeCreateReqDto;
import org.dromara.nova.system.dto.request.DictTypeUpdateReqDto;
import org.dromara.nova.system.dto.response.DictDataRespDto;
import org.dromara.nova.system.dto.response.DictTypeRespDto;
import org.dromara.nova.system.service.DictionaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理：左侧 Type + 右侧 Data。
 */
@Tag(name = "字典管理", description = "维护当前 Tenant 的字典类型和字典数据。")
@RestController
@RequestMapping("/api/system/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService dictionaryService;

    /**
     * 查询字典类型。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/types")
    @SaCheckPermission("system:dictionary:list")
    @Operation(summary = "查询字典类型", description = "查询字典类型。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<DictTypeRespDto>> types(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(dictionaryService.types(keyword));
    }

    /**
     * 新增字典类型。
     *
     * @param req DictTypeCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/types")
    @SaCheckPermission("system:dictionary:add")
    @Operation(summary = "新增字典类型", description = "新增字典类型。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DictTypeRespDto> createType(@Valid @RequestBody DictTypeCreateReqDto req) {
        return R.ok(dictionaryService.createType(req));
    }

    /**
     * 修改字典类型。
     *
     * @param id  字典资源 ID
     * @param req DictTypeUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/types/{id}")
    @SaCheckPermission("system:dictionary:update")
    @Operation(summary = "修改字典类型", description = "修改字典类型。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DictTypeRespDto> updateType(@Parameter(description = "字典资源 ID", required = true) @PathVariable Long id, @Valid @RequestBody DictTypeUpdateReqDto req) {
        return R.ok(dictionaryService.updateType(id, req));
    }

    /**
     * 删除字典类型。
     *
     * @param id 字典资源 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/types/{id}")
    @SaCheckPermission("system:dictionary:delete")
    @Operation(summary = "删除字典类型", description = "删除字典类型。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> deleteType(@Parameter(description = "字典资源 ID", required = true) @PathVariable Long id) {
        dictionaryService.deleteType(id);
        return R.ok();
    }

    /**
     * 分页查询字典数据。
     *
     * @param typeId   字典类型 ID
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页条数
     * @param label    字典标签
     * @param value    字典值
     * @param status   状态
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/types/{typeId}/data")
    @SaCheckPermission("system:dictionary:list")
    @Operation(summary = "分页查询字典数据", description = "分页查询字典数据。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<DictDataRespDto>> data(@Parameter(description = "字典类型 ID", required = true) @PathVariable Long typeId, @Parameter(description = "页码，从 1 开始", required = false) @RequestParam(defaultValue = "1") long pageNum, @Parameter(description = "每页条数", required = false) @RequestParam(defaultValue = "10") long pageSize, @Parameter(description = "字典标签", required = false) @RequestParam(required = false) String label, @Parameter(description = "字典值", required = false) @RequestParam(required = false) String value, @Parameter(description = "状态", required = false) @RequestParam(required = false) Integer status) {
        return R.ok(dictionaryService.data(typeId, pageNum, pageSize, label, value, status));
    }

    /**
     * 新增字典数据。
     *
     * @param typeId 字典类型 ID
     * @param req    DictDataCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/types/{typeId}/data")
    @SaCheckPermission("system:dictionary:add")
    @Operation(summary = "新增字典数据", description = "新增字典数据。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DictDataRespDto> createData(@Parameter(description = "字典类型 ID", required = true) @PathVariable Long typeId, @Valid @RequestBody DictDataCreateReqDto req) {
        return R.ok(dictionaryService.createData(typeId, req));
    }

    /**
     * 修改字典数据。
     *
     * @param id  字典资源 ID
     * @param req DictDataUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/data/{id}")
    @SaCheckPermission("system:dictionary:update")
    @Operation(summary = "修改字典数据", description = "修改字典数据。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<DictDataRespDto> updateData(@Parameter(description = "字典资源 ID", required = true) @PathVariable Long id, @Valid @RequestBody DictDataUpdateReqDto req) {
        return R.ok(dictionaryService.updateData(id, req));
    }

    /**
     * 删除字典数据。
     *
     * @param id 字典资源 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/data/{id}")
    @SaCheckPermission("system:dictionary:delete")
    @Operation(summary = "删除字典数据", description = "删除字典数据。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> deleteData(@Parameter(description = "字典资源 ID", required = true) @PathVariable Long id) {
        dictionaryService.deleteData(id);
        return R.ok();
    }
}
