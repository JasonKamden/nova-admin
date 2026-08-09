import {request} from '../request';

export function fetchTenantDashboard() {
    return request<Api.Dashboard.TenantDashboard>({
        url: '/api/dashboard'
    });
}

export function fetchPlatformDashboard() {
    return request<Api.Dashboard.PlatformDashboard>({
        url: '/api/platform/dashboard'
    });
}
