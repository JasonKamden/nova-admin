<script lang="tsx" setup>
import {computed, ref} from 'vue';
import dayjs from 'dayjs';
import {NButton, NPopconfirm} from 'naive-ui';
import {fetchKickOnlineUser, fetchOnlineUserList} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';

defineOptions({name: 'MonitorOnline'});

const appStore = useAppStore();
const {hasAuth} = useAuth();
const keyword = ref<string | null>(null);
const kickingId = ref<string | null>(null);
const showKick = computed(() => hasAuth('monitor:online:kick'));

const {columns, columnChecks, data, loading, getData, scrollX} = useNaiveTable({
    api: () => fetchOnlineUserList(keyword.value),
    transform: response => {
        const {data: rows, error} = response;
        return error ? [] : rows;
    },
    columns: () => [
        {key: 'username', title: $t('page.monitor.username'), minWidth: 140},
        {key: 'contextType', title: 'Context', width: 100, align: 'center'},
        {key: 'tenantName', title: $t('page.monitor.tenantName'), minWidth: 160, render: row => row.tenantName || '-'},
        {key: 'departmentName', title: $t('page.monitor.departmentName'), minWidth: 160, render: row => row.departmentName || '-'},
        {key: 'ip', title: $t('page.monitor.ip'), minWidth: 140, render: row => row.ip || '-'},
        {key: 'userAgent', title: $t('page.monitor.userAgent'), minWidth: 240, render: row => row.userAgent || '-'},
        {key: 'loginTime', title: $t('page.monitor.loginTime'), minWidth: 180, render: row => dayjs(row.loginTime).format('YYYY-MM-DD HH:mm:ss')},
        {key: 'lastActivityTime', title: $t('page.monitor.lastActivityTime'), minWidth: 180, render: row => dayjs(row.lastActivityTime).format('YYYY-MM-DD HH:mm:ss')},
        {
            key: 'operate',
            title: $t('common.operate'),
            width: 140,
            align: 'center',
            render: row =>
                showKick.value ? (
                    <NPopconfirm onPositiveClick={() => handleKick(row.sessionId, row.username)}>
                        {{
                            default: () => $t('page.monitor.kickConfirm', {name: row.username}),
                            trigger: () => (
                                <NButton ghost size="small" type="error" loading={kickingId.value === row.sessionId}>
                                    {$t('common.delete')}
                                </NButton>
                            )
                        }}
                    </NPopconfirm>
                ) : null
        }
    ]
});

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
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :label-width="92" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.monitor.keyword')" span="24 s:16 m:8">
            <NInput v-model:value="keyword" :placeholder="$t('common.keywordSearch')" clearable />
          </NFormItemGi>
          <NFormItemGi span="24 m:16">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getData">{{ $t('common.search') }}</NButton>
              <NButton @click="keyword = null; getData()">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>
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
