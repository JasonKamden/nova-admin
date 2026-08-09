<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import type {TreeOption} from 'naive-ui';
import {fetchMenuTree, fetchRoleMenuIds, fetchUpdateRoleMenus} from '@/service/api';
import {$t} from '@/locales';

defineOptions({
  name: 'RoleMenuModal'
});

interface Props {
  roleId: number | null;
  roleName: string;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();
const visible = defineModel<boolean>('visible', {default: false});

const state = reactive({
  loading: false,
  submitting: false
});

const keyword = ref('');
const menuTree = ref<Api.Menu.Item[]>([]);
const checkedKeys = ref<number[]>([]);
const expandedKeys = ref<number[]>([]);

const treeOptions = computed(() => toTreeOptions(menuTree.value));

function toTreeOptions(nodes: Api.Menu.Item[]): TreeOption[] {
  return nodes.map(node => ({
    key: node.id,
    label: `${node.menuName}${node.permissionCode ? ` (${node.permissionCode})` : ''}`,
    children: toTreeOptions(node.children || [])
  }));
}

function collectKeys(nodes: Api.Menu.Item[]): number[] {
  return nodes.flatMap(node => [node.id, ...collectKeys(node.children || [])]);
}

async function loadData() {
  state.loading = true;
  const [menuResp, checkedResp] = await Promise.all([fetchMenuTree(keyword.value || null), fetchRoleMenuIds(props.roleId!)]);
  state.loading = false;

  if (!menuResp.error) {
    menuTree.value = menuResp.data;
    expandedKeys.value = collectKeys(menuResp.data);
  }

  if (!checkedResp.error) {
    checkedKeys.value = checkedResp.data;
  }
}

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  state.submitting = true;
  const {error} = await fetchUpdateRoleMenus(props.roleId!, checkedKeys.value);
  state.submitting = false;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    closeModal();
    emit('submitted');
  }
}

watch(
    () => visible.value,
    async show => {
      if (show && props.roleId) {
        keyword.value = '';
        await loadData();
      }
    }
);
</script>

<template>
  <NModal v-model:show="visible" :mask-closable="false" class="w-760px" preset="card">
    <template #header>
      <div class="text-16px font-600">{{ $t('page.role.menuTitle') }} - {{ roleName }}</div>
    </template>

    <NSpin :show="state.loading">
      <div class="mb-16px flex gap-12px">
        <NInput v-model:value="keyword" :placeholder="$t('common.keywordSearch')" clearable />
        <NButton @click="loadData">{{ $t('common.search') }}</NButton>
      </div>
      <NTree
        v-model:checked-keys="checkedKeys"
        v-model:expanded-keys="expandedKeys"
        :data="treeOptions"
        block-line
        cascade
        check-on-click
        checkable
        expand-on-click
        key-field="key"
        label-field="label"
      />
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
