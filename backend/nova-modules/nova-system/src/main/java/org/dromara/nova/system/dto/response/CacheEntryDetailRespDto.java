package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 逻辑缓存条目详情。
 *
 * @param key       缓存键
 * @param valueType 缓存值类型
 * @param valueJson 缓存值 JSON 文本
 */
@Schema(description = "逻辑缓存条目详情响应")
public record CacheEntryDetailRespDto(
        @Schema(description = "缓存键") String key,
        @Schema(description = "缓存值类型") String valueType,
        @Schema(description = "缓存值 JSON 文本") String valueJson) {
}
