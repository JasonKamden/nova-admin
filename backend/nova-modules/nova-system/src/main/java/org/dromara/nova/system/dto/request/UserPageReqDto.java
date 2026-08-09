package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 用户分页查询。
 *
 * @param pageNum          页码，从 1 开始
 * @param pageSize         每页条数
 * @param username         登录账号
 * @param nickname         用户昵称或姓名
 * @param phone            联系电话或手机号
 * @param email            邮箱地址
 * @param departmentId部门ID
 * @param status           状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "当前 Tenant 用户分页查询参数")
public record UserPageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                             @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                             @Schema(description = "登录账号") String username,
                             @Schema(description = "用户昵称或姓名") String nickname,
                             @Schema(description = "联系电话或手机号") String phone,
                             @Schema(description = "邮箱地址") String email,
                             @Schema(description = "Department ID") Long departmentId,
                             @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status) {
}
