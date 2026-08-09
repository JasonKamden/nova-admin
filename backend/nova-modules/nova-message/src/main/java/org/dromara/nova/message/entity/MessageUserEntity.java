package org.dromara.nova.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.nova.message.dto.response.RecipientRespDto;

import java.time.LocalDateTime;

/**
 * 发送时冻结的收件人快照。
 */
@AutoMapper(target = RecipientRespDto.class, reverseConvertGenerate = false)
@Table("sys_message_user")
@Data
public class MessageUserEntity {
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
     * 用户 ID。
     */
    @Column("user_id")
    private Long userId;
    /**
     * 部门ID。
     */
    @Column("department_id")
    private Long departmentId;
    /**
     * 登录账号。
     */
    @Column("username")
    private String username;
    /**
     * 用户昵称或姓名。
     */
    @Column("nickname")
    private String nickname;
    /**
     * 部门名称。
     */
    @Column("department_name")
    private String departmentName;
    /**
     * 阅读状态：0 未读，1 已读。
     */
    @Column("read_status")
    private Integer readStatus;
    /**
     * 消息接收时间。
     */
    @Column("receive_time")
    private LocalDateTime receiveTime;
    /**
     * 阅读时间。
     */
    @Column("read_time")
    private LocalDateTime readTime;
}
