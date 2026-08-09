package org.dromara.nova.system.service;

import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.system.dto.response.OnlineUserRespDto;

import java.util.Collection;
import java.util.List;

/**
 * 在线用户服务。
 */
public interface OnlineUserService {
    /**
     * 校验账号密码并创建登录会话。
     */
    void login(String token, CurrentLoginUser user);

    /**
     * 更新在线会话最近活动时间。
     */
    void activity(String token, CurrentLoginUser user);

    /**
     * 注销当前登录会话。
     */
    void logout(String token);

    /**
     * 查询当前授权范围内的数据列表。
     */
    List<OnlineUserRespDto> list(String keyword);

    /**
     * 统计当前 Tenant 在线用户数。
     */
    long countCurrentTenant();

    /**
     * 强制注销指定在线会话。
     */
    void forceLogout(String sessionId);

    /**
     * 使指定 Tenant 用户会话失效。
     */
    void invalidateTenantUsers(Long tenantId, Collection<Long> userIds);

    /**
     * 使指定用户全部会话失效。
     */
    void invalidateUsers(Collection<Long> userIds);
}
