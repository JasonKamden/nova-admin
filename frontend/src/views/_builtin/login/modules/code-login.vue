<script lang="ts" setup>
import {computed, reactive} from 'vue';
import {useRouterPush} from '@/hooks/common/router';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {useCaptcha} from '@/hooks/business/captcha';
import {$t} from '@/locales';

defineOptions({
  name: 'CodeLogin'
});

const {toggleLoginModule} = useRouterPush();
const {formRef, validate} = useNaiveForm();
const {label, isCounting, loading, getCaptcha} = useCaptcha();

interface FormModel {
  phone: string;
  code: string;
}

const model: FormModel = reactive({
  phone: '',
  code: ''
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  const {formRules} = useFormRules();

  return {
    phone: formRules.phone,
    code: formRules.code
  };
});

async function handleSubmit() {
  await validate();
  // request
  window.$message?.success($t('page.login.common.validateSuccess'));
}
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" :show-label="false" size="large" @keyup.enter="handleSubmit">
    <NFormItem path="phone">
      <NInput v-model:value="model.phone" :placeholder="$t('page.login.common.phonePlaceholder')"/>
    </NFormItem>
    <NFormItem path="code">
      <div class="w-full flex-y-center gap-16px">
        <NInput v-model:value="model.code" :placeholder="$t('page.login.common.codePlaceholder')"/>
        <NButton :disabled="isCounting" :loading="loading" size="large" @click="getCaptcha(model.phone)">
          {{ label }}
        </NButton>
      </div>
    </NFormItem>
    <NSpace :size="18" class="w-full" vertical>
      <NButton block round size="large" type="primary" @click="handleSubmit">
        {{ $t('common.confirm') }}
      </NButton>
      <NButton block round size="large" @click="toggleLoginModule('pwd-login')">
        {{ $t('page.login.common.back') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped></style>
