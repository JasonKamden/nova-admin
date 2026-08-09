package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.RoleRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;

/**
 * Tenant 角色实体；角色功能权限与 DataScope 数据权限分别配置。
 */

@AutoMapper(target = RoleSimpleRespDto.class, reverseConvertGenerate = false)
@AutoMapper(target = RoleRespDto.class, reverseConvertGenerate = false)
@Table("sys_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleEntity extends BaseEntity {
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
     * 角色编码，Tenant 内唯一。
     */
    @Column("role_code")
    private String roleCode;
    /**
     * 角色名称。
     */
    @Column("role_name")
    private String roleName;
    /**
     * 数据权限范围。
     */
    @Column("data_scope")
    private String dataScope;
    /**
     * 是否系统内置数据。
     */
    @Column("built_in")
    private Boolean builtIn;
    /**
     * 排序值，数值越小越靠前。
     */
    @Column("sort")
    private Integer sort;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private Integer status;
    /**
     * 备注。
     */
    @Column("remark")
    private String remark;
}
