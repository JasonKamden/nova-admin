package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户与 Tenant 成员关系实体，描述用户所在 Tenant、Department 及成员状态。
 */

@Table("sys_user_tenant")
@Data
public class UserTenantEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 用户 ID。
     */
    @Column("user_id")
    private Long userId;
    /**
     * Tenant ID；Tenant 业务写入以服务端可信 Context 为准。
     */
    @Column("tenant_id")
    private Long tenantId;
    /**
     * 部门ID。
     */
    @Column("department_id")
    private Long departmentId;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private Integer status;
    /**
     * 加入 Tenant 时间。
     */
    @Column("join_time")
    private LocalDateTime joinTime;
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
    /**
     * 最后更新人用户 ID。
     */
    @Column("update_by")
    private Long updateBy;
    /**
     * 最后更新时间。
     */
    @Column("update_time")
    private LocalDateTime updateTime;
}
