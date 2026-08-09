package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 逻辑缓存条目预览。
 *
 * @param key          缓存键
 * @param valuePreview 缓存值预览
 * @param valueType    缓存值类型
 */
@Schema(description = "逻辑缓存条目预览响应")
public record CacheEntryRespDto(
        @Schema(description = "缓存键") String key,
        @Schema(description = "缓存值预览") String valuePreview,
        @Schema(description = "缓存值类型") String valueType) {
}
