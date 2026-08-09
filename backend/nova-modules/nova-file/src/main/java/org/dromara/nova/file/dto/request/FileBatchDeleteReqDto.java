package org.dromara.nova.file.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 批量删除文件。
 *
 * @param fileIds 文件 ID 集合
 */
@Schema(description = "文件批量删除请求参数")
public record FileBatchDeleteReqDto(@Schema(description = "文件 ID 集合") @NotEmpty List<Long> fileIds) {
}
