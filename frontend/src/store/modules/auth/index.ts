import {computed, reactive, ref} from 'vue';
import {useRoute} from 'vue-router';
import {defineStore} from 'pinia';
import {useLoading} from '@sa/hooks';
import {fetchGetCurrentUser, fetchGetUserPermissions, fetchLogin, fetchLogout} from '@/service/api';
import {useRouterPush} from '@/hooks/common/router';
import {localStg} from '@/utils/storage';
import {SetupStoreId} from '@/enum';
import {$t} from '@/locales';
import {useContextStore} from '../context';
import {useRouteStore} from '../route';
import {useTabStore} from '../tab';
import {clearAuthStorage, getToken} from './shared';

export const useAuthStore = defineStore(SetupStoreId.Auth, () => {
    const route = useRoute();
    const authStore = useAuthStore();
    const contextStore = useContextStore();
    const routeStore = useRouteStore();
    const tabStore = useTabStore();
    const {toLogin, redirectFromLogin} = useRouterPush(false);
    const {loading: loginLoading, startLoading, endLoading} = useLoading();

    const token = ref('');
    const resetting = ref(false);

    const userInfo: Api.Auth.CurrentUser = reactive({
        userId: 0,
        username: '',
        nickname: '',
        avatar: null,
        platformAdmin: false,
        contextType: 'PLATFORM',
        tenantId: null,
        tenantName: null,
        departmentId: null,
        departmentName: null,
        roles: [],
        permissions: []
    });

    /** is super role in static route */
    const isStaticSuper = computed(() => {
        const {VITE_AUTH_ROUTE_MODE, VITE_STATIC_SUPER_ROLE} = import.meta.env;

        return VITE_AUTH_ROUTE_MODE === 'static' && userInfo.roles.includes(VITE_STATIC_SUPER_ROLE);
    });

    /** Is login */
    const isLogin = computed(() => Boolean(token.value));

    /** Reset auth store */
    async function resetStore() {
        if (resetting.value) {
            return;
        }

        resetting.value = true;
        recordUserId();

        const currentToken = token.value || getToken();

        if (currentToken) {
            await fetchLogout().catch(() => undefined);
        }

        clearAuthStorage();

        try {
            authStore.$reset();
            contextStore.clear();

            if (!route.meta.constant) {
                await toLogin();
            }

            tabStore.cacheTabs();
            tabStore.clearTabs();
            routeStore.resetStore();
        } finally {
            resetting.value = false;
        }
    }

    /** Record the user ID of the previous login session Used to compare with the current user ID on next login */
    function recordUserId() {
        if (!userInfo.userId) {
            return;
        }

        // Store current user ID locally for next login comparison
        localStg.set('lastLoginUserId', String(userInfo.userId));
    }

    /**
     * Check if current login user is different from previous login user If different, clear all tabs
     *
     * @returns {boolean} Whether to clear all tabs
     */
    function checkTabClear(): boolean {
        if (!userInfo.userId) {
            return false;
        }

        const lastLoginUserId = localStg.get('lastLoginUserId');

        // Clear all tabs if current user is different from previous user
        if (!lastLoginUserId || Number(lastLoginUserId) !== userInfo.userId) {
            localStg.remove('globalTabs');
            tabStore.clearTabs();

            localStg.remove('lastLoginUserId');
            return true;
        }

        localStg.remove('lastLoginUserId');
        return false;
    }

    /**
     * Login
     *
     * @param userName User name
     * @param password Password
     * @param [redirect=true] Whether to redirect after login. Default is `true`
     */
    async function login(data: Api.Auth.LoginReq, redirect = true) {
        startLoading();

        const {data: loginResp, error} = await fetchLogin(data);

        if (!error) {
            const pass = await loginByToken(loginResp);

            if (pass) {
                // Check if the tab needs to be cleared
                const isClear = checkTabClear();
                let needRedirect = redirect;

                if (isClear) {
                    // If the tab needs to be cleared,it means we don't need to redirect.
                    needRedirect = false;
                }
                await redirectFromLogin(needRedirect);

                window.$notification?.success({
                    title: $t('page.login.common.loginSuccess'),
                    content: $t('page.login.common.welcomeBack', {userName: userInfo.nickname || userInfo.username}),
                    duration: 4500
                });

                endLoading();
                return true;
            }
        } else {
            resetStore();
        }

        endLoading();
        return false;
    }

    async function loginByToken(loginResp: Api.Auth.LoginResp) {
        localStg.set('token', loginResp.token);
        localStg.set('contextType', loginResp.contextType);
        localStg.set('tenantId', loginResp.tenantId);

        const pass = await getUserInfo();

        if (pass) {
            token.value = loginResp.token;

            return true;
        }

        return false;
    }

    async function getUserInfo() {
        const [{data: info, error}, permissionsResp] = await Promise.all([
            fetchGetCurrentUser(),
            fetchGetUserPermissions()
        ]);

        if (!error) {
            Object.assign(userInfo, info);
            contextStore.applyCurrent({
                contextType: info.contextType,
                tenantId: info.tenantId,
                tenantName: info.tenantName
            });
            await contextStore.getContextOptions();

            if (!permissionsResp.error) {
                userInfo.permissions = permissionsResp.data;
            }

            return true;
        }

        return false;
    }

    async function initUserInfo() {
        const maybeToken = getToken();

        if (maybeToken) {
            token.value = maybeToken;
            const pass = await getUserInfo();

            if (!pass) {
                resetStore();
            }
        }
    }

    return {
        token,
        userInfo,
        isStaticSuper,
        isLogin,
        loginLoading,
        resetStore,
        login,
        initUserInfo
    };
});
