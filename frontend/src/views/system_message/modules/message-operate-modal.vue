<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {
    fetchCreateMessage,
    fetchDepartmentSelector,
    fetchMessageDetail,
    fetchRoleOptions,
    fetchUpdateMessage,
    fetchUserPage
} from '@/service/api';
import {messageTypeOptions, recipientTypeOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import {toDepartmentTreeOptions} from '@/views/system_department/modules/shared';

defineOptions({name: 'MessageOperateModal'});

interface Props {
    mode: 'add' | 'edit' | 'detail';
    messageId: number | null;
}

const props = defineProps<Props>();
const emit = defineEmits<{submitted: []}>();
const visible = defineModel<boolean>('visible', {default: false});
const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule} = useFormRules();
const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => $t(isAdd.value ? 'page.message.addTitle' : readonly.value ? 'page.message.detailTitle' : 'page.message.editTitle'));

const state = reactive({loading: false, submitting: false});
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
const roleOptions = ref<Array<{label: string; value: number}>>([]);
const userOptions = ref<Array<{label: string; value: number}>>([]);

const rules = {
    title: createRequiredRule($t('page.message.form.title')),
    contentHtml: createRequiredRule($t('page.message.form.contentHtml')),
    messageType: createRequiredRule($t('page.message.messageType')),
    recipientType: createRequiredRule($t('page.message.recipientType'))
};

const isDepartmentRecipient = computed(() => model.recipient.recipientType === 'DEPARTMENT');
const isRoleRecipient = computed(() => model.recipient.recipientType === 'ROLE');
const isUserRecipient = computed(() => model.recipient.recipientType === 'USER');

function closeModal() {
    visible.value = false;
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
    const {data, error} = await fetchDepartmentSelector();
    if (!error) {
        departmentOptions.value = toDepartmentTreeOptions(data);
    }
}

async function loadRoles() {
    const {data, error} = await fetchRoleOptions(null);
    if (!error) {
        roleOptions.value = data.map(item => ({label: `${item.roleName} (${item.roleCode})`, value: item.id}));
    }
}

async function searchUsers(keyword = '') {
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
        userOptions.value = data.records.map(item => ({label: `${item.nickname} (${item.username})`, value: item.id}));
    }
}

async function loadDetail() {
    if (!props.messageId || props.mode === 'add') {
        return;
    }
    state.loading = true;
    const {data, error} = await fetchMessageDetail(props.messageId);
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
        fileIds: []
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
        await Promise.all([loadDepartments(), loadRoles(), searchUsers(), loadDetail()]);
    }
);
</script>

<template>
  <NModal v-model:show="visible" preset="card" class="w-860px" :mask-closable="false">
    <template #header><div class="text-16px font-600">{{ title }}</div></template>
    <NSpin :show="state.loading">
      <NForm ref="formRef" :model="model" :rules="rules" :disabled="readonly" :label-width="110" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.message.title')" path="title" span="12"><NInput v-model:value="model.title" /></NFormItemGi>
          <NFormItemGi :label="$t('page.message.messageType')" path="messageType" span="12">
            <NSelect v-model:value="model.messageType" :options="messageTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.recipientType')" path="recipientType" span="12">
            <NSelect v-model:value="model.recipient.recipientType" :options="recipientTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" />
          </NFormItemGi>
          <NFormItemGi v-if="isDepartmentRecipient" :label="$t('page.message.departments')" span="24">
            <NTreeSelect
              v-model:value="model.recipient.departmentIds"
              :options="departmentOptions"
              key-field="key"
              label-field="label"
              value-field="value"
              multiple
              clearable
            />
          </NFormItemGi>
          <NFormItemGi v-if="isDepartmentRecipient" :label="$t('page.message.includeChildren')" span="12">
            <NSwitch v-model:value="model.recipient.includeChildren" />
          </NFormItemGi>
          <NFormItemGi v-if="isRoleRecipient" :label="$t('page.message.roles')" span="24">
            <NSelect v-model:value="model.recipient.roleIds" :options="roleOptions" clearable filterable multiple />
          </NFormItemGi>
          <NFormItemGi v-if="isUserRecipient" :label="$t('page.message.users')" span="24">
            <NSelect
              v-model:value="model.recipient.userIds"
              :options="userOptions"
              clearable
              filterable
              multiple
              remote
              @search="searchUsers"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.message.contentHtml')" path="contentHtml" span="24">
            <NInput v-model:value="model.contentHtml" type="textarea" :rows="8" />
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
        <NButton v-if="!readonly" type="primary" :loading="state.submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
