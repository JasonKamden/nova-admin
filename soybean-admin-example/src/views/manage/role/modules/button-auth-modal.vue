<script lang="ts" setup>
import {computed, shallowRef} from 'vue';
import {$t} from '@/locales';

defineOptions({
  name: 'ButtonAuthModal'
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

const title = computed(() => $t('common.edit') + $t('page.manage.role.buttonAuth'));

type ButtonConfig = {
  id: number;
  label: string;
  code: string;
};

const tree = shallowRef<ButtonConfig[]>([]);

async function getAllButtons() {
  // request
  tree.value = [
    {id: 1, label: 'button1', code: 'code1'},
    {id: 2, label: 'button2', code: 'code2'},
    {id: 3, label: 'button3', code: 'code3'},
    {id: 4, label: 'button4', code: 'code4'},
    {id: 5, label: 'button5', code: 'code5'},
    {id: 6, label: 'button6', code: 'code6'},
    {id: 7, label: 'button7', code: 'code7'},
    {id: 8, label: 'button8', code: 'code8'},
    {id: 9, label: 'button9', code: 'code9'},
    {id: 10, label: 'button10', code: 'code10'}
  ];
}

const checks = shallowRef<number[]>([]);

async function getChecks() {
  console.log(props.roleId);
  // request
  checks.value = [1, 2, 3, 4, 5];
}

function handleSubmit() {
  console.log(checks.value, props.roleId);
  // request

  window.$message?.success?.($t('common.modifySuccess'));

  closeModal();
}

function init() {
  getAllButtons();
  getChecks();
}

// init
init();
</script>

<template>
  <NModal v-model:show="visible" :title="title" class="w-480px" preset="card">
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
