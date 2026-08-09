import {computed, ref} from 'vue';
import {defineStore} from 'pinia';
import {
    fetchMessageCenterDetail,
    fetchMessageCenterRead,
    fetchMessageCenterReadAll,
    fetchMessageCenterRecent,
    fetchMessageCenterUnreadCount
} from '@/service/api';
import {SetupStoreId} from '@/enum';
import {getAuthorization} from '@/service/request/shared';
import {getServiceBaseURL} from '@/utils/service';

export const useMessageStore = defineStore(SetupStoreId.Message, () => {
    const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
    const {baseURL} = getServiceBaseURL(import.meta.env, isHttpProxy);
    const {baseURL: directBaseURL} = getServiceBaseURL(import.meta.env, false);

    const unreadCount = ref(0);
    const recentMessages = ref<Api.MessageCenter.Item[]>([]);
    const connected = ref(false);
    const loading = ref(false);
    const initialized = ref(false);
    const reconnectAttempt = ref(0);
    const reconnectTimer = ref<number | null>(null);
    const abortController = ref<AbortController | null>(null);
    const connectionId = ref(0);

    const badgeValue = computed(() => {
        if (unreadCount.value <= 0) {
            return undefined;
        }

        return unreadCount.value > 99 ? '99+' : String(unreadCount.value);
    });

    function syncReadStatus(messageId: number) {
        const target = recentMessages.value.find(item => item.messageId === messageId);

        if (target && target.readStatus === 0) {
            target.readStatus = 1;
            target.readTime = new Date().toISOString();
        }
    }

    function clearReconnectTimer() {
        if (reconnectTimer.value !== null) {
            window.clearTimeout(reconnectTimer.value);
            reconnectTimer.value = null;
        }
    }

    function disconnect() {
        clearReconnectTimer();
        abortController.value?.abort();
        abortController.value = null;
        connected.value = false;
    }

    async function refreshUnreadCount() {
        const {data, error} = await fetchMessageCenterUnreadCount();

        if (!error) {
            unreadCount.value = Number(data || 0);
        }
    }

    async function refreshRecent(limit = 10) {
        const {data, error} = await fetchMessageCenterRecent(limit);

        if (!error) {
            recentMessages.value = data;
        }
    }

    async function initialize(force = false) {
        if (loading.value) {
            return;
        }

        if (initialized.value && !force) {
            return;
        }

        loading.value = true;

        try {
            await Promise.all([refreshUnreadCount(), refreshRecent()]);
            initialized.value = true;
            void connect();
        } finally {
            loading.value = false;
        }
    }

    async function readMessage(messageId: number) {
        const {data, error} = await fetchMessageCenterRead(messageId);

        if (!error) {
            unreadCount.value = Number(data || 0);
            syncReadStatus(messageId);
        }
    }

    async function readAllMessages() {
        const {data, error} = await fetchMessageCenterReadAll();

        if (!error) {
            unreadCount.value = Number(data || 0);
            recentMessages.value = recentMessages.value.map(item => ({
                ...item,
                readStatus: 1,
                readTime: item.readTime || new Date().toISOString()
            }));
        }
    }

    async function fetchDetail(messageId: number) {
        const {data, error} = await fetchMessageCenterDetail(messageId);

        if (!error) {
            syncReadStatus(messageId);
            await refreshUnreadCount();
            return data;
        }

        return null;
    }

    async function handleEvent(type: Api.MessageCenter.SseEventType) {
        if (type === 'CONNECTED') {
            connected.value = true;
            reconnectAttempt.value = 0;
            await Promise.all([refreshUnreadCount(), refreshRecent()]);
            return;
        }

        if (type === 'HEARTBEAT') {
            connected.value = true;
            return;
        }

        if (type === 'MESSAGE_CREATED' || type === 'MESSAGE_WITHDRAWN' || type === 'UNREAD_COUNT_CHANGED') {
            await Promise.all([refreshUnreadCount(), refreshRecent()]);
        }
    }

    function scheduleReconnect() {
        if (!initialized.value || !getAuthorization()) {
            return;
        }

        clearReconnectTimer();

        const delay = Math.min(1000 * 2 ** reconnectAttempt.value, 15000);
        reconnectAttempt.value += 1;
        reconnectTimer.value = window.setTimeout(() => {
            void connect();
        }, delay);
    }

    async function connect() {
        if (!initialized.value) {
            return;
        }

        const Authorization = getAuthorization();

        if (!Authorization) {
            return;
        }

        disconnect();

        const currentConnectionId = connectionId.value + 1;
        connectionId.value = currentConnectionId;

        const controller = new AbortController();
        abortController.value = controller;
        connected.value = false;

        try {
            const sseBaseURL = isHttpProxy ? directBaseURL : baseURL;

            const response = await fetch(`${sseBaseURL}/api/message-center/sse`, {
                method: 'GET',
                headers: {
                    Authorization,
                    Accept: 'text/event-stream'
                },
                signal: controller.signal
            });

            if (!response.ok || !response.body) {
                scheduleReconnect();
                return;
            }

            const reader = response.body.getReader();
            const decoder = new TextDecoder('utf-8');
            let buffer = '';
            let eventName = '';
            let dataLines: string[] = [];

            const flushEvent = async () => {
                if (!eventName) {
                    dataLines = [];
                    return;
                }

                if (connectionId.value !== currentConnectionId) {
                    dataLines = [];
                    return;
                }

                const dataText = dataLines.join('\n').trim();

                try {
                    if (dataText) {
                        JSON.parse(dataText);
                    }
                } catch {
                    // ignore invalid payload parsing; event handling uses event name and REST resync
                }

                await handleEvent(eventName as Api.MessageCenter.SseEventType);
                eventName = '';
                dataLines = [];
            };

            while (true) {
                const {done, value} = await reader.read();

                if (done) {
                    break;
                }

                buffer += decoder.decode(value, {stream: true});
                const lines = buffer.split(/\r?\n/);
                buffer = lines.pop() || '';

                for (const line of lines) {
                    if (!line) {
                        await flushEvent();
                        continue;
                    }

                    if (line.startsWith('event:')) {
                        eventName = line.slice(6).trim();
                        continue;
                    }

                    if (line.startsWith('data:')) {
                        dataLines.push(line.slice(5).trim());
                    }
                }
            }

            if (!controller.signal.aborted && connectionId.value === currentConnectionId) {
                connected.value = false;
                scheduleReconnect();
            }
        } catch {
            if (!controller.signal.aborted && connectionId.value === currentConnectionId) {
                connected.value = false;
                scheduleReconnect();
            }
        }
    }

    function clear() {
        disconnect();
        unreadCount.value = 0;
        recentMessages.value = [];
        loading.value = false;
        initialized.value = false;
        reconnectAttempt.value = 0;
    }

    return {
        unreadCount,
        recentMessages,
        connected,
        loading,
        initialized,
        badgeValue,
        initialize,
        connect,
        disconnect,
        refreshUnreadCount,
        refreshRecent,
        readMessage,
        readAllMessages,
        fetchDetail,
        clear
    };
});
