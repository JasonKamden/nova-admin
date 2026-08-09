<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {fetchOperationLogPage} from '@/service/api';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {formatDateTime} from '@/utils/date-time';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import OperationLogDetailModal from './modules/operation-log-detail-modal.vue';

defineOptions({name: 'MonitorOperationLog'});

const appStore = useAppStore();
const detailVisible = ref(false);
const activeId = ref<number | null>(null);
const requestMethodOptions = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'].map(item => ({label: item, value: item}));
const statusOptions = computed(() => [
  {label: 'SUCCESS', value: 'SUCCESS'},
  {label: 'FAILED', value: 'FAILED'}
]);

const searchParams = ref<Api.Monitor.OperationLogPageParams>({
  pageNum: 1,
  pageSize: 10,
  module: null,
  operationType: null,
  operator: null,
  requestMethod: null,
  status: null,
  requestIp: null,
  startTime: null,
  endTime: null
});

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => fetchOperationLogPage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {key: 'module', title: $t('page.monitor.module'), minWidth: 120},
    {key: 'operationType', title: $t('page.monitor.operationType'), minWidth: 120},
    {key: 'operationDescription', title: $t('page.monitor.description'), minWidth: 220, ellipsis: {tooltip: true}, render: row => row.operationDescription || '-'},
    {key: 'username', title: $t('page.monitor.operator'), minWidth: 140, render: row => row.username || '-'},
    {
      key: 'requestMethod',
      title: $t('page.monitor.requestMethod'),
      width: 100,
      align: 'center',
      render: row => row.requestMethod || '-'
    },
    {key: 'requestUri', title: $t('page.monitor.requestUri'), minWidth: 220, ellipsis: {tooltip: true}, render: row => row.requestUri || '-'},
    {key: 'requestIp', title: $t('page.monitor.requestIp'), minWidth: 140, render: row => row.requestIp || '-'},
    {
      key: 'status',
      title: $t('page.monitor.status'),
      width: 100,
      align: 'center',
      render: row => <NTag type={row.status === 'SUCCESS' ? 'success' : 'warning'}>{row.status || '-'}</NTag>
    },
    {
      key: 'durationMs',
      title: $t('page.monitor.durationMs'),
      width: 100,
      align: 'center',
      render: row => row.durationMs ?? '-'
    },
    {
      key: 'operationTime',
      title: $t('page.monitor.operationTime'),
      minWidth: 180,
      render: row => formatDateTime(row.operationTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      width: 140,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {
                  key: 'detail',
                  label: $t('common.detail'),
                  type: 'info',
                  onClick: () => {
                    activeId.value = row.id;
                    detailVisible.value = true;
                  }
                }
              ]}
              maxVisible={1}
          />
      )
    }
  ]
});

function resetSearch() {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
    module: null,
    operationType: null,
    operator: null,
    requestMethod: null,
    status: null,
    requestIp: null,
    startTime: null,
    endTime: null
  };
  getDataByPage(1);
}

function handleTimeRange(value: [number, number] | null) {
  searchParams.value.startTime = value ? new Date(value[0]).toISOString() : null;
  searchParams.value.endTime = value ? new Date(value[1]).toISOString() : null;
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.monitor.module')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.module" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.operator')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.operator" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.requestMethod')" class="pr-24px" span="24 s:12 m:6">
        <NSelect v-model:value="searchParams.requestMethod" :options="requestMethodOptions" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect v-model:value="searchParams.status" :options="statusOptions" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.requestIp')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.requestIp" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.operationTime')" class="pr-24px" span="24 s:12 m:12">
        <NDatePicker
          clearable
          type="datetimerange"
          @update:value="handleTimeRange"
        />
      </NFormItemGi>
      <template #actions>
        <NFormItemGi class="pr-24px" span="24">
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
      </template>
    </SearchPanel>
    <NCard
      :bordered="false" :title="$t('route.monitor_operation_log')" class="card-wrapper sm:flex-1-hidden"
      size="small"
    >
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default><span class="hidden"></span></template>
        </TableHeaderOperation>
      </template>
      <NDataTable
        :columns="columns" :data="data" :flex-height="!appStore.isMobile" :loading="loading"
        :pagination="mobilePagination" :row-key="row => row.id" class="sm:h-full" remote size="small"
      />
    </NCard>
    <OperationLogDetailModal v-model:visible="detailVisible" :log-id="activeId" />
  </div>
</template>
