package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.ContextTenantOptionRespDto;
import org.dromara.nova.system.dto.response.TenantRespDto;

import java.time.LocalDate;

/**
 * Tenant 业务单位实体；PLATFORM 本身不是 Tenant，不使用伪 tenantId 表示平台。
 */

@AutoMapper(target = TenantRespDto.class, reverseConvertGenerate = false)
@AutoMapper(target = ContextTenantOptionRespDto.class, reverseConvertGenerate = false)
@Table("sys_tenant")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends BaseEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    @AutoMapping(targetClass = ContextTenantOptionRespDto.class, target = "tenantId")
    private Long id;
    /**
     * Tenant 编码，全局唯一。
     */
    @Column("tenant_code")
    private String tenantCode;
    /**
     * Tenant 名称。
     */
    @Column("tenant_name")
    private String tenantName;
    /**
     * 联系人姓名。
     */
    @Column("contact_name")
    private String contactName;
    /**
     * 联系人电话。
     */
    @Column("contact_phone")
    private String contactPhone;
    /**
     * 联系人邮箱。
     */
    @Column("contact_email")
    private String contactEmail;
    /**
     * Tenant 有效期截止日期。
     */
    @Column("expire_at")
    private LocalDate expireAt;
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
