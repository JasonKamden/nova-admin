package org.dromara.nova.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅读情况用户明细。
 *
 * @param userId             用户 ID
 * @param username           登录账号
 * @param nickname           用户昵称或姓名
 * @param departmentId       部门ID
 * @param departmentName部门名称
 * @param readStatus         阅读状态：0 未读，1 已读
 * @param receiveTime        消息接收时间
 * @param readTime           阅读时间
 */
@Schema(description = "消息接收用户阅读明细响应")
public record RecipientRespDto(@Schema(description = "用户 ID") Long userId,
                               @Schema(description = "登录账号") String username,
                               @Schema(description = "用户昵称或姓名") String nickname,
                               @Schema(description = "Department ID") Long departmentId,
                               @Schema(description = "Department 名称") String departmentName,
                               @Schema(description = "阅读状态：0 未读，1 已读") Integer readStatus,
                               @Schema(description = "消息接收时间") LocalDateTime receiveTime,
                               @Schema(description = "阅读时间") LocalDateTime readTime) {
}
