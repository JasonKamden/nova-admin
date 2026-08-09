package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.dromara.nova.system.entity.UserEntity;

/**
 * 个人基本资料修改；不允许修改 Tenant/Department/Role/平台身份。
 *
 * @param nickname 用户昵称或姓名
 * @param gender   性别编码
 * @param phone    联系电话或手机号
 * @param email    邮箱地址
 * @param bio      个人简介
 */
@AutoMapper(target = UserEntity.class, reverseConvertGenerate = false)
@Schema(description = "个人中心修改基本资料请求")
public record ProfileUpdateReqDto(@Schema(description = "用户昵称或姓名") String nickname,
                                  @Schema(description = "性别编码") String gender,
                                  @Schema(description = "联系电话或手机号") String phone,
                                  @Schema(description = "邮箱地址") @Email String email,
                                  @Schema(description = "个人简介") String bio) {
}
