package org.dromara.nova.system.controller.platform;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.response.PlatformDashboardRespDto;
import org.dromara.nova.system.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PLATFORM 工作台。
 */
@Tag(name = "平台工作台", description = "返回 PLATFORM Context 下的平台级统计数据。")
@RestController
@RequestMapping("/api/platform/dashboard")
@RequiredArgsConstructor
public class PlatformDashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "查询 PLATFORM 工作台", description = "查询 PLATFORM 工作台。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
/**
 * 查询当前 Context 对应的工作台聚合数据。。
 * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
 */
    public R<PlatformDashboardRespDto> dashboard() {
        return R.ok(dashboardService.platformDashboard());
    }
}
