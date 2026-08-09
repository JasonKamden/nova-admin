import {request} from '../request';

export function fetchMessagePage(params: Api.Message.PageParams) {
    return request<Api.Common.PageResult<Api.Message.Item>>({
        url: '/api/system/messages',
        params
    });
}

export function fetchMessageDetail(id: number) {
    return request<Api.Message.Detail>({
        url: `/api/system/messages/${id}`
    });
}

export function fetchCreateMessage(data: Api.Message.CreateReq) {
    return request<Api.Message.Item>({
        url: '/api/system/messages',
        method: 'post',
        data
    });
}

export function fetchUpdateMessage(id: number, data: Api.Message.UpdateReq) {
    return request<Api.Message.Item>({
        url: `/api/system/messages/${id}`,
        method: 'put',
        data
    });
}

export function fetchDeleteMessage(id: number) {
    return request<void>({
        url: `/api/system/messages/${id}`,
        method: 'delete'
    });
}

export function fetchPreviewMessageRecipients(id: number) {
    return request<Api.Message.RecipientSummary>({
        url: `/api/system/messages/${id}/recipient-preview`,
        method: 'post'
    });
}

export function fetchSendMessage(id: number) {
    return request<void>({
        url: `/api/system/messages/${id}/send`,
        method: 'post'
    });
}

export function fetchWithdrawMessage(id: number) {
    return request<void>({
        url: `/api/system/messages/${id}/withdraw`,
        method: 'post'
    });
}

export function fetchMessageRecipients(id: number, params: Api.Message.RecipientPageParams) {
    return request<Api.Common.PageResult<Api.Message.RecipientItem>>({
        url: `/api/system/messages/${id}/recipients`,
        params
    });
}
