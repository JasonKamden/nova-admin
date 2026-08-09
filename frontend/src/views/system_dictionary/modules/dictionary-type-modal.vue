<script lang="ts" setup>
import {computed, reactive, watch} from 'vue';
import {fetchCreateDictionaryType, fetchUpdateDictionaryType} from '@/service/api';
import {statusOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';

defineOptions({name: 'DictionaryTypeModal'});

interface Props {
  mode: 'add' | 'edit';
  typeItem: Api.Dictionary.TypeItem | null;
}

const props = defineProps<Props>();
const emit = defineEmits<{ submitted: [{ item: Api.Dictionary.TypeItem; mode: 'add' | 'edit' }] }>();
const visible = defineModel<boolean>('visible', {default: false});
const {formRef, validate, restoreValidation} = useNaiveForm();
const {createRequiredRule} = useFormRules();

const isAdd = computed(() => props.mode === 'add');
const title = computed(() => $t(isAdd.value ? 'page.dictionary.addTypeTitle' : 'page.dictionary.editTypeTitle'));
const state = reactive({submitting: false});
const model = reactive({
  dictName: '',
  dictCode: '',
  status: 1,
  remark: ''
});

const rules = {
  dictName: createRequiredRule($t('page.dictionary.form.dictName')),
  dictCode: createRequiredRule($t('page.dictionary.form.dictCode'))
};

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  state.submitting = true;
  const payload = {
    dictName: model.dictName.trim(),
    status: model.status,
    remark: model.remark.trim() || null
  };
  const response = isAdd.value
      ? await fetchCreateDictionaryType({dictCode: model.dictCode.trim(), ...payload})
      : await fetchUpdateDictionaryType(props.typeItem!.id, payload);
  state.submitting = false;
  if (!response.error) {
    window.$message?.success($t(isAdd.value ? 'common.addSuccess' : 'common.updateSuccess'));
    closeModal();
    emit('submitted', {item: response.data, mode: props.mode});
  }
}

watch(
    () => visible.value,
    show => {
      if (!show) return;
      restoreValidation();
      model.dictName = props.typeItem?.dictName || '';
      model.dictCode = props.typeItem?.dictCode || '';
      model.status = props.typeItem?.status ?? 1;
      model.remark = props.typeItem?.remark || '';
    }
);
</script>

<template>
  <NModal v-model:show="visible" :mask-closable="false" class="w-620px" preset="card">
    <template #header>
      <div class="text-16px font-600">{{ title }}</div>
    </template>
    <NForm ref="formRef" :label-width="100" :model="model" :rules="rules" label-placement="left">
      <NGrid :cols="24" :x-gap="16">
        <NFormItemGi :label="$t('page.dictionary.typeName')" path="dictName" span="12">
          <NInput v-model:value="model.dictName" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.typeCode')" path="dictCode" span="12">
          <NInput v-model:value="model.dictCode" :disabled="!isAdd" />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.status')" path="status" span="12">
          <NSelect
            v-model:value="model.status"
            :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))"
          />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.dictionary.remark')" path="remark" span="24">
          <NInput v-model:value="model.remark" :rows="3" type="textarea" />
        </NFormItemGi>
      </NGrid>
    </NForm>
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
