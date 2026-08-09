package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dromara.nova.common.core.enums.ContextType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人中心聚合响应。
 *
 * @param userId         用户 ID
 * @param username       登录账号
 * @param nickname       用户昵称或姓名
 * @param avatar         用户头像地址或文件标识
 * @param gender         性别编码
 * @param phone          联系电话或手机号
 * @param email          邮箱地址
 * @param bio            个人简介
 * @param status         状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param platformAdmin  是否平台管理员
 * @param contextType    当前运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId       Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param tenantName     Tenant 名称
 * @param departmentId   部门ID
 * @param departmentName 部门名称
 * @param roles          当前用户角色集合
 * @param createTime     创建时间
 * @param lastLoginTime  最近一次登录时间
 * @param lastLoginIp    最近一次登录 IP
 */
@Schema(description = "个人中心聚合响应")
public record ProfileRespDto(@Schema(description = "用户 ID") Long userId,
                             @Schema(description = "登录账号") String username,
                             @Schema(description = "用户昵称或姓名") String nickname,
                             @Schema(description = "用户头像地址或文件标识") String avatar,
                             @Schema(description = "性别编码") String gender,
                             @Schema(description = "联系电话或手机号") String phone,
                             @Schema(description = "邮箱地址") String email,
                             @Schema(description = "个人简介") String bio,
                             @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                             @Schema(description = "是否平台管理员") boolean platformAdmin,
                             @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") ContextType contextType,
                             @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId,
                             @Schema(description = "Tenant 名称") String tenantName,
                             @Schema(description = "Department ID") Long departmentId,
                             @Schema(description = "Department 名称") String departmentName,
                             @Schema(description = "当前用户角色集合") List<RoleSimpleRespDto> roles,
                             @Schema(description = "创建时间") LocalDateTime createTime,
                             @Schema(description = "最近一次登录时间") LocalDateTime lastLoginTime,
                             @Schema(description = "最近一次登录 IP") String lastLoginIp) {
}
