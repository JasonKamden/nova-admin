<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {fetchDeleteTenant, fetchTenantOptions, fetchTenantPage, fetchUpdateTenantStatus} from '@/service/api';
import {statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {useContextStore} from '@/store/modules/context';
import {formatDateTime} from '@/utils/date-time';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import TenantOperateModal from './modules/tenant-operate-modal.vue';

defineOptions({
  name: 'PlatformTenant'
});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const contextStore = useContextStore();
const {hasAuth} = useAuth();

const searchParams = ref<Api.Tenant.PageParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: null,
  status: null
});

const modalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeTenantId = ref<number | null>(null);
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('platform:tenant:add'));
const showUpdate = computed(() => hasAuth('platform:tenant:update'));
const showDelete = computed(() => hasAuth('platform:tenant:delete'));

const {
  columns,
  columnChecks,
  data,
  loading,
  getData,
  getDataByPage,
  mobilePagination
} = useNaivePaginatedTable({
  api: () => fetchTenantPage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      key: 'index',
      title: $t('common.index'),
      align: 'center',
      width: 72,
      render: (_, index) => {
        const page = searchParams.value.pageNum;
        const pageSize = searchParams.value.pageSize;

        return (page - 1) * pageSize + index + 1;
      }
    },
    {
      key: 'tenantCode',
      title: $t('page.tenant.tenantCode'),
      minWidth: 150
    },
    {
      key: 'tenantName',
      title: $t('page.tenant.tenantName'),
      minWidth: 180
    },
    {
      key: 'status',
      title: $t('page.tenant.status'),
      align: 'center',
      width: 100,
      render: row => {
        const config = statusRecord[row.status as keyof typeof statusRecord];

        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {
      key: 'expireAt',
      title: $t('page.tenant.expireAt'),
      align: 'center',
      width: 140,
      render: row => row.expireAt || '-'
    },
    {
      key: 'contactName',
      title: $t('page.tenant.contactName'),
      minWidth: 120,
      render: row => row.contactName || '-'
    },
    {
      key: 'contactPhone',
      title: $t('page.tenant.contactPhone'),
      minWidth: 140,
      render: row => row.contactPhone || '-'
    },
    {
      key: 'contactEmail',
      title: $t('page.tenant.contactEmail'),
      minWidth: 220,
      render: row => row.contactEmail || '-'
    },
    {
      key: 'createTime',
      title: $t('page.tenant.createTime'),
      minWidth: 180,
      render: row => formatDateTime(row.createTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      align: 'center',
      width: 260,
      render: row => {
        const nextStatus = row.status === 1 ? 0 : 1;
        const actionText = row.status === 1 ? $t('common.disable') : $t('common.enable');

        return (
            <TableRowActions
                actions={[
                  {key: 'detail', label: $t('common.detail'), type: 'info', onClick: () => openModal('detail', row.id)},
                  {
                    key: 'edit',
                    label: $t('common.edit'),
                    type: 'primary',
                    show: showUpdate.value,
                    onClick: () => openModal('edit', row.id)
                  },
                  {
                    key: 'status',
                    label: actionText,
                    type: row.status === 1 ? 'warning' : 'success',
                    show: showUpdate.value,
                    loading: actionLoadingId.value === row.id,
                    confirmText: $t('page.tenant.statusConfirm', {action: actionText, name: row.tenantName}),
                    onClick: () => handleStatusChange(row.id, nextStatus)
                  },
                  {
                    key: 'delete',
                    label: $t('common.delete'),
                    type: 'error',
                    show: showDelete.value,
                    loading: deletingId.value === row.id,
                    confirmText: $t('page.tenant.deleteConfirm', {name: row.tenantName}),
                    onClick: () => handleDelete(row.id)
                  }
                ]}
            />
        );
      }
    }
  ]
});

function openModal(mode: OperateMode, tenantId: number | null = null) {
  operateMode.value = mode;
  activeTenantId.value = tenantId;
  modalVisible.value = true;
}

function handleReset() {
  searchParams.value.keyword = null;
  searchParams.value.status = null;
  searchParams.value.pageNum = 1;
  getDataByPage(1);
}

async function refreshContextOptions() {
  const {error} = await fetchTenantOptions();

  if (!error) {
    await contextStore.getContextOptions();
  }
}

async function handleSubmitted() {
  await getDataByPage(searchParams.value.pageNum);
  await refreshContextOptions();
}

async function handleStatusChange(tenantId: number, status: number) {
  actionLoadingId.value = tenantId;

  const {error} = await fetchUpdateTenantStatus(tenantId, status);

  actionLoadingId.value = null;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
    await refreshContextOptions();
  }
}

async function handleDelete(tenantId: number) {
  deletingId.value = tenantId;

  const {error} = await fetchDeleteTenant(tenantId);

  deletingId.value = null;

  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
    await refreshContextOptions();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.tenant.keyword')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.keyword" :placeholder="$t('common.keywordSearch')" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.tenant.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
          clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:12">
        <NSpace class="w-full" justify="end">
          <NButton @click="handleReset">
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

    <NCard :bordered="false" :title="$t('route.platform_tenant')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton v-if="showAdd" ghost size="small" type="primary" @click="openModal('add')">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
              {{ $t('common.add') }}
            </NButton>
          </template>
        </TableHeaderOperation>
      </template>

      <NDataTable
        :columns="columns"
        :data="data"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        :pagination="mobilePagination"
        :row-key="row => row.id"
        :scroll-x="1600"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>

    <TenantOperateModal
      v-model:visible="modalVisible"
      :mode="operateMode"
      :tenant-id="activeTenantId"
      @submitted="handleSubmitted"
    />
  </div>
</template>
