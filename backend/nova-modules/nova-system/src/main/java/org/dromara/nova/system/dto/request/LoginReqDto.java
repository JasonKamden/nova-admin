package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求。
 *
 * @param username 登录账号
 * @param password 登录密码，仅用于认证请求，不会回显
 */
@Schema(description = "账号密码登录请求")
public record LoginReqDto(@Schema(description = "登录账号") @NotBlank(message = "账号不能为空") String username,
                          @Schema(description = "登录密码，仅用于认证请求，不会回显", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank(message = "密码不能为空") String password) {
}
