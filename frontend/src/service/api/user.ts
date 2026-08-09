import {request} from '../request';
import { getAuthorization } from '../request/shared';

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

export function fetchImportUsers(file: globalThis.File) {
    const formData = new FormData();
    formData.append('file', file);

    return request<Api.User.ImportResult>({
        url: '/api/system/users/import',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

async function downloadUserFile(path: string) {
    const response = await fetch(path, {
        headers: {
            Authorization: getAuthorization() || ''
        }
    });

    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }

    const blob = await response.blob();
    const disposition = response.headers.get('Content-Disposition') || '';
    const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i);
    const plainMatch = disposition.match(/filename="?([^"]+)"?/i);
    const fileName = utf8Match?.[1] ? decodeURIComponent(utf8Match[1]) : plainMatch?.[1] || '';
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
}

export function downloadUserImportTemplate() {
    return downloadUserFile('/api/system/users/import-template');
}

export function downloadUserExport(params: Api.User.PageParams) {
    const searchParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
            searchParams.set(key, String(value));
        }
    });

    const query = searchParams.toString();
    const url = query ? `/api/system/users/export?${query}` : '/api/system/users/export';

    return downloadUserFile(url);
}
