<script lang="ts" setup>
import {computed} from 'vue';
import {getPaletteColorByNumber, mixColor} from '@sa/color';
import {useAppStore} from '@/store/modules/app';
import {useThemeStore} from '@/store/modules/theme';
import {$t} from '@/locales';
import PwdLogin from './modules/pwd-login.vue';

const appStore = useAppStore();
const themeStore = useThemeStore();

const activeModule = computed(() => ({
  label: 'page.login.pwdLogin.title' as App.I18n.I18nKey,
  component: PwdLogin
}));

const bgThemeColor = computed(() =>
    themeStore.darkMode ? getPaletteColorByNumber(themeStore.themeColor, 600) : themeStore.themeColor
);

const bgColor = computed(() => {
  const COLOR_WHITE = '#ffffff';

  const ratio = themeStore.darkMode ? 0.5 : 0.2;

  return mixColor(COLOR_WHITE, themeStore.themeColor, ratio);
});
</script>

<template>
  <div :style="{ backgroundColor: bgColor }" class="relative size-full flex-center overflow-hidden">
    <WaveBg :theme-color="bgThemeColor"/>
    <NCard :bordered="false" class="relative z-4 w-auto rd-12px">
      <div class="w-400px lt-sm:w-300px">
        <header class="flex-y-center justify-between">
          <SystemLogo class="size-64px lt-sm:size-48px"/>
          <h3 class="text-28px text-primary font-500 lt-sm:text-22px">{{ $t('system.title') }}</h3>
          <div class="i-flex-col">
            <ThemeSchemaSwitch
                :show-tooltip="false"
                :theme-schema="themeStore.themeScheme"
                class="text-20px lt-sm:text-18px"
                @switch="themeStore.toggleThemeScheme"
            />
            <LangSwitch
                v-if="themeStore.header.multilingual.visible"
                :lang="appStore.locale"
                :lang-options="appStore.localeOptions"
                :show-tooltip="false"
                @change-lang="appStore.changeLocale"
            />
          </div>
        </header>
        <main class="pt-24px">
          <h3 class="text-18px text-primary font-medium">{{ $t(activeModule.label) }}</h3>
          <div class="pt-24px">
            <Transition :name="themeStore.page.animateMode" appear mode="out-in">
              <component :is="activeModule.component"/>
            </Transition>
          </div>
        </main>
      </div>
    </NCard>
  </div>
</template>

<style scoped></style>
