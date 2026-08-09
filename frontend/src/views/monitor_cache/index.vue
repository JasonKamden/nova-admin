<script lang="tsx" setup>
import {computed, onMounted, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {fetchCacheList, fetchClearCache, fetchRedisStatus, fetchRefreshCache} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';

defineOptions({name: 'MonitorCache'});

const appStore = useAppStore();
const {hasAuth} = useAuth();
const filters = ref({name: null as string | null, type: null as string | null, module: null as string | null});
const redisStatus = ref('-');
const actionCode = ref<string | null>(null);
const showClear = computed(() => hasAuth('monitor:cache:clear'));
const showRefresh = computed(() => hasAuth('monitor:cache:refresh'));

const {columns, columnChecks, data, loading, getData, scrollX} = useNaiveTable({
  api: () => fetchCacheList(filters.value),
  transform: response => {
    const {data: rows, error} = response;
    return error ? [] : rows;
  },
  columns: () => [
    {key: 'code', title: $t('page.monitor.cacheCode'), minWidth: 180},
    {key: 'name', title: $t('page.monitor.cacheName'), minWidth: 160},
    {key: 'type', title: $t('page.monitor.cacheType'), minWidth: 120, render: row => row.type || '-'},
    {key: 'module', title: $t('page.monitor.cacheModule'), minWidth: 120, render: row => row.module || '-'},
    {key: 'scope', title: $t('page.monitor.cacheScope'), minWidth: 120, render: row => row.scope || '-'},
    {key: 'defaultTtlSeconds', title: $t('page.monitor.defaultTtlSeconds'), width: 100, align: 'center'},
    {
      key: 'clearable',
      title: $t('page.monitor.clearable'),
      width: 90,
      align: 'center',
      render: row => (row.clearable ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'refreshable',
      title: $t('page.monitor.refreshable'),
      width: 90,
      align: 'center',
      render: row => (row.refreshable ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {key: 'status', title: $t('page.monitor.cacheStatus'), minWidth: 100, render: row => row.status || '-'},
    {key: 'description', title: $t('page.monitor.description'), minWidth: 200, render: row => row.description || '-'},
    {
      key: 'operate',
      title: $t('common.operate'),
      width: 220,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {
                  key: 'clear',
                  label: $t('page.monitor.clear'),
                  type: 'warning',
                  show: showClear.value && row.clearable,
                  loading: actionCode.value === `clear:${row.code}`,
                  confirmText: $t('page.monitor.clearConfirm', {name: row.name}),
                  onClick: () => handleClear(row.code)
                },
                {
                  key: 'refresh',
                  label: $t('page.monitor.refreshCache'),
                  type: 'primary',
                  show: showRefresh.value && row.refreshable,
                  loading: actionCode.value === `refresh:${row.code}`,
                  confirmText: $t('page.monitor.refreshConfirm', {name: row.name}),
                  onClick: () => handleRefresh(row.code)
                }
              ]}
          />
      )
    }
  ]
});

async function refreshRedisStatus() {
  const {data: status, error} = await fetchRedisStatus();
  if (!error) {
    redisStatus.value = status;
  }
}

async function handleClear(code: string) {
  actionCode.value = `clear:${code}`;
  const {error} = await fetchClearCache(code);
  actionCode.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

async function handleRefresh(code: string) {
  actionCode.value = `refresh:${code}`;
  const {error} = await fetchRefreshCache(code);
  actionCode.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

function resetFilters() {
  filters.value = {name: null, type: null, module: null};
  getData();
}

async function refreshAll() {
  await getData();
  await refreshRedisStatus();
}

onMounted(refreshAll);
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="filters">
      <NFormItemGi :label="$t('page.monitor.cacheName')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.name" clearable/>
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.cacheType')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.type" clearable/>
      </NFormItemGi>
      <NFormItemGi :label="$t('page.monitor.cacheModule')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.module" clearable/>
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:6">
        <NSpace class="w-full" justify="end">
          <NTag type="info">{{ $t('page.monitor.redisStatus') }}: {{ redisStatus }}</NTag>
          <NButton @click="resetFilters">
            <template #icon>
              <icon-ic-round-refresh class="text-icon"/>
            </template>
            {{ $t('common.reset') }}
          </NButton>
          <NButton ghost type="primary" @click="getData">
            <template #icon>
              <icon-ic-round-search class="text-icon"/>
            </template>
            {{ $t('common.search') }}
          </NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>
    <NCard :bordered="false" :title="$t('route.monitor_cache')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="refreshAll">
          <template #default><span class="hidden"></span></template>
        </TableHeaderOperation>
      </template>
      <NDataTable
          :columns="columns"
          :data="data"
          :flex-height="!appStore.isMobile"
          :loading="loading"
          :row-key="row => row.code"
          :scroll-x="scrollX"
          class="sm:h-full"
          size="small"
      />
    </NCard>
  </div>
</template>
