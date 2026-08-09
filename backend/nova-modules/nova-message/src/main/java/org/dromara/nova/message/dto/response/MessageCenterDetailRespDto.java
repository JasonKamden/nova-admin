package org.dromara.nova.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前用户消息详情。
 *
 * @param messageId   消息 ID
 * @param title       消息标题
 * @param messageType 消息类型
 * @param contentHtml 经过安全清洗的富文本消息正文
 * @param sendTime    消息发送时间
 * @param fileIds     文件 ID 集合
 */
@Schema(description = "当前登录用户消息详情响应")
public record MessageCenterDetailRespDto(@Schema(description = "消息 ID") Long messageId,
                                         @Schema(description = "消息标题") String title,
                                         @Schema(description = "消息类型") String messageType,
                                         @Schema(description = "经过安全清洗的富文本消息正文") String contentHtml,
                                         @Schema(description = "消息发送时间") LocalDateTime sendTime,
                                         @Schema(description = "文件 ID 集合") List<Long> fileIds) {
}
