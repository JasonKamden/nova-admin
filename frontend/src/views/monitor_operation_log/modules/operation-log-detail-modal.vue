<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { fetchOperationLogDetail } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { formatDateTime } from '@/utils/date-time';
import LogCodeViewer from '@/components/business/log-code-viewer.vue';

defineOptions({ name: 'OperationLogDetailModal' });

const props = defineProps<{ logId: number | null }>();
const visible = defineModel<boolean>('visible', { default: false });
const appStore = useAppStore();
const loading = ref(false);
const detail = ref<Api.Monitor.OperationLogDetail | null>(null);
const drawerWidth = computed(() => (appStore.isMobile ? '100%' : 'min(75vw, 1280px)'));

watch(
  () => visible.value,
  async show => {
    if (!show || !props.logId) return;
    loading.value = true;
    const { data, error } = await fetchOperationLogDetail(props.logId);
    loading.value = false;
    if (!error) {
      detail.value = data;
    }
  }
);
</script>

<template>
  <NDrawer v-model:show="visible" :width="drawerWidth" display-directive="show">
    <NDrawerContent :native-scrollbar="false" :title="$t('page.monitor.detailTitle')" closable>
      <NSpin :show="loading">
        <div v-if="detail" class="flex-col-stretch gap-20px pb-8px">
          <section class="flex-col-stretch gap-12px">
            <div class="text-16px font-600">{{ $t('page.monitor.basicInfo') }}</div>
            <NDescriptions :column="appStore.isMobile ? 1 : 2" bordered label-placement="left">
              <NDescriptionsItem label="ID">{{ detail.basic.id }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.module')">{{ detail.basic.module || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.operationType')">{{ detail.basic.operationType || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.operator')">{{ detail.basic.username || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.requestIp')">{{ detail.basic.requestIp || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.durationMs')">{{ detail.basic.durationMs ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.operationTime')">
                {{ formatDateTime(detail.basic.operationTime) }}
              </NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.requestId')">{{ detail.basic.requestId || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.traceId')">{{ detail.basic.traceId || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.userAgent')">{{ detail.basic.userAgent || '-' }}</NDescriptionsItem>
            </NDescriptions>
          </section>

          <section class="flex-col-stretch gap-12px">
            <div class="text-16px font-600">{{ $t('page.monitor.requestInfo') }}</div>
            <NDescriptions :column="appStore.isMobile ? 1 : 2" bordered label-placement="left">
              <NDescriptionsItem :label="$t('page.monitor.requestMethod')">{{ detail.request.method || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.requestUri')">{{ detail.request.uri || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.contentType')">{{ detail.request.contentType || '-' }}</NDescriptionsItem>
            </NDescriptions>
            <div class="grid gap-12px md:grid-cols-2">
              <LogCodeViewer :code="detail.request.headers" :collapsed-height="220" />
              <LogCodeViewer :code="detail.request.queryParams" :collapsed-height="220" />
              <LogCodeViewer :code="detail.request.pathParams" :collapsed-height="220" />
              <LogCodeViewer :code="detail.request.body" :collapsed-height="220" />
            </div>
          </section>

          <section class="flex-col-stretch gap-12px">
            <div class="text-16px font-600">{{ $t('page.monitor.responseInfo') }}</div>
            <NDescriptions :column="appStore.isMobile ? 1 : 2" bordered label-placement="left">
              <NDescriptionsItem :label="$t('page.monitor.httpStatus')">{{ detail.response.httpStatus ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.businessCode')">{{ detail.response.businessCode ?? '-' }}</NDescriptionsItem>
            </NDescriptions>
            <LogCodeViewer :code="detail.response.body" />
          </section>

          <section class="flex-col-stretch gap-12px">
            <div class="text-16px font-600">{{ $t('page.monitor.exceptionInfo') }}</div>
            <NDescriptions :column="appStore.isMobile ? 1 : 2" bordered label-placement="left">
              <NDescriptionsItem :label="$t('page.monitor.exceptionType')">{{ detail.exception.type || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.errorCode')">{{ detail.exception.errorCode || '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.exceptionMessage')" :span="2">
                {{ detail.exception.message || '-' }}
              </NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.monitor.exceptionLocation')" :span="2">
                {{ detail.exception.location || '-' }}
              </NDescriptionsItem>
            </NDescriptions>
            <LogCodeViewer :code="detail.exception.stack" :language="detail.exception.stack ? 'text' : 'json'" />
          </section>
        </div>

        <NEmpty v-else :description="$t('common.noData')" class="py-32px" />
      </NSpin>
    </NDrawerContent>
  </NDrawer>
</template>
