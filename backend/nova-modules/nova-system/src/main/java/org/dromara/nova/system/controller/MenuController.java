package org.dromara.nova.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.response.MenuRespDto;
import org.dromara.nova.system.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Tenant 侧只读 Menu Tree，用于 RoleMenu 和动态路由。全局 Menu 定义由 PLATFORM 维护。
 */
@Tag(name = "当前菜单", description = "查询当前用户在当前 Context 下可访问的菜单树。")
@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    /**
     * 查询当前用户菜单树。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:menu:list")
    @Operation(summary = "查询当前用户菜单树", description = "查询当前用户菜单树。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<MenuRespDto>> tree(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(menuService.tree(keyword));
    }
}
