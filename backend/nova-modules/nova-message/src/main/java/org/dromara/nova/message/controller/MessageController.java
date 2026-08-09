package org.dromara.nova.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.message.dto.request.MessageCreateReqDto;
import org.dromara.nova.message.dto.request.MessagePageReqDto;
import org.dromara.nova.message.dto.request.MessageUpdateReqDto;
import org.dromara.nova.message.dto.request.RecipientPageReqDto;
import org.dromara.nova.message.dto.response.MessageDetailRespDto;
import org.dromara.nova.message.dto.response.MessageRespDto;
import org.dromara.nova.message.dto.response.RecipientRespDto;
import org.dromara.nova.message.dto.response.RecipientSummaryRespDto;
import org.dromara.nova.message.service.MessageService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员消息管理。
 */
@Tag(name = "消息管理", description = "管理员维护消息草稿、发送、撤回以及查看接收和阅读情况。")
@RestController
@RequestMapping("/api/system/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    /**
     * 分页查询消息。
     *
     * @param req MessagePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping
    @SaCheckPermission("system:message:list")
    @Operation(summary = "分页查询消息", description = "分页查询消息。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<MessageRespDto>> page(@ParameterObject @Valid MessagePageReqDto req) {
        return R.ok(messageService.page(req));
    }

    /**
     * 查询消息详情。
     *
     * @param id 消息 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:message:detail")
    @Operation(summary = "查询消息详情", description = "查询消息详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MessageDetailRespDto> detail(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        return R.ok(messageService.detail(id));
    }

    /**
     * 创建消息草稿。
     *
     * @param req MessageCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping
    @SaCheckPermission("system:message:add")
    @Operation(summary = "创建消息草稿", description = "创建消息草稿。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MessageRespDto> create(@Valid @RequestBody MessageCreateReqDto req) {
        return R.ok(messageService.create(req));
    }

    /**
     * 修改消息草稿。
     *
     * @param id  消息 ID
     * @param req MessageUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:message:update")
    @Operation(summary = "修改消息草稿", description = "修改消息草稿。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<MessageRespDto> update(@Parameter(description = "消息 ID", required = true) @PathVariable Long id, @Valid @RequestBody MessageUpdateReqDto req) {
        return R.ok(messageService.update(id, req));
    }

    /**
     * 删除消息草稿。
     *
     * @param id 消息 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:message:delete")
    @Operation(summary = "删除消息草稿", description = "删除消息草稿。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        messageService.delete(id);
        return R.ok();
    }

    /**
     * 预览预计接收人数。
     *
     * @param id 消息 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/{id}/recipient-preview")
    @SaCheckPermission("system:message:send")
    @Operation(summary = "预览预计接收人数", description = "预览预计接收人数。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<RecipientSummaryRespDto> preview(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        return R.ok(messageService.previewRecipients(id));
    }

    /**
     * 发送消息。
     *
     * @param id 消息 ID
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/{id}/send")
    @SaCheckPermission("system:message:send")
    @Operation(summary = "发送消息", description = "发送消息。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> send(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        messageService.send(id);
        return R.ok();
    }

    /**
     * 撤回已发送消息。
     *
     * @param id 消息 ID
     * @return 统一成功响应；无业务响应体。
     */
    @PostMapping("/{id}/withdraw")
    @SaCheckPermission("system:message:withdraw")
    @Operation(summary = "撤回已发送消息", description = "撤回已发送消息。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> withdraw(@Parameter(description = "消息 ID", required = true) @PathVariable Long id) {
        messageService.withdraw(id);
        return R.ok();
    }

    /**
     * 分页查询消息接收与阅读情况。
     *
     * @param id  消息 ID
     * @param req RecipientPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/{id}/recipients")
    @SaCheckPermission("system:message:read-status")
    @Operation(summary = "分页查询消息接收与阅读情况", description = "分页查询消息接收与阅读情况。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<RecipientRespDto>> recipients(@Parameter(description = "消息 ID", required = true) @PathVariable Long id, @ParameterObject @Valid RecipientPageReqDto req) {
        return R.ok(messageService.recipients(id, req));
    }
}
