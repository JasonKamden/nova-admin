<script lang="ts" setup>
import {computed, ref} from 'vue';
import {useMessage} from 'naive-ui';
import type {ProSearchFormColumns} from 'pro-naive-ui';
import {createProSearchForm} from 'pro-naive-ui';
import {$t} from '@/locales';
import ConfigProvider from '../../ConfigProvider.vue';

interface Info {
  appName: string;
  appStatus: string;
  createTime: number;
  responseDate: number;
  endTime: number;
}

const loading = ref(false);
const loading2 = ref(false);
const message = useMessage();

const form = createProSearchForm<Partial<Info>>({
  onReset: () => {
    message.success('reset success');
  },
  onSubmit: async values => {
    message.success(JSON.stringify(values));
    loading.value = true;
    await delay(1500);
    loading.value = false;
  }
});

const form2 = createProSearchForm<Partial<Info>>({
  defaultCollapsed: true,
  onReset: () => {
    message.success('reset success');
  },
  onSubmit: async values => {
    message.success(JSON.stringify(values));
    loading2.value = true;
    await delay(1500);
    loading2.value = false;
  }
});

const columns = computed<ProSearchFormColumns<Info>>(() => {
  return [
    {
      title: $t('page.proNaive.form.query.appName'),
      path: 'appName'
    },
    {
      title: $t('page.proNaive.form.query.createTime'),
      path: 'createTime',
      field: 'date'
    },
    {
      title: $t('page.proNaive.form.query.appStatus'),
      path: 'appStatus'
    },
    {
      title: $t('page.proNaive.form.query.responseDate'),
      path: 'responseDate',
      field: 'date-time'
    },
    {
      title: $t('page.proNaive.form.query.endDate'),
      path: 'endTime',
      field: 'date'
    }
  ];
});

const columns2 = computed(() => {
  return Array.from({length: 20}, (_, i) => ({
    title: `${$t('page.proNaive.form.query.field')}${i}`,
    path: `field${i}`
  }));
});

function delay(time: number) {
  return new Promise<void>(resolve => {
    setTimeout(resolve, time);
  });
}
</script>

<template>
  <ConfigProvider>
    <div class="bg-#fff">
      <ProCard :show-collapse="false" :title="$t('page.proNaive.form.query.title1')">
        <ProSearchForm :columns="columns" :form="form" :loading="loading"/>
      </ProCard>
      <ProCard :show-collapse="false" :title="$t('page.proNaive.form.query.title2')" class="mt-12px">
        <ProSearchForm :collapsed-rows="2" :columns="columns2" :form="form2" :loading="loading2"/>
      </ProCard>
    </div>
  </ConfigProvider>
</template>
