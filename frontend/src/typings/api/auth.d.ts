declare namespace Api {
    namespace Auth {
        interface LoginReq {
            username: string;
            password: string;
            captchaId: string;
            captchaCode: string;
        }

        interface CaptchaResp {
            captchaId: string;
            imageBase64: string;
        }

        interface LoginResp {
            token: string;
            userId: number;
            username: string;
            contextType: Api.Common.ContextType;
            tenantId: number | null;
        }

        interface CurrentUser {
            userId: number;
            username: string;
            nickname: string;
            avatar: string | null;
            platformAdmin: boolean;
            contextType: Api.Common.ContextType;
            tenantId: number | null;
            tenantName: string | null;
            departmentId: number | null;
            departmentName: string | null;
            roles: string[];
            permissions: string[];
        }
    }
}
