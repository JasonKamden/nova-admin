<script lang="ts" setup>
import type {RouteKey} from '@elegant-router/types';
import {GLOBAL_HEADER_MENU_ID, GLOBAL_SIDER_MENU_ID} from '@/constants/app';
import {useAppStore} from '@/store/modules/app';
import {useThemeStore} from '@/store/modules/theme';
import {useRouterPush} from '@/hooks/common/router';
import FirstLevelMenu from '../components/first-level-menu.vue';
import {useMenu, useMixMenuContext} from '../context';

defineOptions({
  name: 'TopHybridSidebarFirst'
});

const appStore = useAppStore();
const themeStore = useThemeStore();
const {routerPushByKeyWithMetaQuery} = useRouterPush();
const {
  firstLevelMenus,
  secondLevelMenus,
  activeFirstLevelMenuKey,
  handleSelectFirstLevelMenu,
  activeDeepestLevelMenuKey
} = useMixMenuContext('TopHybridSidebarFirst');
const {selectedKey} = useMenu();

/**
 * Handle first level menu select
 * @param key RouteKey
 */
function handleSelectMenu(key: RouteKey) {
  handleSelectFirstLevelMenu(key);

  // if there are second level menus, select the deepest one by default
  activeDeepestLevelMenuKey();
}
</script>

<template>
  <Teleport :to="`#${GLOBAL_HEADER_MENU_ID}`">
    <NMenu
        :indent="18"
        :options="secondLevelMenus"
        :value="selectedKey"
        mode="horizontal"
        responsive
        @update:value="routerPushByKeyWithMetaQuery"
    />
  </Teleport>
  <Teleport :to="`#${GLOBAL_SIDER_MENU_ID}`">
    <div class="h-full pt-2">
      <FirstLevelMenu
          :active-menu-key="activeFirstLevelMenuKey"
          :dark-mode="themeStore.darkMode"
          :menus="firstLevelMenus"
          :sider-collapse="appStore.siderCollapse"
          :theme-color="themeStore.themeColor"
          @select="handleSelectMenu"
          @toggle-sider-collapse="appStore.toggleSiderCollapse"
      />
    </div>
  </Teleport>
</template>

<style scoped></style>
