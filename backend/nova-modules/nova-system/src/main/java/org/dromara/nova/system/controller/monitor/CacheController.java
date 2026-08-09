package org.dromara.nova.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.CacheBatchReqDto;
import org.dromara.nova.system.dto.response.CacheRespDto;
import org.dromara.nova.system.service.CacheManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 逻辑业务缓存管理，不提供 Redis 控制台。
 */
@Tag(name = "缓存管理", description = "管理系统注册的逻辑业务缓存，不暴露任意 Redis Key/Value。")
@RestController
@RequestMapping("/api/system/caches")
@RequiredArgsConstructor
public class CacheController {
    private final CacheManagementService cacheManagementService;

    /**
     * 查询逻辑缓存列表。
     *
     * @param name   缓存名称
     * @param type   缓存类型
     * @param module 所属模块
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("monitor:cache:list")
    @Operation(summary = "查询逻辑缓存列表", description = "查询逻辑缓存列表。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<CacheRespDto>> list(@Parameter(description = "缓存名称", required = false) @RequestParam(required = false) String name, @Parameter(description = "缓存类型", required = false) @RequestParam(required = false) String type, @Parameter(description = "所属模块", required = false) @RequestParam(required = false) String module) {
        return R.ok(cacheManagementService.list(name, type, module));
    }

    /**
     * 查询逻辑缓存详情。
     *
     * @param cacheCode 逻辑缓存编码
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{cacheCode}")
    @SaCheckPermission("monitor:cache:detail")
    @Operation(summary = "查询逻辑缓存详情", description = "查询逻辑缓存详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<CacheRespDto> detail(@Parameter(description = "逻辑缓存编码", required = true) @PathVariable String cacheCode) {
        return R.ok(cacheManagementService.detail(cacheCode));
    }

    /**
     * 清理逻辑缓存。
     *
     * @param cacheCode 逻辑缓存编码
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/{cacheCode}/clear")
    @SaCheckPermission("monitor:cache:clear")
    @Operation(summary = "清理逻辑缓存", description = "清理逻辑缓存。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> clear(@Parameter(description = "逻辑缓存编码", required = true) @PathVariable String cacheCode) {
        cacheManagementService.clear(cacheCode);
        return R.ok();
    }

    /**
     * 刷新逻辑缓存。
     *
     * @param cacheCode 逻辑缓存编码
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/{cacheCode}/refresh")
    @SaCheckPermission("monitor:cache:refresh")
    @Operation(summary = "刷新逻辑缓存", description = "刷新逻辑缓存。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> refresh(@Parameter(description = "逻辑缓存编码", required = true) @PathVariable String cacheCode) {
        cacheManagementService.refresh(cacheCode);
        return R.ok();
    }

    /**
     * 批量清理逻辑缓存。
     *
     * @param req CacheBatchReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/batch-clear")
    @SaCheckPermission("monitor:cache:clear")
    @Operation(summary = "批量清理逻辑缓存", description = "批量清理逻辑缓存。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> batch(@Valid @RequestBody CacheBatchReqDto req) {
        cacheManagementService.batchClear(req);
        return R.ok();
    }

    /**
     * 查询 Redis 连通状态。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/redis/status")
    @SaCheckPermission("monitor:cache:list")
    @Operation(summary = "查询 Redis 连通状态", description = "查询 Redis 连通状态。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<String> redis() {
        return R.ok(cacheManagementService.redisStatus());
    }
}
