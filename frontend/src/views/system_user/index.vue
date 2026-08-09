<script lang="tsx" setup>
import {computed, ref} from 'vue';
import dayjs from 'dayjs';
import {NButton, NPopconfirm, NSpace, NTag} from 'naive-ui';
import {
    fetchDeleteUser,
    fetchUpdateUserStatus
} from '@/service/api';
import {genderOptions, statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import UserOperateModal from './modules/user-operate-modal.vue';
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
const passwordModalVisible = ref(false);
const roleModalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeUserId = ref<number | null>(null);
const activeUserName = ref('');
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('system:user:add'));
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
            render: row => (row.lastLoginTime ? dayjs(row.lastLoginTime).format('YYYY-MM-DD HH:mm:ss') : '-')
        },
        {
            key: 'createTime',
            title: $t('page.user.createTime'),
            minWidth: 180,
            render: row => dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
        },
        {
            key: 'operate',
            title: $t('common.operate'),
            fixed: 'right',
            width: 430,
            align: 'center',
            render: row => {
                const nextStatus = row.status === 1 ? 0 : 1;
                const actionText = row.status === 1 ? $t('common.disable') : $t('common.enable');

                return (
                    <NSpace justify="center" size={8} wrap={false}>
                        <NButton ghost size="small" type="info" onClick={() => openModal('detail', row.id, row.nickname)}>
                            {$t('common.detail')}
                        </NButton>
                        {showUpdate.value ? (
                            <NButton ghost size="small" type="primary" onClick={() => openModal('edit', row.id, row.nickname)}>
                                {$t('common.edit')}
                            </NButton>
                        ) : null}
                        {showRole.value ? (
                            <NButton ghost size="small" type="primary" onClick={() => openRoleModal(row.id, row.nickname)}>
                                {$t('page.user.roleSetting')}
                            </NButton>
                        ) : null}
                        {showPassword.value ? (
                            <NButton ghost size="small" type="warning" onClick={() => openPasswordModal(row.id, row.nickname)}>
                                {$t('page.user.resetPassword')}
                            </NButton>
                        ) : null}
                        {showUpdate.value ? (
                            <NPopconfirm onPositiveClick={() => handleStatusChange(row.id, nextStatus)}>
                                {{
                                    default: () => $t('page.user.statusConfirm', {action: actionText, name: row.nickname}),
                                    trigger: () => (
                                        <NButton
                                            ghost
                                            size="small"
                                            type={row.status === 1 ? 'warning' : 'success'}
                                            loading={actionLoadingId.value === row.id}
                                        >
                                            {actionText}
                                        </NButton>
                                    )
                                }}
                            </NPopconfirm>
                        ) : null}
                        {showDelete.value ? (
                            <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
                                {{
                                    default: () => $t('page.user.deleteConfirm', {name: row.nickname}),
                                    trigger: () => (
                                        <NButton ghost size="small" type="error" loading={deletingId.value === row.id}>
                                            {$t('common.delete')}
                                        </NButton>
                                    )
                                }}
                            </NPopconfirm>
                        ) : null}
                    </NSpace>
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
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :model="searchParams" :label-width="92" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.user.username')" span="24 s:12 m:6">
            <NInput v-model:value="searchParams.username" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.nickname')" span="24 s:12 m:6">
            <NInput v-model:value="searchParams.nickname" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.phone')" span="24 s:12 m:6">
            <NInput v-model:value="searchParams.phone" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.status')" span="24 s:12 m:6">
            <NSelect
              v-model:value="searchParams.status"
              :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
              clearable
            />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.user.email')" span="24 s:12 m:8">
            <NInput v-model:value="searchParams.email" clearable />
          </NFormItemGi>
          <NFormItemGi span="24 m:16">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getDataByPage(1)">{{ $t('common.search') }}</NButton>
              <NButton @click="handleReset">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>

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
