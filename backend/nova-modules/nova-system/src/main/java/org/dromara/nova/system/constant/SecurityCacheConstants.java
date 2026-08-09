package org.dromara.nova.system.constant;

import java.time.Duration;

/**
 * 登录安全相关 Redis Key 和 TTL 常量。
 */
public final class SecurityCacheConstants {
    /**
     * 图形验证码 Redis Key 前缀。
     */
    public static final String CAPTCHA_KEY_PREFIX = "security:captcha:";

    /**
     * 图形验证码获取频控 Redis Key 前缀。
     */
    public static final String CAPTCHA_RATE_LIMIT_KEY_PREFIX = "security:captcha:rate:";

    /**
     * 图形验证码有效期。
     */
    public static final Duration CAPTCHA_TTL = Duration.ofMinutes(2);

    /**
     * 图形验证码频控窗口。
     */
    public static final Duration CAPTCHA_RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

    /**
     * 图形验证码频控窗口内允许的最大请求次数。
     */
    public static final int CAPTCHA_RATE_LIMIT_MAX_REQUESTS = 20;

    private SecurityCacheConstants() {
    }
}
