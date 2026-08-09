package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.system.entity.DepartmentEntity;

/**
 * Department 修改请求。
 *
 * @param parentId       父节点 ID；根节点可为空
 * @param departmentName Department 名称
 * @param leaderUserId   Department 负责人用户 ID
 * @param phone          联系电话或手机号
 * @param email          邮箱地址
 * @param sort           排序值，数值越小越靠前
 * @param status         状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@AutoMapper(target = DepartmentEntity.class, reverseConvertGenerate = false)
@Schema(description = "修改 Department 请求参数")
public record DepartmentUpdateReqDto(@Schema(description = "父节点 ID；根节点可为空") Long parentId,
                                     @Schema(description = "Department 名称") @NotBlank String departmentName,
                                     @Schema(description = "Department 负责人用户 ID") Long leaderUserId,
                                     @Schema(description = "联系电话或手机号") String phone,
                                     @Schema(description = "邮箱地址") @Email String email,
                                     @Schema(description = "排序值，数值越小越靠前") Integer sort,
                                     @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status) {
}
