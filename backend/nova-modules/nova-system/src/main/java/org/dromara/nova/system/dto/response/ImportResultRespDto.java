package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 批量导入结果。
 *
 * @param total   总数量
 * @param success 成功数量
 * @param failed  失败数量
 * @param errors  错误明细集合
 */
@Schema(description = "用户导入处理结果")
public record ImportResultRespDto(@Schema(description = "总数量") int total,
                                  @Schema(description = "成功数量") int success,
                                  @Schema(description = "失败数量") int failed,
                                  @Schema(description = "错误明细集合") List<String> errors) {
}
