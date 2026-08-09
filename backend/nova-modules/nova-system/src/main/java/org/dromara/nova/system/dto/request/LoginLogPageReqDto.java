package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * 登录日志分页查询。
 *
 * @param pageNum     页码，从 1 开始
 * @param pageSize    每页条数
 * @param username    登录账号
 * @param loginStatus 登录结果状态
 * @param ip          客户端 IP 地址
 * @param startTime   查询开始时间
 * @param endTime     查询结束时间
 */
@Schema(description = "登录日志分页查询参数")
public record LoginLogPageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                                 @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                                 @Schema(description = "登录账号") String username,
                                 @Schema(description = "登录结果状态") Integer loginStatus,
                                 @Schema(description = "客户端 IP 地址") String ip,
                                 @Schema(description = "查询开始时间") LocalDateTime startTime,
                                 @Schema(description = "查询结束时间") LocalDateTime endTime) {
}
