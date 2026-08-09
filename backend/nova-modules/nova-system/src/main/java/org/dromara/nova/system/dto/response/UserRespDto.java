package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前 Tenant 用户列表/详情。
 *
 * @param id             主键 ID
 * @param username       登录账号
 * @param nickname       用户昵称或姓名
 * @param avatar         用户头像地址或文件标识
 * @param gender         性别编码
 * @param phone          联系电话或手机号
 * @param email          邮箱地址
 * @param bio            个人简介
 * @param status         状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param departmentId   Department ID
 * @param departmentCode Department 编码，Tenant 内唯一
 * @param departmentName Department 名称
 * @param roles          当前用户角色集合
 * @param lastLoginTime  最近一次登录时间
 * @param lastLoginIp    最近一次登录 IP
 * @param createTime     创建时间
 */
@Schema(description = "用户与当前 Tenant 成员信息响应")
public record UserRespDto(
        @Schema(description = "主键 ID") Long id,
        @Schema(description = "登录账号") String username,
        @Schema(description = "用户昵称或姓名") String nickname,
        @Schema(description = "用户头像地址或文件标识") String avatar,
        @Schema(description = "性别编码") String gender,
        @Schema(description = "联系电话或手机号") String phone,
        @Schema(description = "邮箱地址") String email,
        @Schema(description = "个人简介") String bio,
        @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
        @Schema(description = "Department ID") Long departmentId,
        @Schema(description = "Department 编码，Tenant 内唯一") String departmentCode,
        @Schema(description = "Department 名称") String departmentName,
        @Schema(description = "当前用户角色集合") List<RoleSimpleRespDto> roles,
        @Schema(description = "最近一次登录时间") LocalDateTime lastLoginTime,
        @Schema(description = "最近一次登录 IP") String lastLoginIp,
        @Schema(description = "创建时间") LocalDateTime createTime
) {
}
