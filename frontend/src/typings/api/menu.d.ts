declare namespace Api {
    namespace Menu {
        type MenuType = 'DIRECTORY' | 'MENU' | 'BUTTON';

        interface Item {
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
            status: number;
            visible: boolean;
            keepAlive: boolean;
            children: Item[];
        }

        interface SaveParams {
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
            status: number;
            visible: boolean;
            keepAlive: boolean;
        }

        interface TreeOption {
            label: string;
            key: number;
            value: number;
            disabled?: boolean;
            children?: TreeOption[];
        }
    }
}
