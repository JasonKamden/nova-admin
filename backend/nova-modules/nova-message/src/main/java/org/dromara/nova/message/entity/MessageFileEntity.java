package org.dromara.nova.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 消息附件关系。
 */
@Table("sys_message_file")
@Data
public class MessageFileEntity {
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
     * 消息 ID。
     */
    @Column("message_id")
    private Long messageId;
    /**
     * 文件 ID。
     */
    @Column("file_id")
    private Long fileId;
}
