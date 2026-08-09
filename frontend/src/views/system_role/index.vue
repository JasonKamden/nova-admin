<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {dataScopeOptions, statusOptions, statusRecord} from '@/constants/business';
import {TABLE_COLUMN_WIDTH} from '@/constants/table-column';
import {fetchDeleteRole, fetchRolePage, fetchUpdateRoleStatus} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {formatDateTime} from '@/utils/date-time';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import RoleMenuModal from './modules/role-menu-modal.vue';
import RoleOperateModal from './modules/role-operate-modal.vue';

defineOptions({
  name: 'SystemRole'
});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const {hasAuth} = useAuth();

const searchParams = ref<Api.Role.PageParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: null,
  status: null
});

const modalVisible = ref(false);
const menuModalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeRoleId = ref<number | null>(null);
const activeRoleName = ref('');
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('system:role:add'));
const showUpdate = computed(() => hasAuth('system:role:update'));
const showDelete = computed(() => hasAuth('system:role:delete'));
const showMenu = computed(() => hasAuth('system:role:menu'));

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => fetchRolePage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      key: 'index',
      title: $t('common.index'),
      align: 'center',
      width: 72,
      render: (_, index) => (searchParams.value.pageNum - 1) * searchParams.value.pageSize + index + 1
    },
    {
      key: 'roleCode',
      title: $t('page.role.roleCode'),
      minWidth: 140
    },
    {
      key: 'roleName',
      title: $t('page.role.roleName'),
      minWidth: 160
    },
    {
      key: 'dataScope',
      title: $t('page.role.dataScope'),
      minWidth: 220,
      ellipsis: {tooltip: true},
      render: row => {
        const option = dataScopeOptions.find(item => item.value === row.dataScope);
        return option ? $t(option.label) : row.dataScope;
      }
    },
    {
      key: 'builtIn',
      title: $t('page.role.builtIn'),
      width: 100,
      align: 'center',
      render: row => (row.builtIn ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'sort',
      title: $t('page.role.sort'),
      width: 90,
      align: 'center',
      render: row => row.sort ?? '-'
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
    },
    {
      key: 'remark',
      title: $t('page.role.remark'),
      minWidth: 220,
      ellipsis: {tooltip: true},
      render: row => row.remark || '-'
    },
    {
      key: 'createTime',
      title: $t('page.role.createTime'),
      width: TABLE_COLUMN_WIDTH.DATETIME,
      render: row => formatDateTime(row.createTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      align: 'center',
      width: 260,
      render: row => {
        const nextStatus = row.status === 1 ? 0 : 1;
        const actionText = row.status === 1 ? $t('common.disable') : $t('common.enable');

        return (
            <TableRowActions
                actions={[
                  {
                    key: 'detail',
                    label: $t('common.detail'),
                    type: 'info',
                    onClick: () => openModal('detail', row.id, row.roleName)
                  },
                  {
                    key: 'edit',
                    label: $t('common.edit'),
                    type: 'primary',
                    show: showUpdate.value,
                    onClick: () => openModal('edit', row.id, row.roleName)
                  },
                  {
                    key: 'menu',
                    label: $t('page.role.menuSetting'),
                    type: 'primary',
                    show: showMenu.value,
                    onClick: () => openMenuModal(row.id, row.roleName)
                  },
                  {
                    key: 'status',
                    label: actionText,
                    type: row.status === 1 ? 'warning' : 'success',
                    show: showUpdate.value,
                    loading: actionLoadingId.value === row.id,
                    confirmText: $t('page.role.statusConfirm', {action: actionText, name: row.roleName}),
                    onClick: () => handleStatusChange(row.id, nextStatus)
                  },
                  {
                    key: 'delete',
                    label: $t('common.delete'),
                    type: 'error',
                    show: showDelete.value,
                    loading: deletingId.value === row.id,
                    confirmText: $t('page.role.deleteConfirm', {name: row.roleName}),
                    onClick: () => handleDelete(row.id)
                  }
                ]}
            />
        );
      }
    }
  ]
});

function openModal(mode: OperateMode, roleId: number | null = null, roleName = '') {
  operateMode.value = mode;
  activeRoleId.value = roleId;
  activeRoleName.value = roleName;
  modalVisible.value = true;
}

function openMenuModal(roleId: number, roleName: string) {
  activeRoleId.value = roleId;
  activeRoleName.value = roleName;
  menuModalVisible.value = true;
}

function handleReset() {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
    keyword: null,
    status: null
  };
  getDataByPage(1);
}

async function handleStatusChange(roleId: number, status: number) {
  actionLoadingId.value = roleId;
  const {error} = await fetchUpdateRoleStatus(roleId, status);
  actionLoadingId.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

async function handleDelete(roleId: number) {
  deletingId.value = roleId;
  const {error} = await fetchDeleteRole(roleId);
  deletingId.value = null;
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.role.keyword')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.keyword" :placeholder="$t('common.keywordSearch')" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.role.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
          clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:12">
        <NSpace class="w-full" justify="end">
          <NButton @click="handleReset">
            <template #icon>
              <icon-ic-round-refresh class="text-icon" />
            </template>
            {{ $t('common.reset') }}
          </NButton>
          <NButton ghost type="primary" @click="getDataByPage(1)">
            <template #icon>
              <icon-ic-round-search class="text-icon" />
            </template>
            {{ $t('common.search') }}
          </NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>

    <NCard :bordered="false" :title="$t('route.system_role')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton v-if="showAdd" ghost size="small" type="primary" @click="openModal('add')">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
              {{ $t('common.add') }}
            </NButton>
          </template>
        </TableHeaderOperation>
      </template>

      <NDataTable
        :columns="columns"
        :data="data"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        :pagination="mobilePagination"
        :row-key="row => row.id"
        :scroll-x="1850"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>

    <RoleOperateModal v-model:visible="modalVisible" :mode="operateMode" :role-id="activeRoleId" @submitted="getData" />
    <RoleMenuModal
      v-model:visible="menuModalVisible"
      :role-id="activeRoleId"
      :role-name="activeRoleName"
      @submitted="getData"
    />
  </div>
</template>
