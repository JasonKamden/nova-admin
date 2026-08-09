<script lang="tsx" setup>
import {computed, onMounted, ref} from 'vue';
import {NButton, NPopconfirm, NSpace, NTag} from 'naive-ui';
import {fetchCacheList, fetchClearCache, fetchRefreshCache, fetchRedisStatus} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';

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
                <NSpace justify="center" size={8}>
                    {showClear.value && row.clearable ? (
                        <NPopconfirm onPositiveClick={() => handleClear(row.code)}>
                            {{
                                default: () => $t('page.monitor.clearConfirm', {name: row.name}),
                                trigger: () => (
                                    <NButton
                                        ghost
                                        size="small"
                                        type="warning"
                                        loading={actionCode.value === `clear:${row.code}`}
                                    >
                                        {$t('page.monitor.clear')}
                                    </NButton>
                                )
                            }}
                        </NPopconfirm>
                    ) : null}
                    {showRefresh.value && row.refreshable ? (
                        <NPopconfirm onPositiveClick={() => handleRefresh(row.code)}>
                            {{
                                default: () => $t('page.monitor.refreshConfirm', {name: row.name}),
                                trigger: () => (
                                    <NButton
                                        ghost
                                        size="small"
                                        type="primary"
                                        loading={actionCode.value === `refresh:${row.code}`}
                                    >
                                        {$t('page.monitor.refreshCache')}
                                    </NButton>
                                )
                            }}
                        </NPopconfirm>
                    ) : null}
                </NSpace>
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
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :label-width="92" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.monitor.cacheName')" span="24 s:12 m:6"><NInput v-model:value="filters.name" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.cacheType')" span="24 s:12 m:6"><NInput v-model:value="filters.type" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.monitor.cacheModule')" span="24 s:12 m:6"><NInput v-model:value="filters.module" clearable /></NFormItemGi>
          <NFormItemGi span="24 m:6">
            <NSpace class="w-full" justify="end">
              <NTag type="info">{{ $t('page.monitor.redisStatus') }}: {{ redisStatus }}</NTag>
              <NButton type="primary" @click="getData">{{ $t('common.search') }}</NButton>
              <NButton @click="resetFilters">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>
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
