package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.DictTypeRespDto;

/**
 * Tenant 字典类型实体；字典编码创建后原则上保持稳定。
 */

@AutoMapper(target = DictTypeRespDto.class, reverseConvertGenerate = false)
@Table("sys_dict_type")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeEntity extends BaseEntity {
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
     * 字典名称。
     */
    @Column("dict_name")
    private String dictName;
    /**
     * 字典编码，Tenant 内唯一。
     */
    @Column("dict_code")
    private String dictCode;
    /**
     * 是否系统内置数据。
     */
    @Column("built_in")
    private Boolean builtIn;
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
