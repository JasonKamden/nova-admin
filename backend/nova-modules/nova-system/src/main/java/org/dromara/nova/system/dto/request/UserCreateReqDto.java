package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.dromara.nova.system.entity.UserEntity;

import java.util.List;

/**
 * Tenant 新增成员请求。
 *
 * @param username        登录账号
 * @param nickname        用户昵称或姓名
 * @param gender          性别编码
 * @param phone           联系电话或手机号
 * @param email           邮箱地址
 * @param bio             个人简介
 * @param departmentId    部门ID
 * @param initialPassword 新增用户初始密码，仅写入时使用，不回显
 * @param roleIds         角色 ID 集合，全量替换提交
 */
@AutoMapper(target = UserEntity.class, reverseConvertGenerate = false)
@Schema(description = "当前 Tenant 新增成员请求；tenantId 不由客户端提交")
public record UserCreateReqDto(@Schema(description = "登录账号") @NotBlank String username,
                               @Schema(description = "用户昵称或姓名") @NotBlank String nickname,
                               @Schema(description = "性别编码") String gender,
                               @Schema(description = "联系电话或手机号") String phone,
                               @Schema(description = "邮箱地址") @Email String email,
                               @Schema(description = "个人简介") String bio,
                               @Schema(description = "Department ID") Long departmentId,
                               @Schema(description = "新增用户初始密码，仅写入时使用，不回显", accessMode = Schema.AccessMode.WRITE_ONLY) @Size(min = 8, max = 64) String initialPassword,
                               @Schema(description = "角色 ID 集合，全量替换提交") List<Long> roleIds) {
}
