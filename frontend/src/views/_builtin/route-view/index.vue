<script lang="ts" setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';

const route = useRoute();

const description = computed(() => {
  return route.meta.title || route.name || route.path;
});
</script>

<template>
  <NCard :bordered="false" class="card-wrapper">
    <NResult description="该业务页面将在后续阶段完成真实联调开发。" status="info" title="Phase 2+ 页面待开发">
      <template #footer>
        <div class="text-14px text-text-secondary">
          当前路由：{{ description }}
        </div>
      </template>
    </NResult>
  </NCard>
</template>
