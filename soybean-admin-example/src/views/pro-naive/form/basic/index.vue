<script lang="ts" setup>
import {ref} from 'vue';
import {useMessage} from 'naive-ui';
import {createProForm, zhCN} from 'pro-naive-ui';
import {$t} from '@/locales';
import ConfigProvider from '../../ConfigProvider.vue';

const submiting = ref(false);
const message = useMessage();

const form = createProForm({
  initialValues: {
    attributes: [
      {
        name: $t('page.proNaive.form.basic.color'),
        items: [
          {name: $t('page.proNaive.form.basic.specificationColorRed')},
          {name: $t('page.proNaive.form.basic.specificationColorOrange')}
        ]
      }
    ]
  },
  onReset: () => {
    message.success('reset success');
  },
  onSubmit: async values => {
    submiting.value = true;
    await delay(1000);
    message.success(JSON.stringify(values));
    submiting.value = false;
  }
});

function delay(time: number) {
  return new Promise<void>(resolve => {
    setTimeout(resolve, time);
  });
}

function fillValues() {
  const values = {
    appName: $t('page.proNaive.form.basic.appName'),
    appStatus: 0,
    responseDate: Date.now()
  };
  // 方式一：直接修改 form.values.value
  // form.values.value.appName = '应用名称';
  // form.values.value.appStatus = 0;
  // form.values.value.responseDate = Date.now();

  // 方式二：使用 Object.assign
  Object.assign(form.values.value, values);

  // 方式三：直接重写 form.values.value
  // form.values.value = {...}
}
</script>

<template>
  <ConfigProvider :locale="zhCN">
    <ProForm
        :form="form"
        :loading="submiting"
        :rules="{
        appName: {
          required: true
        }
      }"
    >
      <ProCard :show-collapse="false" :title="$t('page.proNaive.form.basic.title')">
        <template #header-extra>
          <NFlex>
            <NButton @click="fillValues">{{ $t('page.proNaive.form.basic.fillValue') }}</NButton>
            <NButton attr-type="reset">{{ $t('page.proNaive.form.basic.reset') }}</NButton>
            <NButton :loading="submiting" attr-type="submit" type="primary">
              {{ $t('page.proNaive.form.basic.submit') }}
            </NButton>
          </NFlex>
        </template>
        <NGrid :x-gap="16" cols="1 s:2 l:3" responsive="screen">
          <NGi>
            <ProInput
                :title="$t('page.proNaive.form.basic.appName')"
                :tooltip="$t('page.proNaive.form.basic.appName')"
                path="appName"
            />
          </NGi>
          <NGi>
            <ProSelect
                :field-props="{
                options: [
                  { label: $t('page.proNaive.form.basic.normal'), value: 0 },
                  { label: $t('page.proNaive.form.basic.anomaly'), value: 1 }
                ]
              }"
                :title="$t('page.proNaive.form.basic.appStatus')"
                path="appStatus"
            />
          </NGi>
          <NGi>
            <ProDate :title="$t('page.proNaive.form.basic.createTime')" path="createTime"/>
          </NGi>
          <NGi>
            <ProDate :title="$t('page.proNaive.form.basic.responseDate')" path="responseDate" required/>
          </NGi>
          <NGi :span="3">
            <ProFormList
                :copy-button-props="false"
                :creator-button-props="{
                content: $t('page.proNaive.form.basic.addSpecificateItem')
              }"
                :creator-initial-value="
                () => ({
                  name: $t('page.proNaive.form.basic.color'),
                  items: [
                    { name: $t('page.proNaive.form.basic.specificationColorRed') },
                    { name: $t('page.proNaive.form.basic.specificationColorOrange') }
                  ]
                })
              "
                :min="1"
                :title="$t('page.proNaive.form.basic.specificationInfo')"
                path="attributes"
            >
              <template #item="{ index, itemDom, actionDom }">
                <NCard
                    :title="`${$t('page.proNaive.form.basic.specificate')}${index + 1}`"
                    class="mb-8px"
                    embedded
                    size="small"
                >
                  <template #header-extra>
                    <component :is="actionDom"/>
                  </template>
                  <component :is="itemDom"/>
                </NCard>
              </template>
              <ProInput
                  :field-props="{
                  class: 'w-230px!'
                }"
                  :title="$t('page.proNaive.form.basic.specificationName')"
                  path="name"
              />
              <ProFormList
                  :copy-button-props="false"
                  :creator-button-props="{
                  dashed: false,
                  block: false,
                  renderIcon: undefined,
                  ghost: true,
                  text: true,
                  type: 'info',
                  content: $t('page.proNaive.form.basic.add')
                }"
                  :min="1"
                  :remove-button-props="{
                  tooltip: $t('page.proNaive.form.basic.delete')
                }"
                  :title="$t('page.proNaive.form.basic.specificationValue')"
                  path="items"
              >
                <template #item="{ itemDom, actionDom }">
                  <div class="me-25px inline-flex">
                    <component :is="itemDom"/>
                    <div class="ml-8px">
                      <component :is="actionDom"/>
                    </div>
                  </div>
                </template>
                <ProInput
                    :field-props="{
                    class: 'w-104px!'
                  }"
                    path="name"
                />
              </ProFormList>
            </ProFormList>
          </NGi>
        </NGrid>
      </ProCard>
    </ProForm>
  </ConfigProvider>
</template>
