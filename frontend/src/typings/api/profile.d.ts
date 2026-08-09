declare namespace Api {
    namespace Profile {
        interface RoleOption {
            id: number;
            roleCode: string;
            roleName: string;
        }

        interface Item {
            userId: number;
            username: string;
            nickname: string;
            avatar: string | null;
            gender: string | null;
            phone: string | null;
            email: string | null;
            bio: string | null;
            status: number;
            platformAdmin: boolean;
            contextType: Api.Common.ContextType;
            tenantId: number | null;
            tenantName: string | null;
            departmentId: number | null;
            departmentName: string | null;
            roles: RoleOption[];
            createTime: string;
            lastLoginTime: string | null;
            lastLoginIp: string | null;
        }

        interface UpdateReq {
            nickname: string;
            gender: string | null;
            phone: string | null;
            email: string | null;
            bio: string | null;
        }

        interface UpdatePasswordReq {
            oldPassword: string;
            newPassword: string;
        }
    }
}
