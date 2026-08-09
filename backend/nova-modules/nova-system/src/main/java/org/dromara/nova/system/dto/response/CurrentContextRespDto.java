package org.dromara.nova.system.dto.response;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.dromara.nova.common.core.enums.ContextType;
import org.dromara.nova.common.security.model.CurrentLoginUser;

/**
 * 当前 Context。
 *
 * @param contextType 当前运行上下文类型：PLATFORM 或 TENANT
 * @param tenantId    Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param tenantName  Tenant 名称
 */
@AutoMapper(target = CurrentLoginUser.class, reverseConvertGenerate = true)
@Schema(description = "当前 PLATFORM/TENANT Context 响应")
public record CurrentContextRespDto(
        @Schema(description = "当前运行上下文类型：PLATFORM 或 TENANT") ContextType contextType,
        @Schema(description = "Tenant ID；Tenant 业务写入以服务端可信 Context 为准") Long tenantId,
        @Schema(description = "Tenant 名称") String tenantName) {
}
