<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace} from 'naive-ui';
import {fetchKickOnlineUser, fetchOnlineUserList} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {useContextStore} from '@/store/modules/context';
import {TABLE_COLUMN_WIDTH} from '@/constants/table-column';
import {formatContextType} from '@/utils/context';
import {formatDateTime} from '@/utils/date-time';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';

defineOptions({name: 'MonitorOnline'});

const appStore = useAppStore();
const contextStore = useContextStore();
const {hasAuth} = useAuth();
const filterModel = ref({keyword: null as string | null});
const kickingId = ref<string | null>(null);
const detailVisible = ref(false);
const activeRow = ref<Api.Monitor.OnlineUserItem | null>(null);
const showKick = computed(() => hasAuth('monitor:online:kick'));
const clientLabel = computed(() => (appStore.locale === 'zh-CN' ? '客户端' : 'Client'));
const forceLogoutLabel = computed(() => (appStore.locale === 'zh-CN' ? '强制下线' : 'Force Logout'));

const {columns, columnChecks, data, loading, getData, scrollX} = useNaiveTable({
  api: () => fetchOnlineUserList(filterModel.value.keyword),
  transform: response => {
    const {data: rows, error} = response;
    return error ? [] : rows;
  },
  columns: () => [
    {key: 'username', title: $t('page.monitor.username'), width: 130},
    ...(contextStore.isPlatform
      ? [
          {
            key: 'contextType',
            title: $t('page.profile.currentContext'),
            width: 110,
            align: 'center' as const,
            render: (row: Api.Monitor.OnlineUserItem) => formatContextType(row.contextType)
          },
          {
            key: 'tenantName',
            title: $t('page.monitor.tenantName'),
            minWidth: 140,
            render: (row: Api.Monitor.OnlineUserItem) => row.tenantName || '-'
          }
        ]
      : []),
    {
      key: 'departmentName',
      title: $t('page.monitor.departmentName'),
      width: 150,
      render: row => row.departmentName || '-'
    },
    {key: 'ip', title: $t('page.monitor.ip'), width: TABLE_COLUMN_WIDTH.IP, render: row => row.ip || '-'},
    {
      key: 'userAgent',
      title: clientLabel.value,
      width: 200,
      ellipsis: {tooltip: true},
      render: row => parseUserAgent(row.userAgent)
    },
    {
      key: 'loginTime',
      title: $t('page.monitor.loginTime'),
      width: TABLE_COLUMN_WIDTH.DATETIME,
      render: row => formatDateTime(row.loginTime)
    },
    {
      key: 'lastActivityTime',
      title: $t('page.monitor.lastActivityTime'),
      width: TABLE_COLUMN_WIDTH.DATETIME,
      render: row => formatDateTime(row.lastActivityTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      width: 110,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {
                  key: 'detail',
                  label: $t('common.detail'),
                  type: 'info',
                  onClick: () => {
                    activeRow.value = row;
                    detailVisible.value = true;
                  }
                },
                {
                  key: 'kick',
                  label: forceLogoutLabel.value,
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

function parseUserAgent(userAgent: string | null) {
  if (!userAgent) {
    return '-';
  }

  const browser = userAgent.match(/(Chrome|Firefox|Safari|Edge|Opera)\/?([\d.]+)?/i)?.[1] || 'Browser';
  const os =
    userAgent.match(/(Windows NT|Mac OS X|Android|iPhone OS|Linux)/i)?.[1]?.replace('NT', '').trim() || 'OS';

  return `${browser} / ${os}`;
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
      <template #actions>
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
      </template>
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

    <BusinessFormContainer v-model:visible="detailVisible" :title="$t('common.detail')" :width="720">
      <NDescriptions v-if="activeRow" :column="2" bordered label-placement="left" size="small">
        <NDescriptionsItem :label="$t('page.user.username')">{{ activeRow.username }}</NDescriptionsItem>
        <NDescriptionsItem v-if="contextStore.isPlatform" :label="$t('page.profile.currentContext')">
          {{ formatContextType(activeRow.contextType) }}
        </NDescriptionsItem>
        <NDescriptionsItem v-if="contextStore.isPlatform" :label="$t('page.monitor.tenantName')">{{ activeRow.tenantName || '-' }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.monitor.departmentName')">{{ activeRow.departmentName || '-' }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.monitor.ip')">{{ activeRow.ip || '-' }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.monitor.loginTime')">{{ formatDateTime(activeRow.loginTime) }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.monitor.lastActivityTime')">{{ formatDateTime(activeRow.lastActivityTime) }}</NDescriptionsItem>
        <NDescriptionsItem :label="clientLabel">{{ parseUserAgent(activeRow.userAgent) }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.monitor.userAgent')" :span="2">{{ activeRow.userAgent || '-' }}</NDescriptionsItem>
      </NDescriptions>
      <template #action>
        <NSpace justify="end">
          <NButton @click="detailVisible = false">{{ $t('common.close') }}</NButton>
        </NSpace>
      </template>
    </BusinessFormContainer>
  </div>
</template>
