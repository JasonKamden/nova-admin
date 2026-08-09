<script lang="ts" setup>
import {computed, ref} from 'vue';
import {useAppStore} from '@/store/modules/app';
import {$t} from '@/locales';
import AppearanceSettings from './modules/appearance/index.vue';
import LayoutSettings from './modules/layout/index.vue';
import GeneralSettings from './modules/general/index.vue';
import ConfigOperation from './modules/config-operation.vue';
import PresetSettings from './modules/preset/index.vue';

defineOptions({
  name: 'ThemeDrawer'
});

const appStore = useAppStore();
const activeTab = ref('appearance');

const drawerWidth = computed(() => {
  const width = 400;

  // On mobile devices, use 90% of viewport width with a maximum of 400px
  if (appStore.isMobile) {
    return `min(90vw, ${width}px)`;
  }

  return width;
});
</script>

<template>
  <NDrawer v-model:show="appStore.themeDrawerVisible" :width="drawerWidth" display-directive="show">
    <NDrawerContent :native-scrollbar="false" :title="$t('theme.themeDrawerTitle')" closable>
      <NTabs v-model:value="activeTab" class="mb-16px" size="medium" type="segment">
        <NTab :tab="$t('theme.tabs.appearance')" name="appearance"></NTab>
        <NTab :tab="$t('theme.tabs.layout')" name="layout"></NTab>
        <NTab :tab="$t('theme.tabs.general')" name="general"></NTab>
        <NTab :tab="$t('theme.tabs.preset')" name="preset"></NTab>
      </NTabs>

      <div class="min-h-400px">
        <KeepAlive>
          <AppearanceSettings v-if="activeTab === 'appearance'"/>
          <LayoutSettings v-else-if="activeTab === 'layout'"/>
          <GeneralSettings v-else-if="activeTab === 'general'"/>
          <PresetSettings v-else-if="activeTab === 'preset'"/>
        </KeepAlive>
      </div>

      <template #footer>
        <ConfigOperation/>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped>
:deep(.n-tab) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.n-tab-pane) {
  padding: 0;
}
</style>
