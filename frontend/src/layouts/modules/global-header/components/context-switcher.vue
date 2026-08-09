<script lang="ts" setup>
import {computed, h, ref} from 'vue';
import type {DropdownOption} from 'naive-ui';
import {NText} from 'naive-ui';
import {useContextStore} from '@/store/modules/context';
import {useAuthStore} from '@/store/modules/auth';
import {useRouteStore} from '@/store/modules/route';
import {useTabStore} from '@/store/modules/tab';
import {useAppStore} from '@/store/modules/app';
import {useMessageStore} from '@/store/modules/message';

defineOptions({
  name: 'ContextSwitcher'
});

const authStore = useAuthStore();
const contextStore = useContextStore();
const routeStore = useRouteStore();
const tabStore = useTabStore();
const appStore = useAppStore();
const messageStore = useMessageStore();

const switching = ref(false);

const disabled = computed(() => !authStore.isLogin || (!contextStore.options.platform && contextStore.options.tenants.length <= 1));

const options = computed<DropdownOption[]>(() => {
  const items: DropdownOption[] = [];

  if (contextStore.options.platform) {
    items.push({
      key: 'platform',
      label: () => h(NText, {depth: contextStore.isPlatform ? 1 : 3, strong: contextStore.isPlatform}, () => '平台管理')
    });
  }

  contextStore.options.tenants.forEach(tenant => {
    const active = !contextStore.isPlatform && contextStore.current.tenantId === tenant.tenantId;

    items.push({
      key: `tenant:${tenant.tenantId}`,
      label: () =>
        h('div', {class: 'flex items-center justify-between gap-12px'}, [
          h(NText, {depth: active ? 1 : 3, strong: active}, () => tenant.tenantName),
          h('span', {class: 'text-12px text-text-secondary'}, tenant.tenantCode)
        ])
    });
  });

  return items;
});

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
    await rebuildAfterSwitch();
  }

  switching.value = false;
}

async function handleSelect(key: string) {
  if (key === 'platform') {
    if (!contextStore.isPlatform) {
      await performSwitch('platform');
    }
    return;
  }

  const tenantId = Number(key.replace('tenant:', ''));

  if (!contextStore.isPlatform && contextStore.current.tenantId === tenantId) {
    return;
  }

  await performSwitch('tenant', tenantId);
}
</script>

<template>
  <NDropdown :disabled="disabled" :options="options" trigger="click" @select="handleSelect">
    <NButton :disabled="disabled" :loading="switching" quaternary>
      {{ contextStore.contextLabel }}
      <template #icon>
        <icon-mdi-chevron-down class="text-icon" />
      </template>
    </NButton>
  </NDropdown>
</template>
