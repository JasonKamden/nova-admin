/**
 * Namespace Api
 *
 * All backend api type
 */
declare namespace Api {
    namespace Common {
        type ContextType = 'PLATFORM' | 'TENANT';

        interface Response<T = unknown> {
            code: number;
            message: string;
            data: T;
            success: boolean;
            timestamp: string;
            traceId: string | null;
            requestId: string | null;
        }

        interface PageResult<T = unknown> {
            records: T[];
            total: number;
            pageNum: number;
            pageSize: number;
        }

        interface PageParams {
            pageNum: number;
            pageSize: number;
        }
    }
}
