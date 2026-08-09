<script lang="ts" setup>
import {computed} from 'vue';
import {actionDelegationMiddleware, useCaptcha, useForm} from '@sa/alova/client';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import {sendCaptcha, verifyCaptcha} from '@/service-alova/api';

defineOptions({
  name: 'CaptchaVerification'
});

const {loading, send, countdown} = useCaptcha(sendCaptcha, {
  middleware: actionDelegationMiddleware('captcha:send')
});
const label = computed(() => {
  return countdown.value > 0
      ? $t('page.login.codeLogin.reGetCode', {time: countdown.value})
      : $t('page.login.codeLogin.getCode');
});
const {
  form,
  loading: submiting,
  send: submit
} = useForm(formData => verifyCaptcha(formData.phone, formData.code), {
  initialForm: {
    phone: '',
    code: ''
  }
});

const {formRef, validate} = useNaiveForm();

const rules = computed<Record<keyof typeof form.value, App.Global.FormRule[]>>(() => {
  const {formRules} = useFormRules();

  return {
    phone: formRules.phone,
    code: formRules.code
  };
});

async function handleSubmit() {
  await validate();
  await submit();
  // request
  window.$message?.success($t('page.login.common.validateSuccess'));
}
</script>

<template>
  <NForm ref="formRef" :model="form" :rules="rules" :show-label="false" size="large" @keyup.enter="handleSubmit">
    <NFormItem path="phone">
      <NInput v-model:value="form.phone" :maxlength="11" :placeholder="$t('page.login.common.phonePlaceholder')"/>
    </NFormItem>
    <NFormItem path="code">
      <div class="w-full flex-y-center gap-16px">
        <NInput v-model:value="form.code" :placeholder="$t('page.login.common.codePlaceholder')"/>
        <NButton :disabled="countdown > 0" :loading="loading" size="large" @click="send(form.phone)">
          {{ label }}
        </NButton>
      </div>
    </NFormItem>
    <NSpace :size="18" class="w-full" vertical>
      <NButton :loading="submiting" block round size="large" type="primary" @click="handleSubmit">
        {{ $t('common.confirm') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped></style>
