package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * 操作日志分页查询。
 *
 * @param pageNum       页码，从 1 开始
 * @param pageSize      每页条数
 * @param module        操作所属业务模块
 * @param operationType 操作类型
 * @param operator      操作人
 * @param requestMethod HTTP 请求方法
 * @param status        状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param requestIp     请求客户端 IP
 * @param startTime     查询开始时间
 * @param endTime       查询结束时间
 */
@Schema(description = "操作日志分页查询参数")
public record OperationLogPageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                                     @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                                     @Schema(description = "操作所属业务模块") String module,
                                     @Schema(description = "操作类型") String operationType,
                                     @Schema(description = "操作人") String operator,
                                     @Schema(description = "HTTP 请求方法") String requestMethod,
                                     @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") String status,
                                     @Schema(description = "请求客户端 IP") String requestIp,
                                     @Schema(description = "查询开始时间") LocalDateTime startTime,
                                     @Schema(description = "查询结束时间") LocalDateTime endTime) {
}
