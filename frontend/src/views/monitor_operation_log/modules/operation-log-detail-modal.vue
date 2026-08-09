<script lang="ts" setup>
import {ref, watch} from 'vue';
import {fetchOperationLogDetail} from '@/service/api';
import {$t} from '@/locales';

defineOptions({name: 'OperationLogDetailModal'});

const props = defineProps<{ logId: number | null }>();
const visible = defineModel<boolean>('visible', {default: false});
const loading = ref(false);
const detail = ref<Api.Monitor.OperationLogDetail | null>(null);

watch(
    () => visible.value,
    async show => {
      if (!show || !props.logId) return;
      loading.value = true;
      const {data, error} = await fetchOperationLogDetail(props.logId);
      loading.value = false;
      if (!error) {
        detail.value = data;
      }
    }
);
</script>

<template>
  <NModal v-model:show="visible" :mask-closable="false" class="w-980px" preset="card">
    <template #header>
      <div class="text-16px font-600">{{ $t('page.monitor.detailTitle') }}</div>
    </template>
    <NSpin :show="loading">
      <template v-if="detail">
        <NDescriptions :column="2" bordered class="mb-16px">
          <NDescriptionsItem label="ID">{{ detail.basic.id }}</NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.monitor.module')">{{ detail.basic.module }}</NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.monitor.operationType')">{{
              detail.basic.operationType
            }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.monitor.operator')">{{ detail.basic.username || '-' }}</NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.monitor.requestIp')">{{
              detail.basic.requestIp || '-'
            }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.monitor.durationMs')">{{
              detail.basic.durationMs ?? '-'
            }}
          </NDescriptionsItem>
        </NDescriptions>
        <NCollapse :default-expanded-names="['request', 'response', 'exception']">
          <NCollapseItem name="request" title="Request">
            <NCode :code="JSON.stringify(detail.request, null, 2)" language="json" word-wrap/>
          </NCollapseItem>
          <NCollapseItem name="response" title="Response">
            <NCode :code="JSON.stringify(detail.response, null, 2)" language="json" word-wrap/>
          </NCollapseItem>
          <NCollapseItem name="exception" title="Exception">
            <NCode :code="JSON.stringify(detail.exception, null, 2)" language="json" word-wrap/>
          </NCollapseItem>
        </NCollapse>
      </template>
    </NSpin>
  </NModal>
</template>
