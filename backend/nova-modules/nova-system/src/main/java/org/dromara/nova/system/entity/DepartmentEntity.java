package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.DepartmentRespDto;

/**
 * Tenant 内部门树节点实体；所有业务查询必须受当前 Tenant Context 约束。
 */

@AutoMapper(target = DepartmentRespDto.class, reverseConvertGenerate = false)
@Table("sys_department")
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentEntity extends BaseEntity {
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
     * 父节点 ID；根节点可为空。
     */
    @Column("parent_id")
    private Long parentId;
    /**
     * 部门编码，Tenant 内唯一。
     */
    @Column("department_code")
    private String departmentCode;
    /**
     * 部门名称。
     */
    @Column("department_name")
    private String departmentName;
    /**
     * 部门负责人用户 ID。
     */
    @Column("leader_user_id")
    private Long leaderUserId;
    /**
     * 联系电话或手机号。
     */
    @Column("phone")
    private String phone;
    /**
     * 邮箱地址。
     */
    @Column("email")
    private String email;
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
}
