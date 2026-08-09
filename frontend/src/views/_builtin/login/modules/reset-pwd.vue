<script lang="ts" setup>
import {computed, reactive} from 'vue';
import {useRouterPush} from '@/hooks/common/router';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';

defineOptions({
  name: 'ResetPwd'
});

const {toggleLoginModule} = useRouterPush();
const {formRef, validate} = useNaiveForm();

interface FormModel {
  phone: string;
  code: string;
  password: string;
  confirmPassword: string;
}

const model: FormModel = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
});

type RuleRecord = Partial<Record<keyof FormModel, App.Global.FormRule[]>>;

const rules = computed<RuleRecord>(() => {
  const {formRules, createConfirmPwdRule} = useFormRules();

  return {
    phone: formRules.phone,
    password: formRules.pwd,
    confirmPassword: createConfirmPwdRule(model.password)
  };
});

async function handleSubmit() {
  await validate();
  // request to reset password
  window.$message?.success($t('page.login.common.validateSuccess'));
}
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" :show-label="false" size="large" @keyup.enter="handleSubmit">
    <NFormItem path="phone">
      <NInput v-model:value="model.phone" :placeholder="$t('page.login.common.phonePlaceholder')"/>
    </NFormItem>
    <NFormItem path="code">
      <NInput v-model:value="model.code" :placeholder="$t('page.login.common.codePlaceholder')"/>
    </NFormItem>
    <NFormItem path="password">
      <NInput
          v-model:value="model.password"
          :placeholder="$t('page.login.common.passwordPlaceholder')"
          show-password-on="click"
          type="password"
      />
    </NFormItem>
    <NFormItem path="confirmPassword">
      <NInput
          v-model:value="model.confirmPassword"
          :placeholder="$t('page.login.common.confirmPasswordPlaceholder')"
          show-password-on="click"
          type="password"
      />
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
