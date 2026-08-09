import {request} from '../request';

export function fetchFilePage(params: Api.File.PageParams) {
    return request<Api.Common.PageResult<Api.File.Item>>({
        url: '/api/system/files',
        params
    });
}

export function fetchFileDetail(id: number) {
    return request<Api.File.Item>({
        url: `/api/files/${id}`
    });
}

export function fetchUploadFile(file: globalThis.File) {
    const formData = new FormData();
    formData.append('file', file);

    return request<Api.File.Item>({
        url: '/api/files/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export function fetchDeleteFile(id: number) {
    return request<void>({
        url: `/api/system/files/${id}`,
        method: 'delete'
    });
}

export function fetchBatchDeleteFiles(fileIds: number[]) {
    return request<void>({
        url: '/api/system/files/batch',
        method: 'delete',
        data: {fileIds}
    });
}
