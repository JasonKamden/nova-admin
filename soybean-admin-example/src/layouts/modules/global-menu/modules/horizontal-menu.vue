<script lang="ts" setup>
import {GLOBAL_HEADER_MENU_ID} from '@/constants/app';
import {useRouteStore} from '@/store/modules/route';
import {useRouterPush} from '@/hooks/common/router';
import {useMenu} from '../context';

defineOptions({
  name: 'HorizontalMenu'
});

const routeStore = useRouteStore();
const {routerPushByKeyWithMetaQuery} = useRouterPush();
const {selectedKey} = useMenu();
</script>

<template>
  <Teleport :to="`#${GLOBAL_HEADER_MENU_ID}`">
    <NMenu
        :indent="18"
        :options="routeStore.menus"
        :value="selectedKey"
        mode="horizontal"
        responsive
        @update:value="routerPushByKeyWithMetaQuery"
    />
  </Teleport>
</template>

<style scoped></style>
