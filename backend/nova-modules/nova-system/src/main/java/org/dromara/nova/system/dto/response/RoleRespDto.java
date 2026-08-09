package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色响应。
 *
 * @param id                  主键 ID
 * @param roleCode            角色编码，Tenant 内唯一
 * @param roleName            角色名称
 * @param dataScope           数据权限范围
 * @param builtIn             是否系统内置数据
 * @param sort                排序值，数值越小越靠前
 * @param status              状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark              备注
 * @param customDepartmentIds CUSTOM 数据范围授权的部门ID 集合
 * @param createTime          创建时间
 */
@Schema(description = "角色详情响应")
public record RoleRespDto(@Schema(description = "主键 ID") Long id,
                          @Schema(description = "角色编码，Tenant 内唯一") String roleCode,
                          @Schema(description = "角色名称") String roleName,
                          @Schema(description = "数据权限范围") String dataScope,
                          @Schema(description = "是否系统内置数据") boolean builtIn,
                          @Schema(description = "排序值，数值越小越靠前") Integer sort,
                          @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                          @Schema(description = "备注") String remark,
                          @Schema(description = "CUSTOM 数据范围授权的部门ID 集合") List<Long> customDepartmentIds,
                          @Schema(description = "创建时间") LocalDateTime createTime) {
}
