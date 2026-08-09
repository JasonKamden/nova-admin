import {request} from '../request';

export function fetchDictionaryTypes(keyword?: string | null) {
    return request<Api.Dictionary.TypeItem[]>({
        url: '/api/system/dictionaries/types',
        params: {keyword}
    });
}

export function fetchCreateDictionaryType(data: Api.Dictionary.TypeCreateReq) {
    return request<Api.Dictionary.TypeItem>({
        url: '/api/system/dictionaries/types',
        method: 'post',
        data
    });
}

export function fetchUpdateDictionaryType(id: number, data: Api.Dictionary.TypeUpdateReq) {
    return request<Api.Dictionary.TypeItem>({
        url: `/api/system/dictionaries/types/${id}`,
        method: 'put',
        data
    });
}

export function fetchDeleteDictionaryType(id: number) {
    return request<void>({
        url: `/api/system/dictionaries/types/${id}`,
        method: 'delete'
    });
}

export function fetchDictionaryDataPage(typeId: number, params: Api.Dictionary.DataPageParams) {
    return request<Api.Common.PageResult<Api.Dictionary.DataItem>>({
        url: `/api/system/dictionaries/types/${typeId}/data`,
        params
    });
}

export function fetchCreateDictionaryData(typeId: number, data: Api.Dictionary.DataCreateReq) {
    return request<Api.Dictionary.DataItem>({
        url: `/api/system/dictionaries/types/${typeId}/data`,
        method: 'post',
        data
    });
}

export function fetchUpdateDictionaryData(id: number, data: Api.Dictionary.DataUpdateReq) {
    return request<Api.Dictionary.DataItem>({
        url: `/api/system/dictionaries/data/${id}`,
        method: 'put',
        data
    });
}

export function fetchDeleteDictionaryData(id: number) {
    return request<void>({
        url: `/api/system/dictionaries/data/${id}`,
        method: 'delete'
    });
}
