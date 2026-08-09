<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue';
import {
  fetchCreateMessage,
  fetchDepartmentSelector,
  fetchMessageDetail,
  fetchUpdateMessage
} from '@/service/api';
import { messageTypeOptions, recipientTypeOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import MessageAttachmentPicker from '@/components/business/message-attachment-picker.vue';
import RichHtmlContent from '@/components/business/rich-html-content.vue';
import RichTextEditor from '@/components/business/rich-text-editor.vue';
import RoleSelectorTable from '@/components/business/relation-selector/role-selector-table.vue';
import UserSelectorTable from '@/components/business/relation-selector/user-selector-table.vue';
import { toDepartmentTreeOptions } from '@/views/system_department/modules/shared';

defineOptions({ name: 'MessageOperateModal' });

interface Props {
  mode: 'add' | 'edit' | 'detail';
  messageId: number | null;
}

const props = defineProps<Props>();
const emit = defineEmits<{ submitted: [] }>();
const visible = defineModel<boolean>('visible', { default: false });
const { formRef, validate, restoreValidation } = useNaiveForm();
const { createRequiredRule } = useFormRules();
const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => $t(isAdd.value ? 'page.message.addTitle' : readonly.value ? 'page.message.detailTitle' : 'page.message.editTitle'));

const state = reactive({ loading: false, submitting: false });
const roleSelectorVisible = ref(false);
const userSelectorVisible = ref(false);
const model = reactive<Api.Message.CreateReq>({
  title: '',
  messageType: 'ANNOUNCEMENT',
  contentHtml: '',
  recipient: {
    recipientType: 'ALL',
    departmentIds: [],
    includeChildren: false,
    roleIds: [],
    userIds: []
  },
  fileIds: []
});
const detail = ref<Api.Message.Detail | null>(null);
const departmentOptions = ref<Api.Department.TreeOption[]>([]);

const rules = {
  title: createRequiredRule($t('page.message.form.title')),
  contentHtml: createRequiredRule($t('page.message.form.contentHtml')),
  messageType: createRequiredRule($t('page.message.messageType')),
  recipientType: createRequiredRule($t('page.message.recipientType'))
};

const isDepartmentRecipient = computed(() => model.recipient.recipientType === 'DEPARTMENT');
const isRoleRecipient = computed(() => model.recipient.recipientType === 'ROLE');
const isUserRecipient = computed(() => model.recipient.recipientType === 'USER');
const selectedRoleCount = computed(() => model.recipient.roleIds.length);
const selectedUserCount = computed(() => model.recipient.userIds.length);

function closeModal() {
  visible.value = false;
}

function closeRoleSelector() {
  roleSelectorVisible.value = false;
}

function closeUserSelector() {
  userSelectorVisible.value = false;
}

function resetModel() {
  model.title = '';
  model.messageType = 'ANNOUNCEMENT';
  model.contentHtml = '';
  model.recipient = {
    recipientType: 'ALL',
    departmentIds: [],
    includeChildren: false,
    roleIds: [],
    userIds: []
  };
  model.fileIds = [];
  detail.value = null;
}

async function loadDepartments() {
  const { data, error } = await fetchDepartmentSelector();
  if (!error) {
    departmentOptions.value = toDepartmentTreeOptions(data);
  }
}

async function loadDetail() {
  if (!props.messageId || props.mode === 'add') {
    return;
  }
  state.loading = true;
  const { data, error } = await fetchMessageDetail(props.messageId);
  state.loading = false;
  if (!error) {
    detail.value = data;
    model.title = data.title;
    model.messageType = data.messageType;
    model.contentHtml = data.contentHtml;
    model.fileIds = data.fileIds || [];
    try {
      const rule = JSON.parse(data.recipientRuleJson || '{}');
      model.recipient = {
        recipientType: data.recipientType,
        departmentIds: rule.departmentIds || [],
        includeChildren: Boolean(rule.includeChildren),
        roleIds: rule.roleIds || [],
        userIds: rule.userIds || []
      };
    } catch {
      model.recipient = {
        recipientType: data.recipientType,
        departmentIds: [],
        includeChildren: false,
        roleIds: [],
        userIds: []
      };
    }
  }
}

async function handleSubmit() {
  if (readonly.value) {
    closeModal();
    return;
  }
  await validate();
  state.submitting = true;
  const payload: Api.Message.CreateReq = {
    title: model.title.trim(),
    messageType: model.messageType,
    contentHtml: model.contentHtml.trim(),
    recipient: {
      recipientType: model.recipient.recipientType,
      departmentIds: isDepartmentRecipient.value ? model.recipient.departmentIds : [],
      includeChildren: isDepartmentRecipient.value ? model.recipient.includeChildren : false,
      roleIds: isRoleRecipient.value ? model.recipient.roleIds : [],
      userIds: isUserRecipient.value ? model.recipient.userIds : []
    },
    fileIds: model.fileIds
  };
  const response = isAdd.value ? await fetchCreateMessage(payload) : await fetchUpdateMessage(props.messageId!, payload);
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
    if (!show) return;
    resetModel();
    restoreValidation();
    await Promise.all([loadDepartments(), loadDetail()]);
  }
);
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="title" :width="960">
    <NSpin :show="state.loading">
      <NForm ref="formRef" :disabled="readonly" :label-width="110" :model="model" :rules="rules" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.message.title')" path="title" span="12">
            <NInput v-model:value="model.title" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.messageType')" path="messageType" span="6">
            <NSelect
              v-model:value="model.messageType"
              :options="messageTypeOptions.map(item => ({label: $t(item.label), value: item.value}))"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.recipientType')" path="recipientType" span="6">
            <NSelect
              v-model:value="model.recipient.recipientType"
              :options="recipientTypeOptions.map(item => ({label: $t(item.label), value: item.value}))"
            />
          </NFormItemGi>
          <NFormItemGi v-if="isDepartmentRecipient" :label="$t('page.message.departments')" span="18">
            <NTreeSelect
              v-model:value="model.recipient.departmentIds"
              :options="departmentOptions"
              clearable
              key-field="key"
              label-field="label"
              multiple
              value-field="value"
            />
          </NFormItemGi>
          <NFormItemGi v-if="isDepartmentRecipient" :label="$t('page.message.includeChildren')" span="6">
            <NSwitch v-model:value="model.recipient.includeChildren" />
          </NFormItemGi>
          <NFormItemGi v-if="isRoleRecipient" :label="$t('page.message.roles')" span="24">
            <div class="flex flex-wrap items-center justify-between gap-12px rounded-12px border border-#e5e7eb px-14px py-12px">
              <span class="text-14px text-text-secondary">
                {{ $t('common.selectedItems', {count: selectedRoleCount}) }}
              </span>
              <NButton :disabled="readonly" ghost type="primary" @click="roleSelectorVisible = true">
                {{ $t('page.message.roles') }}
              </NButton>
            </div>
          </NFormItemGi>
          <NFormItemGi v-if="isUserRecipient" :label="$t('page.message.users')" span="24">
            <div class="flex flex-wrap items-center justify-between gap-12px rounded-12px border border-#e5e7eb px-14px py-12px">
              <span class="text-14px text-text-secondary">
                {{ $t('common.selectedItems', {count: selectedUserCount}) }}
              </span>
              <NButton :disabled="readonly" ghost type="primary" @click="userSelectorVisible = true">
                {{ $t('page.message.users') }}
              </NButton>
            </div>
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.contentHtml')" path="contentHtml" span="24">
            <RichTextEditor
              v-if="!readonly"
              v-model="model.contentHtml"
              :min-height="280"
              :placeholder="$t('page.message.editorPlaceholder')"
            />
            <RichHtmlContent v-else :html="model.contentHtml" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.attachments')" span="24">
            <MessageAttachmentPicker v-model:file-ids="model.fileIds" />
          </NFormItemGi>
          <NFormItemGi v-if="readonly && detail" :label="$t('page.message.recipientRule')" span="24">
            <NCode :code="detail.recipientRuleJson" language="json" show-line-numbers />
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

  <BusinessFormContainer
    v-model:visible="roleSelectorVisible"
    :title="$t('page.message.roles')"
    :width="760"
  >
    <RoleSelectorTable v-model:selected-ids="model.recipient.roleIds" :disabled="readonly" />
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeRoleSelector">{{ $t('common.close') }}</NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>

  <BusinessFormContainer
    v-model:visible="userSelectorVisible"
    :title="$t('page.message.users')"
    :width="800"
  >
    <UserSelectorTable v-model:selected-ids="model.recipient.userIds" :disabled="readonly" />
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeUserSelector">{{ $t('common.close') }}</NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
