package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 角色菜单全量替换。
 *
 * @param menuIds 菜单 ID 集合，全量替换提交
 */
@Schema(description = "角色菜单授权全量替换请求")
public record RoleMenuUpdateReqDto(@Schema(description = "菜单 ID 集合，全量替换提交") @NotNull List<Long> menuIds) {
}
