package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 逻辑缓存列表。
 *
 * @param code              编码
 * @param name              名称
 * @param type              类型
 * @param module            操作所属业务模块
 * @param scope             缓存或业务作用域
 * @param defaultTtlSeconds 默认缓存 TTL，单位秒
 * @param clearable         是否允许手工清理
 * @param refreshable       是否支持刷新/重建缓存
 * @param description       说明
 * @param status            状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "逻辑缓存元数据响应")
public record CacheRespDto(@Schema(description = "编码") String code, @Schema(description = "名称") String name,
                           @Schema(description = "类型") String type,
                           @Schema(description = "操作所属业务模块") String module,
                           @Schema(description = "缓存或业务作用域") String scope,
                           @Schema(description = "默认缓存 TTL，单位秒") long defaultTtlSeconds,
                           @Schema(description = "是否允许手工清理") boolean clearable,
                           @Schema(description = "是否支持刷新/重建缓存") boolean refreshable,
                           @Schema(description = "说明") String description,
                           @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") String status) {
}
