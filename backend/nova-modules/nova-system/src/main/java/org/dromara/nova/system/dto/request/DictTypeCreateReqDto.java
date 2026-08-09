package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.system.entity.DictTypeEntity;

/**
 * 字典类型新增。
 *
 * @param dictName 字典名称
 * @param dictCode 字典编码，Tenant 内唯一
 * @param status   状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark   备注
 */
@AutoMapper(target = DictTypeEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增字典类型请求参数")
public record DictTypeCreateReqDto(@Schema(description = "字典名称") @NotBlank String dictName,
                                   @Schema(description = "字典编码，Tenant 内唯一") @NotBlank String dictCode,
                                   @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status,
                                   @Schema(description = "备注") String remark) {
}
