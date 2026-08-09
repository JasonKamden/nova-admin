/** The global namespace for the app */
declare namespace App {
    /** Theme namespace */
    namespace Theme {
        type ColorPaletteNumber = import('@sa/color').ColorPaletteNumber;

        /** NaiveUI theme overrides that can be specified in preset */
        type NaiveUIThemeOverride = import('naive-ui').GlobalThemeOverrides;

        /** Theme setting */
        interface ThemeSetting {
            /** Theme scheme */
            themeScheme: UnionKey.ThemeScheme;
            /** grayscale mode */
            grayscale: boolean;
            /** colour weakness mode */
            colourWeakness: boolean;
            /** Whether to recommend color */
            recommendColor: boolean;
            /** Theme color */
            themeColor: string;
            /** Theme radius */
            themeRadius: number;
            /** Other color */
            otherColor: OtherColor;
            /** Whether info color is followed by the primary color */
            isInfoFollowPrimary: boolean;
            /** Layout */
            layout: {
                /** Layout mode */
                mode: UnionKey.ThemeLayoutMode;
                /** Scroll mode */
                scrollMode: UnionKey.ThemeScrollMode;
            };
            /** Page */
            page: {
                /** Whether to show the page transition */
                animate: boolean;
                /** Page animate mode */
                animateMode: UnionKey.ThemePageAnimateMode;
            };
            /** Header */
            header: {
                /** Header height */
                height: number;
                /** Header breadcrumb */
                breadcrumb: {
                    /** Whether to show the breadcrumb */
                    visible: boolean;
                    /** Whether to show the breadcrumb icon */
                    showIcon: boolean;
                };
                /** Multilingual */
                multilingual: {
                    /** Whether to show the multilingual */
                    visible: boolean;
                };
                globalSearch: {
                    /** Whether to show the GlobalSearch */
                    visible: boolean;
                };
            };
            /** Tab */
            tab: {
                /** Whether to show the tab */
                visible: boolean;
                /**
                 * Whether to cache the tab
                 *
                 * If cache, the tabs will get from the local storage when the page is refreshed
                 */
                cache: boolean;
                /** Tab height */
                height: number;
                /** Tab mode */
                mode: UnionKey.ThemeTabMode;
                /** Whether to close tab by middle click */
                closeTabByMiddleClick: boolean;
            };
            /** Fixed header and tab */
            fixedHeaderAndTab: boolean;
            /** Sider */
            sider: {
                /** Inverted sider */
                inverted: boolean;
                /** Sider width */
                width: number;
                /** Collapsed sider width */
                collapsedWidth: number;
                /** Sider width when the layout is 'vertical-mix', 'top-hybrid-sidebar-first', or 'top-hybrid-header-first' */
                mixWidth: number;
                /**
                 * Collapsed sider width when the layout is 'vertical-mix', 'top-hybrid-sidebar-first', or
                 * 'top-hybrid-header-first'
                 */
                mixCollapsedWidth: number;
                /** Child menu width when the layout is 'vertical-mix', 'top-hybrid-sidebar-first', or 'top-hybrid-header-first' */
                mixChildMenuWidth: number;
                /** Whether to auto select the first submenu */
                autoSelectFirstMenu: boolean;
            };
            /** Footer */
            footer: {
                /** Whether to show the footer */
                visible: boolean;
                /** Whether fixed the footer */
                fixed: boolean;
                /** Footer height */
                height: number;
                /**
                 * Whether float the footer to the right when the layout is 'top-hybrid-sidebar-first' or
                 * 'top-hybrid-header-first'
                 */
                right: boolean;
            };
            /** Watermark */
            watermark: {
                /** Whether to show the watermark */
                visible: boolean;
                /** Watermark text */
                text: string;
                /** Whether to use user name as watermark text */
                enableUserName: boolean;
                /** Whether to use current time as watermark text */
                enableTime: boolean;
                /** Time format for watermark text */
                timeFormat: string;
            };
            /** define some theme settings tokens, will transform to css variables */
            tokens: {
                light: ThemeSettingToken;
                dark?: {
                    [K in keyof ThemeSettingToken]?: Partial<ThemeSettingToken[K]>;
                };
            };
        }

        interface OtherColor {
            info: string;
            success: string;
            warning: string;
            error: string;
        }

        interface ThemeColor extends OtherColor {
            primary: string;
        }

        type ThemeColorKey = keyof ThemeColor;

        type ThemePaletteColor = {
            [key in ThemeColorKey | `${ThemeColorKey}-${ColorPaletteNumber}`]: string;
        };

        type BaseToken = Record<string, Record<string, string>>;

        interface ThemeSettingTokenColor {
            /** the progress bar color, if not set, will use the primary color */
            nprogress?: string;
            container: string;
            layout: string;
            inverted: string;
            'base-text': string;
        }

        interface ThemeSettingTokenBoxShadow {
            header: string;
            sider: string;
            tab: string;
        }

        interface ThemeSettingToken {
            colors: ThemeSettingTokenColor;
            boxShadow: ThemeSettingTokenBoxShadow;
        }

        type ThemeTokenColor = ThemePaletteColor & ThemeSettingTokenColor;

        /** Theme token CSS variables */
        type ThemeTokenCSSVars = {
            colors: ThemeTokenColor & { [key: string]: string };
            boxShadow: ThemeSettingTokenBoxShadow & { [key: string]: string };
        };
    }

    /** Global namespace */
    namespace Global {
        type VNode = import('vue').VNode;
        type RouteLocationNormalizedLoaded = import('vue-router').RouteLocationNormalizedLoaded;
        type RouteKey = import('@elegant-router/types').RouteKey;
        type RouteMap = import('@elegant-router/types').RouteMap;
        type RoutePath = import('@elegant-router/types').RoutePath;
        type LastLevelRouteKey = import('@elegant-router/types').LastLevelRouteKey;

        /** The router push options */
        type RouterPushOptions = {
            query?: Record<string, string>;
            params?: Record<string, string>;
            force?: boolean;
        };

        /** The global header props */
        interface HeaderProps {
            /** Whether to show the logo */
            showLogo?: boolean;
            /** Whether to show the menu toggler */
            showMenuToggler?: boolean;
            /** Whether to show the menu */
            showMenu?: boolean;
        }

        /** The global menu */
        type Menu = {
            /**
             * The menu key
             *
             * Equal to the route key
             */
            key: string;
            /** The menu label */
            label: string;
            /** The menu i18n key */
            i18nKey?: I18n.I18nKey | null;
            /** The route key */
            routeKey: string;
            /** The route path */
            routePath: string;
            /** The menu icon */
            icon?: () => VNode;
            /** The menu children */
            children?: Menu[];
        };

        type Breadcrumb = Omit<Menu, 'children'> & {
            options?: Breadcrumb[];
        };

        /** Tab route */
        type TabRoute = Pick<RouteLocationNormalizedLoaded, 'name' | 'path' | 'meta'> &
            Partial<Pick<RouteLocationNormalizedLoaded, 'fullPath' | 'query' | 'matched'>>;

        /** The global tab */
        type Tab = {
            /** The tab id */
            id: string;
            /** The tab label */
            label: string;
            /**
             * The new tab label
             *
             * If set, the tab label will be replaced by this value
             */
            newLabel?: string;
            /**
             * The old tab label
             *
             * when reset the tab label, the tab label will be replaced by this value
             */
            oldLabel?: string;
            /** The tab route key */
            routeKey: string;
            /** The tab route path */
            routePath: string;
            /** The tab route full path */
            fullPath: string;
            /** The tab fixed index */
            fixedIndex?: number | null;
            /**
             * Tab icon
             *
             * Iconify icon
             */
            icon?: string;
            /**
             * Tab local icon
             *
             * Local icon
             */
            localIcon?: string;
            /** I18n key */
            i18nKey?: I18n.I18nKey | null;
        };

        /** Form rule */
        type FormRule = import('naive-ui').FormItemRule;

        /** The global dropdown key */
        type DropdownKey = 'closeCurrent' | 'closeOther' | 'closeLeft' | 'closeRight' | 'closeAll' | 'pin' | 'unpin';
    }

    /**
     * I18n namespace
     *
     * Locales type
     */
    namespace I18n {
        type RouteKey = import('@elegant-router/types').RouteKey;

        type LangType = 'en-US' | 'zh-CN';

        type LangOption = {
            label: string;
            key: LangType;
        };

        type I18nRouteKey = Exclude<RouteKey, 'root' | 'not-found'>;

        type FormMsg = {
            required: string;
            invalid: string;
        };

        type Schema = {
            system: {
                title: string;
                updateTitle: string;
                updateContent: string;
                updateConfirm: string;
                updateCancel: string;
            };
            common: {
                action: string;
                add: string;
                addSuccess: string;
                backToHome: string;
                batchDelete: string;
                cancel: string;
                close: string;
                check: string;
                selectAll: string;
                expandColumn: string;
                columnSetting: string;
                config: string;
                confirm: string;
                detail: string;
                delete: string;
                deleteSuccess: string;
                disable: string;
                disabled: string;
                enable: string;
                enabled: string;
                confirmDelete: string;
                edit: string;
                warning: string;
                error: string;
                index: string;
                keywordSearch: string;
                logout: string;
                logoutConfirm: string;
                lookForward: string;
                modify: string;
                modifySuccess: string;
                noData: string;
                operate: string;
                pleaseCheckValue: string;
                refresh: string;
                reset: string;
                search: string;
                switch: string;
                tip: string;
                trigger: string;
                update: string;
                updateSuccess: string;
                userCenter: string;
                yesOrNo: {
                    yes: string;
                    no: string;
                };
            };
            request: {
                logout: string;
                logoutMsg: string;
                logoutWithModal: string;
                logoutWithModalMsg: string;
                refreshToken: string;
                tokenExpired: string;
            };
            theme: {
                themeDrawerTitle: string;
                tabs: {
                    appearance: string;
                    layout: string;
                    general: string;
                    preset: string;
                };
                appearance: {
                    themeSchema: { title: string } & Record<UnionKey.ThemeScheme, string>;
                    grayscale: string;
                    colourWeakness: string;
                    themeColor: {
                        title: string;
                        followPrimary: string;
                    } & Record<Theme.ThemeColorKey, string>;
                    recommendColor: string;
                    recommendColorDesc: string;
                    themeRadius: {
                        title: string;
                    };
                    preset: {
                        title: string;
                        apply: string;
                        applySuccess: string;
                        [key: string]:
                            | {
                            name: string;
                            desc: string;
                        }
                            | string;
                    };
                };
                layout: {
                    layoutMode: { title: string } & Record<UnionKey.ThemeLayoutMode, string> & {
                        [K in `${UnionKey.ThemeLayoutMode}_detail`]: string;
                    };
                    tab: {
                        title: string;
                        visible: string;
                        cache: string;
                        cacheTip: string;
                        height: string;
                        mode: { title: string } & Record<UnionKey.ThemeTabMode, string>;
                        closeByMiddleClick: string;
                        closeByMiddleClickTip: string;
                    };
                    header: {
                        title: string;
                        height: string;
                        breadcrumb: {
                            visible: string;
                            showIcon: string;
                        };
                    };
                    sider: {
                        title: string;
                        inverted: string;
                        width: string;
                        collapsedWidth: string;
                        mixWidth: string;
                        mixCollapsedWidth: string;
                        mixChildMenuWidth: string;
                        autoSelectFirstMenu: string;
                        autoSelectFirstMenuTip: string;
                    };
                    footer: {
                        title: string;
                        visible: string;
                        fixed: string;
                        height: string;
                        right: string;
                    };
                    content: {
                        title: string;
                        scrollMode: { title: string; tip: string } & Record<UnionKey.ThemeScrollMode, string>;
                        page: {
                            animate: string;
                            mode: { title: string } & Record<UnionKey.ThemePageAnimateMode, string>;
                        };
                        fixedHeaderAndTab: string;
                    };
                };
                general: {
                    title: string;
                    watermark: {
                        title: string;
                        visible: string;
                        text: string;
                        enableUserName: string;
                        enableTime: string;
                        timeFormat: string;
                    };
                    multilingual: {
                        title: string;
                        visible: string;
                    };
                    globalSearch: {
                        title: string;
                        visible: string;
                    };
                };
                configOperation: {
                    copyConfig: string;
                    copySuccessMsg: string;
                    resetConfig: string;
                    resetSuccessMsg: string;
                };
            };
            route: Record<I18nRouteKey, string>;
            page: {
                login: {
                    common: {
                        loginOrRegister: string;
                        userNamePlaceholder: string;
                        phonePlaceholder: string;
                        codePlaceholder: string;
                        passwordPlaceholder: string;
                        confirmPasswordPlaceholder: string;
                        codeLogin: string;
                        confirm: string;
                        back: string;
                        validateSuccess: string;
                        loginSuccess: string;
                        welcomeBack: string;
                    };
                    pwdLogin: {
                        title: string;
                        rememberMe: string;
                        captchaPlaceholder: string;
                        refreshCaptcha: string;
                        submit: string;
                        forgetPassword: string;
                        register: string;
                        otherAccountLogin: string;
                        otherLoginMode: string;
                        superAdmin: string;
                        admin: string;
                        user: string;
                    };
                    codeLogin: {
                        title: string;
                        getCode: string;
                        reGetCode: string;
                        sendCodeSuccess: string;
                        imageCodePlaceholder: string;
                    };
                    register: {
                        title: string;
                        agreement: string;
                        protocol: string;
                        policy: string;
                    };
                    resetPwd: {
                        title: string;
                    };
                    bindWeChat: {
                        title: string;
                    };
                };
                home: {
                    branchDesc: string;
                    greeting: string;
                    platformDesc: string;
                    tenantDesc: string;
                    currentSpace: string;
                    currentDepartment: string;
                    userCount: string;
                    departmentCount: string;
                    roleCount: string;
                    onlineUserCount: string;
                    tenantCount: string;
                    enabledTenantCount: string;
                    disabledTenantCount: string;
                    platformUserCount: string;
                    todayLoginCount: string;
                    loginTrend: string;
                    userStatus: string;
                    recentOperations: string;
                    operator: string;
                    platformOverview: string;
                    platformSummary: string;
                    weatherDesc: string;
                    projectCount: string;
                    todo: string;
                    message: string;
                    downloadCount: string;
                    registerCount: string;
                    schedule: string;
                    study: string;
                    work: string;
                    rest: string;
                    entertainment: string;
                    visitCount: string;
                    turnover: string;
                    dealCount: string;
                    projectNews: {
                        title: string;
                        moreNews: string;
                        desc1: string;
                        desc2: string;
                        desc3: string;
                        desc4: string;
                        desc5: string;
                    };
                    creativity: string;
                };
                profile: {
                    basicTitle: string;
                    securityTitle: string;
                    currentContext: string;
                    platformAdmin: string;
                    tenantUser: string;
                    uploadAvatar: string;
                    oldPassword: string;
                    newPassword: string;
                    confirmPassword: string;
                    updatePassword: string;
                    passwordTip: string;
                    passwordUpdated: string;
                    form: {
                        nickname: string;
                        oldPassword: string;
                        newPassword: string;
                        passwordRule: string;
                    };
                };
                tenant: {
                    keyword: string;
                    tenantCode: string;
                    tenantName: string;
                    contactName: string;
                    contactPhone: string;
                    contactEmail: string;
                    expireAt: string;
                    status: string;
                    remark: string;
                    createTime: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    adminUsername: string;
                    adminNickname: string;
                    adminPassword: string;
                    statusConfirm: string;
                    deleteConfirm: string;
                    form: {
                        tenantCode: string;
                        tenantName: string;
                        adminUsername: string;
                        adminNickname: string;
                        adminPassword: string;
                        adminPasswordRule: string;
                    };
                };
                department: {
                    keyword: string;
                    departmentCode: string;
                    departmentName: string;
                    parentDepartment: string;
                    leaderUser: string;
                    phone: string;
                    email: string;
                    sort: string;
                    status: string;
                    addRoot: string;
                    addChild: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    statusConfirm: string;
                    deleteConfirm: string;
                    form: {
                        departmentCode: string;
                        departmentName: string;
                    };
                };
                dictionary: {
                    typeName: string;
                    typeCode: string;
                    dataCount: string;
                    dataLabel: string;
                    dataValue: string;
                    tagType: string;
                    sort: string;
                    status: string;
                    remark: string;
                    typeTitle: string;
                    dataTitle: string;
                    addTypeTitle: string;
                    editTypeTitle: string;
                    addDataTitle: string;
                    editDataTitle: string;
                    deleteTypeConfirm: string;
                    deleteDataConfirm: string;
                    form: {
                        dictName: string;
                        dictCode: string;
                        dictLabel: string;
                        dictValue: string;
                    };
                    tagDefault: string;
                    tagPrimary: string;
                    tagInfo: string;
                    tagSuccess: string;
                    tagWarning: string;
                    tagError: string;
                };
                config: {
                    keyword: string;
                    configName: string;
                    configCode: string;
                    configValue: string;
                    configType: string;
                    sensitive: string;
                    builtIn: string;
                    status: string;
                    remark: string;
                    updateTime: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    deleteConfirm: string;
                    form: {
                        configName: string;
                        configCode: string;
                        configValue: string;
                    };
                    typeString: string;
                    typeNumber: string;
                    typeBoolean: string;
                    typeJson: string;
                };
                message: {
                    title: string;
                    messageType: string;
                    recipientType: string;
                    status: string;
                    creator: string;
                    recipientCount: string;
                    readCount: string;
                    unreadCount: string;
                    readRate: string;
                    createTime: string;
                    sendTime: string;
                    contentHtml: string;
                    recipientRule: string;
                    includeChildren: string;
                    departments: string;
                    roles: string;
                    users: string;
                    previewRecipients: string;
                    send: string;
                    withdraw: string;
                    recipientDetail: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    recipientTitle: string;
                    deleteConfirm: string;
                    sendConfirm: string;
                    withdrawConfirm: string;
                    form: {
                        title: string;
                        contentHtml: string;
                    };
                    typeAnnouncement: string;
                    typeNotice: string;
                    typeReminder: string;
                    recipientAll: string;
                    recipientDepartment: string;
                    recipientRole: string;
                    recipientUser: string;
                    statusDraft: string;
                    statusSent: string;
                    statusWithdrawn: string;
                    readUnread: string;
                    readRead: string;
                };
                monitor: {
                    keyword: string;
                    username: string;
                    ip: string;
                    status: string;
                    loginType: string;
                    loginTime: string;
                    failureReason: string;
                    module: string;
                    operationType: string;
                    operator: string;
                    requestMethod: string;
                    requestUri: string;
                    requestIp: string;
                    durationMs: string;
                    operationTime: string;
                    detailTitle: string;
                    sessionId: string;
                    tenantName: string;
                    departmentName: string;
                    userAgent: string;
                    lastActivityTime: string;
                    kickConfirm: string;
                    cacheCode: string;
                    cacheName: string;
                    cacheType: string;
                    cacheModule: string;
                    cacheScope: string;
                    cacheStatus: string;
                    defaultTtlSeconds: string;
                    clearable: string;
                    refreshable: string;
                    description: string;
                    redisStatus: string;
                    clear: string;
                    refreshCache: string;
                    clearConfirm: string;
                    refreshConfirm: string;
                };
                file: {
                    fileName: string;
                    contentType: string;
                    storageType: string;
                    status: string;
                    fileSize: string;
                    sha256: string;
                    ownerUserId: string;
                    createTime: string;
                    upload: string;
                    preview: string;
                    download: string;
                    deleteConfirm: string;
                    batchDeleteConfirm: string;
                };
                user: {
                    username: string;
                    nickname: string;
                    phone: string;
                    email: string;
                    department: string;
                    role: string;
                    gender: string;
                    genderUnknown: string;
                    genderMale: string;
                    genderFemale: string;
                    bio: string;
                    status: string;
                    lastLoginTime: string;
                    lastLoginIp: string;
                    createTime: string;
                    initialPassword: string;
                    resetPassword: string;
                    roleSetting: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    roleTitle: string;
                    passwordTitle: string;
                    statusConfirm: string;
                    deleteConfirm: string;
                    passwordConfirm: string;
                    form: {
                        username: string;
                        nickname: string;
                        initialPassword: string;
                        newPassword: string;
                    };
                };
                role: {
                    keyword: string;
                    roleCode: string;
                    roleName: string;
                    dataScope: string;
                    builtIn: string;
                    sort: string;
                    status: string;
                    remark: string;
                    createTime: string;
                    customDepartments: string;
                    menuSetting: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    menuTitle: string;
                    statusConfirm: string;
                    deleteConfirm: string;
                    form: {
                        roleCode: string;
                        roleName: string;
                    };
                    dataScopeOptions: {
                        all: string;
                        tenant: string;
                        department: string;
                        departmentAndChildren: string;
                        self: string;
                        custom: string;
                    };
                };
                menu: {
                    keyword: string;
                    menuName: string;
                    parentMenu: string;
                    menuType: string;
                    routeName: string;
                    routePath: string;
                    componentPath: string;
                    permissionCode: string;
                    icon: string;
                    i18nKey: string;
                    sort: string;
                    status: string;
                    visible: string;
                    keepAlive: string;
                    externalLink: string;
                    addTitle: string;
                    editTitle: string;
                    detailTitle: string;
                    statusConfirm: string;
                    deleteConfirm: string;
                    platformOnlyTip: string;
                    form: {
                        menuType: string;
                        menuName: string;
                    };
                    typeDirectory: string;
                    typeMenu: string;
                    typeButton: string;
                };
            };
            form: {
                required: string;
                userName: FormMsg;
                phone: FormMsg;
                pwd: FormMsg;
                confirmPwd: FormMsg;
                code: FormMsg;
                email: FormMsg;
            };
            dropdown: Record<Global.DropdownKey, string>;
            icon: {
                themeConfig: string;
                themeSchema: string;
                lang: string;
                fullscreen: string;
                fullscreenExit: string;
                reload: string;
                collapse: string;
                expand: string;
                pin: string;
                unpin: string;
            };
            datatable: {
                itemCount: string;
                fixed: {
                    left: string;
                    right: string;
                    unFixed: string;
                };
            };
        };

        type GetI18nKey<T extends Record<string, unknown>, K extends keyof T = keyof T> = K extends string
            ? T[K] extends Record<string, unknown>
                ? `${K}.${GetI18nKey<T[K]>}`
                : K
            : never;

        type I18nKey = GetI18nKey<Schema>;

        type TranslateOptions<Locales extends string> = import('vue-i18n').TranslateOptions<Locales>;

        interface $T {
            (key: I18nKey): string;

            (key: I18nKey, plural: number, options?: TranslateOptions<LangType>): string;

            (key: I18nKey, defaultMsg: string, options?: TranslateOptions<I18nKey>): string;

            (key: I18nKey, list: unknown[], options?: TranslateOptions<I18nKey>): string;

            (key: I18nKey, list: unknown[], plural: number): string;

            (key: I18nKey, list: unknown[], defaultMsg: string): string;

            (key: I18nKey, named: Record<string, unknown>, options?: TranslateOptions<LangType>): string;

            (key: I18nKey, named: Record<string, unknown>, plural: number): string;

            (key: I18nKey, named: Record<string, unknown>, defaultMsg: string): string;
        }
    }

    /** Service namespace */
    namespace Service {
        /** Other baseURL key */
        type OtherBaseURLKey = 'demo';

        interface ServiceConfigItem {
            /** The backend service base url */
            baseURL: string;
            /** The proxy pattern of the backend service base url */
            proxyPattern: string;
        }

        interface OtherServiceConfigItem extends ServiceConfigItem {
            key: OtherBaseURLKey;
        }

        /** The backend service config */
        interface ServiceConfig extends ServiceConfigItem {
            /** Other backend service config */
            other: OtherServiceConfigItem[];
        }

        interface SimpleServiceConfig extends Pick<ServiceConfigItem, 'baseURL'> {
            other: Record<OtherBaseURLKey, string>;
        }

        /** The backend service response data */
        type Response<T = unknown> = {
            code: number;
            message: string;
            data: T;
            success: boolean;
            timestamp: string;
            traceId: string | null;
            requestId: string | null;
        };

        /** The demo backend service response data */
        type DemoResponse<T = unknown> = {
            /** The backend service response code */
            status: string;
            /** The backend service response message */
            message: string;
            /** The backend service response data */
            result: T;
        };
    }
}
