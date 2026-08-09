import {request} from '../request';

export function fetchConfigPage(params: Api.Config.PageParams) {
    return request<Api.Common.PageResult<Api.Config.Item>>({
        url: '/api/system/configs',
        params
    });
}

export function fetchCreateConfig(data: Api.Config.CreateReq) {
    return request<Api.Config.Item>({
        url: '/api/system/configs',
        method: 'post',
        data
    });
}

export function fetchUpdateConfig(id: number, data: Api.Config.UpdateReq) {
    return request<Api.Config.Item>({
        url: `/api/system/configs/${id}`,
        method: 'put',
        data
    });
}

export function fetchDeleteConfig(id: number) {
    return request<void>({
        url: `/api/system/configs/${id}`,
        method: 'delete'
    });
}
