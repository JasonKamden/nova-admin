package org.dromara.nova.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.message.dto.request.MessageCenterPageReqDto;
import org.dromara.nova.message.dto.response.MessageCenterDetailRespDto;
import org.dromara.nova.message.dto.response.MessageCenterRespDto;
import org.dromara.nova.message.service.MessageCenterService;
import org.dromara.nova.message.sse.SseConnectionRegistry;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 当前用户消息中心、顶部铃铛与 SSE。
 */
@Tag(name = "消息中心", description = "提供当前登录用户的未读数量、最近消息、消息分页、已读处理和 SSE 实时通知。")
@RestController
@RequestMapping("/api/message-center")
@RequiredArgsConstructor
public class MessageCenterController {
    private final MessageCenterService messageCenterService;
    private final SseConnectionRegistry sseConnectionRegistry;

    /**
     * 查询真实未读数量。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/unread-count")
    @Operation(summary = "查询真实未读数量", description = "查询真实未读数量。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Long> unread() {
        return R.ok(messageCenterService.unreadCount());
    }

    /**
     * 查询最近消息。
     *
     * @param limit 最近消息返回条数，默认 10
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/recent")
    @Operation(summary = "查询最近消息", description = "查询最近消息。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<List<MessageCenterRespDto>> recent(@Parameter(description = "最近消息返回条数，默认 10", required = false) @RequestParam(defaultValue = "10") int limit) {
        return R.ok(messageCenterService.recent(limit));
    }

    /**
     * 分页查询我的消息。
     *
     * @param req MessageCenterPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/messages")
    @Operation(summary = "分页查询我的消息", description = "分页查询我的消息。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<MessageCenterRespDto>> page(@ParameterObject MessageCenterPageReqDto req) {
        return R.ok(messageCenterService.page(req));
    }

    /**
     * 查看消息详情并自动标记已读。
     *
     * @param id 消息 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/messages/{id}")
    @Operation(summary = "查看消息详情并自动标记已读", description = "查看消息详情并自动标记已读。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MessageCenterDetailRespDto> detail(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        return R.ok(messageCenterService.detailAndRead(id));
    }

    /**
     * 标记单条消息已读。
     *
     * @param id 消息 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/messages/{id}/read")
    @Operation(summary = "标记单条消息已读", description = "标记单条消息已读。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Long> read(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        return R.ok(messageCenterService.markRead(id));
    }

    /**
     * 全部标记为已读。
     *
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/read-all")
    @Operation(summary = "全部标记为已读", description = "全部标记为已读。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Long> readAll() {
        return R.ok(messageCenterService.readAll());
    }

    /**
     * 建立消息 SSE 实时连接。
     *
     * @return 当前用户当前 Context 的 SSE 长连接。
     */
    @GetMapping(value = "/sse", produces = "text/event-stream")
    @Operation(summary = "建立消息 SSE 实时连接", description = "建立消息 SSE 实时连接。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public SseEmitter sseConnectionRegistry() {
        return sseConnectionRegistry.connect(LoginUserUtils.getUserId(), TenantContextSupport.current().tenantId());
    }
}
