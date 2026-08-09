package org.dromara.nova.message.sse;

import java.io.Serializable;
import java.util.List;

/**
 * 多节点消息实时事件；前端收到后可用 REST 校准最新未读数。
 *
 * @param type      类型
 * @param tenantId  Tenant ID；Tenant 业务写入以服务端可信 Context 为准
 * @param messageId 消息 ID
 * @param title     消息标题
 * @param userIds   用户 ID 集合
 */
public record MessagePushEvent(String type, Long tenantId, Long messageId, String title,
                               List<Long> userIds) implements Serializable {
}
