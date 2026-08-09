package org.dromara.nova.message.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.message.dto.request.MessageCenterPageReqDto;
import org.dromara.nova.message.dto.response.MessageCenterDetailRespDto;
import org.dromara.nova.message.dto.response.MessageCenterRespDto;
import org.dromara.nova.message.entity.MessageEntity;
import org.dromara.nova.message.entity.MessageFileEntity;
import org.dromara.nova.message.entity.MessageUserEntity;
import org.dromara.nova.message.enums.MessageStatus;
import org.dromara.nova.message.mapper.MessageFileMapper;
import org.dromara.nova.message.mapper.MessageMapper;
import org.dromara.nova.message.mapper.MessageUserMapper;
import org.dromara.nova.message.service.MessageCenterService;
import org.dromara.nova.message.sse.MessagePushEvent;
import org.dromara.nova.message.sse.MessagePushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dromara.nova.message.entity.table.MessageEntityTableDef.MESSAGE_ENTITY;
import static org.dromara.nova.message.entity.table.MessageFileEntityTableDef.MESSAGE_FILE_ENTITY;
import static org.dromara.nova.message.entity.table.MessageUserEntityTableDef.MESSAGE_USER_ENTITY;

/**
 * 当前登录用户消息消费。
 */
@Service
@RequiredArgsConstructor
public class MessageCenterServiceImpl implements MessageCenterService {
    private final MessageUserMapper messageUserMapper;
    private final MessageMapper messageMapper;
    private final MessageFileMapper messageFileMapper;
    private final MessagePushService messagePushService;

    /**
     * 查询当前用户真实未读消息数量。
     *
     * @return 业务计算结果。
     */
    @Override
    public long unreadCount() {
        QueryWrapper q = activeBase();
        return q == null ? 0 : messageUserMapper.selectCountByQuery(q.and(MESSAGE_USER_ENTITY.READ_STATUS.eq(0)));
    }

    /**
     * 查询当前用户最近消息。
     *
     * @param limit 返回数量上限
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<MessageCenterRespDto> recent(int limit) {
        int size = Math.clamp(limit, 1, 20);
        QueryWrapper q = activeBase();
        return q == null ? List.of() : messageUserMapper.selectListByQuery(q.orderBy(MESSAGE_USER_ENTITY.RECEIVE_TIME.desc()).limit(size)).stream().map(this::row).toList();
    }

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req MessageCenterPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<MessageCenterRespDto> page(MessageCenterPageReqDto req) {
        QueryWrapper q = activeBase();
        if (q == null) return PageResult.of(List.of(), 0, req.pageNum(), req.pageSize());
        if (req.readStatus() != null) q.and(MESSAGE_USER_ENTITY.READ_STATUS.eq(req.readStatus()));
        Page<MessageUserEntity> p = messageUserMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(MESSAGE_USER_ENTITY.RECEIVE_TIME.desc()));
        List<MessageCenterRespDto> rows = p.getRecords().stream().map(this::row).toList();
        return PageResult.of(rows, p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 读取消息详情并幂等标记为已读。
     *
     * @param messageId 消息 ID
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageCenterDetailRespDto detailAndRead(Long messageId) {
        MessageUserEntity link = requireLink(messageId);
        MessageEntity m = requireSent(messageId);
        mark(link, m);
        List<Long> files = messageFileMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_FILE_ENTITY.TENANT_ID.eq(m.getTenantId())).and(MESSAGE_FILE_ENTITY.MESSAGE_ID.eq(messageId))).stream().map(MessageFileEntity::getFileId).toList();
        return new MessageCenterDetailRespDto(m.getId(), m.getTitle(), m.getMessageType(), m.getContentHtml(), m.getSendTime(), files);
    }

    /**
     * 幂等标记指定消息为已读并返回最新未读数量。
     *
     * @param messageId 消息 ID
     * @return 业务计算结果。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long markRead(Long messageId) {
        MessageUserEntity link = requireLink(messageId);
        MessageEntity m = requireSent(messageId);
        mark(link, m);
        return unreadCount();
    }

    /**
     * 将当前用户当前 Context 下全部可读消息标记为已读。
     *
     * @return 业务计算结果。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long readAll() {
        QueryWrapper active = activeBase();
        List<MessageUserEntity> links = active == null ? List.of() : messageUserMapper.selectListByQuery(active.and(MESSAGE_USER_ENTITY.READ_STATUS.eq(0)));
        Set<Long> messageIds = new HashSet<>();
        for (MessageUserEntity link : links) {
            link.setReadStatus(1);
            link.setReadTime(LocalDateTime.now());
            messageUserMapper.update(link);
            messageIds.add(link.getMessageId());
        }
        for (Long id : messageIds) refreshReadCount(id);
        messagePushService.publish(new MessagePushEvent("UNREAD_COUNT_CHANGED", TenantContextSupport.requireTenantId(), null, null, List.of(LoginUserUtils.getUserId())));
        return 0;
    }

    /**
     * 构造当前用户可见已发送消息的基础查询。
     *
     * @return 方法处理结果。
     */
    private QueryWrapper activeBase() {
        Long tenantId = TenantContextSupport.requireTenantId();
        List<Long> messageIds = messageMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_ENTITY.TENANT_ID.eq(tenantId)).and(MESSAGE_ENTITY.STATUS.eq(MessageStatus.SENT.name())).and(MESSAGE_ENTITY.DELETED.eq(false))).stream().map(MessageEntity::getId).toList();
        return messageIds.isEmpty() ? null : base().and(MESSAGE_USER_ENTITY.MESSAGE_ID.in(messageIds));
    }

    /**
     * 构造当前用户消息接收快照基础查询。
     *
     * @return 方法处理结果。
     */
    private QueryWrapper base() {
        return QueryWrapper.create().where(MESSAGE_USER_ENTITY.TENANT_ID.eq(TenantContextSupport.requireTenantId())).and(MESSAGE_USER_ENTITY.USER_ID.eq(LoginUserUtils.getUserId()));
    }

    /**
     * 加载当前用户的消息接收快照。
     *
     * @param messageId 消息 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private MessageUserEntity requireLink(Long messageId) {
        MessageUserEntity link = messageUserMapper.selectOneByQuery(base().and(MESSAGE_USER_ENTITY.MESSAGE_ID.eq(messageId)));
        if (link == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "消息不存在");
        return link;
    }

    /**
     * 加载可阅读的已发送消息。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private MessageEntity requireSent(Long id) {
        MessageEntity m = messageMapper.selectOneByQuery(QueryWrapper.create().where(MESSAGE_ENTITY.ID.eq(id)).and(MESSAGE_ENTITY.TENANT_ID.eq(TenantContextSupport.requireTenantId())).and(MESSAGE_ENTITY.DELETED.eq(false)));
        if (m == null || MessageStatus.WITHDRAWN.name().equals(m.getStatus()))
            throw new BusinessException(CommonResultCode.NOT_FOUND, "消息已撤回或不存在");
        return m;
    }

    /**
     * 幂等更新单条消息阅读状态。
     *
     * @param link 当前用户消息接收快照
     * @param m    消息实体
     */
    private void mark(MessageUserEntity link, MessageEntity m) {
        if (link.getReadStatus() != null && link.getReadStatus() == 1) return;
        link.setReadStatus(1);
        link.setReadTime(LocalDateTime.now());
        messageUserMapper.update(link);
        refreshReadCount(m.getId());
        messagePushService.publish(new MessagePushEvent("UNREAD_COUNT_CHANGED", m.getTenantId(), m.getId(), m.getTitle(), List.of(LoginUserUtils.getUserId())));
    }

    /**
     * 重新统计并更新消息已读人数。
     *
     * @param messageId 消息 ID
     */
    private void refreshReadCount(Long messageId) {
        long count = messageUserMapper.selectCountByQuery(QueryWrapper.create().where(MESSAGE_USER_ENTITY.MESSAGE_ID.eq(messageId)).and(MESSAGE_USER_ENTITY.READ_STATUS.eq(1)));
        MessageEntity m = messageMapper.selectOneById(messageId);
        if (m != null) {
            m.setReadCount((int) Math.min(Integer.MAX_VALUE, count));
            messageMapper.update(m);
        }
    }

    /**
     * 将查询结果转换为列表响应 DTO。
     *
     * @param link 当前用户消息接收快照
     * @return 业务响应 DTO。
     */
    private MessageCenterRespDto row(MessageUserEntity link) {
        MessageEntity m = messageMapper.selectOneByQuery(QueryWrapper.create().where(MESSAGE_ENTITY.ID.eq(link.getMessageId())).and(MESSAGE_ENTITY.STATUS.eq(MessageStatus.SENT.name())).and(MESSAGE_ENTITY.DELETED.eq(false)));
        if (m == null) return null;
        String text = org.jsoup.Jsoup.parse(m.getContentHtml()).text();
        String summary = text.length() > 120 ? text.substring(0, 120) + "…" : text;
        return new MessageCenterRespDto(m.getId(), m.getTitle(), m.getMessageType(), summary, link.getReadStatus(), link.getReceiveTime(), link.getReadTime(), m.getSendTime());
    }
}
