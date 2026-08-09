package org.dromara.nova.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.cache.util.RedisUtils;
import org.dromara.nova.common.core.util.DigestUtils;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.common.web.util.IpUtils;
import org.dromara.nova.common.web.util.ServletUtils;
import org.dromara.nova.system.dto.response.OnlineUserRespDto;
import org.dromara.nova.system.security.DataScopeRule;
import org.dromara.nova.system.security.DataScopeService;
import org.dromara.nova.system.service.OnlineUserService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Redis 保存在线会话索引，真实 Token 仅保存在服务端值中。
 */
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {
    private static final String PREFIX = "security:online:";
    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;
    private final DataScopeService dataScopeService;

    /**
     * 记录新登录会话的在线快照。
     *
     * @param token 登录 Token，仅登录响应返回，不写业务日志
     * @param user  用户名或昵称查询条件
     */
    @Override
    public void login(String token, CurrentLoginUser user) {
        save(token, user, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * 更新在线会话的最近活动时间。
     *
     * @param token 登录 Token，仅登录响应返回，不写业务日志
     * @param user  用户名或昵称查询条件
     */
    @Override
    public void activity(String token, CurrentLoginUser user) {
        Snapshot old = read(DigestUtils.sha256(token));
        LocalDateTime login = old == null ? LocalDateTime.now() : old.loginTime();
        save(token, user, login, LocalDateTime.now());
    }

    /**
     * 注销当前登录会话并清理服务端会话状态。
     *
     * @param token 登录 Token，仅登录响应返回，不写业务日志
     */
    @Override
    public void logout(String token) {
        if (token != null) redisUtils.delete(PREFIX + DigestUtils.sha256(token));
    }

    /**
     * 查询当前权限范围内的业务数据列表。
     *
     * @param keyword 模糊搜索关键字
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<OnlineUserRespDto> list(String keyword) {
        Long tenantId = TenantContextSupport.current().tenantId();
        boolean platform = TenantContextSupport.current().isPlatform();
        DataScopeRule rule = platform ? null : dataScopeService.current();
        List<OnlineUserRespDto> result = new ArrayList<>();
        for (String key : redisUtils.scanKeys(PREFIX, 5000)) {
            Snapshot s = readKey(key);
            if (s == null) continue;
            if (platform) {
                if (!"PLATFORM".equals(s.contextType())) continue;
            } else {
                if (!Objects.equals(tenantId, s.tenantId())) continue;
                if (!allowed(rule, s)) continue;
            }
            if (keyword != null && !keyword.isBlank() && !s.username().contains(keyword)) continue;
            result.add(toResp(s));
        }
        result.sort(Comparator.comparing(OnlineUserRespDto::lastActivityTime).reversed());
        return result;
    }

    /**
     * 统计当前 Tenant 在线用户数。
     *
     * @return 业务计算结果。
     */
    @Override
    public long countCurrentTenant() {
        return list(null).size();
    }

    /**
     * 强制注销指定在线会话。
     *
     * @param sessionId 服务端在线会话标识，不是原始 Token
     */
    @Override
    @OperationAudit(module = "ONLINE_USER", type = "FORCE_LOGOUT", description = "强制下线在线用户")
    public void forceLogout(String sessionId) {
        Snapshot s = read(sessionId);
        if (s == null) return;
        var context = TenantContextSupport.current();
        if (context.isPlatform()) {
            if (!"PLATFORM".equals(s.contextType()))
                throw new org.dromara.nova.common.core.exception.BusinessException(org.dromara.nova.common.core.enums.CommonResultCode.FORBIDDEN);
        } else {
            if (!Objects.equals(context.tenantId(), s.tenantId()) || !allowed(dataScopeService.current(), s))
                throw new org.dromara.nova.common.core.exception.BusinessException(org.dromara.nova.common.core.enums.CommonResultCode.FORBIDDEN);
        }
        StpUtil.logoutByTokenValue(s.token());
        redisUtils.delete(PREFIX + sessionId);
    }

    /**
     * 使指定 Tenant 下的一组用户会话失效。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param userIds  用户 ID 集合
     */
    @Override
    public void invalidateTenantUsers(Long tenantId, Collection<Long> userIds) {
        if (tenantId == null || userIds == null || userIds.isEmpty()) return;
        Set<Long> targets = new HashSet<>(userIds);
        for (String key : redisUtils.scanKeys(PREFIX, 5000)) {
            Snapshot s = readKey(key);
            if (s != null && Objects.equals(tenantId, s.tenantId()) && targets.contains(s.userId())) {
                StpUtil.logoutByTokenValue(s.token());
                redisUtils.delete(key);
            }
        }
    }

    /**
     * 使指定用户的全部在线会话失效。
     *
     * @param userIds 用户 ID 集合
     */
    @Override
    public void invalidateUsers(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        Set<Long> targets = new HashSet<>(userIds);
        for (String key : redisUtils.scanKeys(PREFIX, 5000)) {
            Snapshot s = readKey(key);
            if (s != null && targets.contains(s.userId())) {
                StpUtil.logoutByTokenValue(s.token());
                redisUtils.delete(key);
            }
        }
    }

    /**
     * 保存或刷新 Redis 中的在线会话快照。
     *
     * @param token    登录 Token，仅登录响应返回，不写业务日志
     * @param user     用户名或昵称查询条件
     * @param login    登录时间
     * @param activity 最近活动时间
     */
    private void save(String token, CurrentLoginUser user, LocalDateTime login, LocalDateTime activity) {
        try {
            String id = DigestUtils.sha256(token);
            Snapshot s = new Snapshot(token, id, user.userId(), user.username(), user.contextType().name(), user.tenantId(), user.tenantName(), user.departmentId(), user.departmentName(), IpUtils.currentIp(), ServletUtils.userAgent(), login, activity);
            redisUtils.set(PREFIX + id, objectMapper.writeValueAsString(s), Duration.ofDays(30));
        } catch (Exception ignored) {
        }
    }

    /**
     * 根据服务端会话标识读取在线会话快照。
     *
     * @param id 服务端在线会话标识
     * @return 方法处理结果。
     */
    private Snapshot read(String id) {
        return readKey(PREFIX + id);
    }

    /**
     * 从 Redis 读取并反序列化在线会话快照。
     *
     * @param key 缓存或会话 Key
     * @return 方法处理结果。
     */
    private Snapshot readKey(String key) {
        try {
            String json = redisUtils.get(key);
            return json == null ? null : objectMapper.readValue(json, Snapshot.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断在线会话快照是否位于当前 DataScope 可见范围。
     *
     * @param rule 当前用户解析后的 DataScope 规则
     * @param s    在线会话快照
     * @return 业务校验或处理结果。
     */
    private boolean allowed(DataScopeRule rule, Snapshot s) {
        if (rule == null || rule.allTenant()) return true;
        if (rule.selfUserId() != null && Objects.equals(rule.selfUserId(), s.userId())) return true;
        return s.departmentId() != null && rule.departmentIds().contains(s.departmentId());
    }

    /**
     * 将实体转换为响应 DTO。
     *
     * @param s 在线会话快照
     * @return 业务响应 DTO。
     */
    private OnlineUserRespDto toResp(Snapshot s) {
        return new OnlineUserRespDto(s.sessionId(), s.userId(), s.username(), s.contextType(), s.tenantId(), s.tenantName(), s.departmentId(), s.departmentName(), s.ip(), s.userAgent(), s.loginTime(), s.lastActivityTime());
    }

    /**
     * 在线会话内部快照。
     *
     * @param token            登录 Token，仅登录响应返回，不写业务日志
     * @param sessionId        服务端在线会话标识，不是原始 Token
     * @param userId           用户 ID
     * @param username         登录账号
     * @param contextType      运行上下文类型：PLATFORM 或 TENANT
     * @param tenantId         Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @param tenantName       Tenant 名称
     * @param departmentId     Department ID
     * @param departmentName   Department 名称
     * @param ip               客户端 IP 地址
     * @param userAgent        客户端 User-Agent
     * @param loginTime        登录时间
     * @param lastActivityTime 最近活动时间
     */
    private record Snapshot(String token, String sessionId, Long userId, String username, String contextType,
                            Long tenantId, String tenantName, Long departmentId, String departmentName, String ip,
                            String userAgent, LocalDateTime loginTime, LocalDateTime lastActivityTime) {
    }
}
