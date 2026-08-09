package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.common.core.enums.DataScopeType;
import org.dromara.nova.system.entity.RoleEntity;

import java.util.List;

/**
 * 角色新增请求。
 *
 * @param roleCode            角色编码，Tenant 内唯一
 * @param roleName            角色名称
 * @param dataScope           数据权限范围
 * @param sort                排序值，数值越小越靠前
 * @param status              状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark              备注
 * @param customDepartmentIds CUSTOM 数据范围授权的 Department ID 集合
 */
@AutoMapper(target = RoleEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增角色请求参数")
public record RoleCreateReqDto(@Schema(description = "角色编码，Tenant 内唯一") @NotBlank String roleCode,
                               @Schema(description = "角色名称") @NotBlank String roleName,
                               @Schema(description = "数据权限范围") @NotNull DataScopeType dataScope,
                               @Schema(description = "排序值，数值越小越靠前") Integer sort,
                               @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status,
                               @Schema(description = "备注") String remark,
                               @Schema(description = "CUSTOM 数据范围授权的 Department ID 集合") List<Long> customDepartmentIds) {
}
