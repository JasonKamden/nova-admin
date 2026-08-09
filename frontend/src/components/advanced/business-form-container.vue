<script lang="ts" setup>
import {computed} from 'vue';
import {useAppStore} from '@/store/modules/app';
import {useThemeStore} from '@/store/modules/theme';

defineOptions({
  name: 'BusinessFormContainer'
});

interface Props {
  title: string;
  width?: number;
  segmented?: boolean;
  maskClosable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  width: 760,
  segmented: false,
  maskClosable: false
});

const visible = defineModel<boolean>('visible', {default: false});

const appStore = useAppStore();
const themeStore = useThemeStore();

const drawerWidth = computed(() => (appStore.isMobile ? '100%' : `${props.width}px`));
const modalStyle = computed(() => ({
  width: appStore.isMobile ? 'calc(100vw - 32px)' : `${props.width}px`,
  maxWidth: appStore.isMobile ? 'calc(100vw - 32px)' : 'min(calc(100vw - 48px), 1000px)'
}));
</script>

<template>
  <NDrawer
      v-if="themeStore.businessFormMode === 'drawer'"
      v-model:show="visible"
      :mask-closable="maskClosable"
      :width="drawerWidth"
      display-directive="show"
  >
    <NDrawerContent :native-scrollbar="false" :title="title" closable>
      <div class="max-h-[calc(100vh-180px)] overflow-y-auto">
        <slot/>
      </div>
      <template #footer>
        <slot name="action"/>
      </template>
    </NDrawerContent>
  </NDrawer>

  <NModal
      v-else
      v-model:show="visible"
      :mask-closable="maskClosable"
      :segmented="segmented ? { content: true, footer: 'soft' } : undefined"
      :style="modalStyle"
      preset="card"
  >
    <template #header>
      <div class="text-16px font-600">{{ title }}</div>
    </template>
    <div class="max-h-[calc(100vh-220px)] overflow-y-auto">
      <slot/>
    </div>
    <template #action>
      <slot name="action"/>
    </template>
  </NModal>
</template>
