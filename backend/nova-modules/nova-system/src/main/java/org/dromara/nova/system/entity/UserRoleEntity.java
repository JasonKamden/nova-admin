package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Tenant 内用户角色关联实体，表示用户在当前 Tenant 能执行的权限集合来源。
 */

@Table("sys_user_role")
@Data
public class UserRoleEntity {
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
     * 用户 ID。
     */
    @Column("user_id")
    private Long userId;
    /**
     * 角色 ID。
     */
    @Column("role_id")
    private Long roleId;
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
