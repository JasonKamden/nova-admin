<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import dayjs from 'dayjs';
import {messageTypeOptions} from '@/constants/business';
import {$t} from '@/locales';
import {fetchFileDetail} from '@/service/api';
import {getAuthorization} from '@/service/request/shared';
import {useMessageStore} from '@/store/modules/message';

defineOptions({
  name: 'MessageDetailDrawer'
});

interface Props {
  messageId: number | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  updated: [];
}>();

const visible = defineModel<boolean>('visible', {default: false});

const messageStore = useMessageStore();

const loading = ref(false);
const detail = ref<Api.MessageCenter.Detail | null>(null);
const attachments = ref<Api.File.Item[]>([]);

const messageTypeLabel = computed(() => {
  const item = messageTypeOptions.find(option => option.value === detail.value?.messageType);
  return item ? $t(item.label) : $t('common.noData');
});

async function loadAttachments(fileIds: number[]) {
  if (!fileIds.length) {
    attachments.value = [];
    return;
  }

  const results = await Promise.all(fileIds.map(async id => fetchFileDetail(id)));

  attachments.value = results.filter(item => !item.error).map(item => item.data);
}

async function loadDetail() {
  if (!visible.value || !props.messageId) {
    return;
  }

  loading.value = true;

  try {
    const data = await messageStore.fetchDetail(props.messageId);

    detail.value = data;

    if (data) {
      await loadAttachments(data.fileIds);
      emit('updated');
    }
  } finally {
    loading.value = false;
  }
}

async function handleFileOpen(fileId: number, mode: 'preview' | 'download') {
  const response = await fetch(`/api/files/${fileId}/${mode}`, {
    headers: {
      Authorization: getAuthorization() || ''
    }
  });

  if (!response.ok) {
    window.$message?.error('Request failed');
    return;
  }

  const blob = await response.blob();
  const url = URL.createObjectURL(blob);

  if (mode === 'preview') {
    window.open(url, '_blank');
    return;
  }

  const link = document.createElement('a');
  link.href = url;
  link.download = '';
  link.click();
  URL.revokeObjectURL(url);
}

watch(
    () => [visible.value, props.messageId],
    async () => {
      if (!visible.value) {
        return;
      }

      await loadDetail();
    }
);
</script>

<template>
  <NDrawer v-model:show="visible" :width="720" display-directive="show">
    <NDrawerContent :native-scrollbar="false" :title="$t('page.message.detailTitle')" closable>
      <NSpin :show="loading">
        <div class="flex-col-stretch gap-16px">
          <NCard :bordered="false" embedded size="small">
            <div class="flex items-start justify-between gap-16px">
              <div class="min-w-0 flex-1">
                <div class="text-18px font-600">{{ detail?.title || '-' }}</div>
                <div class="mt-8px flex flex-wrap items-center gap-8px text-12px text-#666">
                  <NTag size="small" type="info">{{ messageTypeLabel }}</NTag>
                  <span>{{ detail?.sendTime ? dayjs(detail.sendTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}</span>
                </div>
              </div>
            </div>
          </NCard>

          <NCard :bordered="false" embedded size="small">
            <template #header>{{ $t('page.message.contentHtml') }}</template>
            <!-- eslint-disable-next-line vue/no-v-html -->
            <div v-if="detail?.contentHtml" class="message-rich-content" v-html="detail.contentHtml"></div>
            <NEmpty v-else :description="$t('common.noData')"/>
          </NCard>

          <NCard :bordered="false" embedded size="small">
            <template #header>Attachment</template>
            <div v-if="attachments.length" class="flex-col-stretch gap-12px">
              <div
                  v-for="file in attachments"
                  :key="file.id"
                  class="flex flex-wrap items-center justify-between gap-12px rounded-10px bg-#f6f8fb px-14px py-12px"
              >
                <div class="min-w-0 flex-1">
                  <div class="truncate text-14px font-500">{{ file.originalName }}</div>
                  <div class="mt-4px text-12px text-#666">{{ file.contentType || '-' }}</div>
                </div>
                <NSpace>
                  <NButton size="small" @click="handleFileOpen(file.id, 'preview')">{{
                      $t('page.file.preview')
                    }}
                  </NButton>
                  <NButton ghost size="small" type="primary" @click="handleFileOpen(file.id, 'download')">
                    {{ $t('page.file.download') }}
                  </NButton>
                </NSpace>
              </div>
            </div>
            <NEmpty v-else :description="$t('common.noData')"/>
          </NCard>
        </div>
      </NSpin>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped>
.message-rich-content {
  line-height: 1.7;
  word-break: break-word;
}

.message-rich-content :deep(p) {
  margin: 0 0 12px;
}

.message-rich-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
}

.message-rich-content :deep(td),
.message-rich-content :deep(th) {
  border: 1px solid #e5e7eb;
  padding: 8px 10px;
}
</style>
