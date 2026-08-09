package org.dromara.nova.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 消息中心列表。
 *
 * @param messageId   消息 ID
 * @param title       消息标题
 * @param messageType 消息类型
 * @param summary     消息摘要
 * @param readStatus  阅读状态：0 未读，1 已读
 * @param receiveTime 消息接收时间
 * @param readTime    阅读时间
 * @param sendTime    消息发送时间
 */
@Schema(description = "消息中心列表项响应")
public record MessageCenterRespDto(@Schema(description = "消息 ID") Long messageId,
                                   @Schema(description = "消息标题") String title,
                                   @Schema(description = "消息类型") String messageType,
                                   @Schema(description = "消息摘要") String summary,
                                   @Schema(description = "阅读状态：0 未读，1 已读") Integer readStatus,
                                   @Schema(description = "消息接收时间") LocalDateTime receiveTime,
                                   @Schema(description = "阅读时间") LocalDateTime readTime,
                                   @Schema(description = "消息发送时间") LocalDateTime sendTime) {
}
