<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import {SimpleScrollbar} from '@sa/materials';
import {GLOBAL_SIDER_MENU_ID} from '@/constants/app';
import {useAppStore} from '@/store/modules/app';
import {useThemeStore} from '@/store/modules/theme';
import {useRouteStore} from '@/store/modules/route';
import {useRouterPush} from '@/hooks/common/router';
import {useMenu} from '../context';

defineOptions({
  name: 'VerticalMenu'
});

const route = useRoute();
const appStore = useAppStore();
const themeStore = useThemeStore();
const routeStore = useRouteStore();
const {routerPushByKeyWithMetaQuery} = useRouterPush();
const {selectedKey} = useMenu();

const inverted = computed(() => !themeStore.darkMode && themeStore.sider.inverted);

const expandedKeys = ref<string[]>([]);

function updateExpandedKeys() {
  if (appStore.siderCollapse || !selectedKey.value) {
    expandedKeys.value = [];
    return;
  }
  expandedKeys.value = routeStore.getSelectedMenuKeyPath(selectedKey.value);
}

watch(
    () => route.name,
    () => {
      updateExpandedKeys();
    },
    {immediate: true}
);
</script>

<template>
  <Teleport :to="`#${GLOBAL_SIDER_MENU_ID}`">
    <SimpleScrollbar>
      <NMenu
          v-model:expanded-keys="expandedKeys"
          :collapsed="appStore.siderCollapse"
          :collapsed-icon-size="22"
          :collapsed-width="themeStore.sider.collapsedWidth"
          :indent="18"
          :inverted="inverted"
          :options="routeStore.menus"
          :value="selectedKey"
          mode="vertical"
          @update:value="routerPushByKeyWithMetaQuery"
      />
    </SimpleScrollbar>
  </Teleport>
</template>

<style scoped></style>
