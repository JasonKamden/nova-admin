package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tenant 响应。
 *
 * @param id           主键 ID
 * @param tenantCode   Tenant 编码，全局唯一
 * @param tenantName   Tenant 名称
 * @param contactName  联系人姓名
 * @param contactPhone 联系人电话
 * @param contactEmail 联系人邮箱
 * @param expireAt     Tenant 有效期截止日期
 * @param status       状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param remark       备注
 * @param createTime   创建时间
 */
@Schema(description = "Tenant 响应")
public record TenantRespDto(@Schema(description = "主键 ID") Long id,
                            @Schema(description = "Tenant 编码，全局唯一") String tenantCode,
                            @Schema(description = "Tenant 名称") String tenantName,
                            @Schema(description = "联系人姓名") String contactName,
                            @Schema(description = "联系人电话") String contactPhone,
                            @Schema(description = "联系人邮箱") String contactEmail,
                            @Schema(description = "Tenant 有效期截止日期") LocalDate expireAt,
                            @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status,
                            @Schema(description = "备注") String remark,
                            @Schema(description = "创建时间") LocalDateTime createTime) {
}
