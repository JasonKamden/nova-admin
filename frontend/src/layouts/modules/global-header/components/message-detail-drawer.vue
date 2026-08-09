<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {messageTypeOptions} from '@/constants/business';
import {$t} from '@/locales';
import {fetchFileDetail} from '@/service/api';
import {useMessageStore} from '@/store/modules/message';
import {formatDateTime} from '@/utils/date-time';
import RichHtmlContent from '@/components/business/rich-html-content.vue';
import {openFileByMode} from '@/utils/file';

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
  try {
    await openFileByMode(fileId, mode);
  } catch {
    window.$message?.error($t('common.error'));
  }
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
                  <span>{{ formatDateTime(detail?.sendTime) }}</span>
                </div>
              </div>
            </div>
          </NCard>

          <NCard :bordered="false" embedded size="small">
            <template #header>{{ $t('page.message.contentHtml') }}</template>
            <RichHtmlContent v-if="detail?.contentHtml" :html="detail.contentHtml" />
            <NEmpty v-else :description="$t('common.noData')" />
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
                  <NButton size="small" @click="handleFileOpen(file.id, 'preview')">
                    {{
                      $t('page.file.preview')
                    }}
                  </NButton>
                  <NButton ghost size="small" type="primary" @click="handleFileOpen(file.id, 'download')">
                    {{ $t('page.file.download') }}
                  </NButton>
                </NSpace>
              </div>
            </div>
            <NEmpty v-else :description="$t('common.noData')" />
          </NCard>
        </div>
      </NSpin>
    </NDrawerContent>
  </NDrawer>
</template>
