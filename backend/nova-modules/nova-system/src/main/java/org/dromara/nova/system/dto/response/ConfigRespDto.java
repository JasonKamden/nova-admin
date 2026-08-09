package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 参数响应；敏感值由 Service 脱敏。
 *
 * @param id          主键 ID
 * @param configName  参数名称
 * @param configCode  参数编码，Tenant 内唯一
 * @param configValue 参数值
 * @param configType  参数值类型
 * @param sensitive   是否敏感参数；敏感值查询时脱敏
 * @param builtIn     是否系统内置数据
 * @param status      状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark      备注
 * @param updateTime  最后更新时间
 */
@Schema(description = "参数配置响应")
public record ConfigRespDto(@Schema(description = "主键 ID") Long id,
                            @Schema(description = "参数名称") String configName,
                            @Schema(description = "参数编码，Tenant 内唯一") String configCode,
                            @Schema(description = "参数值") String configValue,
                            @Schema(description = "参数值类型") String configType,
                            @Schema(description = "是否敏感参数；敏感值查询时脱敏") boolean sensitive,
                            @Schema(description = "是否系统内置数据") boolean builtIn,
                            @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                            @Schema(description = "备注") String remark,
                            @Schema(description = "最后更新时间") LocalDateTime updateTime) {
}
