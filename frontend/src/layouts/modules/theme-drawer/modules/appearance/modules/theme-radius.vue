<script lang="ts" setup>
import {useThemeStore} from '@/store/modules/theme';
import {$t} from '@/locales';
import SettingItem from '../../../components/setting-item.vue';

defineOptions({
  name: 'ThemeRadius'
});

const themeStore = useThemeStore();
</script>

<template>
  <NDivider>{{ $t('theme.appearance.themeRadius.title') }}</NDivider>
  <TransitionGroup class="flex-col-stretch gap-12px" name="setting-list" tag="div">
    <SettingItem key="1" :label="$t('theme.appearance.themeRadius.title')">
      <NInputNumber v-model:value="themeStore.themeRadius" :max="16" :min="0" :step="1" class="w-120px" size="small" />
    </SettingItem>
  </TransitionGroup>
</template>

<style scoped></style>
