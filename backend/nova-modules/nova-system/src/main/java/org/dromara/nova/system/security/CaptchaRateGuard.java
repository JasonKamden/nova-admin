package org.dromara.nova.system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.nova.common.cache.util.RedisUtils;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.util.DigestUtils;
import org.dromara.nova.common.web.util.IpUtils;
import org.dromara.nova.system.constant.SecurityCacheConstants;
import org.springframework.stereotype.Component;

/**
 * 图形验证码接口频率保护：同一 IP 在短窗口内请求过多时临时拒绝。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaRateGuard {
    private final RedisUtils redisUtils;

    /**
     * 校验当前 IP 的验证码获取频率。
     */
    public void check() {
        try {
            String key = key();
            long count = redisUtils.increment(key);
            if (count == 1) {
                redisUtils.expire(key, SecurityCacheConstants.CAPTCHA_RATE_LIMIT_WINDOW);
            }
            if (count > SecurityCacheConstants.CAPTCHA_RATE_LIMIT_MAX_REQUESTS) {
                log.warn("Captcha request rejected by rate guard. ip={}", IpUtils.currentIp());
                throw new BusinessException(CommonResultCode.TOO_MANY_REQUESTS, "验证码请求过于频繁，请稍后重试");
            }
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("Captcha rate guard unavailable, continuing safely. ip={}", IpUtils.currentIp());
        }
    }

    private String key() {
        return SecurityCacheConstants.CAPTCHA_RATE_LIMIT_KEY_PREFIX + DigestUtils.sha256(IpUtils.currentIp());
    }
}
