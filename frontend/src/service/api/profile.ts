import {request} from '../request';

export function fetchProfile() {
    return request<Api.Profile.Item>({
        url: '/api/profile'
    });
}

export function fetchUpdateProfile(data: Api.Profile.UpdateReq) {
    return request<Api.Profile.Item>({
        url: '/api/profile',
        method: 'put',
        data
    });
}

export function fetchUpdateProfilePassword(data: Api.Profile.UpdatePasswordReq) {
    return request<void>({
        url: '/api/profile/password',
        method: 'put',
        data
    });
}

export function fetchUpdateProfileAvatar(file: globalThis.File) {
    const formData = new FormData();
    formData.append('file', file);

    return request<string>({
        url: '/api/profile/avatar',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
