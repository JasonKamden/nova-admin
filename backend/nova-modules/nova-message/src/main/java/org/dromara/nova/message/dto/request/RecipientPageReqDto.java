package org.dromara.nova.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 消息阅读情况分页。
 *
 * @param pageNum          页码，从 1 开始
 * @param pageSize         每页条数
 * @param user             用户名或昵称查询条件
 * @param departmentId部门ID
 * @param readStatus       阅读状态：0 未读，1 已读
 */
@Schema(description = "消息接收与阅读情况分页查询参数")
public record RecipientPageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                                  @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                                  @Schema(description = "用户名或昵称查询条件") String user,
                                  @Schema(description = "Department ID") Long departmentId,
                                  @Schema(description = "阅读状态：0 未读，1 已读") Integer readStatus) {
}
