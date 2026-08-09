<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue';
import dayjs from 'dayjs';
import {messageTypeOptions, readStatusRecord} from '@/constants/business';
import {$t} from '@/locales';
import {fetchMessageCenterPage} from '@/service/api';
import {useMessageStore} from '@/store/modules/message';

defineOptions({
  name: 'MessageCenterPanel'
});

const emit = defineEmits<{
  openDetail: [messageId: number];
  updated: [];
}>();

const messageStore = useMessageStore();

const activeTab = ref<'all' | 'unread' | 'read'>('all');
const pageNum = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const rows = ref<Api.MessageCenter.Item[]>([]);
const total = ref(0);

const readStatus = computed<Api.MessageCenter.ReadStatus | null>(() => {
  if (activeTab.value === 'unread') {
    return 0;
  }

  if (activeTab.value === 'read') {
    return 1;
  }

  return null;
});

async function loadData() {
  loading.value = true;

  try {
    const {data, error} = await fetchMessageCenterPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      readStatus: readStatus.value
    });

    if (!error) {
      rows.value = data.records;
      total.value = data.total;
    }
  } finally {
    loading.value = false;
  }
}

function getMessageTypeLabel(type: Api.Message.MessageType) {
  return $t(messageTypeOptions.find(item => item.value === type)?.label || 'common.noData');
}

async function handleReadAll() {
  await messageStore.readAllMessages();
  await Promise.all([loadData(), messageStore.refreshRecent()]);
  emit('updated');
}

function handleOpenDetail(messageId: number) {
  emit('openDetail', messageId);
}

watch(
    () => activeTab.value,
    async () => {
      pageNum.value = 1;
      await loadData();
    }
);

watch(
    () => messageStore.unreadCount,
    async (value, previousValue) => {
      if (previousValue === undefined || value === previousValue) {
        return;
      }

      await loadData();
    }
);

onMounted(() => {
  void loadData();
});

defineExpose({
  loadData
});
</script>

<template>
  <div class="mb-16px flex flex-wrap items-center justify-between gap-12px">
    <div>
      <div class="text-16px font-600">{{ $t('page.messageCenter.title') }}</div>
      <div class="mt-4px text-13px text-#666">{{ $t('page.messageCenter.subtitle') }}</div>
    </div>
    <NSpace>
      <NButton @click="loadData">
        <template #icon>
          <icon-mdi-refresh class="text-icon"/>
        </template>
        {{ $t('common.refresh') }}
      </NButton>
      <NButton ghost type="primary" @click="handleReadAll">
        <template #icon>
          <icon-material-symbols-drafts-outline-rounded class="text-icon"/>
        </template>
        {{ $t('common.readAll') }}
      </NButton>
    </NSpace>
  </div>

  <NTabs v-model:value="activeTab" animated type="segment">
    <NTabPane :tab="$t('page.messageCenter.all')" name="all"/>
    <NTabPane :tab="`${$t('page.messageCenter.unread')} (${messageStore.unreadCount})`" name="unread"/>
    <NTabPane :tab="$t('page.messageCenter.read')" name="read"/>
  </NTabs>

  <NSpin :show="loading">
    <div v-if="rows.length" class="mt-16px flex-col-stretch gap-12px">
      <div
          v-for="item in rows"
          :key="item.messageId"
          class="cursor-pointer rounded-14px border border-solid border-#edf1f7 bg-white px-16px py-14px transition-all hover:border-primary hover:shadow-sm"
          @click="handleOpenDetail(item.messageId)"
      >
        <div class="flex flex-wrap items-start justify-between gap-12px">
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-8px">
              <NTag :type="item.readStatus === 0 ? 'warning' : 'success'" size="small">
                {{ $t(readStatusRecord[item.readStatus].label) }}
              </NTag>
              <NTag size="small" type="info">{{ getMessageTypeLabel(item.messageType) }}</NTag>
            </div>
            <div class="mt-10px truncate text-15px font-600">{{ item.title }}</div>
            <div class="mt-6px line-clamp-2 text-13px text-#666">{{ item.summary || '-' }}</div>
          </div>
          <div class="text-right text-12px text-#8a94a6">
            <div>{{ item.sendTime ? dayjs(item.sendTime).format('YYYY-MM-DD HH:mm') : '-' }}</div>
            <div class="mt-20px text-primary">{{ $t('page.messageCenter.openDetail') }}</div>
          </div>
        </div>
      </div>
    </div>
    <NEmpty v-else :description="$t('page.messageCenter.empty')" class="py-48px"/>
  </NSpin>

  <div class="mt-16px flex justify-end">
    <NPagination
        v-model:page="pageNum"
        v-model:page-size="pageSize"
        :item-count="total"
        :page-sizes="[10, 20, 50]"
        show-size-picker
        @update:page="loadData"
        @update:page-size="loadData"
    />
  </div>
</template>
