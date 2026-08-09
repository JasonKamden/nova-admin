import {request} from '../request';

export function fetchLogin(username: string, password: string) {
    return request<Api.Auth.LoginResp>({
        url: '/api/auth/login',
        method: 'post',
        data: {
            username,
            password
        }
    });
}

export function fetchGetCurrentUser() {
    return request<Api.Auth.CurrentUser>({url: '/api/auth/me'});
}

export function fetchGetUserMenus() {
    return request<Api.Route.BackendMenu[]>({url: '/api/auth/menus'});
}

export function fetchGetUserPermissions() {
    return request<string[]>({url: '/api/auth/permissions'});
}

export function fetchLogout() {
    return request<void>({
        url: '/api/auth/logout',
        method: 'post'
    });
}
