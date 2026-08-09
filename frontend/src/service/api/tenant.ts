import {request} from '../request';

export function fetchTenantPage(params: Api.Tenant.PageParams) {
    return request<Api.Common.PageResult<Api.Tenant.Item>>({
        url: '/api/platform/tenants',
        params
    });
}

export function fetchTenantDetail(tenantId: number) {
    return request<Api.Tenant.Item>({
        url: `/api/platform/tenants/${tenantId}`
    });
}

export function fetchTenantOptions(keyword?: string) {
    return request<Api.Route.ContextTenantOption[]>({
        url: '/api/platform/tenants/options',
        params: {keyword}
    });
}

export function fetchCreateTenant(data: Api.Tenant.CreateReq) {
    return request<Api.Tenant.Item>({
        url: '/api/platform/tenants',
        method: 'post',
        data
    });
}

export function fetchUpdateTenant(tenantId: number, data: Api.Tenant.UpdateReq) {
    return request<Api.Tenant.Item>({
        url: `/api/platform/tenants/${tenantId}`,
        method: 'put',
        data
    });
}

export function fetchUpdateTenantStatus(tenantId: number, status: number) {
    return request<void>({
        url: `/api/platform/tenants/${tenantId}/status`,
        method: 'put',
        data: {status}
    });
}

export function fetchDeleteTenant(tenantId: number) {
    return request<void>({
        url: `/api/platform/tenants/${tenantId}`,
        method: 'delete'
    });
}
