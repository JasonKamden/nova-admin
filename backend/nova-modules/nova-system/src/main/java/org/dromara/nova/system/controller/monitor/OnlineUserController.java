package org.dromara.nova.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.system.dto.response.OnlineUserRespDto;
import org.dromara.nova.system.service.OnlineUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户与强制下线。
 */
@Tag(name = "在线用户", description = "查询当前授权范围内的在线会话并支持强制下线。")
@RestController
@RequestMapping("/api/system/online-users")
@RequiredArgsConstructor
public class OnlineUserController {
    private final OnlineUserService onlineUserService;

    /**
     * 查询在线用户。
     *
     * @param keyword 模糊搜索关键字
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("monitor:online:list")
    @Operation(summary = "查询在线用户", description = "查询在线用户。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<OnlineUserRespDto>> list(@Parameter(description = "模糊搜索关键字", required = false) @RequestParam(required = false) String keyword) {
        return R.ok(onlineUserService.list(keyword));
    }

    /**
     * 强制下线在线会话。
     *
     * @param sessionId 在线会话 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{sessionId}")
    @SaCheckPermission("monitor:online:kick")
    @Operation(summary = "强制下线在线会话", description = "强制下线在线会话。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> kick(@Parameter(description = "在线会话 ID", required = true) @PathVariable String sessionId) {
        onlineUserService.forceLogout(sessionId);
        return R.ok();
    }
}
