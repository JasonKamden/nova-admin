<script lang="tsx" setup>
import {computed, ref} from 'vue';
import dayjs from 'dayjs';
import {NButton, NSpace, NTag} from 'naive-ui';
import {
  fetchDeleteMessage,
  fetchMessagePage,
  fetchPreviewMessageRecipients,
  fetchSendMessage,
  fetchWithdrawMessage
} from '@/service/api';
import {
  messageStatusOptions,
  messageStatusRecord,
  messageTypeOptions,
  recipientTypeOptions
} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import MessageOperateModal from './modules/message-operate-modal.vue';
import MessageRecipientsModal from './modules/message-recipients-modal.vue';

defineOptions({name: 'SystemMessage'});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const {hasAuth} = useAuth();
const searchParams = ref<Api.Message.PageParams>({
  pageNum: 1,
  pageSize: 10,
  title: null,
  messageType: null,
  status: null,
  creator: null,
  startTime: null,
  endTime: null
});

const modalVisible = ref(false);
const recipientsModalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeMessageId = ref<number | null>(null);
const activeMessageName = ref('');
const previewLoadingId = ref<number | null>(null);
const sendLoadingId = ref<number | null>(null);
const withdrawLoadingId = ref<number | null>(null);
const deleteLoadingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('system:message:add'));
const showUpdate = computed(() => hasAuth('system:message:update'));
const showDelete = computed(() => hasAuth('system:message:delete'));
const showSend = computed(() => hasAuth('system:message:send'));
const showWithdraw = computed(() => hasAuth('system:message:withdraw'));
const showRecipient = computed(() => hasAuth('system:message:read-status'));

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => fetchMessagePage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {key: 'title', title: $t('page.message.title'), minWidth: 180},
    {
      key: 'messageType',
      title: $t('page.message.messageType'),
      minWidth: 110,
      render: row => $t(messageTypeOptions.find(item => item.value === row.messageType)?.label || 'common.noData')
    },
    {
      key: 'recipientType',
      title: $t('page.message.recipientType'),
      minWidth: 120,
      render: row => $t(recipientTypeOptions.find(item => item.value === row.recipientType)?.label || 'common.noData')
    },
    {
      key: 'status',
      title: $t('page.message.status'),
      width: 100,
      align: 'center',
      render: row => {
        const config = messageStatusRecord[row.status as keyof typeof messageStatusRecord];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {key: 'recipientCount', title: $t('page.message.recipientCount'), width: 100, align: 'center'},
    {key: 'readCount', title: $t('page.message.readCount'), width: 100, align: 'center'},
    {
      key: 'readRate',
      title: $t('page.message.readRate'),
      width: 100,
      align: 'center',
      render: row => `${Number(row.readRate || 0).toFixed(2)}%`
    },
    {
      key: 'createTime',
      title: $t('page.message.createTime'),
      minWidth: 180,
      render: row => dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      key: 'sendTime',
      title: $t('page.message.sendTime'),
      minWidth: 180,
      render: row => (row.sendTime ? dayjs(row.sendTime).format('YYYY-MM-DD HH:mm:ss') : '-')
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      width: 260,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {
                  key: 'detail',
                  label: $t('common.detail'),
                  type: 'info',
                  onClick: () => openModal('detail', row.id, row.title)
                },
                {
                  key: 'edit',
                  label: $t('common.edit'),
                  type: 'primary',
                  show: showUpdate.value && row.status === 'DRAFT',
                  onClick: () => openModal('edit', row.id, row.title)
                },
                {
                  key: 'preview',
                  label: $t('page.message.previewRecipients'),
                  type: 'primary',
                  show: showSend.value && row.status === 'DRAFT',
                  loading: previewLoadingId.value === row.id,
                  onClick: () => handlePreview(row.id)
                },
                {
                  key: 'send',
                  label: $t('page.message.send'),
                  type: 'success',
                  show: showSend.value && row.status === 'DRAFT',
                  loading: sendLoadingId.value === row.id,
                  confirmText: $t('page.message.sendConfirm', {name: row.title}),
                  onClick: () => handleSend(row.id)
                },
                {
                  key: 'withdraw',
                  label: $t('page.message.withdraw'),
                  type: 'warning',
                  show: showWithdraw.value && row.status === 'SENT',
                  loading: withdrawLoadingId.value === row.id,
                  confirmText: $t('page.message.withdrawConfirm', {name: row.title}),
                  onClick: () => handleWithdraw(row.id)
                },
                {
                  key: 'recipient',
                  label: $t('page.message.recipientDetail'),
                  type: 'primary',
                  show: showRecipient.value && row.status !== 'DRAFT',
                  onClick: () => openRecipientsModal(row.id, row.title)
                },
                {
                  key: 'delete',
                  label: $t('common.delete'),
                  type: 'error',
                  show: showDelete.value && row.status === 'DRAFT',
                  loading: deleteLoadingId.value === row.id,
                  confirmText: $t('page.message.deleteConfirm', {name: row.title}),
                  onClick: () => handleDelete(row.id)
                }
              ]}
          />
      )
    }
  ]
});

function openModal(mode: OperateMode, id?: number, title?: string) {
  operateMode.value = mode;
  activeMessageId.value = id || null;
  activeMessageName.value = title || '';
  modalVisible.value = true;
}

function openRecipientsModal(id: number, title: string) {
  activeMessageId.value = id;
  activeMessageName.value = title;
  recipientsModalVisible.value = true;
}

function handleReset() {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
    title: null,
    messageType: null,
    status: null,
    creator: null,
    startTime: null,
    endTime: null
  };
  getDataByPage(1);
}

async function handlePreview(id: number) {
  previewLoadingId.value = id;
  const {data: summary, error} = await fetchPreviewMessageRecipients(id);
  previewLoadingId.value = null;
  if (!error) {
    window.$dialog?.info({
      title: $t('page.message.previewRecipients'),
      content: `total=${summary.total}, read=${summary.read}, unread=${summary.unread}, rate=${Number(summary.readRate).toFixed(2)}%`,
      positiveText: $t('common.confirm')
    });
  }
}

async function handleSend(id: number) {
  previewLoadingId.value = id;
  const row = data.value.find(item => item.id === id);
  const {data: summary, error} = await fetchPreviewMessageRecipients(id);
  previewLoadingId.value = null;

  if (error || !row) {
    return;
  }

  window.$dialog?.info({
    title: $t('page.message.sendPreviewTitle'),
    positiveText: $t('page.message.confirmSend'),
    negativeText: $t('common.cancel'),
    content: `${row.title}\n${$t('page.message.messageType')}: ${
      $t(messageTypeOptions.find(item => item.value === row.messageType)?.label || 'common.noData')
    }\n${$t('page.message.recipientType')}: ${
      $t(recipientTypeOptions.find(item => item.value === row.recipientType)?.label || 'common.noData')
    }\n${$t('page.message.expectedRecipients', {count: summary.total})}`,
    async onPositiveClick() {
      sendLoadingId.value = id;
      const {error: sendError} = await fetchSendMessage(id);
      sendLoadingId.value = null;
      if (!sendError) {
        window.$message?.success($t('common.updateSuccess'));
        await getData();
      }
    }
  });
}

async function handleWithdraw(id: number) {
  withdrawLoadingId.value = id;
  const {error} = await fetchWithdrawMessage(id);
  withdrawLoadingId.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

async function handleDelete(id: number) {
  deleteLoadingId.value = id;
  const {error} = await fetchDeleteMessage(id);
  deleteLoadingId.value = null;
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.message.title')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.title" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.message.messageType')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.messageType"
          :options="messageTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable
        />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.message.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="messageStatusOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable
        />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.message.creator')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.creator" clearable />
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

    <NCard :bordered="false" :title="$t('route.system_message')" class="card-wrapper sm:flex-1-hidden" size="small">
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
        :scroll-x="2300"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>

    <MessageOperateModal
      v-model:visible="modalVisible"
      :message-id="activeMessageId"
      :mode="operateMode"
      @submitted="getData"
    />
    <MessageRecipientsModal
      v-model:visible="recipientsModalVisible"
      :message-id="activeMessageId"
      :title="activeMessageName"
    />
  </div>
</template>
