<script lang="ts" setup>
import {$t} from '@/locales';

defineOptions({
  name: 'TableHeaderOperation'
});

interface Props {
  itemAlign?: NaiveUI.Align;
  disabledDelete?: boolean;
  loading?: boolean;
}

defineProps<Props>();

interface Emits {
  (e: 'add'): void;

  (e: 'delete'): void;

  (e: 'import'): void;

  (e: 'export'): void;

  (e: 'refresh'): void;
}

const emit = defineEmits<Emits>();

const columns = defineModel<NaiveUI.TableColumnCheck[]>('columns', {
  default: () => []
});

function add() {
  emit('add');
}

function batchDelete() {
  emit('delete');
}

function refresh() {
  emit('refresh');
}

function importData() {
  emit('import');
}

function exportData() {
  emit('export');
}
</script>

<template>
  <NSpace :align="itemAlign" class="lt-sm:w-200px" justify="end" wrap>
    <slot name="prefix"></slot>
    <slot name="default">
      <NButton ghost size="small" type="primary" @click="add">
        <template #icon>
          <icon-ic-round-plus class="text-icon"/>
        </template>
        {{ $t('common.add') }}
      </NButton>
      <NPopconfirm @positive-click="batchDelete">
        <template #trigger>
          <NButton :disabled="disabledDelete" ghost size="small" type="error">
            <template #icon>
              <icon-ic-round-delete class="text-icon"/>
            </template>
            {{ $t('common.batchDelete') }}
          </NButton>
        </template>
        {{ $t('common.confirmDelete') }}
      </NPopconfirm>
    </slot>
    <slot name="actions"></slot>
    <slot name="import-trigger">
      <NButton v-if="$slots.importTrigger" ghost size="small" type="primary" @click="importData">
        <template #icon>
          <icon-mdi-file-import-outline class="text-icon"/>
        </template>
        {{ $t('common.import') }}
      </NButton>
    </slot>
    <slot name="export-trigger">
      <NButton v-if="$slots.exportTrigger" ghost size="small" type="primary" @click="exportData">
        <template #icon>
          <icon-mdi-file-export-outline class="text-icon"/>
        </template>
        {{ $t('common.export') }}
      </NButton>
    </slot>
    <NButton size="small" @click="refresh">
      <template #icon>
        <icon-mdi-refresh :class="{ 'animate-spin': loading }" class="text-icon"/>
      </template>
      {{ $t('common.refresh') }}
    </NButton>
    <TableColumnSetting v-model:columns="columns"/>
    <slot name="suffix"></slot>
  </NSpace>
</template>

<style scoped></style>
