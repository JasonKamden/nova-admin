<script lang="tsx" setup>
import {ref, watch} from 'vue';
import dayjs from 'dayjs';
import {NTag} from 'naive-ui';
import {fetchMessageRecipients} from '@/service/api';
import {readStatusOptions, readStatusRecord} from '@/constants/business';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';

defineOptions({name: 'MessageRecipientsModal'});

interface Props {
    messageId: number | null;
    title: string;
}

const props = defineProps<Props>();
const visible = defineModel<boolean>('visible', {default: false});

const searchParams = ref<Api.Message.RecipientPageParams>({
    pageNum: 1,
    pageSize: 10,
    user: null,
    departmentId: null,
    readStatus: null
});

const {columns, data, loading, getDataByPage, mobilePagination} = useNaivePaginatedTable({
    api: () => {
        if (!props.messageId) {
            return Promise.resolve({data: {records: [], total: 0, pageNum: 1, pageSize: 10}, error: null} as any);
        }
        return fetchMessageRecipients(props.messageId, searchParams.value);
    },
    transform: response => defaultTransform(response),
    onPaginationParamsChange: params => {
        searchParams.value.pageNum = params.page || 1;
        searchParams.value.pageSize = params.pageSize || 10;
    },
    columns: () => [
        {key: 'username', title: $t('page.user.username'), minWidth: 140},
        {key: 'nickname', title: $t('page.user.nickname'), minWidth: 140},
        {key: 'departmentName', title: $t('page.user.department'), minWidth: 160, render: row => row.departmentName || '-'},
        {
            key: 'readStatus',
            title: $t('page.message.status'),
            width: 100,
            align: 'center',
            render: row => {
                const config = readStatusRecord[row.readStatus as keyof typeof readStatusRecord];
                return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
            }
        },
        {key: 'receiveTime', title: $t('page.message.createTime'), minWidth: 180, render: row => (row.receiveTime ? dayjs(row.receiveTime).format('YYYY-MM-DD HH:mm:ss') : '-')},
        {key: 'readTime', title: $t('page.message.sendTime'), minWidth: 180, render: row => (row.readTime ? dayjs(row.readTime).format('YYYY-MM-DD HH:mm:ss') : '-')}
    ]
});

watch(
    () => visible.value,
    show => {
        if (show) {
            searchParams.value.pageNum = 1;
            searchParams.value.pageSize = 10;
            searchParams.value.user = null;
            searchParams.value.departmentId = null;
            searchParams.value.readStatus = null;
            getDataByPage(1);
        }
    }
);
</script>

<template>
  <NModal v-model:show="visible" preset="card" class="w-900px" :mask-closable="false">
    <template #header><div class="text-16px font-600">{{ $t('page.message.recipientTitle') }} - {{ title }}</div></template>
    <div class="mb-16px">
      <NForm :model="searchParams" :label-width="80" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.user.username')" span="24 s:12 m:8"><NInput v-model:value="searchParams.user" clearable /></NFormItemGi>
          <NFormItemGi :label="$t('page.message.status')" span="24 s:12 m:6">
            <NSelect v-model:value="searchParams.readStatus" :options="readStatusOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable />
          </NFormItemGi>
          <NFormItemGi span="24 m:10">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getDataByPage(1)">{{ $t('common.search') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </div>
    <NDataTable
      :columns="columns"
      :data="data"
      :loading="loading"
      :pagination="mobilePagination"
      :row-key="row => row.userId"
      remote
      size="small"
    />
  </NModal>
</template>
