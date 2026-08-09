package org.dromara.nova.system.controller.platform;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.MenuCreateReqDto;
import org.dromara.nova.system.dto.request.MenuUpdateReqDto;
import org.dromara.nova.system.dto.request.StatusUpdateReqDto;
import org.dromara.nova.system.dto.response.MenuRespDto;
import org.dromara.nova.system.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PLATFORM 维护全局 Menu 定义。
 */
@Tag(name = "平台菜单管理", description = "PLATFORM 管理员维护全局菜单、路由和按钮权限定义。")
@RestController
@RequestMapping("/api/platform/menus")
@RequiredArgsConstructor
public class PlatformMenuController {
    private final MenuService menuService;

    /**
     * 全局 Menu Tree。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("platform:menu:list")
    @Operation(summary = "全局 Menu Tree", description = "全局 Menu Tree。接口执行服务端权限、Context 和业务规则校验。")
    public R<List<MenuRespDto>> tree(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(menuService.tree(keyword));
    }

    /**
     * 查询全局菜单详情。
     *
     * @param id 菜单 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("platform:menu:list")
    @Operation(summary = "查询全局菜单详情", description = "查询全局菜单详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MenuRespDto> detail(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id) {
        return R.ok(menuService.detail(id));
    }

    /**
     * 新增全局菜单。
     *
     * @param req MenuCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("platform:menu:add")
    @Operation(summary = "新增全局菜单", description = "新增全局菜单。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MenuRespDto> create(@Valid @RequestBody MenuCreateReqDto req) {
        return R.ok(menuService.create(req));
    }

    /**
     * 修改全局菜单。
     *
     * @param id  菜单 ID
     * @param req MenuUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("platform:menu:update")
    @Operation(summary = "修改全局菜单", description = "修改全局菜单。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MenuRespDto> update(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id, @Valid @RequestBody MenuUpdateReqDto req) {
        return R.ok(menuService.update(id, req));
    }

    /**
     * 修改全局菜单状态。
     *
     * @param id  菜单 ID
     * @param req StatusUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("platform:menu:update")
    @Operation(summary = "修改全局菜单状态", description = "修改全局菜单状态。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> status(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id, @Valid @RequestBody StatusUpdateReqDto req) {
        menuService.updateStatus(id, req);
        return R.ok();
    }

    /**
     * 删除全局菜单。
     *
     * @param id 菜单 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("platform:menu:delete")
    @Operation(summary = "删除全局菜单", description = "删除全局菜单。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "菜单 ID", required = true) @PathVariable Long id) {
        menuService.delete(id);
        return R.ok();
    }
}
