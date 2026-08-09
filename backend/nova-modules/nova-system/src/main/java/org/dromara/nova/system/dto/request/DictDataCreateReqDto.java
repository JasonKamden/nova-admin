package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.system.entity.DictDataEntity;

/**
 * 字典数据新增。
 *
 * @param dictLabel 字典标签
 * @param dictValue 字典值
 * @param tagType   字典标签语义样式：default/primary/info/success/warning/error
 * @param sort      排序值，数值越小越靠前
 * @param status    状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark    备注
 */
@AutoMapper(target = DictDataEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增字典数据请求参数")
public record DictDataCreateReqDto(@Schema(description = "字典标签") @NotBlank String dictLabel,
                                   @Schema(description = "字典值") @NotBlank String dictValue,
                                   @Schema(description = "字典标签语义样式：default/primary/info/success/warning/error") String tagType,
                                   @Schema(description = "排序值，数值越小越靠前") Integer sort,
                                   @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status,
                                   @Schema(description = "备注") String remark) {
}
