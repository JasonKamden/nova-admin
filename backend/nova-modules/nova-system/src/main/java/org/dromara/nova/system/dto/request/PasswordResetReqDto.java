package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * 管理员重置用户密码。
 *
 * @param newPassword 新密码
 */
@Schema(description = "管理员重置用户密码请求")
public record PasswordResetReqDto(
        @Schema(description = "新密码", accessMode = Schema.AccessMode.WRITE_ONLY) @Size(min = 8, max = 64) String newPassword) {
}
