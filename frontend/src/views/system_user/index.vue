<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {downloadUserExport, fetchDeleteUser, fetchUpdateUserStatus} from '@/service/api';
import {genderOptions, statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {formatDateTime} from '@/utils/date-time';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import UserOperateModal from './modules/user-operate-modal.vue';
import UserImportModal from './modules/user-import-modal.vue';
import UserPasswordModal from './modules/user-password-modal.vue';
import UserRoleModal from './modules/user-role-modal.vue';

defineOptions({
  name: 'SystemUser'
});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const {hasAuth} = useAuth();

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

const modalVisible = ref(false);
const importModalVisible = ref(false);
const passwordModalVisible = ref(false);
const roleModalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeUserId = ref<number | null>(null);
const activeUserName = ref('');
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);
const exporting = ref(false);

const showAdd = computed(() => hasAuth('system:user:add'));
const showImport = computed(() => hasAuth('system:user:import'));
const showExport = computed(() => hasAuth('system:user:export'));
const showUpdate = computed(() => hasAuth('system:user:update'));
const showDelete = computed(() => hasAuth('system:user:delete'));
const showPassword = computed(() => hasAuth('system:user:password'));
const showRole = computed(() => hasAuth('system:user:role'));

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => import('@/service/api').then(({fetchUserPage}) => fetchUserPage(searchParams.value)),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      key: 'index',
      title: $t('common.index'),
      width: 72,
      align: 'center',
      render: (_, index) => (searchParams.value.pageNum - 1) * searchParams.value.pageSize + index + 1
    },
    {
      key: 'username',
      title: $t('page.user.username'),
      minWidth: 140
    },
    {
      key: 'nickname',
      title: $t('page.user.nickname'),
      minWidth: 140
    },
    {
      key: 'departmentName',
      title: $t('page.user.department'),
      minWidth: 180,
      render: row => row.departmentName || '-'
    },
    {
      key: 'roles',
      title: $t('page.user.role'),
      minWidth: 220,
      render: row => row.roles?.length ? row.roles.map(item => item.roleName).join(' / ') : '-'
    },
    {
      key: 'gender',
      title: $t('page.user.gender'),
      width: 100,
      align: 'center',
      render: row => {
        const gender = genderOptions.find(item => item.value === (row.gender || ''));
        return gender ? $t(gender.label) : '-';
      }
    },
    {
      key: 'phone',
      title: $t('page.user.phone'),
      minWidth: 140,
      render: row => row.phone || '-'
    },
    {
      key: 'email',
      title: $t('page.user.email'),
      minWidth: 220,
      render: row => row.email || '-'
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
    },
    {
      key: 'lastLoginTime',
      title: $t('page.user.lastLoginTime'),
      minWidth: 180,
      render: row => formatDateTime(row.lastLoginTime)
    },
    {
      key: 'createTime',
      title: $t('page.user.createTime'),
      minWidth: 180,
      render: row => formatDateTime(row.createTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      width: 260,
      align: 'center',
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
                    onClick: () => openModal('detail', row.id, row.nickname)
                  },
                  {
                    key: 'edit',
                    label: $t('common.edit'),
                    type: 'primary',
                    show: showUpdate.value,
                    onClick: () => openModal('edit', row.id, row.nickname)
                  },
                  {
                    key: 'role',
                    label: $t('page.user.roleSetting'),
                    type: 'primary',
                    show: showRole.value,
                    onClick: () => openRoleModal(row.id, row.nickname)
                  },
                  {
                    key: 'password',
                    label: $t('page.user.resetPassword'),
                    type: 'warning',
                    show: showPassword.value,
                    onClick: () => openPasswordModal(row.id, row.nickname)
                  },
                  {
                    key: 'status',
                    label: actionText,
                    type: row.status === 1 ? 'warning' : 'success',
                    show: showUpdate.value,
                    loading: actionLoadingId.value === row.id,
                    confirmText: $t('page.user.statusConfirm', {action: actionText, name: row.nickname}),
                    onClick: () => handleStatusChange(row.id, nextStatus)
                  },
                  {
                    key: 'delete',
                    label: $t('common.delete'),
                    type: 'error',
                    show: showDelete.value,
                    loading: deletingId.value === row.id,
                    confirmText: $t('page.user.deleteConfirm', {name: row.nickname}),
                    onClick: () => handleDelete(row.id)
                  }
                ]}
            />
        );
      }
    }
  ]
});

function openModal(mode: OperateMode, userId: number | null = null, username = '') {
  operateMode.value = mode;
  activeUserId.value = userId;
  activeUserName.value = username;
  modalVisible.value = true;
}

function openPasswordModal(userId: number, username: string) {
  activeUserId.value = userId;
  activeUserName.value = username;
  passwordModalVisible.value = true;
}

function openRoleModal(userId: number, username: string) {
  activeUserId.value = userId;
  activeUserName.value = username;
  roleModalVisible.value = true;
}

function handleReset() {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
    username: null,
    nickname: null,
    phone: null,
    email: null,
    departmentId: null,
    status: null
  };
  getDataByPage(1);
}

async function handleStatusChange(userId: number, status: number) {
  actionLoadingId.value = userId;
  const {error} = await fetchUpdateUserStatus(userId, status);
  actionLoadingId.value = null;
  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

async function handleDelete(userId: number) {
  deletingId.value = userId;
  const {error} = await fetchDeleteUser(userId);
  deletingId.value = null;
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
  }
}

async function handleExport() {
  exporting.value = true;

  try {
    await downloadUserExport(searchParams.value);
  } catch {
    window.$message?.error($t('common.error'));
  } finally {
    exporting.value = false;
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.user.username')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.username" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.nickname')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.nickname" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.phone')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.phone" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
          clearable
        />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.user.email')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.email" clearable />
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

    <NCard :bordered="false" :title="$t('route.system_user')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton v-if="showAdd" ghost size="small" type="primary" @click="openModal('add')">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
              {{ $t('common.add') }}
            </NButton>
            <NButton v-if="showImport" ghost size="small" type="primary" @click="importModalVisible = true">
              <template #icon>
                <icon-mdi-file-import-outline class="text-icon" />
              </template>
              {{ $t('common.import') }}
            </NButton>
            <NButton v-if="showExport" :loading="exporting" ghost size="small" type="primary" @click="handleExport">
              <template #icon>
                <icon-mdi-file-export-outline class="text-icon" />
              </template>
              {{ $t('common.export') }}
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
        :scroll-x="2200"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>

    <UserOperateModal v-model:visible="modalVisible" :mode="operateMode" :user-id="activeUserId" @submitted="getData" />
    <UserImportModal v-model:visible="importModalVisible" @submitted="getDataByPage(1)" />
    <UserPasswordModal
      v-model:visible="passwordModalVisible"
      :user-id="activeUserId"
      :username="activeUserName"
      @submitted="getData"
    />
    <UserRoleModal
      v-model:visible="roleModalVisible"
      :user-id="activeUserId"
      :username="activeUserName"
      @submitted="getData"
    />
  </div>
</template>
