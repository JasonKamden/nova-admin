package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ContextSwitcher Tenant 候选项。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ContextSwitcher Tenant 选项")
public class ContextTenantOptionRespDto {
    @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准")
    private Long tenantId;

    @Schema(description = "Tenant 编码，全局唯一")
    private String tenantCode;

    @Schema(description = "Tenant 名称")
    private String tenantName;
}
