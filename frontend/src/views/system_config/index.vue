<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NSpace, NTag} from 'naive-ui';
import {configTypeOptions, statusOptions, statusRecord} from '@/constants/business';
import {fetchConfigPage, fetchDeleteConfig} from '@/service/api';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {formatDateTime} from '@/utils/date-time';
import SearchPanel from '@/components/advanced/search-panel.vue';
import TableRowActions from '@/components/advanced/table-row-actions.vue';
import ConfigOperateModal from './modules/config-operate-modal.vue';

defineOptions({name: 'SystemConfig'});

type OperateMode = 'add' | 'edit' | 'detail';

const appStore = useAppStore();
const {hasAuth} = useAuth();

const searchParams = ref<Api.Config.PageParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: null,
  configType: null,
  status: null
});

const modalVisible = ref(false);
const operateMode = ref<OperateMode>('add');
const activeItem = ref<Api.Config.Item | null>(null);
const deletingId = ref<number | null>(null);

const showAdd = computed(() => hasAuth('system:config:add'));
const showUpdate = computed(() => hasAuth('system:config:update'));
const showDelete = computed(() => hasAuth('system:config:delete'));

const {columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination} = useNaivePaginatedTable({
  api: () => fetchConfigPage(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.pageNum = params.page || 1;
    searchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {key: 'configName', title: $t('page.config.configName'), minWidth: 150},
    {key: 'configCode', title: $t('page.config.configCode'), minWidth: 160},
    {key: 'configValue', title: $t('page.config.configValue'), minWidth: 180},
    {
      key: 'configType',
      title: $t('page.config.configType'),
      width: 110,
      align: 'center',
      render: row => {
        const option = configTypeOptions.find(item => item.value === row.configType);
        return option ? $t(option.label) : row.configType;
      }
    },
    {
      key: 'sensitive',
      title: $t('page.config.sensitive'),
      width: 90,
      align: 'center',
      render: row => (row.sensitive ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'builtIn',
      title: $t('page.config.builtIn'),
      width: 90,
      align: 'center',
      render: row => (row.builtIn ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
    {
      key: 'status',
      title: $t('page.config.status'),
      width: 90,
      align: 'center',
      render: row => {
        const config = statusRecord[row.status as keyof typeof statusRecord];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {key: 'remark', title: $t('page.config.remark'), minWidth: 180, render: row => row.remark || '-'},
    {
      key: 'updateTime',
      title: $t('page.config.updateTime'),
      minWidth: 180,
      render: row => formatDateTime(row.updateTime)
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      fixed: 'right',
      width: 220,
      align: 'center',
      render: row => (
          <TableRowActions
              actions={[
                {key: 'detail', label: $t('common.detail'), type: 'info', onClick: () => openModal('detail', row)},
                {
                  key: 'edit',
                  label: $t('common.edit'),
                  type: 'primary',
                  show: showUpdate.value,
                  onClick: () => openModal('edit', row)
                },
                {
                  key: 'delete',
                  label: $t('common.delete'),
                  type: 'error',
                  show: showDelete.value,
                  loading: deletingId.value === row.id,
                  confirmText: $t('page.config.deleteConfirm', {name: row.configName}),
                  onClick: () => handleDelete(row.id)
                }
              ]}
          />
      )
    }
  ]
});

function openModal(mode: OperateMode, item?: Api.Config.Item) {
  operateMode.value = mode;
  activeItem.value = item || null;
  modalVisible.value = true;
}

function handleReset() {
  searchParams.value = {pageNum: 1, pageSize: 10, keyword: null, configType: null, status: null};
  getDataByPage(1);
}

async function handleDelete(id: number) {
  deletingId.value = id;
  const {error} = await fetchDeleteConfig(id);
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
      <NFormItemGi :label="$t('page.config.keyword')" class="pr-24px" span="24 s:12 m:6">
        <NInput v-model:value="searchParams.keyword" :placeholder="$t('common.keywordSearch')" clearable />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.config.configType')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.configType"
          :options="configTypeOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable
        />
      </NFormItemGi>
      <NFormItemGi :label="$t('page.config.status')" class="pr-24px" span="24 s:12 m:6">
        <NSelect
          v-model:value="searchParams.status"
          :options="statusOptions.map(item => ({label: $t(item.label), value: item.value}))" clearable
        />
      </NFormItemGi>
      <NFormItemGi class="pr-24px" span="24 m:6">
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

    <NCard :bordered="false" :title="$t('route.system_config')" class="card-wrapper sm:flex-1-hidden" size="small">
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
        :scroll-x="1600"
        class="sm:h-full"
        remote
        size="small"
      />
    </NCard>

    <ConfigOperateModal v-model:visible="modalVisible" :item="activeItem" :mode="operateMode" @submitted="getData" />
  </div>
</template>
