package org.dromara.nova.file.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 文件分页查询。
 *
 * @param pageNum     页码，从 1 开始
 * @param pageSize    每页条数
 * @param fileName    文件名称查询条件
 * @param contentType 文件或请求的 MIME 类型
 * @param storageType 存储类型：LOCAL 或 MINIO
 * @param status      状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "文件分页查询请求参数")
public record FilePageReqDto(@Schema(description = "页码，从 1 开始") @Min(1) long pageNum,
                             @Schema(description = "每页条数") @Min(1) @Max(200) long pageSize,
                             @Schema(description = "文件名称查询条件") String fileName,
                             @Schema(description = "文件或请求的 MIME 类型") String contentType,
                             @Schema(description = "存储类型：LOCAL 或 MINIO") String storageType,
                             @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status) {
}
