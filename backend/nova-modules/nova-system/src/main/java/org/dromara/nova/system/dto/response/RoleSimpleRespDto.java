package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色选择器简要项。
 *
 * @param id        主键 ID
 * @param roleCode  角色编码，Tenant 内唯一
 * @param roleName  角色名称
 * @param dataScope 数据权限范围
 * @param status    状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "角色选择器简要响应")
public record RoleSimpleRespDto(@Schema(description = "主键 ID") Long id,
                                @Schema(description = "角色编码，Tenant 内唯一") String roleCode,
                                @Schema(description = "角色名称") String roleName,
                                @Schema(description = "数据权限范围") String dataScope,
                                @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status) {
}
