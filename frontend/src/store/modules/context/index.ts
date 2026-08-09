import {computed, reactive} from 'vue';
import {defineStore} from 'pinia';
import {
    fetchGetContextOptions,
    fetchGetCurrentContext,
    fetchSwitchToPlatform,
    fetchSwitchToTenant
} from '@/service/api';
import {localStg} from '@/utils/storage';
import {SetupStoreId} from '@/enum';
import {formatContextType, normalizeBusinessText} from '@/utils/context';

export const useContextStore = defineStore(SetupStoreId.Context, () => {
    const current: Api.Route.CurrentContext = reactive({
        contextType: (localStg.get('contextType') || 'PLATFORM') as Api.Common.ContextType,
        tenantId: localStg.get('tenantId') ?? null,
        tenantName: null
    });

    const options: Api.Route.ContextOptions = reactive({
        platform: false,
        tenants: []
    });

    const isPlatform = computed(() => current.contextType === 'PLATFORM');
    const contextLabel = computed(() =>
        isPlatform.value ? '平台管理' : normalizeBusinessText(current.tenantName) || formatContextType('TENANT')
    );

    function persistCurrent() {
        localStg.set('contextType', current.contextType);
        localStg.set('tenantId', current.tenantId);
    }

    function applyCurrent(payload: Api.Route.CurrentContext) {
        Object.assign(current, payload);
        persistCurrent();
    }

    async function getCurrentContext() {
        const {data, error} = await fetchGetCurrentContext();

        if (!error) {
            applyCurrent(data);
            return true;
        }

        return false;
    }

    async function getContextOptions() {
        const {data, error} = await fetchGetContextOptions();

        if (!error) {
            options.platform = data.platform;
            options.tenants = data.tenants;
            return true;
        }

        return false;
    }

    async function switchToPlatform() {
        const {data, error} = await fetchSwitchToPlatform();

        if (!error) {
            applyCurrent(data);
            return data;
        }

        return null;
    }

    async function switchToTenant(tenantId: number) {
        const {data, error} = await fetchSwitchToTenant(tenantId);

        if (!error) {
            applyCurrent(data);
            return data;
        }

        return null;
    }

    function clear() {
        options.platform = false;
        options.tenants = [];
        current.contextType = 'PLATFORM';
        current.tenantId = null;
        current.tenantName = null;
        localStg.remove('contextType');
        localStg.remove('tenantId');
    }

    return {
        current,
        options,
        isPlatform,
        contextLabel,
        applyCurrent,
        getCurrentContext,
        getContextOptions,
        switchToPlatform,
        switchToTenant,
        clear
    };
});
