package org.dromara.nova.message.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.message.entity.MessageEntity;
import org.dromara.nova.message.enums.MessageType;

import java.util.List;

/**
 * 新增消息草稿。
 *
 * @param title       消息标题
 * @param messageType 消息类型
 * @param contentHtml 经过安全清洗的富文本消息正文
 * @param recipient   消息接收范围规则
 * @param fileIds     文件 ID 集合
 */
@AutoMapper(target = MessageEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增消息草稿请求参数")
public record MessageCreateReqDto(@Schema(description = "消息标题") @NotBlank String title,
                                  @Schema(description = "消息类型") @NotNull MessageType messageType,
                                  @Schema(description = "经过安全清洗的富文本消息正文") @NotBlank String contentHtml,
                                  @Schema(description = "消息接收范围规则") @Valid @NotNull MessageRecipientReqDto recipient,
                                  @Schema(description = "文件 ID 集合") List<Long> fileIds) {
}
