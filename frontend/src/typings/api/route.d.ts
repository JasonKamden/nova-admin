declare namespace Api {
    namespace Route {
        type MenuType = 'DIRECTORY' | 'MENU' | 'BUTTON';

        interface BackendMenu {
            id: number;
            menuType: MenuType;
            parentId: number | null;
            menuName: string;
            routeName: string | null;
            routePath: string | null;
            componentPath: string | null;
            externalLink: string | null;
            permissionCode: string | null;
            icon: string | null;
            i18nKey: string | null;
            sort: number | null;
            status: number | null;
            visible: boolean | null;
            keepAlive: boolean | null;
            children: BackendMenu[];
        }

        interface ContextTenantOption {
            tenantId: number;
            tenantCode: string;
            tenantName: string;
        }

        interface ContextOptions {
            platform: boolean;
            tenants: ContextTenantOption[];
        }

        interface CurrentContext {
            contextType: Api.Common.ContextType;
            tenantId: number | null;
            tenantName: string | null;
        }
    }
}
