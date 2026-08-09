<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {useThemeStore} from '@/store/modules/theme';

defineOptions({
  name: 'SearchPanel'
});

interface Props {
  model: Record<string, any>;
  rules?: Record<string, any>;
  labelWidth?: number;
  defaultExpanded?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  rules: undefined,
  labelWidth: 80,
  defaultExpanded: undefined
});

const themeStore = useThemeStore();
const collapseName = 'search';
const expandedNames = ref<string[]>([]);
const initialExpanded = computed(() =>
  props.defaultExpanded === undefined ? themeStore.searchPanelDefaultExpanded : props.defaultExpanded
);

watch(
  initialExpanded,
  value => {
    expandedNames.value = value ? [collapseName] : [];
  },
  {immediate: true}
);
</script>

<template>
  <NCard :bordered="false" class="card-wrapper" size="small">
    <NCollapse v-model:expanded-names="expandedNames">
      <NCollapseItem :title="$t('common.search')" :name="collapseName">
        <NForm :label-width="labelWidth" :model="model" :rules="rules" label-placement="left">
          <NGrid item-responsive responsive="screen">
            <slot />
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>
