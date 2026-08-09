<script lang="tsx" setup>
import { computed, reactive, ref } from 'vue';
import { NButton, NSpace, NTag } from 'naive-ui';
import type { DataTableRowKey } from 'naive-ui';
import { dataScopeOptions, statusOptions, statusRecord } from '@/constants/business';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import { fetchRolePage } from '@/service/api';
import SearchPanel from '@/components/advanced/search-panel.vue';

defineOptions({
  name: 'RoleSelectorTable'
});

interface Props {
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

const selectedIds = defineModel<number[]>('selectedIds', { default: [] });

const filters = reactive({
  roleName: null as string | null,
  roleCode: null as string | null,
  status: null as number | null
});

const searchParams = ref<Api.Role.PageParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: null,
  status: null
});

const {
  columns,
  data,
  loading,
  getDataByPage,
  mobilePagination
} = useNaivePaginatedTable({
  api: () => fetchRolePage(searchParams.value),
  transform: response => defaultTransform(response),
  showTotal: false,
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      type: 'selection',
      width: 48,
      multiple: !props.disabled
    },
    {
      key: 'index',
      title: $t('common.index'),
      align: 'center',
      width: 72,
      render: (_, index) => (searchParams.value.pageNum - 1) * searchParams.value.pageSize + index + 1
    },
    {
      key: 'roleName',
      title: $t('page.role.roleName'),
      minWidth: 180
    },
    {
      key: 'roleCode',
      title: $t('page.role.roleCode'),
      minWidth: 180
    },
    {
      key: 'dataScope',
      title: $t('page.role.dataScope'),
      minWidth: 180,
      render: row => $t(dataScopeOptions.find(item => item.value === row.dataScope)?.label || 'common.noData')
    },
    {
      key: 'status',
      title: $t('page.role.status'),
      width: 100,
      align: 'center',
      render: row => {
        const config = statusRecord[row.status as keyof typeof statusRecord];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    }
  ]
});

const checkedRowKeys = computed<DataTableRowKey[]>(() => selectedIds.value);

function syncRequestParams() {
  const roleName = filters.roleName?.trim() || '';
  const roleCode = filters.roleCode?.trim() || '';

  searchParams.value.keyword = roleName || roleCode || null;
  searchParams.value.status = filters.status;
}

function handleReset() {
  filters.roleName = null;
  filters.roleCode = null;
  filters.status = null;
  syncRequestParams();
  void getDataByPage(1);
}

function handleSearch() {
  syncRequestParams();
  void getDataByPage(1);
}

function clearSelection() {
  selectedIds.value = [];
}

function handleCheckedRowKeys(keys: DataTableRowKey[]) {
  const pageIds = new Set(data.value.map(item => item.id));
  const preservedIds = selectedIds.value.filter(id => !pageIds.has(id));
  const nextIds = [...preservedIds, ...keys.map(key => Number(key))];

  selectedIds.value = Array.from(new Set(nextIds));
}
</script>

<template>
  <div class="flex-col-stretch gap-12px">
    <SearchPanel :model="filters">
      <NFormItemGi :label="$t('page.role.roleName')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.roleName" :disabled="disabled" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.role.roleCode')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.roleCode" :disabled="disabled" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.role.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="filters.status"
          :disabled="disabled"
          :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
          clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:6">
        <NSpace class="w-full" justify="end">
          <NButton :disabled="disabled" @click="handleReset">{{ $t('common.reset') }}</NButton>
          <NButton :disabled="disabled" ghost type="primary" @click="handleSearch">{{ $t('common.search') }}</NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>

    <div class="flex items-center justify-between gap-12px">
      <span class="text-14px text-text-secondary">{{ $t('common.selectedItems', { count: selectedIds.length }) }}</span>
      <NButton v-if="!disabled && selectedIds.length" quaternary size="small" type="primary" @click="clearSelection">
        {{ $t('common.clearSelection') }}
      </NButton>
    </div>

    <NDataTable
      :checked-row-keys="checkedRowKeys"
      :columns="columns"
      :data="data"
      :loading="loading"
      :pagination="mobilePagination"
      :remote="true"
      :row-key="row => row.id"
      :scroll-x="760"
      max-height="420"
      size="small"
      @update:checked-row-keys="handleCheckedRowKeys"
    />
  </div>
</template>
