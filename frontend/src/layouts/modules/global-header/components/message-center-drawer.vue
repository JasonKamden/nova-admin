<script lang="ts" setup>
import {ref, watch} from 'vue';
import {$t} from '@/locales';
import MessageCenterPanel from '@/components/business/message-center-panel.vue';

defineOptions({
  name: 'MessageCenterDrawer'
});

const emit = defineEmits<{
  openDetail: [messageId: number];
  updated: [];
}>();

const visible = defineModel<boolean>('visible', {default: false});
const panelRef = ref<InstanceType<typeof MessageCenterPanel> | null>(null);

function handleOpenDetail(messageId: number) {
  emit('openDetail', messageId);
}

watch(
    () => visible.value,
    async show => {
      if (!show) {
        return;
      }

      await panelRef.value?.loadData();
    }
);
</script>

<template>
  <NDrawer v-model:show="visible" :width="860" display-directive="show">
    <NDrawerContent :native-scrollbar="false" :title="$t('page.messageCenter.title')" closable>
      <MessageCenterPanel ref="panelRef" @updated="emit('updated')" @open-detail="handleOpenDetail" />
    </NDrawerContent>
  </NDrawer>
</template>
