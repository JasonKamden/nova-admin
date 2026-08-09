package org.dromara.nova.message.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.message.dto.request.MessageCreateReqDto;
import org.dromara.nova.message.dto.request.MessagePageReqDto;
import org.dromara.nova.message.dto.request.MessageUpdateReqDto;
import org.dromara.nova.message.dto.request.RecipientPageReqDto;
import org.dromara.nova.message.dto.response.MessageDetailRespDto;
import org.dromara.nova.message.dto.response.MessageRespDto;
import org.dromara.nova.message.dto.response.RecipientRespDto;
import org.dromara.nova.message.dto.response.RecipientSummaryRespDto;

/**
 * 管理员消息发布与追踪。
 */
public interface MessageService {
    /**
     * 按查询条件分页返回数据。
     */
    PageResult<MessageRespDto> page(MessagePageReqDto req);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    MessageDetailRespDto detail(Long id);

    /**
     * 创建业务数据并完成唯一性、权限和 Tenant 校验。
     */
    MessageRespDto create(MessageCreateReqDto req);

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    MessageRespDto update(Long id, MessageUpdateReqDto req);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long id);

    /**
     * 计算消息接收范围的预计接收人数。
     */
    RecipientSummaryRespDto previewRecipients(Long id);

    /**
     * 发送草稿消息并生成接收用户快照。
     */
    void send(Long id);

    /**
     * 撤回已发送消息并保留历史接收/阅读记录。
     */
    void withdraw(Long id);

    /**
     * 分页查询消息接收与阅读明细。
     */
    PageResult<RecipientRespDto> recipients(Long id, RecipientPageReqDto req);
}
