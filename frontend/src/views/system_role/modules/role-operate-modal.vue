<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {dataScopeOptions, statusOptions} from '@/constants/business';
import {fetchCreateRole, fetchDepartmentSelector, fetchRoleDetail, fetchUpdateRole} from '@/service/api';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import {toDepartmentTreeOptions} from '@/views/system_department/modules/shared';

defineOptions({
  name: 'RoleOperateModal'
});

interface Props {
  mode: 'add' | 'edit' | 'detail';
  roleId: number | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();
const visible = defineModel<boolean>('visible', {default: false});

const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule} = useFormRules();

const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => {
  const map = {
    add: $t('page.role.addTitle'),
    edit: $t('page.role.editTitle'),
    detail: $t('page.role.detailTitle')
  } as const;

  return map[props.mode];
});

const state = reactive({
  loading: false,
  submitting: false
});

interface FormModel {
  roleCode: string;
  roleName: string;
  dataScope: Api.Role.DataScopeType;
  sort: number | null;
  status: number;
  remark: string;
  customDepartmentIds: number[];
}

const model = reactive<FormModel>(createDefaultModel());
const departmentOptions = ref<Api.Department.TreeOption[]>([]);
const isCustomScope = computed(() => model.dataScope === 'CUSTOM');

const rules: Partial<Record<keyof FormModel, App.Global.FormRule | App.Global.FormRule[]>> = {
  roleCode: createRequiredRule($t('page.role.form.roleCode')),
  roleName: createRequiredRule($t('page.role.form.roleName')),
  dataScope: createRequiredRule($t('page.role.dataScope'))
};

function createDefaultModel(): FormModel {
  return {
    roleCode: '',
    roleName: '',
    dataScope: 'TENANT',
    sort: 0,
    status: 1,
    remark: '',
    customDepartmentIds: []
  };
}

function resetModel() {
  Object.assign(model, createDefaultModel());
}

async function loadDepartments() {
  const {data, error} = await fetchDepartmentSelector();
  if (!error) {
    departmentOptions.value = toDepartmentTreeOptions(data);
  }
}

async function loadDetail() {
  if (!props.roleId || props.mode === 'add') {
    return;
  }

  state.loading = true;
  const {data, error} = await fetchRoleDetail(props.roleId);
  state.loading = false;

  if (!error) {
    Object.assign(model, {
      roleCode: data.roleCode,
      roleName: data.roleName,
      dataScope: data.dataScope,
      sort: data.sort,
      status: data.status,
      remark: data.remark || '',
      customDepartmentIds: data.customDepartmentIds || []
    });
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
    roleName: model.roleName.trim(),
    dataScope: model.dataScope,
    sort: model.sort,
    status: model.status,
    remark: model.remark.trim() || null,
    customDepartmentIds: model.dataScope === 'CUSTOM' ? model.customDepartmentIds : []
  };

  const response = isAdd.value
      ? await fetchCreateRole({
        roleCode: model.roleCode.trim(),
        ...payload
      })
      : await fetchUpdateRole(props.roleId!, payload);

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
          <NFormItemGi :label="$t('page.role.roleCode')" path="roleCode" span="12">
            <NInput v-model:value="model.roleCode" :disabled="!isAdd || readonly" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.role.roleName')" path="roleName" span="12">
            <NInput v-model:value="model.roleName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.role.dataScope')" path="dataScope" span="12">
            <NSelect
              v-model:value="model.dataScope"
              :options="dataScopeOptions.map(item => ({ label: $t(item.label), value: item.value }))"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.role.status')" path="status" span="12">
            <NSelect
              v-model:value="model.status"
              :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.role.sort')" path="sort" span="12">
            <NInputNumber v-model:value="model.sort" class="w-full" />
          </NFormItemGi>
          <NFormItemGi
            v-if="isCustomScope" :label="$t('page.role.customDepartments')" path="customDepartmentIds"
            span="24"
          >
            <NTreeSelect
              v-model:value="model.customDepartmentIds"
              :options="departmentOptions"
              clearable
              key-field="key"
              label-field="label"
              multiple
              value-field="value"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.role.remark')" path="remark" span="24">
            <NInput v-model:value="model.remark" :rows="3" type="textarea" />
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
