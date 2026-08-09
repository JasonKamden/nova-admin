package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 批量清理逻辑缓存。
 *
 * @param cacheCodes 待批量处理的逻辑缓存编码集合
 */
@Schema(description = "逻辑缓存批量清理请求参数")
public record CacheBatchReqDto(
        @Schema(description = "待批量处理的逻辑缓存编码集合") @NotEmpty List<String> cacheCodes) {
}
