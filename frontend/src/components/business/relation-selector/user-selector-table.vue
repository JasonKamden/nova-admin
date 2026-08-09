<script lang="tsx" setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { NButton, NSpace, NTag } from 'naive-ui';
import type { DataTableRowKey } from 'naive-ui';
import { statusOptions, statusRecord } from '@/constants/business';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import { fetchDepartmentSelector, fetchUserPage } from '@/service/api';
import SearchPanel from '@/components/advanced/search-panel.vue';
import { toDepartmentTreeOptions } from '@/views/system_department/modules/shared';

defineOptions({
  name: 'UserSelectorTable'
});

interface Props {
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

const selectedIds = defineModel<number[]>('selectedIds', { default: [] });

const filters = reactive({
  username: null as string | null,
  nickname: null as string | null,
  departmentId: null as number | null,
  status: null as number | null
});

const searchParams = ref<Api.User.PageParams>({
  pageNum: 1,
  pageSize: 10,
  username: null,
  nickname: null,
  phone: null,
  email: null,
  departmentId: null,
  status: null
});

const departmentOptions = ref<Api.Department.TreeOption[]>([]);

const {
  columns,
  data,
  loading,
  getDataByPage,
  mobilePagination
} = useNaivePaginatedTable({
  api: () => fetchUserPage(searchParams.value),
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
      key: 'username',
      title: $t('page.user.username'),
      minWidth: 160
    },
    {
      key: 'nickname',
      title: $t('page.user.nickname'),
      minWidth: 160
    },
    {
      key: 'departmentName',
      title: $t('page.user.department'),
      minWidth: 180,
      render: row => row.departmentName || '-'
    },
    {
      key: 'status',
      title: $t('page.user.status'),
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

async function loadDepartments() {
  const { data: departmentTree, error } = await fetchDepartmentSelector();

  if (!error) {
    departmentOptions.value = toDepartmentTreeOptions(departmentTree);
  }
}

function syncRequestParams() {
  searchParams.value.username = filters.username?.trim() || null;
  searchParams.value.nickname = filters.nickname?.trim() || null;
  searchParams.value.departmentId = filters.departmentId;
  searchParams.value.status = filters.status;
}

function handleReset() {
  filters.username = null;
  filters.nickname = null;
  filters.departmentId = null;
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

onMounted(() => {
  void loadDepartments();
});
</script>

<template>
  <div class="flex-col-stretch gap-12px">
    <SearchPanel :model="filters">
      <NFormItemGi :label="$t('page.user.username')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.username" :disabled="disabled" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.nickname')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="filters.nickname" :disabled="disabled" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.department')" class="pr-24px" span="24 s:12 m:6">
        <NTreeSelect
          v-model:value="filters.departmentId"
          :disabled="disabled"
          :options="departmentOptions"
          clearable
          key-field="key"
          label-field="label"
          value-field="value"
        />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="filters.status"
          :disabled="disabled"
          :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
          clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24">
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
