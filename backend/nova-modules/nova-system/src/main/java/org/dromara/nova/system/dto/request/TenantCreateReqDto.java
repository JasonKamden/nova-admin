package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.dromara.nova.system.entity.TenantEntity;

import java.time.LocalDate;

/**
 * 平台创建 Tenant 请求。
 *
 * @param tenantCode    Tenant 编码，全局唯一
 * @param tenantName    Tenant 名称
 * @param contactName   联系人姓名
 * @param contactPhone  联系人电话
 * @param contactEmail  联系人邮箱
 * @param expireAt      Tenant 有效期截止日期
 * @param remark        备注
 * @param adminUsername 初始 Tenant 管理员登录账号
 * @param adminNickname 初始 Tenant 管理员昵称
 * @param adminPassword 初始 Tenant 管理员密码，仅用于创建时设置，不回显
 */
@AutoMapper(target = TenantEntity.class, reverseConvertGenerate = false)
@Schema(description = "PLATFORM 创建 Tenant 请求，同时创建或绑定初始 Tenant 管理员")
public record TenantCreateReqDto(@Schema(description = "Tenant 编码，全局唯一") @NotBlank String tenantCode,
                                 @Schema(description = "Tenant 名称") @NotBlank String tenantName,
                                 @Schema(description = "联系人姓名") String contactName,
                                 @Schema(description = "联系人电话") String contactPhone,
                                 @Schema(description = "联系人邮箱") @Email String contactEmail,
                                 @Schema(description = "Tenant 有效期截止日期") @FutureOrPresent LocalDate expireAt,
                                 @Schema(description = "备注") String remark,
                                 @Schema(description = "初始 Tenant 管理员登录账号") @NotBlank String adminUsername,
                                 @Schema(description = "初始 Tenant 管理员昵称") @NotBlank String adminNickname,
                                 @Schema(description = "初始 Tenant 管理员密码，仅用于创建时设置，不回显", accessMode = Schema.AccessMode.WRITE_ONLY) @Size(min = 8, max = 64) String adminPassword) {
}
