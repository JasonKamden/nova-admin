<script lang="ts" setup>
import {computed, reactive, watch} from 'vue';
import dayjs from 'dayjs';
import {fetchCreateTenant, fetchTenantDetail, fetchUpdateTenant} from '@/service/api';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';

defineOptions({
  name: 'TenantOperateModal'
});

interface Props {
  mode: 'add' | 'edit' | 'detail';
  tenantId: number | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule, patternRules} = useFormRules();

const state = reactive({
  detailLoading: false,
  submitting: false
});

const submitting = computed(() => state.submitting);
const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');

const title = computed(() => {
  const titleMap = {
    add: $t('page.tenant.addTitle'),
    edit: $t('page.tenant.editTitle'),
    detail: $t('page.tenant.detailTitle')
  } as const;

  return titleMap[props.mode];
});

interface FormModel {
  tenantCode: string;
  tenantName: string;
  contactName: string;
  contactPhone: string;
  contactEmail: string;
  expireAt: number | null;
  remark: string;
  adminUsername: string;
  adminNickname: string;
  adminPassword: string;
}

const model = reactive<FormModel>(createDefaultModel());

const rules: Partial<Record<keyof FormModel, App.Global.FormRule | App.Global.FormRule[]>> = {
  tenantCode: createRequiredRule($t('page.tenant.form.tenantCode')),
  tenantName: createRequiredRule($t('page.tenant.form.tenantName')),
  contactEmail: patternRules.email,
  adminUsername: createRequiredRule($t('page.tenant.form.adminUsername')),
  adminNickname: createRequiredRule($t('page.tenant.form.adminNickname')),
  adminPassword: [
    createRequiredRule($t('page.tenant.form.adminPassword')),
    {
      min: 8,
      max: 64,
      message: $t('page.tenant.form.adminPasswordRule'),
      trigger: 'blur'
    }
  ]
};

function createDefaultModel(): FormModel {
  return {
    tenantCode: '',
    tenantName: '',
    contactName: '',
    contactPhone: '',
    contactEmail: '',
    expireAt: null,
    remark: '',
    adminUsername: '',
    adminNickname: '',
    adminPassword: ''
  };
}

function resetModel() {
  Object.assign(model, createDefaultModel());
}

function applyDetail(detail: Api.Tenant.Item) {
  Object.assign(model, {
    tenantCode: detail.tenantCode,
    tenantName: detail.tenantName,
    contactName: detail.contactName || '',
    contactPhone: detail.contactPhone || '',
    contactEmail: detail.contactEmail || '',
    expireAt: detail.expireAt ? dayjs(detail.expireAt, 'YYYY-MM-DD').valueOf() : null,
    remark: detail.remark || '',
    adminUsername: '',
    adminNickname: '',
    adminPassword: ''
  });
}

async function loadDetail() {
  if (!props.tenantId || props.mode === 'add') {
    return;
  }

  state.detailLoading = true;

  const {data, error} = await fetchTenantDetail(props.tenantId);

  state.detailLoading = false;

  if (!error) {
    applyDetail(data);
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
    tenantName: model.tenantName.trim(),
    contactName: model.contactName.trim() || null,
    contactPhone: model.contactPhone.trim() || null,
    contactEmail: model.contactEmail.trim() || null,
    expireAt: model.expireAt ? dayjs(model.expireAt).format('YYYY-MM-DD') : null,
    remark: model.remark.trim() || null
  };

  const response = isAdd.value
      ? await fetchCreateTenant({
        tenantCode: model.tenantCode.trim(),
        adminUsername: model.adminUsername.trim(),
        adminNickname: model.adminNickname.trim(),
        adminPassword: model.adminPassword,
        ...payload
      })
      : await fetchUpdateTenant(props.tenantId!, payload);

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
      await loadDetail();
    }
);
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="title" :width="720">
    <NSpin :show="state.detailLoading">
      <NForm ref="formRef" :disabled="readonly" :label-width="110" :model="model" :rules="rules" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.tenant.tenantCode')" path="tenantCode" span="12">
            <NInput v-model:value="model.tenantCode" :disabled="!isAdd" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.tenantName')" path="tenantName" span="12">
            <NInput v-model:value="model.tenantName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.contactName')" path="contactName" span="12">
            <NInput v-model:value="model.contactName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.contactPhone')" path="contactPhone" span="12">
            <NInput v-model:value="model.contactPhone" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.contactEmail')" path="contactEmail" span="12">
            <NInput v-model:value="model.contactEmail" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.expireAt')" path="expireAt" span="12">
            <NDatePicker v-model:value="model.expireAt" clearable type="date" />
          </NFormItemGi>
          <NFormItemGi v-if="isAdd" :label="$t('page.tenant.adminUsername')" path="adminUsername" span="12">
            <NInput v-model:value="model.adminUsername" />
          </NFormItemGi>
          <NFormItemGi v-if="isAdd" :label="$t('page.tenant.adminNickname')" path="adminNickname" span="12">
            <NInput v-model:value="model.adminNickname" />
          </NFormItemGi>
          <NFormItemGi v-if="isAdd" :label="$t('page.tenant.adminPassword')" path="adminPassword" span="24">
            <NInput v-model:value="model.adminPassword" show-password-on="click" type="password" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.tenant.remark')" path="remark" span="24">
            <NInput v-model:value="model.remark" :autosize="{ minRows: 3, maxRows: 5 }" type="textarea" />
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t(readonly ? 'common.close' : 'common.cancel') }}</NButton>
        <NButton v-if="!readonly" :loading="submitting" type="primary" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
