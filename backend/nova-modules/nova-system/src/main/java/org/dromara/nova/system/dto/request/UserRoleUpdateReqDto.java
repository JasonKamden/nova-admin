package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 用户角色全量替换。
 *
 * @param roleIds 角色 ID 集合，全量替换提交
 */
@Schema(description = "用户角色全量替换请求")
public record UserRoleUpdateReqDto(@Schema(description = "角色 ID 集合，全量替换提交") @NotNull List<Long> roleIds) {
}
