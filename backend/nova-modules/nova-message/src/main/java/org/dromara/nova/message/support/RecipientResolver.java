package org.dromara.nova.message.support;

import lombok.RequiredArgsConstructor;
import org.dromara.nova.message.dto.request.MessageRecipientReqDto;
import org.dromara.nova.system.facade.MessageRecipientFacade;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将消息接收规则解析为发送时用户快照。
 */
@Component
@RequiredArgsConstructor
public class RecipientResolver {
    private final MessageRecipientFacade messageRecipientFacade;

    /**
     * 解析消息接收范围并返回真实接收用户。
     *
     * @param r 消息接收规则
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    public List<MessageRecipientFacade.RecipientUser> resolve(MessageRecipientReqDto r) {
        return switch (r.recipientType()) {
            case ALL -> messageRecipientFacade.all();
            case部门 ->
                    messageRecipientFacade.departments(r.departmentIds(), Boolean.TRUE.equals(r.includeChildren()));
            case ROLE -> messageRecipientFacade.roles(r.roleIds());
            case USER -> messageRecipientFacade.users(r.userIds());
        };
    }
}
