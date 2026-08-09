<script lang="tsx" setup>
import {computed, ref} from 'vue';
import {NButton, NPopconfirm, NSpace, NTag} from 'naive-ui';
import {
  fetchDeleteDictionaryData,
  fetchDeleteDictionaryType,
  fetchDictionaryDataPage,
  fetchDictionaryTypes
} from '@/service/api';
import {dictTagTypeOptions, statusOptions, statusRecord} from '@/constants/business';
import {useAuth} from '@/hooks/business/auth';
import {defaultTransform, useNaivePaginatedTable, useNaiveTable} from '@/hooks/common/table';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import DictionaryDataModal from './modules/dictionary-data-modal.vue';
import DictionaryTypeModal from './modules/dictionary-type-modal.vue';

defineOptions({
  name: 'SystemDictionary'
});

type TypeOperateMode = 'add' | 'edit';
type DataOperateMode = 'add' | 'edit';

const appStore = useAppStore();
const {hasAuth} = useAuth();

const typeKeyword = ref<string | null>(null);
const activeTypeId = ref<number | null>(null);
const activeTypeName = ref('');
const activeTypeCode = ref('');
const typeModalVisible = ref(false);
const dataModalVisible = ref(false);
const typeOperateMode = ref<TypeOperateMode>('add');
const dataOperateMode = ref<DataOperateMode>('add');
const activeType = ref<Api.Dictionary.TypeItem | null>(null);
const activeData = ref<Api.Dictionary.DataItem | null>(null);
const dataDeletingId = ref<number | null>(null);
const typeDeletingId = ref<number | null>(null);

const dataSearchParams = ref<Api.Dictionary.DataPageParams>({
  pageNum: 1,
  pageSize: 10,
  label: null,
  value: null,
  status: null
});

const showAdd = computed(() => hasAuth('system:dictionary:add'));
const showUpdate = computed(() => hasAuth('system:dictionary:update'));
const showDelete = computed(() => hasAuth('system:dictionary:delete'));

const {
  columns: typeColumns,
  data: typeData,
  loading: typeLoading,
  getData: getTypes,
  scrollX: typeScrollX
} = useNaiveTable({
  api: () => fetchDictionaryTypes(typeKeyword.value),
  transform: response => {
    const {data, error} = response;
    return error ? [] : data;
  },
  columns: () => [
    {
      key: 'dictName',
      title: $t('page.dictionary.typeName'),
      minWidth: 180
    },
    {
      key: 'dictCode',
      title: $t('page.dictionary.typeCode'),
      minWidth: 160
    },
    {
      key: 'dataCount',
      title: $t('page.dictionary.dataCount'),
      width: 90,
      align: 'center'
    },
    {
      key: 'status',
      title: $t('page.dictionary.status'),
      width: 90,
      align: 'center',
      render: row => {
        const config = statusRecord[row.status as keyof typeof statusRecord];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      width: 200,
      align: 'center',
      render: row => (
          <NSpace justify="center" size={8}>
            {showUpdate.value ? (
                <NButton ghost size="small" type="primary" onClick={() => openTypeModal('edit', row)}>
                  {$t('common.edit')}
                </NButton>
            ) : null}
            {showDelete.value ? (
                <NPopconfirm onPositiveClick={() => handleDeleteType(row.id)}>
                  {{
                    default: () => $t('page.dictionary.deleteTypeConfirm', {name: row.dictName}),
                    trigger: () => (
                        <NButton ghost size="small" type="error" loading={typeDeletingId.value === row.id}>
                          {$t('common.delete')}
                        </NButton>
                    )
                  }}
                </NPopconfirm>
            ) : null}
          </NSpace>
      )
    }
  ]
});

const {
  columns: dataColumns,
  columnChecks,
  data: dataRows,
  loading: dataLoading,
  getData: getDataRows,
  getDataByPage,
  mobilePagination
} = useNaivePaginatedTable({
  api: () => {
    if (!activeTypeId.value) {
      return Promise.resolve({data: {records: [], total: 0, pageNum: 1, pageSize: 10}, error: null} as any);
    }

    return fetchDictionaryDataPage(activeTypeId.value, dataSearchParams.value);
  },
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    dataSearchParams.value.pageNum = params.page || 1;
    dataSearchParams.value.pageSize = params.pageSize || 10;
  },
  columns: () => [
    {
      key: 'dictLabel',
      title: $t('page.dictionary.dataLabel'),
      minWidth: 140
    },
    {
      key: 'dictValue',
      title: $t('page.dictionary.dataValue'),
      minWidth: 140
    },
    {
      key: 'tagType',
      title: $t('page.dictionary.tagType'),
      minWidth: 120,
      render: (row: Api.Dictionary.DataItem) => {
        const option = dictTagTypeOptions.find(item => item.value === (row.tagType || 'default'));
        return row.tagType ?
            <NTag type={(row.tagType as any) || 'default'}>{$t(option?.label || 'common.noData')}</NTag> : '-';
      }
    },
    {
      key: 'sort',
      title: $t('page.dictionary.sort'),
      width: 90,
      align: 'center',
      render: (row: Api.Dictionary.DataItem) => row.sort ?? '-'
    },
    {
      key: 'status',
      title: $t('page.dictionary.status'),
      width: 90,
      align: 'center',
      render: (row: Api.Dictionary.DataItem) => {
        const config = statusRecord[row.status as keyof typeof statusRecord];
        return <NTag type={config?.type || 'default'}>{$t(config?.label || 'common.noData')}</NTag>;
      }
    },
    {
      key: 'remark',
      title: $t('page.dictionary.remark'),
      minWidth: 160,
      render: (row: Api.Dictionary.DataItem) => row.remark || '-'
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      width: 180,
      align: 'center',
      render: (row: Api.Dictionary.DataItem) => (
          <NSpace justify="center" size={8}>
            {showUpdate.value ? (
                <NButton ghost size="small" type="primary" onClick={() => openDataModal('edit', row)}>
                  {$t('common.edit')}
                </NButton>
            ) : null}
            {showDelete.value ? (
                <NPopconfirm onPositiveClick={() => handleDeleteData(row.id)}>
                  {{
                    default: () => $t('page.dictionary.deleteDataConfirm', {name: row.dictLabel}),
                    trigger: () => (
                        <NButton ghost size="small" type="error" loading={dataDeletingId.value === row.id}>
                          {$t('common.delete')}
                        </NButton>
                    )
                  }}
                </NPopconfirm>
            ) : null}
          </NSpace>
      )
    }
  ]
});

function selectType(row: Api.Dictionary.TypeItem) {
  activeType.value = row;
  activeTypeId.value = row.id;
  activeTypeName.value = row.dictName;
  activeTypeCode.value = row.dictCode;
  dataSearchParams.value.pageNum = 1;
  getDataByPage(1);
}

function openTypeModal(mode: TypeOperateMode, row?: Api.Dictionary.TypeItem) {
  typeOperateMode.value = mode;
  activeType.value = row || null;
  typeModalVisible.value = true;
}

function openDataModal(mode: DataOperateMode, row?: Api.Dictionary.DataItem) {
  dataOperateMode.value = mode;
  activeData.value = row || null;
  dataModalVisible.value = true;
}

function handleResetDataSearch() {
  dataSearchParams.value.label = null;
  dataSearchParams.value.value = null;
  dataSearchParams.value.status = null;
  getDataByPage(1);
}

async function handleDeleteType(id: number) {
  typeDeletingId.value = id;
  const {error} = await fetchDeleteDictionaryType(id);
  typeDeletingId.value = null;
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    if (activeTypeId.value === id) {
      activeTypeId.value = null;
      activeType.value = null;
    }
    await getTypes();
    await getDataByPage(1);
  }
}

async function handleDeleteData(id: number) {
  dataDeletingId.value = id;
  const {error} = await fetchDeleteDictionaryData(id);
  dataDeletingId.value = null;
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
    await getTypes();
  }
}

async function handleTypeSubmitted() {
  await getTypes();
}

async function handleDataSubmitted() {
  await getDataByPage(dataSearchParams.value.pageNum);
  await getTypes();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NGrid :cols="24" :x-gap="16" class="min-h-0 flex-1">
      <NGi class="min-h-0" span="24 l:9">
        <NCard :bordered="false" :title="$t('page.dictionary.typeTitle')" class="card-wrapper h-full" size="small">
          <template #header-extra>
            <NSpace>
              <NInput v-model:value="typeKeyword" :placeholder="$t('common.keywordSearch')" class="w-220px" clearable />
              <NButton @click="getTypes">{{ $t('common.search') }}</NButton>
              <NButton v-if="showAdd" ghost size="small" type="primary" @click="openTypeModal('add')">
                {{ $t('common.add') }}
              </NButton>
            </NSpace>
          </template>

          <NDataTable
            :columns="typeColumns"
            :data="typeData"
            :loading="typeLoading"
            :row-key="row => row.id"
            :row-props="row => ({ onClick: () => selectType(row) })"
            :scroll-x="typeScrollX"
            size="small"
          />
        </NCard>
      </NGi>

      <NGi class="min-h-0" span="24 l:15">
        <NCard :bordered="false" class="card-wrapper h-full" size="small">
          <template #header>
            <div class="flex items-center gap-8px">
              <span>{{ $t('page.dictionary.dataTitle') }}</span>
              <NTag v-if="activeTypeId" type="info">{{ activeTypeName }} / {{ activeTypeCode }}</NTag>
            </div>
          </template>
          <template #header-extra>
            <TableHeaderOperation v-model:columns="columnChecks" :loading="dataLoading" @refresh="getDataRows">
              <template #default>
                <NSpace>
                  <NInput
                    v-model:value="dataSearchParams.label" :placeholder="$t('page.dictionary.dataLabel')"
                    class="w-160px" clearable
                  />
                  <NInput
                    v-model:value="dataSearchParams.value" :placeholder="$t('page.dictionary.dataValue')"
                    class="w-160px" clearable
                  />
                  <NSelect
                    v-model:value="dataSearchParams.status"
                    :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
                    class="w-120px"
                    clearable
                  />
                  <NButton :disabled="!activeTypeId" @click="getDataByPage(1)">{{ $t('common.search') }}</NButton>
                  <NButton :disabled="!activeTypeId" @click="handleResetDataSearch">{{ $t('common.reset') }}</NButton>
                  <NButton
                    v-if="showAdd" :disabled="!activeTypeId" ghost size="small" type="primary"
                    @click="openDataModal('add')"
                  >
                    {{ $t('common.add') }}
                  </NButton>
                </NSpace>
              </template>
            </TableHeaderOperation>
          </template>

          <NDataTable
            :columns="dataColumns"
            :data="dataRows"
            :flex-height="!appStore.isMobile"
            :loading="dataLoading"
            :pagination="mobilePagination"
            :row-key="row => row.id"
            class="sm:h-full"
            remote
            size="small"
          />
        </NCard>
      </NGi>
    </NGrid>

    <DictionaryTypeModal
      v-model:visible="typeModalVisible"
      :mode="typeOperateMode"
      :type-item="activeType"
      @submitted="handleTypeSubmitted"
    />
    <DictionaryDataModal
      v-model:visible="dataModalVisible"
      :data-item="activeData"
      :mode="dataOperateMode"
      :type-id="activeTypeId"
      @submitted="handleDataSubmitted"
    />
  </div>
</template>
