package org.dromara.nova.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 文件元数据响应。
 *
 * @param id           主键 ID
 * @param originalName 上传时原始文件名
 * @param extension    文件扩展名
 * @param contentType  文件或请求的 MIME 类型
 * @param fileSize     文件大小，单位字节
 * @param sha256       文件 SHA-256 摘要
 * @param storageType  存储类型：LOCAL 或 MINIO
 * @param status       状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param ownerUserId  文件所属用户 ID
 * @param createTime   创建时间
 */
@Schema(description = "文件元数据响应")
public record FileRespDto(@Schema(description = "主键 ID") Long id,
                          @Schema(description = "上传时原始文件名") String originalName,
                          @Schema(description = "文件扩展名") String extension,
                          @Schema(description = "文件或请求的 MIME 类型") String contentType,
                          @Schema(description = "文件大小，单位字节") long fileSize,
                          @Schema(description = "文件 SHA-256 摘要") String sha256,
                          @Schema(description = "存储类型：LOCAL 或 MINIO") String storageType,
                          @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                          @Schema(description = "文件所属用户 ID") Long ownerUserId,
                          @Schema(description = "创建时间") LocalDateTime createTime) {
}
