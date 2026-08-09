package org.dromara.nova.message.sse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Redisson Topic 跨节点分发，节点内再投递 SSE。
 */
@Service
@RequiredArgsConstructor
public class MessagePushService {
    private static final String TOPIC = "nova:message:event";
    private final RedissonClient redissonClient;
    private final SseConnectionRegistry sseConnectionRegistry;

    /**
     * 订阅跨节点消息推送事件并转发到本节点 SSE 连接。
     */
    @PostConstruct
    public void subscribe() {
        redissonClient.getTopic(TOPIC).addListener(MessagePushEvent.class, (channel, event) -> sseConnectionRegistry.push(event));
    }

    /**
     * 发布消息变更事件到跨节点通道及本节点 SSE 连接。
     *
     * @param event 消息推送事件
     */
    public void publish(MessagePushEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive() &&
                TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    doPublish(event);
                }
            });
            return;
        }

        doPublish(event);
    }

    /**
     * 将事件广播到 Redisson Topic，由各节点转发到本地 SSE 连接。
     *
     * @param event 消息推送事件
     */
    private void doPublish(MessagePushEvent event) {
        redissonClient.getTopic(TOPIC).publish(event);
    }

    /**
     * 向当前有效 SSE 连接发送心跳事件并清理失效连接。
     */
    @Scheduled(fixedDelay = 30000)
    public void heartbeat() {
        sseConnectionRegistry.heartbeat();
    }
}
