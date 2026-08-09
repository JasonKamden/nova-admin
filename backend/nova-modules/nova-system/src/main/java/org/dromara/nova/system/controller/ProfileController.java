package org.dromara.nova.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.request.ProfilePasswordReqDto;
import org.dromara.nova.system.dto.request.ProfileUpdateReqDto;
import org.dromara.nova.system.dto.response.ProfileRespDto;
import org.dromara.nova.system.service.ProfileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 所有已登录用户可访问的个人中心，不进入动态 sys_menu。
 */
@Tag(name = "个人中心", description = "查询和维护当前登录用户个人资料、密码及头像。")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    /**
     * 查询个人中心。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @Operation(summary = "查询个人中心", description = "查询个人中心。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<ProfileRespDto> get() {
        return R.ok(profileService.get());
    }

    /**
     * 修改个人基本资料。
     *
     * @param req ProfileUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping
    @Operation(summary = "修改个人基本资料", description = "修改个人基本资料。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<ProfileRespDto> update(@Valid @RequestBody ProfileUpdateReqDto req) {
        return R.ok(profileService.update(req));
    }

    /**
     * 修改个人密码。
     *
     * @param req ProfilePasswordReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @PutMapping("/password")
    @Operation(summary = "修改个人密码", description = "修改个人密码。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> password(@Valid @RequestBody ProfilePasswordReqDto req) {
        profileService.changePassword(req);
        return R.ok();
    }

    /**
     * 上传并修改头像。
     *
     * @param file 上传文件
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传并修改头像", description = "上传并修改头像。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<String> avatar(@Parameter(description = "上传文件", required = true) @RequestPart("file") MultipartFile file) {
        return R.ok(profileService.updateAvatar(file));
    }

    /**
     * 上传并修改头像。
     *
     * @return 文件流 HTTP 响应，包含 Content-Type、Content-Length 和 Content-Disposition。
     */
    @GetMapping("/avatar")
    @Operation(summary = "上传并修改头像", description = "上传并修改头像。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public ResponseEntity<InputStreamResource> avatar() {
        var avatar = profileService.openAvatar();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(avatar.contentType())).header(HttpHeaders.CACHE_CONTROL, "private, max-age=300").body(new InputStreamResource(avatar.input()));
    }
}
