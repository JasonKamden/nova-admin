import {request} from '../request';

export function fetchLoginLogPage(params: Api.Monitor.LoginLogPageParams) {
    return request<Api.Common.PageResult<Api.Monitor.LoginLogItem>>({
        url: '/api/system/login-logs',
        params
    });
}

export function fetchOperationLogPage(params: Api.Monitor.OperationLogPageParams) {
    return request<Api.Common.PageResult<Api.Monitor.OperationLogItem>>({
        url: '/api/system/operation-logs',
        params
    });
}

export function fetchOperationLogDetail(id: number) {
    return request<Api.Monitor.OperationLogDetail>({
        url: `/api/system/operation-logs/${id}`
    });
}

export function fetchOnlineUserList(keyword?: string | null) {
    return request<Api.Monitor.OnlineUserItem[]>({
        url: '/api/system/online-users',
        params: {keyword}
    });
}

export function fetchKickOnlineUser(sessionId: string) {
    return request<void>({
        url: `/api/system/online-users/${sessionId}`,
        method: 'delete'
    });
}

export function fetchCacheList(params: {name?: string | null; type?: string | null; module?: string | null}) {
    return request<Api.Monitor.CacheItem[]>({
        url: '/api/system/caches',
        params
    });
}

export function fetchCacheDetail(cacheCode: string) {
    return request<Api.Monitor.CacheItem>({
        url: `/api/system/caches/${cacheCode}`
    });
}

export function fetchClearCache(cacheCode: string) {
    return request<void>({
        url: `/api/system/caches/${cacheCode}/clear`,
        method: 'post'
    });
}

export function fetchRefreshCache(cacheCode: string) {
    return request<void>({
        url: `/api/system/caches/${cacheCode}/refresh`,
        method: 'post'
    });
}

export function fetchBatchClearCache(cacheCodes: string[]) {
    return request<void>({
        url: '/api/system/caches/batch-clear',
        method: 'post',
        data: {cacheCodes}
    });
}

export function fetchRedisStatus() {
    return request<string>({
        url: '/api/system/caches/redis/status'
    });
}
