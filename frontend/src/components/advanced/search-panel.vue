<script lang="ts" setup>
defineOptions({
  name: 'SearchPanel'
});

interface Props {
  model: Record<string, any>;
  rules?: Record<string, any>;
  labelWidth?: number;
}

withDefaults(defineProps<Props>(), {
  rules: undefined,
  labelWidth: 80
});
</script>

<template>
  <NCard :bordered="false" class="card-wrapper" size="small">
    <NCollapse>
      <NCollapseItem :title="$t('common.search')">
        <NForm :label-width="labelWidth" :model="model" :rules="rules" label-placement="left">
          <NGrid item-responsive responsive="screen">
            <slot />
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>
