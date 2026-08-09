<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import {SimpleScrollbar} from '@sa/materials';
import {useBoolean} from '@sa/hooks';
import {GLOBAL_SIDER_MENU_ID} from '@/constants/app';
import {useAppStore} from '@/store/modules/app';
import {useThemeStore} from '@/store/modules/theme';
import {useRouteStore} from '@/store/modules/route';
import {useRouterPush} from '@/hooks/common/router';
import {$t} from '@/locales';
import {useMenu, useMixMenuContext} from '../context';
import FirstLevelMenu from '../components/first-level-menu.vue';
import GlobalLogo from '../../global-logo/index.vue';

defineOptions({
  name: 'VerticalMixMenu'
});

const route = useRoute();
const appStore = useAppStore();
const themeStore = useThemeStore();
const routeStore = useRouteStore();
const {routerPushByKeyWithMetaQuery} = useRouterPush();
const {bool: drawerVisible, setBool: setDrawerVisible} = useBoolean();
const {
  firstLevelMenus,
  secondLevelMenus,
  activeFirstLevelMenuKey,
  isActiveFirstLevelMenuHasChildren,
  getActiveFirstLevelMenuKey,
  handleSelectFirstLevelMenu
} = useMixMenuContext('VerticalMixMenu');
const {selectedKey} = useMenu();

const inverted = computed(() => !themeStore.darkMode && themeStore.sider.inverted);

const hasChildMenus = computed(() => secondLevelMenus.value.length > 0);

const showDrawer = computed(() => hasChildMenus.value && (drawerVisible.value || appStore.mixSiderFixed));

function handleSelectMenu(key: string) {
  handleSelectFirstLevelMenu(key);

  if (isActiveFirstLevelMenuHasChildren.value) {
    setDrawerVisible(true);
  }
}

function handleResetActiveMenu() {
  setDrawerVisible(false);

  if (!appStore.mixSiderFixed) {
    getActiveFirstLevelMenuKey();
  }
}

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
    <div class="h-full flex" @mouseleave="handleResetActiveMenu">
      <FirstLevelMenu
        :active-menu-key="activeFirstLevelMenuKey"
        :dark-mode="themeStore.darkMode"
        :inverted="inverted"
        :menus="firstLevelMenus"
        :sider-collapse="appStore.siderCollapse"
        :theme-color="themeStore.themeColor"
        @select="handleSelectMenu"
        @toggle-sider-collapse="appStore.toggleSiderCollapse"
      >
        <GlobalLogo :show-title="false" :style="{ height: themeStore.header.height + 'px' }" />
      </FirstLevelMenu>
      <div
        :style="{ width: appStore.mixSiderFixed && hasChildMenus ? themeStore.sider.mixChildMenuWidth + 'px' : '0px' }"
        class="relative h-full transition-width-300"
      >
        <DarkModeContainer
          :inverted="inverted"
          :style="{ width: showDrawer ? themeStore.sider.mixChildMenuWidth + 'px' : '0px' }"
          class="absolute-lt h-full flex-col-stretch nowrap-hidden shadow-sm transition-all-300"
        >
          <header :style="{ height: themeStore.header.height + 'px' }" class="flex-y-center justify-between px-12px">
            <h2 class="text-16px text-primary font-bold">{{ $t('system.title') }}</h2>
            <PinToggler
              :class="{ 'text-white:88 !hover:text-white': inverted }"
              :pin="appStore.mixSiderFixed"
              @click="appStore.toggleMixSiderFixed"
            />
          </header>
          <SimpleScrollbar>
            <NMenu
              v-model:expanded-keys="expandedKeys"
              :indent="18"
              :inverted="inverted"
              :options="secondLevelMenus"
              :value="selectedKey"
              mode="vertical"
              @update:value="routerPushByKeyWithMetaQuery"
            />
          </SimpleScrollbar>
        </DarkModeContainer>
      </div>
    </div>
  </Teleport>
</template>

<style scoped></style>
