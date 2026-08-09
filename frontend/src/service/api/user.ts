import {request} from '../request';

export function fetchUserPage(params: Api.User.PageParams) {
    return request<Api.Common.PageResult<Api.User.Item>>({
        url: '/api/system/users',
        params
    });
}

export function fetchUserDetail(id: number) {
    return request<Api.User.Item>({
        url: `/api/system/users/${id}`
    });
}

export function fetchCreateUser(data: Api.User.CreateReq) {
    return request<Api.User.Item>({
        url: '/api/system/users',
        method: 'post',
        data
    });
}

export function fetchUpdateUser(id: number, data: Api.User.UpdateReq) {
    return request<Api.User.Item>({
        url: `/api/system/users/${id}`,
        method: 'put',
        data
    });
}

export function fetchUpdateUserStatus(id: number, status: number) {
    return request<void>({
        url: `/api/system/users/${id}/status`,
        method: 'put',
        data: {status}
    });
}

export function fetchResetUserPassword(id: number, data: Api.User.ResetPasswordReq) {
    return request<void>({
        url: `/api/system/users/${id}/password`,
        method: 'put',
        data
    });
}

export function fetchDeleteUser(id: number) {
    return request<void>({
        url: `/api/system/users/${id}`,
        method: 'delete'
    });
}

export function fetchUserRoles(id: number) {
    return request<Api.User.RoleOption[]>({
        url: `/api/system/users/${id}/roles`
    });
}

export function fetchUpdateUserRoles(id: number, roleIds: number[]) {
    return request<void>({
        url: `/api/system/users/${id}/roles`,
        method: 'put',
        data: {roleIds}
    });
}
