package org.dromara.nova.common.security.util;

import cn.dev33.satoken.stp.StpUtil;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.security.constant.SecurityConstants;
import org.dromara.nova.common.security.model.CurrentLoginUser;

/**
 * 统一读取/更新 Sa-Token 登录用户快照。
 */
public final class LoginUserUtils {
    private LoginUserUtils() {
    }

    /**
     * 读取当前 Sa-Token Session 中保存的完整登录用户快照。
     *
     * @return 当前登录用户快照
     */
    public static CurrentLoginUser getLoginUser() {
        StpUtil.checkLogin();
        Object value = StpUtil.getTokenSession().get(SecurityConstants.LOGIN_USER_KEY);
        if (value instanceof CurrentLoginUser user) return user;
        throw new BusinessException(CommonResultCode.UNAUTHORIZED, "登录上下文已失效");
    }

    /**
     * 将登录用户快照写入当前 Sa-Token Token Session。
     *
     * @param user 登录用户快照
     */
    public static void saveLoginUser(CurrentLoginUser user) {
        StpUtil.getTokenSession().set(SecurityConstants.LOGIN_USER_KEY, user);
    }

    /**
     * 返回当前登录用户 ID。
     *
     * @return 当前用户 ID
     */
    public static Long getUserId() {
        return getLoginUser().userId();
    }

    /**
     * 返回当前登录账号。
     *
     * @return 当前登录账号
     */
    public static String getUsername() {
        return getLoginUser().username();
    }

    /**
     * 判断当前登录身份是否为平台管理员。
     *
     * @return 是否为平台管理员
     */
    public static boolean isPlatformAdmin() {
        return getLoginUser().platformAdmin();
    }

    /**
     * 返回当前登录 Context 的 Tenant ID；PLATFORM 下为 null。
     *
     * @return 当前 Tenant ID 或 null
     */
    public static Long getTenantId() {
        return getLoginUser().tenantId();
    }

    /**
     * 返回当前登录用户在当前 Tenant 的 Department ID。
     *
     * @return 当前 Department ID
     */
    public static Long getDepartmentId() {
        return getLoginUser().departmentId();
    }
}
