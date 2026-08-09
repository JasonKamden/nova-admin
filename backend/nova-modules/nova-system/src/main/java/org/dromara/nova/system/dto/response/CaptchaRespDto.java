package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 图形验证码响应。
 *
 * @param captchaId   验证码唯一标识，登录时需原样回传
 * @param imageBase64 Base64 Data URL 格式验证码图片
 */
@Schema(description = "图形验证码响应")
public record CaptchaRespDto(
        @Schema(description = "验证码唯一标识，登录时需原样回传") String captchaId,
        @Schema(description = "Base64 Data URL 格式验证码图片") String imageBase64
) {
}
