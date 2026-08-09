<script lang="ts" setup>
import {$t} from '@/locales';

defineOptions({
  name: 'CreativityBanner'
});
</script>

<template>
  <NCard :bordered="false" :title="$t('page.home.creativity')" class="h-full card-wrapper" size="small">
    <div class="h-full flex-center">
      <icon-local-banner class="text-400px text-primary sm:text-320px" />
    </div>
  </NCard>
</template>

<style scoped></style>
