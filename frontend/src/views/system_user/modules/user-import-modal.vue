<script lang="ts" setup>
import { computed, ref } from 'vue';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import { $t } from '@/locales';
import { fetchImportUsers, downloadUserImportTemplate } from '@/service/api';

defineOptions({
  name: 'UserImportModal'
});

const emit = defineEmits<{ submitted: [] }>();
const visible = defineModel<boolean>('visible', { default: false });

const fileInputRef = ref<HTMLInputElement | null>(null);
const file = ref<File | null>(null);
const importing = ref(false);
const downloadingTemplate = ref(false);
const result = ref<Api.User.ImportResult | null>(null);

const errorRows = computed(() =>
  (result.value?.errors || []).map((message, index) => ({
    id: index + 1,
    message
  }))
);

function resetState() {
  file.value = null;
  result.value = null;
}

function closeModal() {
  visible.value = false;
  resetState();
}

function triggerSelectFile() {
  fileInputRef.value?.click();
}

function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  file.value = input.files?.[0] || null;
  result.value = null;
}

async function handleDownloadTemplate() {
  downloadingTemplate.value = true;
  try {
    await downloadUserImportTemplate();
  } catch {
    window.$message?.error($t('common.error'));
  } finally {
    downloadingTemplate.value = false;
  }
}

async function handleImport() {
  if (!file.value) {
    window.$message?.warning($t('page.user.importSelectFile'));
    return;
  }

  importing.value = true;
  const { data, error } = await fetchImportUsers(file.value);
  importing.value = false;

  if (!error) {
    result.value = data;
    window.$message?.success($t('page.user.importSuccess', { count: data.success }));
    emit('submitted');
  }
}
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="$t('page.user.importTitle')" :width="860">
    <div class="flex-col-stretch gap-16px">
      <input ref="fileInputRef" accept=".xls,.xlsx" class="hidden" type="file" @change="handleFileChange" />

      <NAlert type="info">
        <template #header>{{ $t('page.user.importGuide') }}</template>
        <div>{{ $t('page.user.importGuideDesc') }}</div>
      </NAlert>

      <div class="rounded-14px border border-dashed border-#cbd5e1 p-20px">
        <div class="flex flex-wrap items-center justify-between gap-12px">
          <div>
            <div class="text-15px font-600">{{ $t('page.user.importTemplate') }}</div>
            <div class="mt-4px text-13px text-text-secondary">{{ $t('page.user.importAccept') }}</div>
          </div>
          <NButton :loading="downloadingTemplate" secondary type="primary" @click="handleDownloadTemplate">
            {{ $t('page.user.downloadTemplate') }}
          </NButton>
        </div>
      </div>

      <div class="rounded-14px border border-dashed border-#cbd5e1 p-20px">
        <div class="flex flex-wrap items-center justify-between gap-12px">
          <div>
            <div class="text-15px font-600">{{ $t('page.user.importFile') }}</div>
            <div class="mt-4px text-13px text-text-secondary">
              {{ file ? file.name : $t('page.user.importEmptyFile') }}
            </div>
          </div>
          <NButton ghost type="primary" @click="triggerSelectFile">{{ $t('page.user.selectImportFile') }}</NButton>
        </div>
      </div>

      <NCard v-if="result" :bordered="false" embedded size="small">
        <div class="flex flex-wrap gap-16px text-14px">
          <span>{{ $t('page.user.importTotal') }}: {{ result.total }}</span>
          <span>{{ $t('page.user.importSucceeded') }}: {{ result.success }}</span>
          <span>{{ $t('page.user.importFailed') }}: {{ result.failed }}</span>
        </div>

        <NDataTable
          v-if="errorRows.length"
          :columns="[
            { key: 'id', title: $t('common.index'), width: 72, align: 'center' },
            { key: 'message', title: $t('page.user.importErrorDetail'), minWidth: 540 }
          ]"
          :data="errorRows"
          :pagination="false"
          :row-key="row => row.id"
          class="mt-16px"
          size="small"
        />
      </NCard>
    </div>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="importing" type="primary" @click="handleImport">{{ $t('page.user.startImport') }}</NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
