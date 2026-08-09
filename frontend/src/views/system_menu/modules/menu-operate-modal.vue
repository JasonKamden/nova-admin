<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {
    fetchCreatePlatformMenu,
    fetchPlatformMenuDetail,
    fetchPlatformMenuTree,
    fetchUpdatePlatformMenu
} from '@/service/api';
import type {SelectOption} from 'naive-ui';
import {statusOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import {collectDescendantIds, toMenuTreeOptions} from './shared';

defineOptions({
    name: 'MenuOperateModal'
});

interface Props {
    mode: 'add' | 'edit' | 'detail';
    menuId: number | null;
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
const {createRequiredRule} = useFormRules();

const readonly = computed(() => props.mode === 'detail');
const isAdd = computed(() => props.mode === 'add');
const title = computed(() => {
    const titleMap = {
        add: $t('page.menu.addTitle'),
        edit: $t('page.menu.editTitle'),
        detail: $t('page.menu.detailTitle')
    } as const;

    return titleMap[props.mode];
});

interface FormModel {
    menuType: Api.Menu.MenuType;
    parentId: number | null;
    menuName: string;
    routeName: string;
    routePath: string;
    componentPath: string;
    externalLink: string;
    permissionCode: string;
    icon: string;
    i18nKey: string;
    sort: number | null;
    status: number;
    visible: boolean;
    keepAlive: boolean;
}

const state = reactive({
    loading: false,
    submitting: false
});

const model = reactive<FormModel>(createDefaultModel());
const menuOptions = ref<Api.Menu.TreeOption[]>([]);

const menuTypeOptions: SelectOption[] = [
    {label: $t('page.menu.typeDirectory'), value: 'DIRECTORY'},
    {label: $t('page.menu.typeMenu'), value: 'MENU'},
    {label: $t('page.menu.typeButton'), value: 'BUTTON'}
];

const rules: Partial<Record<keyof FormModel, App.Global.FormRule | App.Global.FormRule[]>> = {
    menuType: createRequiredRule($t('page.menu.form.menuType')),
    menuName: createRequiredRule($t('page.menu.form.menuName'))
};

function createDefaultModel(): FormModel {
    return {
        menuType: 'MENU',
        parentId: props.parentId,
        menuName: '',
        routeName: '',
        routePath: '',
        componentPath: '',
        externalLink: '',
        permissionCode: '',
        icon: '',
        i18nKey: '',
        sort: 0,
        status: 1,
        visible: true,
        keepAlive: true
    };
}

function resetModel() {
    Object.assign(model, createDefaultModel());
}

async function loadMenuTree() {
    const {data, error} = await fetchPlatformMenuTree();

    if (!error) {
        const disabledIds = props.menuId ? collectDescendantIds(data, props.menuId) : new Set<number>();
        menuOptions.value = toMenuTreeOptions(data, disabledIds);
    }
}

async function loadDetail() {
    if (!props.menuId || props.mode === 'add') {
        return;
    }

    state.loading = true;
    const {data, error} = await fetchPlatformMenuDetail(props.menuId);
    state.loading = false;

    if (!error) {
        Object.assign(model, {
            menuType: data.menuType,
            parentId: data.parentId,
            menuName: data.menuName,
            routeName: data.routeName || '',
            routePath: data.routePath || '',
            componentPath: data.componentPath || '',
            externalLink: data.externalLink || '',
            permissionCode: data.permissionCode || '',
            icon: data.icon || '',
            i18nKey: data.i18nKey || '',
            sort: data.sort,
            status: data.status,
            visible: data.visible,
            keepAlive: data.keepAlive
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

    const payload: Api.Menu.SaveParams = {
        menuType: model.menuType,
        parentId: model.parentId,
        menuName: model.menuName.trim(),
        routeName: model.routeName.trim() || null,
        routePath: model.routePath.trim() || null,
        componentPath: model.componentPath.trim() || null,
        externalLink: model.externalLink.trim() || null,
        permissionCode: model.permissionCode.trim() || null,
        icon: model.icon.trim() || null,
        i18nKey: model.i18nKey.trim() || null,
        sort: model.sort,
        status: model.status,
        visible: model.visible,
        keepAlive: model.keepAlive
    };

    const response = isAdd.value
        ? await fetchCreatePlatformMenu(payload)
        : await fetchUpdatePlatformMenu(props.menuId!, payload);

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
        await Promise.all([loadMenuTree(), loadDetail()]);
    }
);
</script>

<template>
  <NModal v-model:show="visible" preset="card" class="w-820px" :mask-closable="false">
    <template #header>
      <div class="text-16px font-600">{{ title }}</div>
    </template>

    <NSpin :show="state.loading">
      <NForm ref="formRef" :model="model" :rules="rules" :disabled="readonly" :label-width="112" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.menu.menuType')" path="menuType" span="12">
            <NSelect v-model:value="model.menuType" :options="menuTypeOptions" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.parentMenu')" path="parentId" span="12">
            <NTreeSelect
              v-model:value="model.parentId"
              :options="menuOptions"
              clearable
              key-field="key"
              label-field="label"
              value-field="value"
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.menuName')" path="menuName" span="12">
            <NInput v-model:value="model.menuName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.sort')" path="sort" span="12">
            <NInputNumber v-model:value="model.sort" class="w-full" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.routeName')" path="routeName" span="12">
            <NInput v-model:value="model.routeName" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.routePath')" path="routePath" span="12">
            <NInput v-model:value="model.routePath" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.componentPath')" path="componentPath" span="12">
            <NInput v-model:value="model.componentPath" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.permissionCode')" path="permissionCode" span="12">
            <NInput v-model:value="model.permissionCode" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.icon')" path="icon" span="12">
            <NInput v-model:value="model.icon" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.i18nKey')" path="i18nKey" span="12">
            <NInput v-model:value="model.i18nKey" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.externalLink')" path="externalLink" span="24">
            <NInput v-model:value="model.externalLink" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.status')" path="status" span="8">
            <NSelect v-model:value="model.status" :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.visible')" path="visible" span="8">
            <NSwitch v-model:value="model.visible" />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.menu.keepAlive')" path="keepAlive" span="8">
            <NSwitch v-model:value="model.keepAlive" />
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t(readonly ? 'common.close' : 'common.cancel') }}</NButton>
        <NButton v-if="!readonly" type="primary" :loading="state.submitting" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>
