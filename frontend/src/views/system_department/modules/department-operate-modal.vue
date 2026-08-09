<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {
  fetchCreateDepartment,
  fetchDepartmentDetail,
  fetchDepartmentSelector,
  fetchUpdateDepartment,
  fetchUserPage
} from '@/service/api';
import {statusOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import {collectDepartmentIds, toDepartmentTreeOptions} from './shared';

defineOptions({
  name: 'DepartmentOperateModal'
});

interface Props {
  mode: 'add' | 'edit' | 'detail';
  departmentId: number | null;
  parentId: number | null;
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

const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => {
  const titleMap = {
    add: $t('page.department.addTitle'),
    edit: $t('page.department.editTitle'),
    detail: $t('page.department.detailTitle')
  } as const;

  return titleMap[props.mode];
});

interface LeaderOption {
  label: string;
  value: number;
}

interface FormModel {
  parentId: number | null;
  departmentCode: string;
  departmentName: string;
  leaderUserId: number | null;
  phone: string;
  email: string;
  sort: number | null;
  status: number;
}

const model = reactive<FormModel>(createDefaultModel());
const departmentTree = ref<Api.Department.Item[]>([]);
const parentOptions = ref<Api.Department.TreeOption[]>([]);
const leaderOptions = ref<LeaderOption[]>([]);

const rules: Partial<Record<keyof FormModel, App.Global.FormRule | App.Global.FormRule[]>> = {
  departmentCode: createRequiredRule($t('page.department.form.departmentCode')),
  departmentName: createRequiredRule($t('page.department.form.departmentName')),
  email: patternRules.email
};

function createDefaultModel(): FormModel {
  return {
    parentId: props.parentId,
    departmentCode: '',
    departmentName: '',
    leaderUserId: null,
    phone: '',
    email: '',
    sort: 0,
    status: 1
  };
}

function resetModel() {
  Object.assign(model, createDefaultModel());
}

async function loadDepartmentTree() {
  const {data, error} = await fetchDepartmentSelector();

  if (!error) {
    departmentTree.value = data;
    const disabledIds = props.departmentId ? collectDepartmentIds(data, props.departmentId) : new Set<number>();
    parentOptions.value = toDepartmentTreeOptions(data, disabledIds);
  }
}

async function loadLeaderOptions(keyword = '') {
  const {data, error} = await fetchUserPage({
    pageNum: 1,
    pageSize: 20,
    username: keyword || null,
    nickname: keyword || null,
    phone: null,
    email: null,
    departmentId: null,
    status: 1
  });

  if (!error) {
    leaderOptions.value = data.records.map(item => ({
      label: `${item.nickname} (${item.username})`,
      value: item.id
    }));
  }
}

async function loadDetail() {
  if (!props.departmentId || props.mode === 'add') {
    return;
  }

  state.detailLoading = true;

  const {data, error} = await fetchDepartmentDetail(props.departmentId);

  state.detailLoading = false;

  if (!error) {
    Object.assign(model, {
      parentId: data.parentId,
      departmentCode: data.departmentCode,
      departmentName: data.departmentName,
      leaderUserId: data.leaderUserId,
      phone: data.phone || '',
      email: data.email || '',
      sort: data.sort,
      status: data.status
    });

    if (data.leaderUserId && data.leaderName) {
      leaderOptions.value = [
        {
          label: data.leaderName,
          value: data.leaderUserId
        }
      ];
    }
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
    parentId: model.parentId,
    departmentName: model.departmentName.trim(),
    leaderUserId: model.leaderUserId,
    phone: model.phone.trim() || null,
    email: model.email.trim() || null,
    sort: model.sort,
    status: model.status
  };

  const response = isAdd.value
      ? await fetchCreateDepartment({
        departmentCode: model.departmentCode.trim(),
        ...payload
      })
      : await fetchUpdateDepartment(props.departmentId!, payload);

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
      await Promise.all([loadDepartmentTree(), loadLeaderOptions(), loadDetail()]);
    }
);
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="title" :width="720">
    <NSpin :show="state.detailLoading">
      <NForm ref="formRef" :disabled="readonly" :label-width="120" :model="model" :rules="rules" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.department.parentDepartment')" path="parentId" span="12">
            <NTreeSelect
              v-model:value="model.parentId"
              :options="parentOptions"
              clearable
              key-field="key"
              label-field="label"
              value-field="value"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.departmentCode')" path="departmentCode" span="12">
            <NInput v-model:value="model.departmentCode" :disabled="!isAdd" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.departmentName')" path="departmentName" span="12">
            <NInput v-model:value="model.departmentName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.leaderUser')" path="leaderUserId" span="12">
            <NSelect
              v-model:value="model.leaderUserId"
              :options="leaderOptions"
              clearable
              filterable
              remote
              @search="loadLeaderOptions"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.phone')" path="phone" span="12">
            <NInput v-model:value="model.phone" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.email')" path="email" span="12">
            <NInput v-model:value="model.email" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.sort')" path="sort" span="12">
            <NInputNumber v-model:value="model.sort" class="w-full" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.status')" path="status" span="12">
            <NSelect
              v-model:value="model.status"
              :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
            />
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
