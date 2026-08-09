<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NPopconfirm, NSpace, NTag} from 'naive-ui';
import {fetchDeleteDepartment, fetchDepartmentTree, fetchUpdateDepartmentStatus} from '@/service/api';
import {statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import DepartmentOperateModal from './modules/department-operate-modal.vue';

defineOptions({
    name: 'SystemDepartment'
});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const {hasAuth} = useAuth();

const searchParams = ref<Api.Department.QueryParams>({
    keyword: null,
    status: null
});

const modalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeDepartmentId = ref<number | null>(null);
const activeParentId = ref<number | null>(null);
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('system:department:add'));
const showUpdate = computed(() => hasAuth('system:department:update'));
const showDelete = computed(() => hasAuth('system:department:delete'));

const {columns, columnChecks, data, loading, getData, scrollX} = useNaiveTable({
    api: () => fetchDepartmentTree(searchParams.value),
    transform: response => {
        const {data: departments, error} = response;

        return error ? [] : departments;
    },
    columns: () => [
        {
            key: 'departmentName',
            title: $t('page.department.departmentName'),
            minWidth: 220
        },
        {
            key: 'departmentCode',
            title: $t('page.department.departmentCode'),
            minWidth: 160
        },
        {
            key: 'leaderName',
            title: $t('page.department.leaderUser'),
            minWidth: 160,
            render: row => row.leaderName || '-'
        },
        {
            key: 'phone',
            title: $t('page.department.phone'),
            minWidth: 150,
            render: row => row.phone || '-'
        },
        {
            key: 'email',
            title: $t('page.department.email'),
            minWidth: 220,
            render: row => row.email || '-'
        },
        {
            key: 'sort',
            title: $t('page.department.sort'),
            width: 100,
            align: 'center',
            render: row => row.sort ?? '-'
        },
        {
            key: 'status',
            title: $t('page.department.status'),
            align: 'center',
            width: 100,
            render: row => {
                const config = statusRecord[row.status as keyof typeof statusRecord];

                return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
            }
        },
        {
            key: 'operate',
            title: $t('common.operate'),
            fixed: 'right',
            align: 'center',
            width: 360,
            render: row => {
                const nextStatus = row.status === 1 ? 0 : 1;
                const actionText = row.status === 1 ? $t('common.disable') : $t('common.enable');

                return (
                    <NSpace justify="center" size={8} wrap={false}>
                        <NButton ghost size="small" type="info" onClick={() => openModal('detail', row.id, null)}>
                            {$t('common.detail')}
                        </NButton>
                        {showAdd.value ? (
                            <NButton ghost size="small" type="primary" onClick={() => openModal('add', null, row.id)}>
                                {$t('page.department.addChild')}
                            </NButton>
                        ) : null}
                        {showUpdate.value ? (
                            <NButton ghost size="small" type="primary" onClick={() => openModal('edit', row.id, null)}>
                                {$t('common.edit')}
                            </NButton>
                        ) : null}
                        {showUpdate.value ? (
                            <NPopconfirm onPositiveClick={() => handleStatusChange(row.id, nextStatus)}>
                                {{
                                    default: () => $t('page.department.statusConfirm', {action: actionText, name: row.departmentName}),
                                    trigger: () => (
                                        <NButton
                                            ghost
                                            loading={actionLoadingId.value === row.id}
                                            size="small"
                                            type={row.status === 1 ? 'warning' : 'success'}
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
                                    default: () => $t('page.department.deleteConfirm', {name: row.departmentName}),
                                    trigger: () => (
                                        <NButton ghost loading={deletingId.value === row.id} size="small" type="error">
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

function openModal(mode: OperateMode, departmentId: number | null, parentId: number | null) {
    operateMode.value = mode;
    activeDepartmentId.value = departmentId;
    activeParentId.value = parentId;
    modalVisible.value = true;
}

function handleReset() {
    searchParams.value.keyword = null;
    searchParams.value.status = null;
    getData();
}

async function handleStatusChange(id: number, status: number) {
    actionLoadingId.value = id;

    const {error} = await fetchUpdateDepartmentStatus(id, status);

    actionLoadingId.value = null;

    if (!error) {
        window.$message?.success($t('common.updateSuccess'));
        await getData();
    }
}

async function handleDelete(id: number) {
    deletingId.value = id;

    const {error} = await fetchDeleteDepartment(id);

    deletingId.value = null;

    if (!error) {
        window.$message?.success($t('common.deleteSuccess'));
        await getData();
    }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" class="card-wrapper" size="small">
      <NForm :label-width="92" :model="searchParams" label-placement="left">
        <NGrid :cols="24" :x-gap="16">
          <NFormItemGi :label="$t('page.department.keyword')" span="24 s:12 m:8">
            <NInput v-model:value="searchParams.keyword" :placeholder="$t('common.keywordSearch')" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.department.status')" span="24 s:12 m:6">
            <NSelect
              v-model:value="searchParams.status"
              :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
              clearable
            />
          </NFormItemGi>
          <NFormItemGi span="24 m:10">
            <NSpace class="w-full" justify="end">
              <NButton type="primary" @click="getData">{{ $t('common.search') }}</NButton>
              <NButton @click="handleReset">{{ $t('common.reset') }}</NButton>
            </NSpace>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NCard>

    <NCard :bordered="false" :title="$t('route.system_department')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton v-if="showAdd" ghost size="small" type="primary" @click="openModal('add', null, null)">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
              {{ $t('page.department.addRoot') }}
            </NButton>
          </template>
        </TableHeaderOperation>
      </template>

      <NDataTable
        children-key="children"
        :columns="columns"
        :data="data"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        :row-key="row => row.id"
        :scroll-x="scrollX"
        class="sm:h-full"
        size="small"
      />
    </NCard>

    <DepartmentOperateModal
      v-model:visible="modalVisible"
      :department-id="activeDepartmentId"
      :mode="operateMode"
      :parent-id="activeParentId"
      @submitted="getData"
    />
  </div>
</template>
