package org.dromara.nova.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * 消息管理分页。
 *
 * @param pageNum     页码，从 1 开始
 * @param pageSize    每页条数
 * @param title       消息标题
 * @param messageType 消息类型
 * @param status      状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param creator     创建人账号或名称
 * @param startTime   查询开始时间
 * @param endTime     查询结束时间
 */
@Schema(description = "消息管理分页查询参数")
public record MessagePageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                                @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                                @Schema(description = "消息标题") String title,
                                @Schema(description = "消息类型") String messageType,
                                @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") String status,
                                @Schema(description = "创建人账号或名称") String creator,
                                @Schema(description = "查询开始时间") LocalDateTime startTime,
                                @Schema(description = "查询结束时间") LocalDateTime endTime) {
}
