<script lang="ts" setup>
import { ref, watch } from 'vue';
import { fetchFileDetail, fetchUploadFile } from '@/service/api';
import { $t } from '@/locales';
import { formatFileSize, openFileByMode } from '@/utils/file';

defineOptions({
  name: 'MessageAttachmentPicker'
});

const fileIds = defineModel<number[]>('fileIds', { default: [] });

const fileInputRef = ref<HTMLInputElement | null>(null);
const loading = ref(false);
const attachments = ref<Api.File.Item[]>([]);

async function loadAttachments() {
  if (!fileIds.value.length) {
    attachments.value = [];
    return;
  }

  const results = await Promise.all(fileIds.value.map(async id => fetchFileDetail(id)));
  attachments.value = results.filter(item => !item.error).map(item => item.data);
}

function triggerUpload() {
  fileInputRef.value?.click();
}

async function handleSelectFile(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  loading.value = true;
  const { data, error } = await fetchUploadFile(file);
  loading.value = false;
  input.value = '';

  if (!error) {
    const nextIds = new Set(fileIds.value);
    nextIds.add(data.id);
    fileIds.value = Array.from(nextIds);
    await loadAttachments();
    window.$message?.success($t('common.addSuccess'));
  }
}

function removeAttachment(fileId: number) {
  fileIds.value = fileIds.value.filter(id => id !== fileId);
  attachments.value = attachments.value.filter(item => item.id !== fileId);
}

async function handleOpen(fileId: number, mode: 'preview' | 'download') {
  try {
    await openFileByMode(fileId, mode);
  } catch {
    window.$message?.error($t('common.error'));
  }
}

watch(
  () => fileIds.value,
  async () => {
    await loadAttachments();
  },
  { immediate: true }
);
</script>

<template>
  <div class="flex-col-stretch gap-12px">
    <input ref="fileInputRef" accept=".png,.jpg,.jpeg,.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip" class="hidden" type="file" @change="handleSelectFile" />

    <div class="rounded-12px border border-dashed border-#cbd5e1 p-16px">
      <div class="flex flex-wrap items-center justify-between gap-12px">
        <div>
          <div class="text-14px font-500">{{ $t('page.message.attachmentHint') }}</div>
          <div class="mt-4px text-12px text-text-secondary">{{ $t('page.message.attachmentHintDesc') }}</div>
        </div>
        <NButton :loading="loading" ghost type="primary" @click="triggerUpload">
          {{ $t('page.file.upload') }}
        </NButton>
      </div>
    </div>

    <div v-if="attachments.length" class="flex-col-stretch gap-10px">
      <div
        v-for="file in attachments"
        :key="file.id"
        class="flex flex-wrap items-center justify-between gap-12px rounded-12px bg-#f8fafc px-14px py-12px dark:bg-#111827"
      >
        <div class="min-w-0 flex-1">
          <div class="truncate text-14px font-500">{{ file.originalName }}</div>
          <div class="mt-4px text-12px text-text-secondary">
            {{ formatFileSize(file.fileSize) }} · {{ file.contentType || '-' }}
          </div>
        </div>
        <NSpace>
          <NButton quaternary size="small" @click="handleOpen(file.id, 'preview')">{{ $t('page.file.preview') }}</NButton>
          <NButton quaternary size="small" type="primary" @click="handleOpen(file.id, 'download')">
            {{ $t('page.file.download') }}
          </NButton>
          <NButton quaternary size="small" type="error" @click="removeAttachment(file.id)">{{ $t('common.delete') }}</NButton>
        </NSpace>
      </div>
    </div>

    <NEmpty v-else :description="$t('common.noData')" />
  </div>
</template>
