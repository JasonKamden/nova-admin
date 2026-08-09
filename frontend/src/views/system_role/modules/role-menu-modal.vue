<script lang="ts" setup>
import {computed, h, reactive, ref, watch} from 'vue';
import type {TreeOption} from 'naive-ui';
import {NButton, NEllipsis} from 'naive-ui';
import {fetchMenuTree, fetchRoleMenuIds, fetchUpdateRoleMenus} from '@/service/api';
import {$t} from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';

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
    label: node.menuName,
    children: node.children?.length ? toTreeOptions(node.children) : undefined
  }));
}

function collectKeys(nodes: Api.Menu.Item[]): number[] {
  return nodes.flatMap(node => [node.id, ...collectKeys(node.children || [])]);
}

function collectFirstLevelExpanded(nodes: Api.Menu.Item[]) {
  return nodes.map(node => node.id);
}

async function loadData() {
  state.loading = true;
  const [menuResp, checkedResp] = await Promise.all([fetchMenuTree(keyword.value || null), fetchRoleMenuIds(props.roleId!)]);
  state.loading = false;

  if (!menuResp.error) {
    menuTree.value = menuResp.data;
    expandedKeys.value = collectFirstLevelExpanded(menuResp.data);
  }

  if (!checkedResp.error) {
    checkedKeys.value = checkedResp.data;
  }
}

function closeModal() {
  visible.value = false;
}

function handleSelectAll() {
  checkedKeys.value = collectKeys(menuTree.value);
}

function handleClearSelection() {
  checkedKeys.value = [];
}

function handleExpandAll() {
  expandedKeys.value = collectKeys(menuTree.value);
}

function handleCollapseAll() {
  expandedKeys.value = [];
}

function renderLabel({option}: { option: TreeOption }) {
  return h('div', {class: 'flex min-w-0 items-center gap-8px'}, [
    h(
      NEllipsis,
      {class: 'min-w-0 flex-1', tooltip: false},
      {
        default: () => String(option.label ?? '')
      }
    )
  ]);
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
  <BusinessFormContainer v-model:visible="visible" :title="`${$t('page.role.menuTitle')} - ${roleName}`" :width="620">
    <NSpin :show="state.loading">
      <div class="mb-16px flex-col-stretch gap-12px">
        <div class="flex flex-wrap gap-12px">
          <NInput v-model:value="keyword" :placeholder="$t('common.keywordSearch')" clearable />
          <NButton type="primary" @click="loadData">{{ $t('common.search') }}</NButton>
        </div>

        <div class="flex flex-wrap gap-8px">
          <NButton quaternary size="small" @click="handleSelectAll">{{ $t('common.selectAll') }}</NButton>
          <NButton quaternary size="small" @click="handleClearSelection">{{ $t('common.clearSelection') }}</NButton>
          <NButton quaternary size="small" @click="handleExpandAll">{{ $t('common.expand') }}</NButton>
          <NButton quaternary size="small" @click="handleCollapseAll">{{ $t('common.collapse') }}</NButton>
        </div>
      </div>

      <div class="overflow-hidden rounded-12px border border-#e5e7eb p-12px">
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
          :render-label="renderLabel"
          class="max-h-70vh overflow-y-auto"
        />
      </div>
    </NSpin>
    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
