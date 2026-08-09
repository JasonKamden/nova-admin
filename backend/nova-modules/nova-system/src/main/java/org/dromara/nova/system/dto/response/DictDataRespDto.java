package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 字典数据。
 *
 * @param id         主键 ID
 * @param dictTypeId 字典类型 ID
 * @param dictLabel  字典标签
 * @param dictValue  字典值
 * @param tagType    字典标签语义样式：default/primary/info/success/warning/error
 * @param sort       排序值，数值越小越靠前
 * @param status     状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark     备注
 */
@Schema(description = "字典数据响应")
public record DictDataRespDto(@Schema(description = "主键 ID") Long id,
                              @Schema(description = "字典类型 ID") Long dictTypeId,
                              @Schema(description = "字典标签") String dictLabel,
                              @Schema(description = "字典值") String dictValue,
                              @Schema(description = "字典标签语义样式：default/primary/info/success/warning/error") String tagType,
                              @Schema(description = "排序值，数值越小越靠前") Integer sort,
                              @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                              @Schema(description = "备注") String remark) {
}
