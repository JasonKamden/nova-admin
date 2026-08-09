<script lang="ts" setup>
import {computed, reactive, watch} from 'vue';
import {fetchCreateDictionaryData, fetchUpdateDictionaryData} from '@/service/api';
import {dictTagTypeOptions, statusOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';

defineOptions({name: 'DictionaryDataModal'});

interface Props {
    mode: 'add' | 'edit';
    typeId: number | null;
    dataItem: Api.Dictionary.DataItem | null;
}

const props = defineProps<Props>();
const emit = defineEmits<{submitted: []}>();
const visible = defineModel<boolean>('visible', {default: false});
const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule} = useFormRules();
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => $t(isAdd.value ? 'page.dictionary.addDataTitle' : 'page.dictionary.editDataTitle'));
const state = reactive({submitting: false});
const model = reactive({
    dictLabel: '',
    dictValue: '',
    tagType: 'default',
    sort: 0 as number | null,
    status: 1,
    remark: ''
});

const rules = {
    dictLabel: createRequiredRule($t('page.dictionary.form.dictLabel')),
    dictValue: createRequiredRule($t('page.dictionary.form.dictValue'))
};

function closeModal() {
    visible.value = false;
}

async function handleSubmit() {
    await validate();
    state.submitting = true;
    const payload = {
        dictLabel: model.dictLabel.trim(),
        dictValue: model.dictValue.trim(),
        tagType: model.tagType || null,
        sort: model.sort,
        status: model.status,
        remark: model.remark.trim() || null
    };
    const response = isAdd.value
        ? await fetchCreateDictionaryData(props.typeId!, payload)
        : await fetchUpdateDictionaryData(props.dataItem!.id, payload);
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
        model.dictLabel = props.dataItem?.dictLabel || '';
        model.dictValue = props.dataItem?.dictValue || '';
        model.tagType = props.dataItem?.tagType || 'default';
        model.sort = props.dataItem?.sort ?? 0;
        model.status = props.dataItem?.status ?? 1;
        model.remark = props.dataItem?.remark || '';
    }
);
</script>

<template>
  <NModal v-model:show="visible" preset="card" class="w-640px" :mask-closable="false">
    <template #header><div class="text-16px font-600">{{ title }}</div></template>
    <NForm ref="formRef" :model="model" :rules="rules" :label-width="100" label-placement="left">
      <NGrid :cols="24" :x-gap="16">
        <NFormItemGi :label="$t('page.dictionary.dataLabel')" path="dictLabel" span="12">
          <NInput v-model:value="model.dictLabel" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.dataValue')" path="dictValue" span="12">
          <NInput v-model:value="model.dictValue" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.tagType')" path="tagType" span="12">
          <NSelect v-model:value="model.tagType" :options="dictTagTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.sort')" path="sort" span="12">
          <NInputNumber v-model:value="model.sort" class="w-full" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.status')" path="status" span="12">
          <NSelect v-model:value="model.status" :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.remark')" path="remark" span="24">
          <NInput v-model:value="model.remark" type="textarea" :rows="3" />
        </NFormItemGi>
      </NGrid>
    </NForm>
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton type="primary" :loading="state.submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
