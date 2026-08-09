<script lang="tsx" setup>
import { computed, ref, watch } from 'vue';
import type { DropdownOption } from 'naive-ui';
import { NButton, NDropdown, NPopconfirm, NSpace, NTag } from 'naive-ui';
import {
  fetchDeleteDictionaryData,
  fetchDeleteDictionaryType,
  fetchDictionaryDataPage,
  fetchDictionaryTypes
} from '@/service/api';
import { dictTagTypeOptions, statusOptions, statusRecord } from '@/constants/business';
import { useAuth } from '@/hooks/business/auth';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { localStg } from '@/utils/storage';
import SearchPanel from '@/components/advanced/search-panel.vue';
import DictionaryDataModal from './modules/dictionary-data-modal.vue';
import DictionaryTypeModal from './modules/dictionary-type-modal.vue';

defineOptions({
  name: 'SystemDictionary'
});

type TypeOperateMode = 'add' | 'edit';
type DataOperateMode = 'add' | 'edit';

const DICTIONARY_PANEL_COLLAPSED_KEY = 'dictionaryTypePanelCollapsed';

const appStore = useAppStore();
const { hasAuth } = useAuth();

const typeKeyword = ref<string | null>(null);
const typeList = ref<Api.Dictionary.TypeItem[]>([]);
const typeLoading = ref(false);
const typePanelCollapsed = ref(localStg.get(DICTIONARY_PANEL_COLLAPSED_KEY) === 'Y');
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
const hasTypeSelection = computed(() => Boolean(activeTypeId.value));

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
      return Promise.resolve({ data: { records: [], total: 0, pageNum: 1, pageSize: 10 }, error: null } as any);
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
      minWidth: 160
    },
    {
      key: 'dictValue',
      title: $t('page.dictionary.dataValue'),
      minWidth: 160
    },
    {
      key: 'tagType',
      title: $t('page.dictionary.tagType'),
      minWidth: 120,
      render: (row: Api.Dictionary.DataItem) => {
        const option = dictTagTypeOptions.find(item => item.value === (row.tagType || 'default'));
        return row.tagType ? <NTag type={(row.tagType as any) || 'default'}>{$t(option?.label || 'common.noData')}</NTag> : '-';
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
      minWidth: 180,
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
                default: () => $t('page.dictionary.deleteDataConfirm', { name: row.dictLabel }),
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

function applyActiveType(typeItem: Api.Dictionary.TypeItem | null, reloadData = true) {
  activeType.value = typeItem;
  activeTypeId.value = typeItem?.id || null;
  activeTypeName.value = typeItem?.dictName || '';
  activeTypeCode.value = typeItem?.dictCode || '';

  if (reloadData) {
    dataSearchParams.value.pageNum = 1;
    void getDataByPage(1);
  }
}

function resolveActiveType(preferredId?: number | null) {
  if (!typeList.value.length) {
    applyActiveType(null);
    return;
  }

  const targetId = preferredId ?? activeTypeId.value;
  const nextType = typeList.value.find(item => item.id === targetId) || typeList.value[0];

  applyActiveType(nextType);
}

async function loadTypes(preferredId?: number | null) {
  typeLoading.value = true;
  const { data, error } = await fetchDictionaryTypes(typeKeyword.value?.trim() || null);
  typeLoading.value = false;

  if (!error) {
    typeList.value = data;
    resolveActiveType(preferredId);
  }
}

function selectType(typeItem: Api.Dictionary.TypeItem) {
  applyActiveType(typeItem);
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

function handleSearchTypes() {
  void loadTypes();
}

function toggleTypePanel() {
  typePanelCollapsed.value = !typePanelCollapsed.value;
}

function handleResetDataSearch() {
  dataSearchParams.value.label = null;
  dataSearchParams.value.value = null;
  dataSearchParams.value.status = null;
  void getDataByPage(1);
}

function getTypeDropdownOptions(typeItem: Api.Dictionary.TypeItem): DropdownOption[] {
  return [
    {
      key: `edit-${typeItem.id}`,
      label: $t('common.edit'),
      show: showUpdate.value
    },
    {
      key: `delete-${typeItem.id}`,
      label: $t('common.delete'),
      show: showDelete.value
    }
  ].filter(item => item.show !== false);
}

function handleTypeAction(key: string, typeItem: Api.Dictionary.TypeItem) {
  if (key.startsWith('edit-')) {
    openTypeModal('edit', typeItem);
    return;
  }

  if (key.startsWith('delete-')) {
    void handleDeleteType(typeItem.id);
  }
}

async function handleDeleteType(id: number) {
  const currentIndex = typeList.value.findIndex(item => item.id === id);
  const fallbackType = typeList.value[currentIndex + 1] || typeList.value[currentIndex - 1] || null;

  typeDeletingId.value = id;
  const { error } = await fetchDeleteDictionaryType(id);
  typeDeletingId.value = null;

  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await loadTypes(activeTypeId.value === id ? fallbackType?.id || null : activeTypeId.value);
  }
}

async function handleDeleteData(id: number) {
  dataDeletingId.value = id;
  const { error } = await fetchDeleteDictionaryData(id);
  dataDeletingId.value = null;

  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    await getDataByPage(1);
    await loadTypes(activeTypeId.value);
  }
}

async function handleTypeSubmitted(payload?: { item: Api.Dictionary.TypeItem; mode: TypeOperateMode }) {
  await loadTypes(payload?.item.id ?? activeTypeId.value);
}

async function handleDataSubmitted() {
  await getDataByPage(dataSearchParams.value.pageNum);
  await loadTypes(activeTypeId.value);
}

watch(typePanelCollapsed, value => {
  localStg.set(DICTIONARY_PANEL_COLLAPSED_KEY, value ? 'Y' : 'N');
});

void loadTypes();
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <div class="min-h-0 flex flex-1 items-stretch gap-12px">
      <NCard
        v-if="!typePanelCollapsed"
        :bordered="false"
        class="card-wrapper h-full w-280px shrink-0"
        size="small"
      >
        <template #header>
          <div class="flex items-center justify-between gap-12px">
            <span>{{ $t('page.dictionary.typeTitle') }}</span>
            <NButton v-if="showAdd" circle ghost size="small" type="primary" @click="openTypeModal('add')">
              <template #icon>
                <icon-ic-round-plus class="text-icon" />
              </template>
            </NButton>
          </div>
        </template>

        <div class="flex-col-stretch gap-12px">
          <NInput
            v-model:value="typeKeyword"
            :placeholder="$t('common.keywordSearch')"
            clearable
            @clear="handleSearchTypes"
            @keyup.enter="handleSearchTypes"
          >
            <template #suffix>
              <NButton quaternary size="tiny" @click="handleSearchTypes">
                <template #icon>
                  <icon-ic-round-search class="text-icon" />
                </template>
              </NButton>
            </template>
          </NInput>

          <NSpin :show="typeLoading">
            <div class="flex-col-stretch gap-8px overflow-y-auto pr-4px">
              <button
                v-for="item in typeList"
                :key="item.id"
                class="w-full rounded-12px border-none px-12px py-10px text-left transition-colors"
                :class="
                  item.id === activeTypeId
                    ? 'bg-primary text-white shadow-sm'
                    : 'bg-#f8fafc text-text hover:bg-#f1f5f9 dark:bg-#1f2937 dark:hover:bg-#334155'
                "
                type="button"
                @click="selectType(item)"
              >
                <div class="flex items-start justify-between gap-8px">
                  <div class="min-w-0 flex-1">
                    <div class="truncate text-14px font-600">{{ item.dictName }}</div>
                    <div
                      class="truncate text-12px"
                      :class="item.id === activeTypeId ? 'text-white/75' : 'text-text-secondary'"
                    >
                      {{ item.dictCode }}
                    </div>
                  </div>

                  <div class="flex items-center gap-6px">
                    <NTag :bordered="false" :type="item.id === activeTypeId ? 'default' : 'info'" size="small">
                      {{ item.dataCount }}
                    </NTag>

                    <NDropdown
                      v-if="showUpdate || showDelete"
                      :options="getTypeDropdownOptions(item)"
                      trigger="click"
                      @select="key => handleTypeAction(String(key), item)"
                    >
                      <NButton
                        quaternary
                        size="tiny"
                        :type="item.id === activeTypeId ? 'default' : 'primary'"
                        @click.stop
                      >
                        <template #icon>
                          <icon-mdi-dots-horizontal class="text-icon" />
                        </template>
                      </NButton>
                    </NDropdown>
                  </div>
                </div>

                <div class="mt-8px flex items-center justify-between gap-8px">
                  <span
                    class="text-12px"
                    :class="item.id === activeTypeId ? 'text-white/75' : 'text-text-secondary'"
                  >
                    {{ $t(statusRecord[item.status as keyof typeof statusRecord]?.label || 'common.noData') }}
                  </span>
                  <NButton
                    v-if="typeDeletingId === item.id"
                    quaternary
                    size="tiny"
                    type="error"
                  >
                    {{ $t('common.delete') }}
                  </NButton>
                </div>
              </button>

              <NEmpty v-if="!typeList.length && !typeLoading" :description="$t('common.noData')" class="py-24px" />
            </div>
          </NSpin>
        </div>
      </NCard>

      <div class="flex items-center">
        <NTooltip placement="right">
          <template #trigger>
            <NButton circle quaternary @click="toggleTypePanel">
              <template #icon>
                <icon-mdi-chevron-right v-if="typePanelCollapsed" class="text-icon" />
                <icon-mdi-chevron-left v-else class="text-icon" />
              </template>
            </NButton>
          </template>
          {{ $t(typePanelCollapsed ? 'page.dictionary.expandTypePanel' : 'page.dictionary.collapseTypePanel') }}
        </NTooltip>
      </div>

      <NCard :bordered="false" class="card-wrapper min-w-0 flex-1" size="small">
        <template #header>
          <div class="flex items-center gap-8px">
            <span>{{ $t('page.dictionary.dataTitle') }}</span>
            <NTag v-if="activeTypeId" type="info">{{ activeTypeName }}</NTag>
          </div>
        </template>

        <SearchPanel :model="dataSearchParams">
          <NFormItemGi :label="$t('page.dictionary.dataLabel')" class="pr-24px" span="24 s:12 m:6">
            <NInput v-model:value="dataSearchParams.label" :disabled="!hasTypeSelection" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.dictionary.dataValue')" class="pr-24px" span="24 s:12 m:6">
            <NInput v-model:value="dataSearchParams.value" :disabled="!hasTypeSelection" clearable />
          </NFormItemGi>
          <NFormItemGi :label="$t('page.dictionary.status')" class="pr-24px" span="24 s:12 m:6">
            <NSelect
              v-model:value="dataSearchParams.status"
              :disabled="!hasTypeSelection"
              :options="statusOptions.map(item => ({ label: $t(item.label), value: item.value }))"
              clearable
            />
          </NFormItemGi>
          <NFormItemGi class="pr-24px" span="24 s:12 m:6">
            <NSpace class="w-full" justify="end">
              <NButton :disabled="!hasTypeSelection" @click="handleResetDataSearch">{{ $t('common.reset') }}</NButton>
              <NButton :disabled="!hasTypeSelection" ghost type="primary" @click="getDataByPage(1)">
                {{ $t('common.search') }}
              </NButton>
            </NSpace>
          </NFormItemGi>
        </SearchPanel>

        <div class="flex-1 overflow-hidden">
          <TableHeaderOperation v-model:columns="columnChecks" :loading="dataLoading" @refresh="getDataRows">
            <template #default>
              <NButton
                v-if="showAdd"
                :disabled="!hasTypeSelection"
                ghost
                size="small"
                type="primary"
                @click="openDataModal('add')"
              >
                <template #icon>
                  <icon-ic-round-plus class="text-icon" />
                </template>
                {{ $t('common.add') }}
              </NButton>
            </template>
          </TableHeaderOperation>

          <NDataTable
            v-if="hasTypeSelection"
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

          <NEmpty
            v-else
            :description="$t('page.dictionary.selectTypeTip')"
            class="flex h-[320px] items-center justify-center"
          />
        </div>
      </NCard>
    </div>

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
