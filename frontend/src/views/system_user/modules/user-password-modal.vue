<script lang="ts" setup>
import {computed, reactive, watch} from 'vue';
import {fetchResetUserPassword} from '@/service/api';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';

defineOptions({
  name: 'UserPasswordModal'
});

interface Props {
  userId: number | null;
  username: string;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();
const visible = defineModel<boolean>('visible', {default: false});

const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule, patternRules} = useFormRules();

const state = reactive({
  submitting: false
});

const model = reactive({
  newPassword: ''
});

const title = computed(() => `${$t('page.user.passwordTitle')} - ${props.username || ''}`);

const rules = {
  newPassword: [createRequiredRule($t('page.user.form.newPassword')), patternRules.pwd]
};

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  state.submitting = true;
  const {error} = await fetchResetUserPassword(props.userId!, {newPassword: model.newPassword});
  state.submitting = false;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    closeModal();
    emit('submitted');
  }
}

watch(
    () => visible.value,
    show => {
      if (show) {
        model.newPassword = '';
        restoreValidation();
      }
    }
);
</script>

<template>
  <NModal v-model:show="visible" :mask-closable="false" class="w-520px" preset="card">
    <template #header>
      <div class="text-16px font-600">{{ title }}</div>
    </template>

    <NAlert :show-icon="false" class="mb-16px" type="warning">
      {{ $t('page.user.passwordConfirm', {name: username}) }}
    </NAlert>

    <NForm ref="formRef" :label-width="100" :model="model" :rules="rules" label-placement="left">
      <NFormItem :label="$t('page.user.resetPassword')" path="newPassword">
        <NInput v-model:value="model.newPassword" show-password-on="click" type="password"/>
      </NFormItem>
    </NForm>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
