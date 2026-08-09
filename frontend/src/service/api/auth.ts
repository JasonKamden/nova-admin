import {request} from '../request';

export function fetchCaptcha() {
    return request<Api.Auth.CaptchaResp>({
        url: '/api/auth/captcha'
    });
}

export function fetchLogin(data: Api.Auth.LoginReq) {
    return request<Api.Auth.LoginResp>({
        url: '/api/auth/login',
        method: 'post',
        data
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
