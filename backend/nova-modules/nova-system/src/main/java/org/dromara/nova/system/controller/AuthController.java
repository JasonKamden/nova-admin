package org.dromara.nova.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.system.dto.request.LoginReqDto;
import org.dromara.nova.system.dto.response.CaptchaRespDto;
import org.dromara.nova.system.dto.response.LoginRespDto;
import org.dromara.nova.system.dto.response.MenuRespDto;
import org.dromara.nova.system.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录认证与当前用户运行时契约。
 */
@Tag(name = "认证与当前用户", description = "提供登录、退出、当前用户、动态菜单和按钮权限契约。")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 获取匿名图形验证码。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码", description = "获取匿名图形验证码，用于账号密码登录前校验。")
    public R<CaptchaRespDto> captcha() {
        return R.ok(authService.captcha());
    }

    /**
     * 账号密码登录。
     *
     * @param req LoginReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登录", description = "账号密码登录。接口执行服务端权限、Context 和业务规则校验。")
    public R<LoginRespDto> login(@Valid @RequestBody LoginReqDto req) {
        return R.ok(authService.login(req));
    }

    /**
     * 退出登录。
     *
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "退出登录。接口执行服务端权限、Context 和业务规则校验。")
    @OperationAudit(module = "AUTH", type = "LOGOUT", description = "退出登录")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 当前登录用户。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/me")
    @Operation(summary = "当前登录用户", description = "当前登录用户。接口执行服务端权限、Context 和业务规则校验。")
    public R<CurrentLoginUser> me() {
        return R.ok(authService.currentUser());
    }

    /**
     * 当前用户动态菜单。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/menus")
    @Operation(summary = "当前用户动态菜单", description = "当前用户动态菜单。接口执行服务端权限、Context 和业务规则校验。")
    public R<List<MenuRespDto>> menus() {
        return R.ok(authService.currentMenus());
    }

    /**
     * 当前用户按钮权限。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/permissions")
    @Operation(summary = "当前用户按钮权限", description = "当前用户按钮权限。接口执行服务端权限、Context 和业务规则校验。")
    public R<List<String>> permissions() {
        return R.ok(authService.currentPermissions());
    }
}
