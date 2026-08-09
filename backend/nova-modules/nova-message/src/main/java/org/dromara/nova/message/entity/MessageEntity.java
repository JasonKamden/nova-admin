package org.dromara.nova.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.message.dto.response.MessageDetailRespDto;
import org.dromara.nova.message.dto.response.MessageRespDto;

import java.time.LocalDateTime;

/**
 * 管理员消息主体。
 */
@AutoMapper(target = MessageRespDto.class, reverseConvertGenerate = false)
@AutoMapper(target = MessageDetailRespDto.class, reverseConvertGenerate = false)
@Table("sys_message")
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageEntity extends BaseEntity {
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
     * 消息标题。
     */
    @Column("title")
    private String title;
    /**
     * 消息类型。
     */
    @Column("message_type")
    private String messageType;
    /**
     * 经过安全清洗的富文本消息正文。
     */
    @Column("content_html")
    private String contentHtml;
    /**
     * 接收范围类型：ALL、DEPARTMENT、ROLE 或 USER。
     */
    @Column("recipient_type")
    private String recipientType;
    /**
     * 接收范围规则快照 JSON。
     */
    @Column("recipient_rule_json")
    private String recipientRuleJson;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private String status;
    /**
     * 实际接收人数。
     */
    @Column("recipient_count")
    private Integer recipientCount;
    /**
     * 已读人数。
     */
    @Column("read_count")
    private Integer readCount;
    /**
     * 消息发送时间。
     */
    @Column("send_time")
    private LocalDateTime sendTime;
    /**
     * 消息撤回时间。
     */
    @Column("withdraw_time")
    private LocalDateTime withdrawTime;
}
