package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 字典类型列表。
 *
 * @param id        主键 ID
 * @param dictName  字典名称
 * @param dictCode  字典编码，Tenant 内唯一
 * @param builtIn   是否系统内置数据
 * @param status    状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark    备注
 * @param dataCount 当前字典类型包含的数据条数
 */
@Schema(description = "字典类型响应")
public record DictTypeRespDto(@Schema(description = "主键 ID") Long id,
                              @Schema(description = "字典名称") String dictName,
                              @Schema(description = "字典编码，Tenant 内唯一") String dictCode,
                              @Schema(description = "是否系统内置数据") boolean builtIn,
                              @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                              @Schema(description = "备注") String remark,
                              @Schema(description = "当前字典类型包含的数据条数") long dataCount) {
}
