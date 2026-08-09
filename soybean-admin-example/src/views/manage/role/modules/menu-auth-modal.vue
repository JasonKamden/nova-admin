<script lang="ts" setup>
import {computed, shallowRef, watch} from 'vue';
import {fetchGetAllPages, fetchGetMenuTree} from '@/service/api';
import {$t} from '@/locales';

defineOptions({
  name: 'MenuAuthModal'
});

interface Props {
  /** the roleId */
  roleId: number;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

const title = computed(() => $t('common.edit') + $t('page.manage.role.menuAuth'));

const home = shallowRef('');

async function getHome() {
  console.log(props.roleId);

  home.value = 'home';
}

async function updateHome(val: string) {
  // request

  home.value = val;
}

const pages = shallowRef<string[]>([]);

async function getPages() {
  const {error, data} = await fetchGetAllPages();

  if (!error) {
    pages.value = data;
  }
}

const pageSelectOptions = computed(() => {
  const opts: CommonType.Option[] = pages.value.map(page => ({
    label: page,
    value: page
  }));

  return opts;
});

const tree = shallowRef<Api.SystemManage.MenuTree[]>([]);

async function getTree() {
  const {error, data} = await fetchGetMenuTree();

  if (!error) {
    tree.value = data;
  }
}

const checks = shallowRef<number[]>([]);

async function getChecks() {
  console.log(props.roleId);
  // request
  checks.value = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21];
}

function handleSubmit() {
  console.log(checks.value, props.roleId);
  // request

  window.$message?.success?.($t('common.modifySuccess'));

  closeModal();
}

function init() {
  getHome();
  getPages();
  getTree();
  getChecks();
}

watch(visible, val => {
  if (val) {
    init();
  }
});
</script>

<template>
  <NModal v-model:show="visible" :title="title" class="w-480px" preset="card">
    <div class="flex-y-center gap-16px pb-12px">
      <div>{{ $t('page.manage.menu.home') }}</div>
      <NSelect :options="pageSelectOptions" :value="home" class="w-160px" size="small" @update:value="updateHome"/>
    </div>
    <NTree
        v-model:checked-keys="checks"
        :data="tree"
        block-line
        checkable
        class="h-280px"
        expand-on-click
        key-field="id"
        virtual-scroll
    />
    <template #footer>
      <NSpace justify="end">
        <NButton class="mt-16px" size="small" @click="closeModal">
          {{ $t('common.cancel') }}
        </NButton>
        <NButton class="mt-16px" size="small" type="primary" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
