import {request} from '../request';

export function fetchMenuTree(keyword?: string | null) {
    return request<Api.Menu.Item[]>({
        url: '/api/system/menus',
        params: {keyword}
    });
}

export function fetchPlatformMenuTree(keyword?: string | null) {
    return request<Api.Menu.Item[]>({
        url: '/api/platform/menus',
        params: {keyword}
    });
}

export function fetchPlatformMenuDetail(id: number) {
    return request<Api.Menu.Item>({
        url: `/api/platform/menus/${id}`
    });
}

export function fetchCreatePlatformMenu(data: Api.Menu.SaveParams) {
    return request<Api.Menu.Item>({
        url: '/api/platform/menus',
        method: 'post',
        data
    });
}

export function fetchUpdatePlatformMenu(id: number, data: Api.Menu.SaveParams) {
    return request<Api.Menu.Item>({
        url: `/api/platform/menus/${id}`,
        method: 'put',
        data
    });
}

export function fetchUpdatePlatformMenuStatus(id: number, status: number) {
    return request<void>({
        url: `/api/platform/menus/${id}/status`,
        method: 'put',
        data: {status}
    });
}

export function fetchDeletePlatformMenu(id: number) {
    return request<void>({
        url: `/api/platform/menus/${id}`,
        method: 'delete'
    });
}
