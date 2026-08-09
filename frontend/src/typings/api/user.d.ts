declare namespace Api {
    namespace User {
        interface RoleOption {
            id: number;
            roleCode: string;
            roleName: string;
            dataScope?: Api.Role.DataScopeType;
            status?: number;
        }

        interface Item {
            id: number;
            username: string;
            nickname: string;
            avatar: string | null;
            gender: string | null;
            phone: string | null;
            email: string | null;
            bio: string | null;
            status: number;
            departmentId: number | null;
            departmentCode: string | null;
            departmentName: string | null;
            roles: RoleOption[];
            lastLoginTime: string | null;
            lastLoginIp: string | null;
            createTime: string;
        }

        interface CreateReq {
            username: string;
            nickname: string;
            gender: string | null;
            phone: string | null;
            email: string | null;
            bio: string | null;
            departmentId: number | null;
            initialPassword: string;
            roleIds: number[];
        }

        interface UpdateReq {
            nickname: string;
            gender: string | null;
            phone: string | null;
            email: string | null;
            bio: string | null;
            departmentId: number | null;
        }

        interface ResetPasswordReq {
            newPassword: string;
        }

        interface UpdateRolesReq {
            roleIds: number[];
        }

        interface PageParams extends Api.Common.PageParams {
            username: string | null;
            nickname: string | null;
            phone: string | null;
            email: string | null;
            departmentId: number | null;
            status: number | null;
        }
    }
}
