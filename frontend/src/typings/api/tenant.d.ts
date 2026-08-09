declare namespace Api {
    namespace Tenant {
        interface Item {
            id: number;
            tenantCode: string;
            tenantName: string;
            contactName: string | null;
            contactPhone: string | null;
            contactEmail: string | null;
            expireAt: string | null;
            status: number;
            remark: string | null;
            createTime: string;
        }

        interface PageParams extends Api.Common.PageParams {
            keyword: string | null;
            status: number | null;
        }

        interface CreateReq {
            tenantCode: string;
            tenantName: string;
            contactName: string | null;
            contactPhone: string | null;
            contactEmail: string | null;
            expireAt: string | null;
            remark: string | null;
            adminUsername: string;
            adminNickname: string;
            adminPassword: string;
        }

        interface UpdateReq {
            tenantName: string;
            contactName: string | null;
            contactPhone: string | null;
            contactEmail: string | null;
            expireAt: string | null;
            remark: string | null;
        }
    }
}
