declare namespace Api {
    namespace Monitor {
        interface LoginLogItem {
            id: number;
            userId: number | null;
            username: string;
            contextType: string;
            tenantId: number | null;
            departmentId: number | null;
            loginType: string | null;
            loginStatus: number;
            ip: string | null;
            userAgent: string | null;
            loginTime: string;
            failureReason: string | null;
            requestId: string | null;
        }

        interface LoginLogPageParams extends Api.Common.PageParams {
            username: string | null;
            loginStatus: number | null;
            ip: string | null;
            startTime: string | null;
            endTime: string | null;
        }

        interface OperationLogItem {
            id: number;
            module: string;
            operationType: string;
            operationDescription: string | null;
            userId: number | null;
            username: string | null;
            contextType: string | null;
            requestMethod: string | null;
            requestUri: string | null;
            requestIp: string | null;
            status: string | null;
            durationMs: number | null;
            operationTime: string;
        }

        interface OperationLogDetail {
            basic: {
                id: number;
                module: string;
                operationType: string;
                operationDescription: string | null;
                userId: number | null;
                username: string | null;
                contextType: string | null;
                tenantId: number | null;
                departmentId: number | null;
                requestIp: string | null;
                userAgent: string | null;
                durationMs: number | null;
                requestId: string | null;
                traceId: string | null;
                operationTime: string;
            };
            request: {
                method: string | null;
                uri: string | null;
                contentType: string | null;
                headers: string | null;
                queryParams: string | null;
                pathParams: string | null;
                body: string | null;
            };
            response: {
                httpStatus: number | null;
                businessCode: number | null;
                body: string | null;
            };
            exception: {
                type: string | null;
                errorCode: string | null;
                message: string | null;
                location: string | null;
                stack: string | null;
            };
        }

        interface OperationLogPageParams extends Api.Common.PageParams {
            module: string | null;
            operationType: string | null;
            operator: string | null;
            requestMethod: string | null;
            status: string | null;
            requestIp: string | null;
            startTime: string | null;
            endTime: string | null;
        }

        interface OnlineUserItem {
            sessionId: string;
            userId: number | null;
            username: string;
            contextType: string;
            tenantId: number | null;
            tenantName: string | null;
            departmentId: number | null;
            departmentName: string | null;
            ip: string | null;
            userAgent: string | null;
            loginTime: string;
            lastActivityTime: string;
        }

        interface CacheItem {
            code: string;
            name: string;
            type: string | null;
            module: string | null;
            scope: string | null;
            defaultTtlSeconds: number;
            clearable: boolean;
            refreshable: boolean;
            description: string | null;
            status: string | null;
        }

        interface CacheEntryItem {
            key: string;
            valuePreview: string;
            valueType: string;
        }

        interface CacheEntryDetail {
            key: string;
            valueType: string;
            valueJson: string;
        }
    }
}
