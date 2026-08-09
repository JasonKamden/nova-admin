package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 角色分页查询。
 *
 * @param pageNum  页码，从 1 开始
 * @param pageSize 每页条数
 * @param keyword  模糊搜索关键字
 * @param status   状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "角色分页查询参数")
public record RolePageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                             @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                             @Schema(description = "模糊搜索关键字") String keyword,
                             @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status) {
}
