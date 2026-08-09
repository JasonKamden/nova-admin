<script lang="ts" setup>
import {computed, reactive, watch} from 'vue';
import {configTypeOptions, statusOptions} from '@/constants/business';
import {fetchCreateConfig, fetchUpdateConfig} from '@/service/api';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';

defineOptions({name: 'ConfigOperateModal'});

interface Props {
    mode: 'add' | 'edit' | 'detail';
    item: Api.Config.Item | null;
}

const props = defineProps<Props>();
const emit = defineEmits<{submitted: []}>();
const visible = defineModel<boolean>('visible', {default: false});
const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule} = useFormRules();
const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => $t(isAdd.value ? 'page.config.addTitle' : readonly.value ? 'page.config.detailTitle' : 'page.config.editTitle'));
const state = reactive({submitting: false});
const model = reactive({
    configName: '',
    configCode: '',
    configValue: '',
    configType: 'STRING' as Api.Config.ConfigType,
    sensitive: false,
    status: 1,
    remark: ''
});

const rules = {
    configName: createRequiredRule($t('page.config.form.configName')),
    configCode: createRequiredRule($t('page.config.form.configCode')),
    configValue: createRequiredRule($t('page.config.form.configValue'))
};

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
        configName: model.configName.trim(),
        configValue: model.configValue,
        configType: model.configType,
        sensitive: model.sensitive,
        status: model.status,
        remark: model.remark.trim() || null
    };
    const response = isAdd.value
        ? await fetchCreateConfig({configCode: model.configCode.trim(), ...payload})
        : await fetchUpdateConfig(props.item!.id, payload);
    state.submitting = false;
    if (!response.error) {
        window.$message?.success($t(isAdd.value ? 'common.addSuccess' : 'common.updateSuccess'));
        closeModal();
        emit('submitted');
    }
}

watch(
    () => visible.value,
    show => {
        if (!show) return;
        restoreValidation();
        model.configName = props.item?.configName || '';
        model.configCode = props.item?.configCode || '';
        model.configValue = props.item?.configValue || '';
        model.configType = props.item?.configType || 'STRING';
        model.sensitive = props.item?.sensitive || false;
        model.status = props.item?.status ?? 1;
        model.remark = props.item?.remark || '';
    }
);
</script>

<template>
  <NModal v-model:show="visible" preset="card" class="w-720px" :mask-closable="false">
    <template #header><div class="text-16px font-600">{{ title }}</div></template>
    <NForm ref="formRef" :model="model" :rules="rules" :disabled="readonly" :label-width="100" label-placement="left">
      <NGrid :cols="24" :x-gap="16">
        <NFormItemGi :label="$t('page.config.configName')" path="configName" span="12"><NInput v-model:value="model.configName" /></NFormItemGi>
        <NFormItemGi :label="$t('page.config.configCode')" path="configCode" span="12"><NInput v-model:value="model.configCode" :disabled="!isAdd || readonly" /></NFormItemGi>
        <NFormItemGi :label="$t('page.config.configType')" path="configType" span="12">
          <NSelect v-model:value="model.configType" :options="configTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.config.status')" path="status" span="12">
          <NSelect v-model:value="model.status" :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.config.sensitive')" path="sensitive" span="12">
          <NSwitch v-model:value="model.sensitive" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.config.configValue')" path="configValue" span="24">
          <NInput v-model:value="model.configValue" type="textarea" :rows="4" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.config.remark')" path="remark" span="24">
          <NInput v-model:value="model.remark" type="textarea" :rows="3" />
        </NFormItemGi>
      </NGrid>
    </NForm>
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t(readonly ? 'common.close' : 'common.cancel') }}</NButton>
        <NButton v-if="!readonly" type="primary" :loading="state.submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
