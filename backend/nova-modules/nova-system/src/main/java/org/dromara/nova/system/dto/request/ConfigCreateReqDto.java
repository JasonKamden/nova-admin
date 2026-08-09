package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.system.entity.ConfigEntity;
import org.dromara.nova.system.enums.ConfigType;

/**
 * 参数新增请求。
 *
 * @param configName  参数名称
 * @param configCode  参数编码，Tenant 内唯一
 * @param configValue 参数值
 * @param configType  参数值类型
 * @param sensitive   是否敏感参数；敏感值查询时脱敏
 * @param status      状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark      备注
 */
@AutoMapper(target = ConfigEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增参数配置请求")
public record ConfigCreateReqDto(@Schema(description = "参数名称") @NotBlank String configName,
                                 @Schema(description = "参数编码，Tenant 内唯一") @NotBlank String configCode,
                                 @Schema(description = "参数值") @NotBlank String configValue,
                                 @Schema(description = "参数值类型") @NotNull ConfigType configType,
                                 @Schema(description = "是否敏感参数；敏感值查询时脱敏") Boolean sensitive,
                                 @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status,
                                 @Schema(description = "备注") String remark) {
}
