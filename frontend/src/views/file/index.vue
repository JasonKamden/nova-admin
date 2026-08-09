<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NPopconfirm, NSpace} from 'naive-ui';
import {statusOptions} from '@/constants/business';
import {fetchBatchDeleteFiles, fetchDeleteFile, fetchFilePage, fetchUploadFile} from '@/service/api';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {formatDateTime} from '@/utils/date-time';
import {localStg} from '@/utils/storage';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';

defineOptions({name: 'FilePage'});

const appStore = useAppStore();
const fileInputRef = ref<HTMLInputElement | null>(null);
const uploading = ref(false);
const deletingId = ref<number | null>(null);
const batchDeleting = ref(false);
const checkedRowKeys = ref<number[]>([]);
const statusLabelMap: Partial<Record<number, App.I18n.I18nKey>> = Object.fromEntries(
    statusOptions.map(item => [item.value, item.label])
);

const searchParams = ref<Api.File.PageParams>({
  pageNum: 1,
  pageSize: 10,
  fileName: null,
  contentType: null,
  storageType: null,
  status: null
});

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => fetchFilePage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      type: 'selection',
      multiple: true
    },
    {key: 'originalName', title: $t('page.file.fileName'), minWidth: 220},
    {key: 'contentType', title: $t('page.file.contentType'), minWidth: 160, render: row => row.contentType || '-'},
    {
      key: 'storageType',
      title: $t('page.file.storageType'),
      width: 110,
      align: 'center',
      render: row => row.storageType || '-'
    },
    {
      key: 'status',
      title: $t('page.file.status'),
      width: 90,
      align: 'center',
      render: row => {
        const label = statusLabelMap[row.status];
        return label ? $t(label) : row.status;
      }
    },
    {key: 'fileSize', title: $t('page.file.fileSize'), width: 110, align: 'center'},
    {key: 'sha256', title: $t('page.file.sha256'), minWidth: 220, render: row => row.sha256 || '-'},
    {
      key: 'ownerUserId',
      title: $t('page.file.ownerUserId'),
      width: 100,
      align: 'center',
      render: row => row.ownerUserId ?? '-'
    },
    {
      key: 'createTime',
      title: $t('page.file.createTime'),
      minWidth: 180,
      render: row => formatDateTime(row.createTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      width: 220,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {
                  key: 'preview',
                  label: $t('page.file.preview'),
                  type: 'info',
                  onClick: () => handleFileOpen(row.id, 'preview')
                },
                {
                  key: 'download',
                  label: $t('page.file.download'),
                  type: 'primary',
                  onClick: () => handleFileOpen(row.id, 'download')
                },
                {
                  key: 'delete',
                  label: $t('common.delete'),
                  type: 'error',
                  loading: deletingId.value === row.id,
                  confirmText: $t('page.file.deleteConfirm', {name: row.originalName}),
                  onClick: () => handleDelete(row.id)
                }
              ]}
          />
      )
    }
  ]
});

const hasChecked = computed(() => checkedRowKeys.value.length > 0);

function resetSearch() {
  searchParams.value = {pageNum: 1, pageSize: 10, fileName: null, contentType: null, storageType: null, status: null};
  getDataByPage(1);
}

function triggerUpload() {
  fileInputRef.value?.click();
}

async function handleUploadChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  uploading.value = true;
  const {error} = await fetchUploadFile(file);
  uploading.value = false;
  input.value = '';

  if (!error) {
    window.$message?.success($t('common.addSuccess'));
    await getDataByPage(1);
  }
}

async function handleDelete(id: number) {
  deletingId.value = id;
  const {error} = await fetchDeleteFile(id);
  deletingId.value = null;

  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
  }
}

async function handleBatchDelete() {
  if (!hasChecked.value) return;

  batchDeleting.value = true;
  const {error} = await fetchBatchDeleteFiles(checkedRowKeys.value);
  batchDeleting.value = false;

  if (!error) {
    checkedRowKeys.value = [];
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
  }
}

async function handleFileOpen(id: number, mode: 'preview' | 'download') {
  const token = localStg.get('token') || '';
  const response = await fetch(`/api/files/${id}/${mode}`, {
    headers: {
      Authorization: token
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
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <input ref="fileInputRef" class="hidden" type="file" @change="handleUploadChange" />
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.file.fileName')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.fileName" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.file.contentType')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.contentType" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.file.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:6">
        <NSpace class="w-full" justify="end">
          <NButton @click="resetSearch">
            <template #icon>
              <icon-ic-round-refresh class="text-icon" />
            </template>
            {{ $t('common.reset') }}
          </NButton>
          <NButton ghost type="primary" @click="getDataByPage(1)">
            <template #icon>
              <icon-ic-round-search class="text-icon" />
            </template>
            {{ $t('common.search') }}
          </NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>
    <NCard :bordered="false" :title="$t('route.file')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="!hasChecked"
          :loading="loading || uploading || batchDeleting"
          @delete="handleBatchDelete"
          @refresh="getData"
        >
          <template #default>
            <NButton :loading="uploading" ghost size="small" type="primary" @click="triggerUpload">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
              {{ $t('page.file.upload') }}
            </NButton>
            <NPopconfirm @positive-click="handleBatchDelete">
              <template #trigger>
                <NButton :disabled="!hasChecked" :loading="batchDeleting" ghost size="small" type="error">
                  {{ $t('common.batchDelete') }}
                </NButton>
              </template>
              {{ $t('page.file.batchDeleteConfirm') }}
            </NPopconfirm>
          </template>
        </TableHeaderOperation>
      </template>
      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="data"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        :pagination="mobilePagination"
        :row-key="row => row.id"
        :scroll-x="1900"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>
  </div>
</template>
