<script lang="ts" setup>
import {useAppStore} from '@/store/modules/app';
import {$t} from '@/locales';

defineOptions({
  name: 'ThemeButton'
});

const appStore = useAppStore();
</script>

<template>
  <ButtonIcon
      :tooltip-content="$t('icon.themeConfig')"
      icon="majesticons:color-swatch-line"
      @click="appStore.openThemeDrawer"
  />
</template>

<style scoped></style>
