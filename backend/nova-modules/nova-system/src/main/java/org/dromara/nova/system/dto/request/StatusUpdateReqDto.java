package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 通用状态修改。
 *
 * @param status 状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "通用启用/停用状态修改请求")
public record StatusUpdateReqDto(
        @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull @Min(0) @Max(1) Integer status) {
}
