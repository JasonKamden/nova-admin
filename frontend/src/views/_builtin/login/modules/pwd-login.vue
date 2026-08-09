<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue';
import {useAuthStore} from '@/store/modules/auth';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {fetchCaptcha} from '@/service/api';
import {localStg} from '@/utils/storage';
import {$t} from '@/locales';

defineOptions({
  name: 'PwdLogin'
});

const REMEMBERED_USERNAME_KEY = 'rememberedUsername';

const authStore = useAuthStore();
const {formRef, validate} = useNaiveForm();
const captchaLoading = ref(false);
const captcha = reactive<Api.Auth.CaptchaResp>({
  captchaId: '',
  imageBase64: ''
});

interface FormModel {
  username: string;
  password: string;
  captchaCode: string;
  rememberMe: boolean;
}

const rememberedUsername = localStg.get(REMEMBERED_USERNAME_KEY) || '';

const model: FormModel = reactive({
  username: rememberedUsername,
  password: '',
  captchaCode: '',
  rememberMe: Boolean(rememberedUsername)
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  // inside computed to make locale reactive, if not apply i18n, you can define it without computed
  const {formRules, createRequiredRule} = useFormRules();

  return {
    username: formRules.userName,
    password: formRules.pwd,
    captchaCode: [createRequiredRule($t('form.code.required'))],
    rememberMe: []
  };
});

async function refreshCaptcha() {
  if (captchaLoading.value) {
    return;
  }

  captchaLoading.value = true;

  const {data, error} = await fetchCaptcha();

  if (!error) {
    captcha.captchaId = data.captchaId;
    captcha.imageBase64 = data.imageBase64;
    model.captchaCode = '';
  } else {
    captcha.captchaId = '';
    captcha.imageBase64 = '';
  }

  captchaLoading.value = false;
}

function persistRememberedUsername() {
  if (model.rememberMe) {
    localStg.set(REMEMBERED_USERNAME_KEY, model.username.trim());
    return;
  }

  localStg.remove(REMEMBERED_USERNAME_KEY);
}

async function handleSubmit() {
  await validate();
  persistRememberedUsername();

  const success = await authStore.login({
    username: model.username.trim(),
    password: model.password,
    captchaId: captcha.captchaId,
    captchaCode: model.captchaCode.trim()
  });

  if (!success) {
    await refreshCaptcha();
  }
}

onMounted(async () => {
  await refreshCaptcha();
});
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" :show-label="false" size="large" @keyup.enter="handleSubmit">
    <NFormItem path="username">
      <NInput v-model:value="model.username" :placeholder="$t('page.login.common.userNamePlaceholder')" />
    </NFormItem>
    <NFormItem path="password">
      <NInput
        v-model:value="model.password"
        :placeholder="$t('page.login.common.passwordPlaceholder')"
        show-password-on="click"
        type="password"
      />
    </NFormItem>
    <NFormItem path="captchaCode">
      <div class="flex w-full items-center gap-12px">
        <NInput
          v-model:value="model.captchaCode"
          class="flex-1"
          :placeholder="$t('page.login.pwdLogin.captchaPlaceholder')"
        />
        <button
          class="h-40px w-132px cursor-pointer overflow-hidden rounded-8px border border-#e5e7eb bg-white p-0 transition hover:border-primary disabled:cursor-not-allowed dark:border-#334155 dark:bg-#111827"
          type="button"
          :disabled="captchaLoading"
          @click="refreshCaptcha"
        >
          <span v-if="captchaLoading" class="text-12px text-text-secondary">{{ $t('common.refresh') }}</span>
          <img
            v-else-if="captcha.imageBase64"
            :src="captcha.imageBase64"
            alt="captcha"
            class="block size-full object-cover"
          />
          <span v-else class="text-12px text-text-secondary">{{ $t('page.login.pwdLogin.refreshCaptcha') }}</span>
        </button>
      </div>
    </NFormItem>
    <NSpace :size="24" vertical>
      <div class="flex-y-center">
        <NCheckbox v-model:checked="model.rememberMe">{{ $t('page.login.pwdLogin.rememberMe') }}</NCheckbox>
      </div>
      <NButton :loading="authStore.loginLoading" block round size="large" type="primary" @click="handleSubmit">
        {{ $t('page.login.pwdLogin.submit') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped></style>
