<script lang="ts" setup>
import {$t} from '@/locales';
import CaptchaVerification from './modules/captcha-verification.vue';
import BrowserVisibilityRequest from './modules/browser-visibility-request.vue';
import PollingRequest from './modules/polling-request.vue';
import NetworkToggleRequest from './modules/network-toggle-request.vue';
import CrossComponentRequest from './modules/cross-component-request.vue';
</script>

<template>
  <NSpace :size="16" vertical>
    <NCard :bordered="false" :title="$t('page.alova.scenes.captchaSend')" class="card-wrapper" segmented size="small">
      <CaptchaVerification class="w-1/3"/>
    </NCard>
    <NCard :bordered="false" :title="$t('page.alova.scenes.autoRequest')" class="card-wrapper" segmented size="small">
      <NSpace :wrap="false">
        <BrowserVisibilityRequest/>
        <PollingRequest/>
        <NetworkToggleRequest/>
      </NSpace>
    </NCard>
    <NCard
        :bordered="false"
        :title="$t('page.alova.scenes.requestCrossComponent')"
        class="card-wrapper"
        segmented
        size="small"
    >
      <CrossComponentRequest/>
    </NCard>
  </NSpace>
</template>

<style scoped></style>
