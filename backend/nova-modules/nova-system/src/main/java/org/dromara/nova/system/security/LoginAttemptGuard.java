package org.dromara.nova.system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.nova.common.cache.util.RedisUtils;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.util.DigestUtils;
import org.dromara.nova.common.web.util.IpUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 登录暴力破解保护：同一账号+IP 在窗口内连续失败达到阈值后临时拒绝。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAttemptGuard {
    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(10);
    private final RedisUtils redisUtils;

    /**
     * 检查登录失败次数和锁定时间，阻止暴力破解。
     *
     * @param username 登录账号
     */
    public void check(String username) {
        try {
            String value = redisUtils.get(key(username));
            if (value != null && Long.parseLong(value) >= MAX_FAILURES) {
                throw new BusinessException(CommonResultCode.TOO_MANY_REQUESTS, "登录失败次数过多，请稍后重试");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Login attempt guard unavailable, continuing authentication safely. username={}", username);
        }
    }

    /**
     * 记录一次登录失败并在达到阈值时触发临时锁定。
     *
     * @param username 登录账号
     */
    public void failure(String username) {
        try {
            String key = key(username);
            long count = redisUtils.increment(key);
            if (count == 1) redisUtils.expire(key, WINDOW);
        } catch (Exception e) {
            log.warn("Failed to update login attempt guard. username={}", username);
        }
    }

    /**
     * 登录成功后清除当前账号/IP 的失败计数和锁定状态。
     *
     * @param username 登录账号
     */
    public void success(String username) {
        try {
            redisUtils.delete(key(username));
        } catch (Exception e) {
            log.warn("Failed to clear login attempt guard. username={}", username);
        }
    }

    /**
     * 根据用户名和客户端 IP 生成登录尝试限流 Key。
     *
     * @param username 登录账号
     * @return 方法处理结果。
     */
    private String key(String username) {
        String source = (username == null ? "" : username.trim().toLowerCase()) + "|" + IpUtils.currentIp();
        return "security:login:failure:" + DigestUtils.sha256(source);
    }
}
