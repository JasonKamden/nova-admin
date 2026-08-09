<script lang="ts" setup>
import {$t} from '@/locales';
import {fetchCustomBackendError} from '@/service-alova/api';

async function logout() {
  await fetchCustomBackendError('8888', $t('request.logoutMsg'));
}

async function logoutWithModal() {
  await fetchCustomBackendError('7777', $t('request.logoutWithModalMsg'));
}

async function refreshToken() {
  await fetchCustomBackendError('9999', $t('request.tokenExpired'));
}

async function handleRepeatedMessageError() {
  await Promise.all([
    fetchCustomBackendError('2222', $t('page.function.request.repeatedErrorMsg1')),
    fetchCustomBackendError('2222', $t('page.function.request.repeatedErrorMsg1')),
    fetchCustomBackendError('2222', $t('page.function.request.repeatedErrorMsg1')),
    fetchCustomBackendError('3333', $t('page.function.request.repeatedErrorMsg2')),
    fetchCustomBackendError('3333', $t('page.function.request.repeatedErrorMsg2')),
    fetchCustomBackendError('3333', $t('page.function.request.repeatedErrorMsg2'))
  ]);
}

async function handleRepeatedModalError() {
  await Promise.all([
    fetchCustomBackendError('7777', $t('request.logoutWithModalMsg')),
    fetchCustomBackendError('7777', $t('request.logoutWithModalMsg')),
    fetchCustomBackendError('7777', $t('request.logoutWithModalMsg'))
  ]);
}
</script>

<template>
  <NSpace :size="16" vertical>
    <NCard :bordered="false" :title="$t('request.logout')" class="card-wrapper" segmented size="small">
      <NButton @click="logout">{{ $t('common.trigger') }}</NButton>
    </NCard>
    <NCard :bordered="false" :title="$t('request.logoutWithModal')" class="card-wrapper" segmented size="small">
      <NButton @click="logoutWithModal">{{ $t('common.trigger') }}</NButton>
    </NCard>
    <NCard :bordered="false" :title="$t('request.refreshToken')" class="card-wrapper" segmented size="small">
      <NButton @click="refreshToken">{{ $t('common.trigger') }}</NButton>
    </NCard>
    <NCard
        :bordered="false"
        :title="$t('page.function.request.repeatedErrorOccurOnce')"
        class="card-wrapper"
        segmented
        size="small"
    >
      <NButton @click="handleRepeatedMessageError">{{ $t('page.function.request.repeatedError') }}(Message)</NButton>
      <NButton class="ml-12px" @click="handleRepeatedModalError">
        {{ $t('page.function.request.repeatedError') }}(Modal)
      </NButton>
    </NCard>
  </NSpace>
</template>

<style scoped></style>
