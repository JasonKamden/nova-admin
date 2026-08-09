import type {ElegantConstRoute} from '@elegant-router/types';

import {views} from '@/router/elegant/imports';

function hasViewComponent(componentPath: string | null) {
    if (!componentPath?.startsWith('view.')) {
        return false;
    }

    const viewName = componentPath.replace('view.', '');

    return Boolean(views[viewName as keyof typeof views]);
}

function normalizeComponentPath(menu: Api.Route.BackendMenu) {
    const isTopLevelMenu = menu.parentId == null;
    const fallbackView = isTopLevelMenu ? 'layout.base$view.route-view' : 'view.route-view';
    const pathSegments = menu.routePath?.split('/').filter(Boolean) || [];
    const isSingleLevelTopMenu = isTopLevelMenu && pathSegments.length <= 1;

    if (!menu.componentPath || !hasViewComponent(menu.componentPath)) {
        return fallbackView;
    }

    if (isSingleLevelTopMenu) {
        return menu.componentPath.replace(/^view\./, 'layout.base$view.');
    }

    return menu.componentPath;
}

function isExternalLink(value: string | null) {
    return Boolean(value && /^https?:\/\//.test(value));
}

export function adaptBackendMenusToRoutes(menus: Api.Route.BackendMenu[]): ElegantConstRoute[] {
    const routes: ElegantConstRoute[] = menus
        .filter(menu => menu.menuType !== 'BUTTON' && menu.routeName && menu.routePath)
        .map<ElegantConstRoute>(menu => ({
            name: menu.routeName!,
            path: menu.routePath!,
            component:
                menu.menuType === 'DIRECTORY'
                    ? 'layout.base'
                    : normalizeComponentPath(menu),
            meta: {
                title: menu.menuName,
                i18nKey: (menu.i18nKey || undefined) as App.I18n.I18nKey | undefined,
                icon: menu.icon || undefined,
                order: menu.sort || 0,
                keepAlive: Boolean(menu.keepAlive),
                hideInMenu: menu.visible === false,
                href: isExternalLink(menu.externalLink) ? menu.externalLink! : undefined
            },
            children: adaptBackendMenusToRoutes(menu.children || [])
        }));

    return routes;
}
