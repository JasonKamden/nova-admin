<script lang="ts" setup>
import {ref} from 'vue';
import {$t} from '@/locales';
import MessageCenterPanel from '@/components/business/message-center-panel.vue';
import MessageDetailDrawer from '@/layouts/modules/global-header/components/message-detail-drawer.vue';

defineOptions({
  name: 'MessageCenterPage'
});

const detailVisible = ref(false);
const activeMessageId = ref<number | null>(null);

function openDetail(messageId: number) {
  activeMessageId.value = messageId;
  detailVisible.value = true;
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard :bordered="false" :title="$t('route.message_center')" class="card-wrapper" size="small">
      <MessageCenterPanel @open-detail="openDetail"/>
    </NCard>

    <MessageDetailDrawer v-model:visible="detailVisible" :message-id="activeMessageId"/>
  </div>
</template>
