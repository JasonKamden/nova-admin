<script lang="tsx" setup>
import {ref} from 'vue';
import dayjs from 'dayjs';
import {NTag} from 'naive-ui';
import {fetchLoginLogPage} from '@/service/api';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';

defineOptions({name: 'MonitorLoginLog'});

const appStore = useAppStore();
const searchParams = ref<Api.Monitor.LoginLogPageParams>({
    pageNum: 1,
    pageSize: 10,
    username: null,
    loginStatus: null,
    ip: null,
    startTime: null,
    endTime: null
});

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
    api: () => fetchLoginLogPage(searchParams.value),
    transform: response => defaultTransform(response),
    onPaginationParamsChange: params => {
        searchParams.value.pageNum = params.page || 1;
        searchParams.value.pageSize = params.pageSize || 10;
    },
    columns: () => [
        {key: 'username', title: $t('page.monitor.username'), minWidth: 140},
        {key: 'contextType', title: 'Context', width: 100, align: 'center'},
        {key: 'loginType', title: $t('page.monitor.loginType'), minWidth: 120, render: row => row.loginType || '-'},
        {
            key: 'loginStatus',
            title: $t('page.monitor.status'),
            width: 90,
            align: 'center',
            render: row => <NTag type={row.loginStatus === 1 ? 'success' : 'error'}>{row.loginStatus === 1 ? $t('common.enabled') : $t('common.disabled')}</NTag>
        },
        {key: 'ip', title: $t('page.monitor.ip'), minWidth: 140, render: row => row.ip || '-'},
        {key: 'failureReason', title: $t('page.monitor.failureReason'), minWidth: 180, render: row => row.failureReason || '-'},
        {key: 'requestId', title: 'RequestId', minWidth: 180, render: row => row.requestId || '-'},
        {key: 'loginTime', title: $t('page.monitor.loginTime'), minWidth: 180, render: row => dayjs(row.loginTime).format('YYYY-MM-DD HH:mm:ss')}
    ]
});

function resetSearch() {
    searchParams.value = {pageNum: 1, pageSize: 10, username: null, loginStatus: null, ip: null, startTime: null, endTime: null};
    getDataByPage(1);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :label-width="92" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.monitor.username')" span="24 s:12 m:6"><NInput v-model:value="searchParams.username" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.ip')" span="24 s:12 m:6"><NInput v-model:value="searchParams.ip" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.status')" span="24 s:12 m:6">
            <NSelect v-model:value="searchParams.loginStatus" :options="[{label:$t('common.enabled'),value:1},{label:$t('common.disabled'),value:0}]" clearable />
          </NFormItemGi>
          <NFormItemGi span="24 m:6">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getDataByPage(1)">{{ $t('common.search') }}</NButton>
              <NButton @click="resetSearch">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>
    <NCard :bordered="false" :title="$t('route.monitor_login_log')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default><span class="hidden"></span></template>
        </TableHeaderOperation>
      </template>
      <NDataTable :columns="columns" :data="data" :flex-height="!appStore.isMobile" :loading="loading" :pagination="mobilePagination" :row-key="row => row.id" remote class="sm:h-full" size="small" />
    </NCard>
  </div>
</template>
