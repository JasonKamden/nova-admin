package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色 CUSTOM DataScope 与 Department 的授权关系实体。
 */

@Table("sys_role_department")
@Data
public class RoleDepartmentEntity {
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
     * 角色 ID。
     */
    @Column("role_id")
    private Long roleId;
    /**
     * Department ID。
     */
    @Column("department_id")
    private Long departmentId;
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
