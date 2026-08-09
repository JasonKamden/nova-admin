package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import org.dromara.nova.system.entity.TenantEntity;

import java.time.LocalDate;

/**
 * Tenant 编辑请求，tenantCode 不允许修改。
 *
 * @param tenantName   Tenant 名称
 * @param contactName  联系人姓名
 * @param contactPhone 联系人电话
 * @param contactEmail 联系人邮箱
 * @param expireAt     Tenant 有效期截止日期
 * @param remark       备注
 */
@AutoMapper(target = TenantEntity.class, reverseConvertGenerate = false)
@Schema(description = "修改 Tenant 请求；Tenant 编码创建后不允许修改")
public record TenantUpdateReqDto(@Schema(description = "Tenant 名称") @NotBlank String tenantName,
                                 @Schema(description = "联系人姓名") String contactName,
                                 @Schema(description = "联系人电话") String contactPhone,
                                 @Schema(description = "联系人邮箱") @Email String contactEmail,
                                 @Schema(description = "Tenant 有效期截止日期") @FutureOrPresent LocalDate expireAt,
                                 @Schema(description = "备注") String remark) {
}
