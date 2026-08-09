package org.dromara.nova.message.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 当前节点 SSE 连接注册表。
 */
@Component
public class SseConnectionRegistry {
    private final ConcurrentMap<Key, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 为当前用户和 Context 注册 SSE 连接并配置断开清理。
     *
     * @param userId   用户 ID
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @return 方法处理结果。
     */
    public SseEmitter connect(Long userId, Long tenantId) {
        SseEmitter emitter = new SseEmitter(0L);
        Key key = new Key(userId, tenantId);
        emitters.computeIfAbsent(key, k -> new CopyOnWriteArraySet<>()).add(emitter);
        Runnable cleanup = () -> remove(key, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());
        send(emitter, "CONNECTED", Map.of("connected", true));
        return emitter;
    }

    /**
     * 向目标用户集合的匹配 SSE 连接推送消息事件。
     *
     * @param event 消息推送事件
     */
    public void push(MessagePushEvent event) {
        for (Long userId : event.userIds() == null ? List.<Long>of() : event.userIds()) {
            Key key = new Key(userId, event.tenantId());
            for (SseEmitter emitter : emitters.getOrDefault(key, new CopyOnWriteArraySet<>()))
                send(emitter, event.type(), event);
        }
    }

    /**
     * 向当前有效 SSE 连接发送心跳事件并清理失效连接。
     */
    public void heartbeat() {
        emitters.forEach((k, set) -> set.forEach(e -> send(e, "HEARTBEAT", Map.of("ts", System.currentTimeMillis()))));
    }

    /**
     * 发送消息并冻结接收用户快照。
     *
     * @param e    SSE 连接
     * @param name 名称
     * @param data 业务数据
     */
    private void send(SseEmitter e, String name, Object data) {
        try {
            e.send(SseEmitter.event().name(name).data(data));
        } catch (IOException ex) {
            e.complete();
        }
    }

    /**
     * 移除并完成指定 SSE 连接。
     *
     * @param key     缓存或会话 Key
     * @param emitter SSE 连接对象
     */
    private void remove(Key key, SseEmitter emitter) {
        var set = emitters.get(key);
        if (set == null) return;
        set.remove(emitter);
        if (set.isEmpty()) emitters.remove(key);
    }

    /**
     * SSE 连接注册键。
     *
     * @param userId   用户 ID
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     */
    private record Key(Long userId, Long tenantId) {
    }
}
