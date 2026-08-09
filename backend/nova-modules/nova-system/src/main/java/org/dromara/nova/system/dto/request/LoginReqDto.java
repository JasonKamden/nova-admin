package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求。
 *
 * @param username    登录账号
 * @param password    登录密码，仅用于认证请求，不会回显
 * @param captchaId   图形验证码唯一标识
 * @param captchaCode 图形验证码输入值，仅用于认证请求，不会回显
 */
@Schema(description = "账号密码登录请求")
public record LoginReqDto(
        @Schema(description = "登录账号") @NotBlank(message = "账号不能为空") String username,
        @Schema(description = "登录密码，仅用于认证请求，不会回显", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank(message = "密码不能为空") String password,
        @Schema(description = "图形验证码唯一标识") @NotBlank(message = "验证码标识不能为空") String captchaId,
        @Schema(description = "图形验证码输入值，仅用于认证请求，不会回显", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank(message = "验证码不能为空") String captchaCode
) {
}
