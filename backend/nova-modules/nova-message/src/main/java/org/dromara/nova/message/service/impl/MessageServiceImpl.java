package org.dromara.nova.message.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.file.service.FileService;
import org.dromara.nova.message.dto.request.*;
import org.dromara.nova.message.dto.response.MessageDetailRespDto;
import org.dromara.nova.message.dto.response.MessageRespDto;
import org.dromara.nova.message.dto.response.RecipientRespDto;
import org.dromara.nova.message.dto.response.RecipientSummaryRespDto;
import org.dromara.nova.message.entity.MessageEntity;
import org.dromara.nova.message.entity.MessageFileEntity;
import org.dromara.nova.message.entity.MessageUserEntity;
import org.dromara.nova.message.enums.MessageStatus;
import org.dromara.nova.message.mapper.MessageFileMapper;
import org.dromara.nova.message.mapper.MessageMapper;
import org.dromara.nova.message.mapper.MessageUserMapper;
import org.dromara.nova.message.service.MessageService;
import org.dromara.nova.message.sse.MessagePushEvent;
import org.dromara.nova.message.sse.MessagePushService;
import org.dromara.nova.message.support.MessageContentSanitizer;
import org.dromara.nova.message.support.RecipientResolver;
import org.dromara.nova.system.facade.MessageRecipientFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.dromara.nova.message.entity.table.MessageEntityTableDef.MESSAGE_ENTITY;
import static org.dromara.nova.message.entity.table.MessageFileEntityTableDef.MESSAGE_FILE_ENTITY;
import static org.dromara.nova.message.entity.table.MessageUserEntityTableDef.MESSAGE_USER_ENTITY;

/**
 * 消息管理完整生命周期。
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    private final Converter converter;
    private final MessageUserMapper messageUserMapper;
    private final MessageFileMapper messageFileMapper;
    private final RecipientResolver recipientResolver;
    private final MessageContentSanitizer messageContentSanitizer;
    private final ObjectMapper objectMapper;
    private final FileService fileService;
    private final MessagePushService messagePushService;

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req MessagePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<MessageRespDto> page(MessagePageReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        QueryWrapper q = QueryWrapper.create().where(MESSAGE_ENTITY.TENANT_ID.eq(tenantId)).and(MESSAGE_ENTITY.DELETED.eq(false));
        if (req.title() != null && !req.title().isBlank()) q.and(MESSAGE_ENTITY.TITLE.like(req.title()));
        if (req.messageType() != null && !req.messageType().isBlank())
            q.and(MESSAGE_ENTITY.MESSAGE_TYPE.eq(req.messageType()));
        if (req.status() != null && !req.status().isBlank()) q.and(MESSAGE_ENTITY.STATUS.eq(req.status()));
        if (req.startTime() != null) q.and(MESSAGE_ENTITY.CREATE_TIME.ge(req.startTime()));
        if (req.endTime() != null) q.and(MESSAGE_ENTITY.CREATE_TIME.le(req.endTime()));
        Page<MessageEntity> p = messageMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(MESSAGE_ENTITY.ID.desc()));
        return PageResult.of(p.getRecords().stream().map(this::row).toList(), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public MessageDetailRespDto detail(Long id) {
        MessageEntity e = require(id);
        List<Long> files = messageFileMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_FILE_ENTITY.MESSAGE_ID.eq(id)).and(MESSAGE_FILE_ENTITY.TENANT_ID.eq(e.getTenantId()))).stream().map(MessageFileEntity::getFileId).toList();
        int total = Optional.ofNullable(e.getRecipientCount()).orElse(0), read = Optional.ofNullable(e.getReadCount()).orElse(0);
        MessageDetailRespDto mapped = converter.convert(e, MessageDetailRespDto.class);
        return new MessageDetailRespDto(mapped.id(), mapped.title(), mapped.messageType(), mapped.status(), mapped.recipientType(), mapped.recipientRuleJson(), total, read, Math.max(0, total - read), rate(total, read), mapped.createBy(), mapped.createTime(), mapped.sendTime(), mapped.contentHtml(), files);
    }

    /**
     * 创建业务数据，执行参数、唯一性、Tenant 和权限校验。
     *
     * @param req MessageCreateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "MESSAGE", type = "CREATE", description = "创建消息草稿")
    public MessageRespDto create(MessageCreateReqDto req) {
        Long tenantId = TenantContextSupport.requireTenantId();
        validateFiles(req.fileIds(), tenantId);
        MessageEntity e = converter.convert(req, MessageEntity.class);
        e.setTenantId(tenantId);
        e.setStatus(MessageStatus.DRAFT.name());
        fill(e, req.title(), req.messageType().name(), req.contentHtml(), req.recipient());
        e.setRecipientCount(0);
        e.setReadCount(0);
        AuditEntitySupport.created(e, LoginUserUtils.getUserId());
        messageMapper.insert(e);
        replaceFiles(e, req.fileIds());
        return row(e);
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param id  主键 ID
     * @param req MessageUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "MESSAGE", type = "UPDATE", description = "修改消息草稿")
    public MessageRespDto update(Long id, MessageUpdateReqDto req) {
        MessageEntity e = requireDraft(id);
        validateFiles(req.fileIds(), e.getTenantId());
        converter.convert(req, e);
        fill(e, req.title(), req.messageType().name(), req.contentHtml(), req.recipient());
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        messageMapper.update(e);
        replaceFiles(e, req.fileIds());
        return row(e);
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "MESSAGE", type = "DELETE", description = "删除消息草稿")
    public void delete(Long id) {
        MessageEntity e = requireDraft(id);
        e.setDeleted(true);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        messageMapper.update(e);
        for (MessageFileEntity f : messageFileMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_FILE_ENTITY.MESSAGE_ID.eq(id)).and(MESSAGE_FILE_ENTITY.TENANT_ID.eq(e.getTenantId())))) {
            fileService.unbind(f.getFileId(), "MESSAGE", String.valueOf(id));
            messageFileMapper.deleteById(f.getId());
        }
    }

    /**
     * 计算消息接收规则对应的预计接收人数。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public RecipientSummaryRespDto previewRecipients(Long id) {
        MessageEntity e = requireDraft(id);
        List<MessageRecipientFacade.RecipientUser> users = resolve(e);
        return new RecipientSummaryRespDto(users.size(), 0, users.size(), 0);
    }

    /**
     * 发送消息并冻结接收用户快照。
     *
     * @param id 主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "MESSAGE", type = "SEND", description = "发送消息")
    public void send(Long id) {
        MessageEntity e = requireDraft(id);
        List<MessageRecipientFacade.RecipientUser> recipients = resolve(e);
        if (recipients.isEmpty()) throw new BusinessException(CommonResultCode.MESSAGE_ERROR, "没有可接收用户");
        LocalDateTime now = LocalDateTime.now();
        for (var u : recipients) {
            MessageUserEntity m = new MessageUserEntity();
            m.setTenantId(e.getTenantId());
            m.setMessageId(e.getId());
            m.setUserId(u.userId());
            m.setDepartmentId(u.departmentId());
            m.setUsername(u.username());
            m.setNickname(u.nickname());
            m.setDepartmentName(u.departmentName());
            m.setReadStatus(0);
            m.setReceiveTime(now);
            messageUserMapper.insert(m);
        }
        e.setStatus(MessageStatus.SENT.name());
        e.setRecipientCount(recipients.size());
        e.setReadCount(0);
        e.setSendTime(now);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        messageMapper.update(e);
        List<Long> users = recipients.stream().map(MessageRecipientFacade.RecipientUser::userId).distinct().toList();
        messagePushService.publish(new MessagePushEvent("MESSAGE_CREATED", e.getTenantId(), e.getId(), e.getTitle(), users));
    }

    /**
     * 撤回已发送消息并保留历史接收和阅读记录。
     *
     * @param id 主键 ID
     */
    @Override
    @OperationAudit(module = "MESSAGE", type = "WITHDRAW", description = "撤回消息")
    public void withdraw(Long id) {
        MessageEntity e = require(id);
        if (!MessageStatus.SENT.name().equals(e.getStatus()))
            throw new BusinessException(CommonResultCode.CONFLICT, "仅已发送消息可撤回");
        e.setStatus(MessageStatus.WITHDRAWN.name());
        e.setWithdrawTime(LocalDateTime.now());
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        messageMapper.update(e);
        List<Long> users = messageUserMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_USER_ENTITY.MESSAGE_ID.eq(id)).and(MESSAGE_USER_ENTITY.TENANT_ID.eq(e.getTenantId()))).stream().map(MessageUserEntity::getUserId).distinct().toList();
        messagePushService.publish(new MessagePushEvent("MESSAGE_WITHDRAWN", e.getTenantId(), e.getId(), e.getTitle(), users));
        messagePushService.publish(new MessagePushEvent("UNREAD_COUNT_CHANGED", e.getTenantId(), e.getId(), e.getTitle(), users));
    }

    /**
     * 分页查询消息接收用户及阅读状态。
     *
     * @param id  主键 ID
     * @param req RecipientPageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<RecipientRespDto> recipients(Long id, RecipientPageReqDto req) {
        MessageEntity e = require(id);
        QueryWrapper q = QueryWrapper.create().where(MESSAGE_USER_ENTITY.TENANT_ID.eq(e.getTenantId())).and(MESSAGE_USER_ENTITY.MESSAGE_ID.eq(id));
        if (req.user() != null && !req.user().isBlank())
            q.and(MESSAGE_USER_ENTITY.USERNAME.like(req.user()).or(MESSAGE_USER_ENTITY.NICKNAME.like(req.user())));
        if (req.departmentId() != null) q.and(MESSAGE_USER_ENTITY.DEPARTMENT_ID.eq(req.departmentId()));
        if (req.readStatus() != null) q.and(MESSAGE_USER_ENTITY.READ_STATUS.eq(req.readStatus()));
        Page<MessageUserEntity> p = messageUserMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(MESSAGE_USER_ENTITY.ID.asc()));
        return PageResult.of(converter.convert(p.getRecords(), RecipientRespDto.class), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private MessageEntity require(Long id) {
        Long tenantId = TenantContextSupport.requireTenantId();
        MessageEntity e = messageMapper.selectOneByQuery(QueryWrapper.create().where(MESSAGE_ENTITY.ID.eq(id)).and(MESSAGE_ENTITY.TENANT_ID.eq(tenantId)).and(MESSAGE_ENTITY.DELETED.eq(false)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "消息不存在");
        return e;
    }

    /**
     * 加载草稿状态消息并校验状态。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private MessageEntity requireDraft(Long id) {
        MessageEntity e = require(id);
        if (!MessageStatus.DRAFT.name().equals(e.getStatus()))
            throw new BusinessException(CommonResultCode.CONFLICT, "仅草稿消息可以修改或删除");
        return e;
    }

    /**
     * 将已校验的业务字段写入目标实体。
     *
     * @param messageEntity 消息实体
     * @param title         消息标题
     * @param type          类型
     * @param html          待清洗的富文本 HTML
     * @param recipient     消息接收范围规则
     */
    private void fill(MessageEntity e, String title, String type, String html, MessageRecipientReqDto recipient) {
        e.setTitle(title);
        e.setMessageType(type);
        e.setContentHtml(messageContentSanitizer.clean(html));
        e.setRecipientType(recipient.recipientType().name());
        try {
            e.setRecipientRuleJson(objectMapper.writeValueAsString(recipient));
        } catch (Exception ex) {
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "接收规则格式错误");
        }
    }

    /**
     * 解析消息接收范围并返回真实接收用户。
     *
     * @param e 消息实体
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<MessageRecipientFacade.RecipientUser> resolve(MessageEntity e) {
        try {
            return recipientResolver.resolve(objectMapper.readValue(e.getRecipientRuleJson(), MessageRecipientReqDto.class));
        } catch (BusinessException x) {
            throw x;
        } catch (Exception x) {
            throw new BusinessException(CommonResultCode.MESSAGE_ERROR, "接收规则解析失败");
        }
    }

    /**
     * 校验文件集合是否允许当前 Tenant 使用。
     *
     * @param ids      业务主键 ID 集合
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     */
    private void validateFiles(List<Long> ids, Long tenantId) {
        fileService.assertAccessible(ids, tenantId);
    }

    /**
     * 全量替换消息附件关联。
     *
     * @param e   消息实体
     * @param ids 业务主键 ID 集合
     */
    private void replaceFiles(MessageEntity e, List<Long> ids) {
        String businessId = String.valueOf(e.getId());
        for (MessageFileEntity old : messageFileMapper.selectListByQuery(QueryWrapper.create().where(MESSAGE_FILE_ENTITY.MESSAGE_ID.eq(e.getId())).and(MESSAGE_FILE_ENTITY.TENANT_ID.eq(e.getTenantId())))) {
            fileService.unbind(old.getFileId(), "MESSAGE", businessId);
            messageFileMapper.deleteById(old.getId());
        }
        if (ids == null) return;
        for (Long fileId : new LinkedHashSet<>(ids)) {
            MessageFileEntity link = new MessageFileEntity();
            link.setTenantId(e.getTenantId());
            link.setMessageId(e.getId());
            link.setFileId(fileId);
            messageFileMapper.insert(link);
            fileService.bind(fileId, "MESSAGE", businessId);
        }
    }

    /**
     * 将查询结果转换为列表响应 DTO。
     *
     * @param e 消息实体
     * @return 业务响应 DTO。
     */
    private MessageRespDto row(MessageEntity messageEntity) {
        int total = Optional.ofNullable(messageEntity.getRecipientCount()).orElse(0), read = Optional.ofNullable(messageEntity.getReadCount()).orElse(0);
        MessageRespDto mapped = converter.convert(messageEntity, MessageRespDto.class);
        return new MessageRespDto(mapped.id(), mapped.title(), mapped.messageType(), mapped.recipientType(), mapped.status(), total, read, rate(total, read), mapped.createBy(), mapped.createTime(), mapped.sendTime());
    }

    /**
     * 计算消息阅读率。
     *
     * @param total 总记录数
     * @param read  已读人数
     * @return 方法处理结果。
     */
    private double rate(int total, int read) {
        return total == 0 ? 0 : Math.round(read * 10000.0 / total) / 100.0;
    }
}
