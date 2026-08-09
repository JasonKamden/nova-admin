package org.dromara.nova.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.message.enums.RecipientType;

import java.util.List;

/**
 * 消息接收范围规则。
 *
 * @param recipientType   接收范围类型：ALL、DEPARTMENT、ROLE 或 USER
 * @param departmentIds   部门ID 集合
 * @param includeChildren 选择部门时是否包含全部下级 Department
 * @param roleIds         角色 ID 集合，全量替换提交
 * @param userIds         用户 ID 集合
 */
@Schema(description = "消息接收范围规则")
public record MessageRecipientReqDto(
        @Schema(description = "接收范围类型：ALL、DEPARTMENT、ROLE 或 USER") @NotNull RecipientType recipientType,
        @Schema(description = "Department ID 集合") List<Long> departmentIds,
        @Schema(description = "选择部门时是否包含全部下级 Department") Boolean includeChildren,
        @Schema(description = "角色 ID 集合，全量替换提交") List<Long> roleIds,
        @Schema(description = "用户 ID 集合") List<Long> userIds) {
}
