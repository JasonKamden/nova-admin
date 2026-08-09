<script lang="ts" setup>
import {computed, ref} from 'vue';
import {messageTypeOptions} from '@/constants/business';
import {useRouterPush} from '@/hooks/common/router';
import {$t} from '@/locales';
import {useMessageStore} from '@/store/modules/message';
import {formatMonthDayTime} from '@/utils/date-time';
import MessageDetailDrawer from './message-detail-drawer.vue';

defineOptions({
  name: 'MessageBell'
});

const messageStore = useMessageStore();
const {routerPushByKey} = useRouterPush();

const popoverVisible = ref(false);
const detailVisible = ref(false);
const activeMessageId = ref<number | null>(null);

const tooltipContent = computed(() => {
  if (messageStore.unreadCount <= 0) {
    return $t('page.messageCenter.empty');
  }

  return `当前有 ${messageStore.unreadCount} 条未读消息`;
});

function getMessageTypeLabel(type: Api.Message.MessageType) {
  return $t(messageTypeOptions.find(item => item.value === type)?.label || 'common.noData');
}

async function handlePopoverShow(show: boolean) {
  popoverVisible.value = show;

  if (show) {
    await messageStore.initialize();
    await messageStore.refreshRecent();
  }
}

function openDetail(messageId: number) {
  activeMessageId.value = messageId;
  detailVisible.value = true;
}

async function handleReadAll() {
  await messageStore.readAllMessages();
}

async function handleDetailUpdated() {
  await Promise.all([messageStore.refreshRecent(), messageStore.refreshUnreadCount()]);
}

async function openMessageCenterPage() {
  popoverVisible.value = false;
  await routerPushByKey('message_center');
}
</script>

<template>
  <NPopover :show="popoverVisible" placement="bottom-end" trigger="click" @update:show="handlePopoverShow">
    <template #trigger>
      <NTooltip :disabled="!messageStore.badgeValue" placement="bottom">
        <template #trigger>
          <NBadge :offset="[-2, 2]" :show="Boolean(messageStore.badgeValue)" :value="messageStore.badgeValue">
            <ButtonIcon :tooltip-content="$t('page.messageCenter.title')">
              <SvgIcon class="text-icon-large" icon="ph:bell-ringing" />
            </ButtonIcon>
          </NBadge>
        </template>
        {{ tooltipContent }}
      </NTooltip>
    </template>

    <div class="w-380px">
      <div class="mb-12px flex items-center justify-between gap-12px">
        <div class="text-15px font-600">{{ $t('page.messageCenter.title') }}</div>
        <NSpace :size="8">
          <NButton quaternary size="tiny" @click="messageStore.refreshRecent()">
            {{ $t('common.refresh') }}
          </NButton>
          <NButton quaternary size="tiny" type="primary" @click="handleReadAll">
            {{ $t('common.readAll') }}
          </NButton>
        </NSpace>
      </div>

      <div class="mb-12px text-12px text-#666">{{ $t('page.messageCenter.latest') }}</div>

      <div v-if="messageStore.recentMessages.length" class="flex-col-stretch gap-10px">
        <div
          v-for="item in messageStore.recentMessages"
          :key="item.messageId"
          class="cursor-pointer rounded-12px bg-#f7f9fc px-12px py-10px transition-colors hover:bg-#eef3fb"
          @click="openDetail(item.messageId)"
        >
          <div class="flex items-center justify-between gap-12px">
            <div class="min-w-0 flex-1">
              <div class="flex items-center gap-8px">
                <span
                  v-if="item.readStatus === 0"
                  class="inline-block size-8px rounded-full bg-warning"
                ></span>
                <span class="truncate text-14px font-600">{{ item.title }}</span>
              </div>
              <div class="mt-6px line-clamp-2 text-12px text-#666">{{ item.summary || '-' }}</div>
            </div>
            <div class="shrink-0 text-right text-11px text-#8a94a6">
              <div>{{ getMessageTypeLabel(item.messageType) }}</div>
              <div class="mt-6px">{{ formatMonthDayTime(item.sendTime) }}</div>
            </div>
          </div>
        </div>
      </div>
      <NEmpty v-else :description="$t('page.messageCenter.empty')" class="py-18px" />

      <div class="mt-14px flex justify-end">
        <NButton text type="primary" @click="openMessageCenterPage">
          {{ $t('common.viewAll') }}
        </NButton>
      </div>
    </div>
  </NPopover>

  <MessageDetailDrawer v-model:visible="detailVisible" :message-id="activeMessageId" @updated="handleDetailUpdated" />
</template>
