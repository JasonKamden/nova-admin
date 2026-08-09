package org.dromara.nova.system.security;

import org.dromara.nova.system.dto.response.CaptchaRespDto;

/**
 * 图形验证码生成与校验契约。
 */
public interface LoginCaptchaService {
    /**
     * 生成新的图形验证码并写入 Redis。
     */
    CaptchaRespDto generate();

    /**
     * 校验并一次性消费验证码。
     *
     * @param captchaId 验证码唯一标识
     * @param captchaCode 用户输入验证码
     */
    void validate(String captchaId, String captchaCode);
}
