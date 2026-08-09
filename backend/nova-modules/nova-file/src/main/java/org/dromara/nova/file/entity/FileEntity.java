package org.dromara.nova.file.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.file.dto.response.FileRespDto;

/**
 * 统一文件元数据。
 */
@AutoMapper(target = FileRespDto.class, reverseConvertGenerate = false)
@Table("sys_file")
@Data
@EqualsAndHashCode(callSuper = true)
public class FileEntity extends BaseEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 当前运行上下文类型：PLATFORM 或 TENANT。
     */
    @Column("context_type")
    private String contextType;
    /**
     * Tenant ID；Tenant 业务写入以服务端可信 Context 为准。
     */
    @Column("tenant_id")
    private Long tenantId;
    /**
     * 文件所属用户 ID。
     */
    @Column("owner_user_id")
    private Long ownerUserId;
    /**
     * 上传时原始文件名。
     */
    @Column("original_name")
    private String originalName;
    /**
     * 文件扩展名。
     */
    @Column("extension")
    private String extension;
    /**
     * 文件或请求的 MIME 类型。
     */
    @Column("content_type")
    private String contentType;
    /**
     * 文件大小，单位字节。
     */
    @Column("file_size")
    private Long fileSize;
    /**
     * 文件 SHA-256 摘要。
     */
    @Column("sha256")
    private String sha256;
    /**
     * 存储类型：LOCAL 或 MINIO。
     */
    @Column("storage_type")
    private String storageType;
    /**
     * 对象存储内部路径。
     */
    @Column("storage_path")
    private String storagePath;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private Integer status;
}
