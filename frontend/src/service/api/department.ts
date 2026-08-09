import {request} from '../request';

export function fetchDepartmentTree(params: Api.Department.QueryParams) {
    return request<Api.Department.Item[]>({
        url: '/api/system/departments',
        params
    });
}

export function fetchDepartmentSelector() {
    return request<Api.Department.Item[]>({
        url: '/api/system/departments/tree'
    });
}

export function fetchDepartmentDetail(id: number) {
    return request<Api.Department.Item>({
        url: `/api/system/departments/${id}`
    });
}

export function fetchCreateDepartment(data: Api.Department.CreateReq) {
    return request<Api.Department.Item>({
        url: '/api/system/departments',
        method: 'post',
        data
    });
}

export function fetchUpdateDepartment(id: number, data: Api.Department.UpdateReq) {
    return request<Api.Department.Item>({
        url: `/api/system/departments/${id}`,
        method: 'put',
        data
    });
}

export function fetchUpdateDepartmentStatus(id: number, status: number) {
    return request<void>({
        url: `/api/system/departments/${id}/status`,
        method: 'put',
        data: {status}
    });
}

export function fetchDeleteDepartment(id: number) {
    return request<void>({
        url: `/api/system/departments/${id}`,
        method: 'delete'
    });
}
