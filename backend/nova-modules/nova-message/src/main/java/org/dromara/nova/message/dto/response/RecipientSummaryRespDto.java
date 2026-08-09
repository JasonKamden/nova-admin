package org.dromara.nova.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 预计/实际接收统计。
 *
 * @param total    总数量
 * @param read     已读数量
 * @param unread   未读数量
 * @param readRate 已读率，0~100 或按接口约定返回
 */
@Schema(description = "消息接收统计响应")
public record RecipientSummaryRespDto(@Schema(description = "总数量") long total,
                                      @Schema(description = "已读数量") long read,
                                      @Schema(description = "未读数量") long unread,
                                      @Schema(description = "已读率，0~100 或按接口约定返回") double readRate) {
}
