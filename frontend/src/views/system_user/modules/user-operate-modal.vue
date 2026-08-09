<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {
  fetchCreateUser,
  fetchDepartmentSelector,
  fetchUpdateUser,
  fetchUserDetail
} from '@/service/api';
import {genderOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import {toDepartmentTreeOptions} from '@/views/system_department/modules/shared';

defineOptions({
  name: 'UserOperateModal'
});

interface Props {
  mode: 'add' | 'edit' | 'detail';
  userId: number | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {default: false});

const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule, formRules, patternRules} = useFormRules();

const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => {
  const map = {
    add: $t('page.user.addTitle'),
    edit: $t('page.user.editTitle'),
    detail: $t('page.user.detailTitle')
  } as const;

  return map[props.mode];
});

const state = reactive({
  loading: false,
  submitting: false
});

interface FormModel {
  username: string;
  nickname: string;
  gender: string | null;
  phone: string;
  email: string;
  bio: string;
  departmentId: number | null;
  initialPassword: string;
}

const model = reactive<FormModel>(createDefaultModel());
const departmentOptions = ref<Api.Department.TreeOption[]>([]);
const detailRoleNames = ref<string[]>([]);

const rules: Partial<Record<keyof FormModel, App.Global.FormRule | App.Global.FormRule[]>> = {
  username: isAdd.value ? formRules.userName : [],
  nickname: createRequiredRule($t('page.user.form.nickname')),
  initialPassword: isAdd.value ? [createRequiredRule($t('page.user.form.initialPassword')), patternRules.pwd] : [],
  phone: model.phone ? patternRules.phone : undefined,
  email: model.email ? patternRules.email : undefined
};

function createDefaultModel(): FormModel {
  return {
    username: '',
    nickname: '',
    gender: null,
    phone: '',
    email: '',
    bio: '',
    departmentId: null,
    initialPassword: ''
  };
}

function resetModel() {
  Object.assign(model, createDefaultModel());
  detailRoleNames.value = [];
}

async function loadDepartments() {
  const {data, error} = await fetchDepartmentSelector();
  if (!error) {
    departmentOptions.value = toDepartmentTreeOptions(data);
  }
}

async function loadDetail() {
  if (!props.userId || props.mode === 'add') {
    return;
  }

  state.loading = true;
  const {data, error} = await fetchUserDetail(props.userId);
  state.loading = false;

  if (!error) {
    Object.assign(model, {
      username: data.username,
      nickname: data.nickname,
      gender: data.gender,
      phone: data.phone || '',
      email: data.email || '',
      bio: data.bio || '',
      departmentId: data.departmentId,
      initialPassword: ''
    });
    detailRoleNames.value = data.roles?.map(item => item.roleName) || [];
  }
}

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  if (readonly.value) {
    closeModal();
    return;
  }

  await validate();
  state.submitting = true;

  const payload = {
    nickname: model.nickname.trim(),
    gender: model.gender || null,
    phone: model.phone.trim() || null,
    email: model.email.trim() || null,
    bio: model.bio.trim() || null,
    departmentId: model.departmentId
  };

  const response = isAdd.value
      ? await fetchCreateUser({
        username: model.username.trim(),
        initialPassword: model.initialPassword,
        roleIds: [],
        ...payload
      })
      : await fetchUpdateUser(props.userId!, payload);

  state.submitting = false;

  if (!response.error) {
    window.$message?.success($t(isAdd.value ? 'common.addSuccess' : 'common.updateSuccess'));
    closeModal();
    emit('submitted');
  }
}

watch(
    () => visible.value,
    async show => {
      if (!show) {
        return;
      }

      resetModel();
      restoreValidation();
      await Promise.all([loadDepartments(), loadDetail()]);
    }
);
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="title" :width="760">
    <NSpin :show="state.loading">
      <NForm ref="formRef" :disabled="readonly" :label-width="110" :model="model" :rules="rules" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.user.username')" path="username" span="12">
            <NInput v-model:value="model.username" :disabled="!isAdd || readonly" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.nickname')" path="nickname" span="12">
            <NInput v-model:value="model.nickname" />
          </NFormItemGi>
          <NFormItemGi v-if="isAdd" :label="$t('page.user.initialPassword')" path="initialPassword" span="12">
            <NInput v-model:value="model.initialPassword" show-password-on="click" type="password" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.gender')" :span="isAdd ? 12 : 12" path="gender">
            <NSelect
              v-model:value="model.gender"
              :options="genderOptions.map(item => ({ label: $t(item.label), value: item.value }))"
              clearable
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.phone')" path="phone" span="12">
            <NInput v-model:value="model.phone" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.email')" path="email" span="12">
            <NInput v-model:value="model.email" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.department')" path="departmentId" span="12">
            <NTreeSelect
              v-model:value="model.departmentId"
              :options="departmentOptions"
              clearable
              key-field="key"
              label-field="label"
              value-field="value"
            />
          </NFormItemGi>
          <NFormItemGi v-if="readonly" :label="$t('page.user.role')" span="12">
            <NInput :value="detailRoleNames.join(' / ')" readonly />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.bio')" path="bio" span="24">
            <NInput v-model:value="model.bio" :rows="3" type="textarea" />
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t(readonly ? 'common.close' : 'common.cancel') }}</NButton>
        <NButton v-if="!readonly" :loading="state.submitting" type="primary" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
