import {request} from '../request';

export function fetchRolePage(params: Api.Role.PageParams) {
    return request<Api.Common.PageResult<Api.Role.Item>>({
        url: '/api/system/roles',
        params
    });
}

export function fetchRoleOptions(keyword?: string | null) {
    return request<Api.Role.Option[]>({
        url: '/api/system/roles/options',
        params: {keyword}
    });
}

export function fetchRoleDetail(id: number) {
    return request<Api.Role.Item>({
        url: `/api/system/roles/${id}`
    });
}

export function fetchCreateRole(data: Api.Role.CreateReq) {
    return request<Api.Role.Item>({
        url: '/api/system/roles',
        method: 'post',
        data
    });
}

export function fetchUpdateRole(id: number, data: Api.Role.UpdateReq) {
    return request<Api.Role.Item>({
        url: `/api/system/roles/${id}`,
        method: 'put',
        data
    });
}

export function fetchUpdateRoleStatus(id: number, status: number) {
    return request<void>({
        url: `/api/system/roles/${id}/status`,
        method: 'put',
        data: {status}
    });
}

export function fetchDeleteRole(id: number) {
    return request<void>({
        url: `/api/system/roles/${id}`,
        method: 'delete'
    });
}

export function fetchRoleMenuIds(id: number) {
    return request<number[]>({
        url: `/api/system/roles/${id}/menus`
    });
}

export function fetchUpdateRoleMenus(id: number, menuIds: number[]) {
    return request<void>({
        url: `/api/system/roles/${id}/menus`,
        method: 'put',
        data: {menuIds}
    });
}
