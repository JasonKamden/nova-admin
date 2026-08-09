import {request} from '../request';

export function fetchMessageCenterUnreadCount() {
    return request<number>({
        url: '/api/message-center/unread-count'
    });
}

export function fetchMessageCenterRecent(limit = 10) {
    return request<Api.MessageCenter.Item[]>({
        url: '/api/message-center/recent',
        params: {limit}
    });
}

export function fetchMessageCenterPage(params: Api.MessageCenter.PageParams) {
    return request<Api.Common.PageResult<Api.MessageCenter.Item>>({
        url: '/api/message-center/messages',
        params
    });
}

export function fetchMessageCenterDetail(messageId: number) {
    return request<Api.MessageCenter.Detail>({
        url: `/api/message-center/messages/${messageId}`
    });
}

export function fetchMessageCenterRead(messageId: number) {
    return request<number>({
        url: `/api/message-center/messages/${messageId}/read`,
        method: 'put'
    });
}

export function fetchMessageCenterReadAll() {
    return request<number>({
        url: '/api/message-center/read-all',
        method: 'put'
    });
}
