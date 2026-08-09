package org.dromara.nova.message.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.message.dto.request.MessageCenterPageReqDto;
import org.dromara.nova.message.dto.response.MessageCenterDetailRespDto;
import org.dromara.nova.message.dto.response.MessageCenterRespDto;

import java.util.List;

/**
 * 当前用户消息消费。
 */
public interface MessageCenterService {
    /**
     * 查询当前用户真实未读消息数量。
     */
    long unreadCount();

    /**
     * 返回当前用户最近消息。
     */
    List<MessageCenterRespDto> recent(int limit);

    /**
     * 按查询条件分页返回数据。
     */
    PageResult<MessageCenterRespDto> page(MessageCenterPageReqDto req);

    /**
     * 读取消息详情并幂等标记为已读。
     */
    MessageCenterDetailRespDto detailAndRead(Long messageId);

    /**
     * 幂等标记单条消息为已读并返回最新未读数。
     */
    long markRead(Long messageId);

    /**
     * 将当前用户合法范围内全部消息标记为已读并返回最新未读数。
     */
    long readAll();
}
