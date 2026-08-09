<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { useContextStore } from '@/store/modules/context';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { useTabStore } from '@/store/modules/tab';
import { useAppStore } from '@/store/modules/app';
import { useMessageStore } from '@/store/modules/message';
import { fetchTenantOptions } from '@/service/api';
import { localStg } from '@/utils/storage';

defineOptions({
  name: 'ContextSwitcher'
});

const RECENT_TENANT_KEY = 'recentContextTenantIds';
const authStore = useAuthStore();
const contextStore = useContextStore();
const routeStore = useRouteStore();
const tabStore = useTabStore();
const appStore = useAppStore();
const messageStore = useMessageStore();

const switching = ref(false);
const panelVisible = ref(false);
const keyword = ref('');
const searching = ref(false);
const remoteTenants = ref<Api.Route.ContextTenantOption[]>([]);

const disabled = computed(() => !authStore.isLogin || (!contextStore.options.platform && contextStore.options.tenants.length <= 1));
const isPlatformUser = computed(() => contextStore.options.platform);
const tenantOptions = computed(() => contextStore.options.tenants);
const recentTenantIds = ref<number[]>(localStg.get(RECENT_TENANT_KEY) || []);

const recentTenants = computed(() => {
  const idSet = new Set(recentTenantIds.value);
  const recent = tenantOptions.value.filter(item => idSet.has(item.tenantId));

  recent.sort((a, b) => recentTenantIds.value.indexOf(a.tenantId) - recentTenantIds.value.indexOf(b.tenantId));

  return recent.slice(0, 5);
});

const searchedTenants = computed(() => {
  const trimmedKeyword = keyword.value.trim().toLowerCase();

  if (!trimmedKeyword) {
    return isPlatformUser.value ? recentTenants.value : tenantOptions.value.slice(0, 12);
  }

  if (isPlatformUser.value) {
    return remoteTenants.value;
  }

  return tenantOptions.value.filter(
    item =>
      item.tenantName.toLowerCase().includes(trimmedKeyword) || item.tenantCode.toLowerCase().includes(trimmedKeyword)
  );
});

function persistRecentTenant(tenantId: number) {
  recentTenantIds.value = [tenantId, ...recentTenantIds.value.filter(id => id !== tenantId)].slice(0, 8);
  localStg.set(RECENT_TENANT_KEY, recentTenantIds.value);
}

async function rebuildAfterSwitch() {
  routeStore.setIsInitAuthRoute(false);
  await authStore.initUserInfo();
  await contextStore.getContextOptions();
  await routeStore.resetStore();
  await routeStore.initAuthRoute();
  await tabStore.clearTabs();
  await appStore.reloadPage();
}

async function performSwitch(type: 'platform' | 'tenant', tenantId?: number) {
  if (switching.value) return;

  switching.value = true;
  messageStore.clear();

  const switched =
    type === 'platform' ? await contextStore.switchToPlatform() : await contextStore.switchToTenant(tenantId!);

  if (switched) {
    if (type === 'tenant' && tenantId) {
      persistRecentTenant(tenantId);
    }

    panelVisible.value = false;
    await rebuildAfterSwitch();
  }

  switching.value = false;
}

async function searchTenants() {
  const trimmedKeyword = keyword.value.trim();

  if (!trimmedKeyword) {
    remoteTenants.value = [];
    return;
  }

  if (!isPlatformUser.value) {
    return;
  }

  searching.value = true;
  const { data, error } = await fetchTenantOptions(trimmedKeyword);
  searching.value = false;

  if (!error) {
    remoteTenants.value = data;
  }
}

watch(
  () => keyword.value,
  value => {
    if (!isPlatformUser.value) return;

    if (!value.trim()) {
      remoteTenants.value = [];
      return;
    }

    void searchTenants();
  }
);

watch(
  () => panelVisible.value,
  visible => {
    if (!visible) {
      keyword.value = '';
      remoteTenants.value = [];
    }
  }
);
</script>

<template>
  <NPopover v-model:show="panelVisible" :disabled="disabled" placement="bottom-end" trigger="click">
    <template #trigger>
      <NButton :disabled="disabled" :loading="switching" quaternary>
        {{ contextStore.contextLabel }}
      </NButton>
    </template>

    <div class="w-320px flex-col-stretch gap-12px">
      <NButton
        v-if="contextStore.options.platform"
        :disabled="contextStore.isPlatform"
        block
        quaternary
        type="primary"
        @click="performSwitch('platform')"
      >
        平台管理
      </NButton>

      <div class="text-13px font-600 text-text-secondary">
        {{ isPlatformUser ? '搜索 Tenant' : '可切换 Tenant' }}
      </div>

      <NInput
        v-model:value="keyword"
        :placeholder="isPlatformUser ? '输入 Tenant 名称或编码' : '搜索 Tenant 名称或编码'"
        clearable
      />

      <div v-if="recentTenants.length && isPlatformUser" class="flex-col-stretch gap-8px">
        <div class="text-12px text-text-secondary">最近 Tenant</div>
        <div class="flex flex-wrap gap-8px">
          <NButton
            v-for="tenant in recentTenants"
            :key="tenant.tenantId"
            :disabled="contextStore.current.tenantId === tenant.tenantId && !contextStore.isPlatform"
            quaternary
            size="small"
            @click="performSwitch('tenant', tenant.tenantId)"
          >
            {{ tenant.tenantName }}
          </NButton>
        </div>
      </div>

      <NSpin :show="searching">
        <div class="max-h-320px flex-col-stretch gap-8px overflow-y-auto">
          <NEmpty v-if="!searchedTenants.length" description="暂无可切换 Tenant" class="py-20px" />
          <button
            v-for="tenant in searchedTenants"
            :key="tenant.tenantId"
            class="w-full rounded-12px border-none bg-#f8fafc px-12px py-10px text-left transition-colors hover:bg-#eef4ff dark:bg-#111827 dark:hover:bg-#1e293b"
            type="button"
            @click="performSwitch('tenant', tenant.tenantId)"
          >
            <div class="truncate text-14px font-500">{{ tenant.tenantName }}</div>
            <div class="mt-4px truncate text-12px text-text-secondary">{{ tenant.tenantCode }}</div>
          </button>
        </div>
      </NSpin>
    </div>
  </NPopover>
</template>
