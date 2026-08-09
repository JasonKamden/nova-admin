package org.dromara.nova.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 消息管理列表。
 *
 * @param id             主键 ID
 * @param title          消息标题
 * @param messageType    消息类型
 * @param recipientType  接收范围类型：ALL、DEPARTMENT、ROLE 或 USER
 * @param status         状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param recipientCount 实际接收人数
 * @param readCount      已读人数
 * @param readRate       已读率，0~100 或按接口约定返回
 * @param createBy       创建人用户 ID
 * @param createTime     创建时间
 * @param sendTime       消息发送时间
 */
@Schema(description = "消息管理列表项响应")
public record MessageRespDto(@Schema(description = "主键 ID") Long id, @Schema(description = "消息标题") String title,
                             @Schema(description = "消息类型") String messageType,
                             @Schema(description = "接收范围类型：ALL、DEPARTMENT、ROLE 或 USER") String recipientType,
                             @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") String status,
                             @Schema(description = "实际接收人数") int recipientCount,
                             @Schema(description = "已读人数") int readCount,
                             @Schema(description = "已读率，0~100 或按接口约定返回") double readRate,
                             @Schema(description = "创建人用户 ID") Long createBy,
                             @Schema(description = "创建时间") LocalDateTime createTime,
                             @Schema(description = "消息发送时间") LocalDateTime sendTime) {
}
