package org.dromara.nova.common.security.model;

import org.dromara.nova.common.core.enums.ContextType;

import java.io.Serializable;
import java.util.List;

/**
 * CurrentLoginUser 数据模型。
 *
 * @param userId         用户 ID
 * @param username       登录账号
 * @param nickname       用户昵称或姓名
 * @param avatar         用户头像地址或文件标识
 * @param platformAdmin  是否平台管理员
 * @param contextType    运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId       Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param tenantName     Tenant 名称
 * @param departmentId   Department ID
 * @param departmentName Department 名称
 * @param roles          当前用户角色集合
 * @param permissions    当前 Context 权限编码集合
 */
public record CurrentLoginUser(
        Long userId, String username, String nickname, String avatar, boolean platformAdmin,
        ContextType contextType, Long tenantId, String tenantName, Long departmentId,
        String departmentName, List<String> roles, List<String> permissions
) implements Serializable {
    public CurrentLoginUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
        permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }
}
