package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改自己的密码。
 *
 * @param oldPassword 当前密码，用于身份校验
 * @param newPassword 新密码
 */
@Schema(description = "个人中心修改密码请求")
public record ProfilePasswordReqDto(
        @Schema(description = "当前密码，用于身份校验", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank String oldPassword,
        @Schema(description = "新密码", accessMode = Schema.AccessMode.WRITE_ONLY) @Size(min = 8, max = 64) String newPassword) {
}
