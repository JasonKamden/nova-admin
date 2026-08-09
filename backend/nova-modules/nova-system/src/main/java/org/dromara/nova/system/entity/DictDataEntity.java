package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.DictDataRespDto;

/**
 * Tenant 字典数据实体，保存字典标签、值、语义标签样式和排序。
 */

@AutoMapper(target = DictDataRespDto.class, reverseConvertGenerate = false)
@Table("sys_dict_data")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataEntity extends BaseEntity {
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
     * 字典类型 ID。
     */
    @Column("dict_type_id")
    private Long dictTypeId;
    /**
     * 字典标签。
     */
    @Column("dict_label")
    private String dictLabel;
    /**
     * 字典值。
     */
    @Column("dict_value")
    private String dictValue;
    /**
     * 字典标签语义样式：default/primary/info/success/warning/error。
     */
    @Column("tag_type")
    private String tagType;
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
