package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.ConfigRespDto;

/**
 * Tenant 参数配置实体；敏感参数查询和日志必须脱敏。
 */

@AutoMapper(target = ConfigRespDto.class, reverseConvertGenerate = false)
@Table("sys_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigEntity extends BaseEntity {
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
     * 参数名称。
     */
    @Column("config_name")
    private String configName;
    /**
     * 参数编码，Tenant 内唯一。
     */
    @Column("config_code")
    private String configCode;
    /**
     * 参数值。
     */
    @Column("config_value")
    private String configValue;
    /**
     * 参数值类型。
     */
    @Column("config_type")
    private String configType;
    /**
     * 是否敏感参数；敏感值查询时脱敏。
     */
    @Column("is_sensitive")
    private Boolean sensitive;
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
