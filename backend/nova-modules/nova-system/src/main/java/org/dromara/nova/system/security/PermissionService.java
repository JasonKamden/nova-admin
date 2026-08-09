package org.dromara.nova.system.security;

import cn.dev33.satoken.stp.StpInterface;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 角色/权限来源。
 */
@Component
public class PermissionService implements StpInterface {
    /**
     * 解析 Sa-Token 当前登录用户在当前 Context 下的权限编码列表。
     *
     * @param loginId   Sa-Token 登录 ID
     * @param loginType 登录类型
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return LoginUserUtils.getLoginUser().permissions();
    }

    /**
     * 解析 Sa-Token 当前登录用户在当前 Context 下的角色编码列表。
     *
     * @param loginId   Sa-Token 登录 ID
     * @param loginType 登录类型
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return LoginUserUtils.getLoginUser().roles();
    }
}
