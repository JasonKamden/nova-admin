declare namespace Api {
    namespace Role {
        type DataScopeType = 'ALL' | 'TENANT' | 'DEPARTMENT' | 'DEPARTMENT_AND_CHILDREN' | 'SELF' | 'CUSTOM';

        interface Item {
            id: number;
            roleCode: string;
            roleName: string;
            dataScope: DataScopeType;
            builtIn: boolean;
            sort: number | null;
            status: number;
            remark: string | null;
            customDepartmentIds: number[];
            createTime: string;
        }

        interface Option {
            id: number;
            roleCode: string;
            roleName: string;
            dataScope: DataScopeType;
            status: number;
        }

        interface PageParams extends Api.Common.PageParams {
            keyword: string | null;
            status: number | null;
        }

        interface CreateReq {
            roleCode: string;
            roleName: string;
            dataScope: DataScopeType;
            sort: number | null;
            status: number;
            remark: string | null;
            customDepartmentIds: number[];
        }

        interface UpdateReq {
            roleName: string;
            dataScope: DataScopeType;
            sort: number | null;
            status: number;
            remark: string | null;
            customDepartmentIds: number[];
        }
    }
}
