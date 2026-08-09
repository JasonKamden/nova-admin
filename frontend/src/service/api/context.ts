import {request} from '../request';

export function fetchGetCurrentContext() {
    return request<Api.Route.CurrentContext>({url: '/api/context/current'});
}

export function fetchGetContextOptions() {
    return request<Api.Route.ContextOptions>({url: '/api/context/options'});
}

export function fetchSwitchToPlatform() {
    return request<Api.Route.CurrentContext>({
        url: '/api/context/platform',
        method: 'put'
    });
}

export function fetchSwitchToTenant(tenantId: number) {
    return request<Api.Route.CurrentContext>({
        url: `/api/context/tenant/${tenantId}`,
        method: 'put'
    });
}
