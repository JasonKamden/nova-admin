<script lang="tsx" setup>
import {computed, ref} from 'vue';
import dayjs from 'dayjs';
import {NButton, NSpace} from 'naive-ui';
import {fetchKickOnlineUser, fetchOnlineUserList} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';

defineOptions({name: 'MonitorOnline'});

const appStore = useAppStore();
const {hasAuth} = useAuth();
const filterModel = ref({keyword: null as string | null});
const kickingId = ref<string | null>(null);
const showKick = computed(() => hasAuth('monitor:online:kick'));

const {columns, columnChecks, data, loading, getData, scrollX} = useNaiveTable({
  api: () => fetchOnlineUserList(filterModel.value.keyword),
  transform: response => {
    const {data: rows, error} = response;
    return error ? [] : rows;
  },
  columns: () => [
    {key: 'username', title: $t('page.monitor.username'), minWidth: 140},
    {key: 'contextType', title: 'Context', width: 100, align: 'center'},
    {key: 'tenantName', title: $t('page.monitor.tenantName'), minWidth: 160, render: row => row.tenantName || '-'},
    {
      key: 'departmentName',
      title: $t('page.monitor.departmentName'),
      minWidth: 160,
      render: row => row.departmentName || '-'
    },
    {key: 'ip', title: $t('page.monitor.ip'), minWidth: 140, render: row => row.ip || '-'},
    {key: 'userAgent', title: $t('page.monitor.userAgent'), minWidth: 240, render: row => row.userAgent || '-'},
    {
      key: 'loginTime',
      title: $t('page.monitor.loginTime'),
      minWidth: 180,
      render: row => dayjs(row.loginTime).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      key: 'lastActivityTime',
      title: $t('page.monitor.lastActivityTime'),
      minWidth: 180,
      render: row => dayjs(row.lastActivityTime).format('YYYY-MM-DD HH:mm:ss')
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
                  key: 'kick',
                  label: $t('common.delete'),
                  type: 'error',
                  show: showKick.value,
                  loading: kickingId.value === row.sessionId,
                  confirmText: $t('page.monitor.kickConfirm', {name: row.username}),
                  onClick: () => handleKick(row.sessionId, row.username)
                }
              ]}
              maxVisible={1}
          />
      )
    }
  ]
});

function resetSearch() {
  filterModel.value.keyword = null;
  getData();
}

async function handleKick(sessionId: string, _username: string) {
  kickingId.value = sessionId;
  const {error} = await fetchKickOnlineUser(sessionId);
  kickingId.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="filterModel">
      <NFormItemGi :label="$t('page.monitor.keyword')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filterModel.keyword" :placeholder="$t('common.keywordSearch')" clearable />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:18">
        <NSpace class="w-full" justify="end">
          <NButton @click="resetSearch">
            <template #icon>
              <icon-ic-round-refresh class="text-icon" />
            </template>
            {{ $t('common.reset') }}
          </NButton>
          <NButton ghost type="primary" @click="getData">
            <template #icon>
              <icon-ic-round-search class="text-icon" />
            </template>
            {{ $t('common.search') }}
          </NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>
    <NCard :bordered="false" :title="$t('route.monitor_online')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default><span class="hidden"></span></template>
        </TableHeaderOperation>
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        :row-key="row => row.sessionId"
        :scroll-x="scrollX"
        class="sm:h-full"
        size="small"
      />
    </NCard>
  </div>
</template>
