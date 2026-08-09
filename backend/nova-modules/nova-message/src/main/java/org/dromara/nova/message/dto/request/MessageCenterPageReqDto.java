package org.dromara.nova.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 当前用户消息中心分页。readStatus: null/0/1。
 *
 * @param pageNum    页码，从 1 开始
 * @param pageSize   每页条数
 * @param readStatus 阅读状态：0 未读，1 已读
 */
@Schema(description = "当前登录用户消息中心分页查询参数")
public record MessageCenterPageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                                      @Schema(description = "每页条数") @Min(1) @Max(100) long pageSize,
                                      @Schema(description = "阅读状态：0 未读，1 已读") Integer readStatus) {
}
