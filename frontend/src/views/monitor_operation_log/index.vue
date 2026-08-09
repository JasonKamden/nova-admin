<script lang="tsx" setup>
import {ref} from 'vue';
import dayjs from 'dayjs';
import {NButton, NTag} from 'naive-ui';
import {fetchOperationLogPage} from '@/service/api';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import OperationLogDetailModal from './modules/operation-log-detail-modal.vue';

defineOptions({name: 'MonitorOperationLog'});

const appStore = useAppStore();
const detailVisible = ref(false);
const activeId = ref<number | null>(null);

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
        {key: 'operationDescription', title: 'Description', minWidth: 180, render: row => row.operationDescription || '-'},
        {key: 'username', title: $t('page.monitor.operator'), minWidth: 140, render: row => row.username || '-'},
        {key: 'requestMethod', title: $t('page.monitor.requestMethod'), width: 100, align: 'center', render: row => row.requestMethod || '-'},
        {key: 'requestUri', title: $t('page.monitor.requestUri'), minWidth: 180, render: row => row.requestUri || '-'},
        {key: 'requestIp', title: $t('page.monitor.requestIp'), minWidth: 140, render: row => row.requestIp || '-'},
        {
            key: 'status',
            title: $t('page.monitor.status'),
            width: 100,
            align: 'center',
            render: row => <NTag type={row.status === 'SUCCESS' ? 'success' : 'warning'}>{row.status || '-'}</NTag>
        },
        {key: 'durationMs', title: $t('page.monitor.durationMs'), width: 100, align: 'center', render: row => row.durationMs ?? '-'},
        {key: 'operationTime', title: $t('page.monitor.operationTime'), minWidth: 180, render: row => dayjs(row.operationTime).format('YYYY-MM-DD HH:mm:ss')},
        {
            key: 'operate',
            title: $t('common.operate'),
            width: 100,
            align: 'center',
            render: row => <NButton ghost size="small" type="info" onClick={() => { activeId.value = row.id; detailVisible.value = true; }}>{$t('common.detail')}</NButton>
        }
    ]
});

function resetSearch() {
    searchParams.value = {pageNum: 1, pageSize: 10, module: null, operationType: null, operator: null, requestMethod: null, status: null, requestIp: null, startTime: null, endTime: null};
    getDataByPage(1);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :label-width="92" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.monitor.module')" span="24 s:12 m:6"><NInput v-model:value="searchParams.module" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.operator')" span="24 s:12 m:6"><NInput v-model:value="searchParams.operator" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.requestMethod')" span="24 s:12 m:6"><NInput v-model:value="searchParams.requestMethod" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.requestIp')" span="24 s:12 m:6"><NInput v-model:value="searchParams.requestIp" clearable /></NFormItemGi>
          <NFormItemGi span="24">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getDataByPage(1)">{{ $t('common.search') }}</NButton>
              <NButton @click="resetSearch">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>
    <NCard :bordered="false" :title="$t('route.monitor_operation_log')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default><span class="hidden"></span></template>
        </TableHeaderOperation>
      </template>
      <NDataTable :columns="columns" :data="data" :flex-height="!appStore.isMobile" :loading="loading" :pagination="mobilePagination" :row-key="row => row.id" remote class="sm:h-full" size="small" />
    </NCard>
    <OperationLogDetailModal v-model:visible="detailVisible" :log-id="activeId" />
  </div>
</template>
