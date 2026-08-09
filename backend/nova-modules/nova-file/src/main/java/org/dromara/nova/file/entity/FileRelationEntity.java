package org.dromara.nova.file.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件与业务对象关系。
 */
@Table("sys_file_relation")
@Data
public class FileRelationEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * Tenant ID；Tenant 业务写入以服务端可信 Context 为准。
     */
    @Column("tenant_id")
    private Long tenantId;
    /**
     * 文件 ID。
     */
    @Column("file_id")
    private Long fileId;
    /**
     * 业务类型。
     */
    @Column("business_type")
    private String businessType;
    /**
     * 业务数据主键。
     */
    @Column("business_id")
    private String businessId;
    /**
     * 创建人用户 ID。
     */
    @Column("create_by")
    private Long createBy;
    /**
     * 创建时间。
     */
    @Column("create_time")
    private LocalDateTime createTime;
}
