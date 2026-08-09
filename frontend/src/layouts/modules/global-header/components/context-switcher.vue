<script lang="ts" setup>
import {computed, ref} from 'vue';
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

const options = computed(() => {
  const items: Array<{ label: string; key: string }> = [];

  if (contextStore.options.platform) {
    items.push({label: '平台管理', key: 'platform'});
  }

  contextStore.options.tenants.forEach(tenant => {
    items.push({
      label: tenant.tenantName,
      key: `tenant:${tenant.tenantId}`
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

async function handleSelect(key: string) {
  if (switching.value) {
    return;
  }

  switching.value = true;
  messageStore.clear();

  const switched =
      key === 'platform'
          ? await contextStore.switchToPlatform()
          : await contextStore.switchToTenant(Number(key.split(':')[1]));

  if (switched) {
    await rebuildAfterSwitch();
  }

  switching.value = false;
}
</script>

<template>
  <NDropdown :disabled="disabled" :options="options" trigger="click" @select="handleSelect">
    <NButton :disabled="disabled" :loading="switching" quaternary>
      {{ contextStore.contextLabel }}
    </NButton>
  </NDropdown>
</template>
