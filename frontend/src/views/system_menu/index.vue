<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {
  fetchDeletePlatformMenu,
  fetchMenuTree,
  fetchPlatformMenuTree,
  fetchUpdatePlatformMenuStatus
} from '@/service/api';
import {menuTypeRecord, statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {useContextStore} from '@/store/modules/context';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import MenuOperateModal from './modules/menu-operate-modal.vue';

defineOptions({
  name: 'SystemMenu'
});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const contextStore = useContextStore();
const {hasAuth} = useAuth();

const searchParams = ref({
  keyword: null as string | null,
  status: null as number | null
});

const modalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeMenuId = ref<number | null>(null);
const activeParentId = ref<number | null>(null);
const actionLoadingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const isPlatform = computed(() => contextStore.isPlatform);
const showAdd = computed(() => isPlatform.value && hasAuth('platform:menu:add'));
const showUpdate = computed(() => isPlatform.value && hasAuth('platform:menu:update'));
const showDelete = computed(() => isPlatform.value && hasAuth('platform:menu:delete'));

const {columns, columnChecks, data, loading, getData} = useNaiveTable({
  api: () => (isPlatform.value ? fetchPlatformMenuTree(searchParams.value.keyword) : fetchMenuTree(searchParams.value.keyword)),
  transform: response => {
    const {data: menus, error} = response;
    return error ? [] : menus;
  },
  columns: () => [
    {
      key: 'menuName',
      title: $t('page.menu.menuName'),
      minWidth: 220
    },
    {
      key: 'menuType',
      title: $t('page.menu.menuType'),
      width: 100,
      align: 'center',
      render: row => {
        const config = menuTypeRecord[row.menuType];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {
      key: 'routeName',
      title: $t('page.menu.routeName'),
      minWidth: 140,
      render: row => row.routeName || '-'
    },
    {
      key: 'routePath',
      title: $t('page.menu.routePath'),
      minWidth: 160,
      render: row => row.routePath || '-'
    },
    {
      key: 'componentPath',
      title: $t('page.menu.componentPath'),
      minWidth: 180,
      render: row => row.componentPath || '-'
    },
    {
      key: 'permissionCode',
      title: $t('page.menu.permissionCode'),
      minWidth: 180,
      render: row => row.permissionCode || '-'
    },
    {
      key: 'icon',
      title: $t('page.menu.icon'),
      minWidth: 120,
      render: row => row.icon || '-'
    },
    {
      key: 'i18nKey',
      title: $t('page.menu.i18nKey'),
      minWidth: 160,
      render: row => row.i18nKey || '-'
    },
    {
      key: 'sort',
      title: $t('page.menu.sort'),
      width: 80,
      align: 'center',
      render: row => row.sort ?? '-'
    },
    {
      key: 'visible',
      title: $t('page.menu.visible'),
      width: 90,
      align: 'center',
      render: row => (row.visible ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'keepAlive',
      title: $t('page.menu.keepAlive'),
      width: 90,
      align: 'center',
      render: row => (row.keepAlive ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'status',
      title: $t('page.menu.status'),
      width: 100,
      align: 'center',
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
      width: 280,
      render: row => {
        const nextStatus = row.status === 1 ? 0 : 1;
        const actionText = row.status === 1 ? $t('common.disable') : $t('common.enable');

        return (
            <TableRowActions
                actions={[
                  {key: 'detail', label: $t('common.detail'), type: 'info', onClick: () => openModal('detail', row.id)},
                  {
                    key: 'add',
                    label: $t('common.add'),
                    type: 'primary',
                    show: showAdd.value,
                    onClick: () => openModal('add', null, row.id)
                  },
                  {
                    key: 'edit',
                    label: $t('common.edit'),
                    type: 'primary',
                    show: showUpdate.value,
                    onClick: () => openModal('edit', row.id)
                  },
                  {
                    key: 'status',
                    label: actionText,
                    type: row.status === 1 ? 'warning' : 'success',
                    show: showUpdate.value,
                    loading: actionLoadingId.value === row.id,
                    confirmText: $t('page.menu.statusConfirm', {action: actionText, name: row.menuName}),
                    onClick: () => handleStatusChange(row.id, nextStatus)
                  },
                  {
                    key: 'delete',
                    label: $t('common.delete'),
                    type: 'error',
                    show: showDelete.value,
                    loading: deletingId.value === row.id,
                    confirmText: $t('page.menu.deleteConfirm', {name: row.menuName}),
                    onClick: () => handleDelete(row.id)
                  }
                ]}
            />
        );
      }
    }
  ]
});

const filteredData = computed(() => {
  if (searchParams.value.status == null) {
    return data.value;
  }

  const filterNodes = (nodes: Api.Menu.Item[]): Api.Menu.Item[] =>
      nodes
          .map(node => ({
            ...node,
            children: filterNodes(node.children || [])
          }))
          .filter(node => node.status === searchParams.value.status || node.children.length > 0);

  return filterNodes(data.value);
});

function openModal(mode: OperateMode, menuId: number | null = null, parentId: number | null = null) {
  operateMode.value = mode;
  activeMenuId.value = menuId;
  activeParentId.value = parentId;
  modalVisible.value = true;
}

function handleReset() {
  searchParams.value.keyword = null;
  searchParams.value.status = null;
  getData();
}

async function handleStatusChange(menuId: number, status: number) {
  actionLoadingId.value = menuId;
  const {error} = await fetchUpdatePlatformMenuStatus(menuId, status);
  actionLoadingId.value = null;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    await getData();
  }
}

async function handleDelete(menuId: number) {
  deletingId.value = menuId;
  const {error} = await fetchDeletePlatformMenu(menuId);
  deletingId.value = null;

  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getData();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SearchPanel :model="searchParams">
      <NFormItemGi :label="$t('page.menu.keyword')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.keyword" :placeholder="$t('common.keywordSearch')" clearable/>
      </NFormItemGi>
      <NFormItemGi :label="$t('page.menu.status')" class="pr-24px" span="24 s:12 m:6">
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
              <icon-ic-round-refresh class="text-icon"/>
            </template>
            {{ $t('common.reset') }}
          </NButton>
          <NButton ghost type="primary" @click="getData">
            <template #icon>
              <icon-ic-round-search class="text-icon"/>
            </template>
            {{ $t('common.search') }}
          </NButton>
        </NSpace>
      </NFormItemGi>
    </SearchPanel>

    <NCard :bordered="false" :title="$t('route.system_menu')" class="card-wrapper sm:flex-1-hidden" size="small">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton v-if="showAdd" ghost size="small" type="primary" @click="openModal('add')">
              <template #icon>
                <icon-ic-round-plus class="text-icon"/>
              </template>
              {{ $t('common.add') }}
            </NButton>
            <span v-else-if="!isPlatform" class="text-12px text-#999">{{ $t('page.menu.platformOnlyTip') }}</span>
            <span v-else class="hidden"></span>
          </template>
        </TableHeaderOperation>
      </template>

      <NDataTable
          :columns="columns"
          :data="filteredData"
          :flex-height="!appStore.isMobile"
          :loading="loading"
          :row-key="row => row.id"
          :scroll-x="2100"
          children-key="children"
          class="sm:h-full"
          size="small"
      />
    </NCard>

    <MenuOperateModal
        v-if="isPlatform"
        v-model:visible="modalVisible"
        :menu-id="activeMenuId"
        :mode="operateMode"
        :parent-id="activeParentId"
        @submitted="getData"
    />
  </div>
</template>
