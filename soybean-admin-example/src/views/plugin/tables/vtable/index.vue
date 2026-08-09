<script lang="tsx" setup>
import {computed, onMounted, ref} from 'vue';
import {
  Group,
  Image,
  ListColumn,
  ListTable,
  Menu,
  PivotChart,
  PivotColumnDimension,
  PivotCorner,
  PivotIndicator,
  PivotRowDimension,
  PivotTable,
  registerChartModule,
  Tag,
  Text,
  VTable
} from '@visactor/vue-vtable';
import VChart from '@visactor/vchart';
import {useThemeStore} from '@/store/modules/theme';
import {customListRecords, listTableRecords, pivotChartColumns, pivotChartIndicators, pivotChartRows} from './data';

registerChartModule('vchart', VChart);
const titleColorPool = ['#3370ff', '#34c724', '#ff9f1a', '#ff4050', '#1f2329'];

const themeStore = useThemeStore();

// list table
const listTableRef = ref(null);
const listOptions = computed(() => {
  const options = {
    theme: themeStore.darkMode ? VTable.themes.DARK : VTable.themes.DEFAULT
  };
  return options;
});
const listRecords = ref<Record<string, string | number>[]>(listTableRecords);

// group table
const groupTableRef = ref(null);
const groupOptions = computed(() => {
  const options = {
    groupBy: ['Category', 'Sub-Category'],
    theme: (themeStore.darkMode ? VTable.themes.DARK : VTable.themes.DEFAULT).extends({
      groupTitleStyle: {
        fontWeight: 'bold',
        bgColor: (args: any) => {
          const {col, row, table} = args;
          const index = table.getGroupTitleLevel(col, row);
          if (index !== undefined) {
            return titleColorPool[index % titleColorPool.length] as string;
          }
          return 'white';
        }
      }
    })
  };
  return options;
});
const groupRecords = ref<Record<string, string | number>[]>(listTableRecords);

// pivot table
const pivotTableRef = ref(null);
const pivotTableOptions = computed(() => {
  return {
    tooltip: {
      isShowOverflowTextTooltip: true
    },
    dataConfig: {
      sortRules: [
        {
          sortField: 'Category',
          sortBy: ['Office Supplies', 'Technology', 'Furniture']
        }
      ]
    },
    widthMode: 'standard',
    theme: themeStore.darkMode ? VTable.themes.DARK : VTable.themes.DEFAULT,
    emptyTip: {
      text: 'no data records'
    }
  };
});
const pivotTableIndicators = ref([
  {
    indicatorKey: 'Quantity',
    title: 'Quantity',
    width: 'auto',
    showSort: false,
    headerStyle: {fontWeight: 'normal'},
    style: {
      padding: [16, 28, 16, 28],
      color(args: any) {
        return args.dataValue >= 0 ? 'black' : 'red';
      }
    }
  },
  {
    indicatorKey: 'Sales',
    title: 'Sales',
    width: 'auto',
    showSort: false,
    headerStyle: {fontWeight: 'normal'},
    format: (rec: string) => `$${Number(rec).toFixed(2)}`,
    style: {
      padding: [16, 28, 16, 28],
      color(args: any) {
        return args.dataValue >= 0 ? 'black' : 'red';
      }
    }
  },
  {
    indicatorKey: 'Profit',
    title: 'Profit',
    width: 'auto',
    showSort: false,
    headerStyle: {fontWeight: 'normal'},
    format: (rec: string) => `$${Number(rec).toFixed(2)}`,
    style: {
      padding: [16, 28, 16, 28],
      color(args: any) {
        return args.dataValue >= 0 ? 'black' : 'red';
      }
    }
  }
]);
const pivotTableRows = ref([
  {
    dimensionKey: 'City',
    title: 'City',
    headerStyle: {textStick: true},
    width: 'auto'
  }
]);
const pivotTableRecords = ref([]);

// pivot chart
const pivotChartRef = ref(null);
const pivotChartOptions = computed(() => {
  return {
    rows: pivotChartRows,
    columns: pivotChartColumns,
    indicators: pivotChartIndicators,
    indicatorsAsCol: false,
    defaultRowHeight: 200,
    defaultHeaderRowHeight: 50,
    defaultColWidth: 280,
    defaultHeaderColWidth: 100,
    indicatorTitle: '指标',
    autoWrapText: true,
    corner: {
      titleOnDimension: 'row',
      headerStyle: {autoWrapText: true}
    },
    legends: {
      orient: 'bottom',
      type: 'discrete',
      data: [
        {label: 'Consumer-Quantity', shape: {fill: '#2E62F1', symbolType: 'circle'}},
        {label: 'Consumer-Quantity', shape: {fill: '#4DC36A', symbolType: 'square'}},
        {label: 'Home Office-Quantity', shape: {fill: '#FF8406', symbolType: 'square'}},
        {label: 'Consumer-Sales', shape: {fill: '#FFCC00', symbolType: 'square'}},
        {label: 'Consumer-Sales', shape: {fill: '#4F44CF', symbolType: 'square'}},
        {label: 'Home Office-Sales', shape: {fill: '#5AC8FA', symbolType: 'square'}},
        {label: 'Consumer-Profit', shape: {fill: '#003A8C', symbolType: 'square'}},
        {label: 'Consumer-Profit', shape: {fill: '#B08AE2', symbolType: 'square'}},
        {label: 'Home Office-Profit', shape: {fill: '#FF6341', symbolType: 'square'}}
      ]
    },
    theme: (themeStore.darkMode ? VTable.themes.DARK : VTable.themes.DEFAULT).extends({
      bodyStyle: {borderColor: 'gray', borderLineWidth: [1, 0, 0, 1]},
      headerStyle: {borderColor: 'gray', borderLineWidth: [0, 0, 1, 1], hover: {cellBgColor: '#CCE0FF'}},
      rowHeaderStyle: {borderColor: 'gray', borderLineWidth: [1, 1, 0, 0], hover: {cellBgColor: '#CCE0FF'}},
      cornerHeaderStyle: {borderColor: 'gray', borderLineWidth: [0, 1, 1, 0], hover: {cellBgColor: ''}},
      cornerRightTopCellStyle: {borderColor: 'gray', borderLineWidth: [0, 0, 1, 1], hover: {cellBgColor: ''}},
      cornerLeftBottomCellStyle: {borderColor: 'gray', borderLineWidth: [1, 1, 0, 0], hover: {cellBgColor: ''}},
      cornerRightBottomCellStyle: {borderColor: 'gray', borderLineWidth: [1, 0, 0, 1], hover: {cellBgColor: ''}},
      rightFrozenStyle: {borderColor: 'gray', borderLineWidth: [1, 0, 1, 1], hover: {cellBgColor: ''}},
      bottomFrozenStyle: {borderColor: 'gray', borderLineWidth: [1, 1, 0, 1], hover: {cellBgColor: ''}},
      selectionStyle: {cellBgColor: '', cellBorderColor: ''},
      frameStyle: {borderLineWidth: 0}
    }),
    emptyTip: {
      text: 'no data records'
    }
  };
});
const pivotChartRecords = ref({} as any);
const handleLegendItemClick = (args: { value: any }) => {
  (pivotChartRef?.value as any)?.vTableInstance.updateFilterRules([
    {
      filterKey: 'Segment-Indicator',
      filteredValues: args.value
    }
  ]);
};

// custom layout list table
const customLayoutListTableRef = ref(null);
const customLayoutListTableOptions = computed(() => {
  return {
    defaultRowHeight: 80,
    theme: themeStore.darkMode ? VTable.themes.DARK : VTable.themes.DEFAULT
  };
});
const customLayoutListTableRecords = ref(customListRecords);
const customLayoutListTableColumnStyle = ref({fontFamily: 'Arial', fontSize: 12, fontWeight: 'bold'});

onMounted(() => {
  // pivot tablt records
  fetch('https://lf9-dp-fe-cms-tos.byteorg.com/obj/bit-cloud/VTable/North_American_Superstore_Pivot_data.json')
      .then(res => res.json())
      .then(jsonData => {
        // update record
        pivotTableRecords.value = jsonData;
      });

  // pivot chart records
  fetch('https://lf9-dp-fe-cms-tos.byteorg.com/obj/bit-cloud/VTable/North_American_Superstore_Pivot_Chart_data.json')
      .then(res => res.json())
      .then(data => {
        // update record
        pivotChartRecords.value = data;
      });
});
</script>

<template>
  <div class="h-full">
    <NSpace :size="16" vertical>
      <NCard :bordered="false" class="h-full w-2/3 card-wrapper" title="List Table">
        <ListTable ref="listTableRef" :options="listOptions" :records="listRecords" height="400px">
          <ListColumn field="Order ID" title="Order ID" width="auto"/>
          <ListColumn field="Customer ID" title="Customer ID" width="auto"/>
          <ListColumn field="Product Name" title="Product Name" width="auto"/>
          <ListColumn field="Category" title="Category" width="auto"/>
          <ListColumn field="Sub-Category" title="Sub-Category" width="auto"/>
          <ListColumn field="Region" title="Region" width="auto"/>
          <ListColumn field="City" title="City" width="auto"/>
          <ListColumn field="Order Date" title="Order Date" width="auto"/>
          <ListColumn field="Quantity" title="Quantity" width="auto"/>
          <ListColumn field="Sales" title="Sales" width="auto"/>
          <ListColumn field="Profit" title="Profit" width="auto"/>
        </ListTable>
      </NCard>

      <NCard :bordered="false" class="h-full w-2/3 card-wrapper" title="Group Table">
        <ListTable ref="groupTableRef" :options="groupOptions" :records="groupRecords" height="400px">
          <ListColumn field="Order ID" title="Order ID" width="auto"/>
          <ListColumn field="Customer ID" title="Customer ID" width="auto"/>
          <ListColumn field="Product Name" title="Product Name" width="auto"/>
          <ListColumn field="Category" title="Category" width="auto"/>
          <ListColumn field="Sub-Category" title="Sub-Category" width="auto"/>
          <ListColumn field="Region" title="Region" width="auto"/>
          <ListColumn field="City" title="City" width="auto"/>
          <ListColumn field="Order Date" title="Order Date" width="auto"/>
          <ListColumn field="Quantity" title="Quantity" width="auto"/>
          <ListColumn field="Sales" title="Sales" width="auto"/>
          <ListColumn field="Profit" title="Profit" width="auto"/>
        </ListTable>
      </NCard>

      <NCard :bordered="false" class="h-full w-2/3 card-wrapper" title="Pivot Table">
        <PivotTable ref="pivotTableRef" :options="pivotTableOptions" :records="pivotTableRecords" height="400px">
          <PivotColumnDimension
              :header-style="{ textStick: true }"
              dimension-key="Category"
              title="Category"
              width="auto"
          />
          <PivotRowDimension
              v-for="(row, index) in pivotTableRows"
              :key="index"
              :dimension-key="row.dimensionKey"
              :header-style="row.headerStyle"
              :title="row.title"
              :width="row.width"
          />
          <PivotIndicator
              v-for="(indicator, index) in pivotTableIndicators"
              :key="index"
              :format="indicator.format"
              :header-style="indicator.headerStyle"
              :indicator-key="indicator.indicatorKey"
              :show-sort="indicator.showSort"
              :style="indicator.style"
              :title="indicator.title"
              :width="indicator.width"
          />
          <PivotCorner title-on-dimension="row"/>
          <Menu :context-menu-items="['copy', 'paste', 'delete', '...']" menu-type="html"/>
        </PivotTable>
      </NCard>

      <NCard :bordered="false" class="h-full w-2/3 card-wrapper" title="Pivot Chart">
        <PivotChart
            ref="pivotChartRef"
            :options="pivotChartOptions"
            :records="pivotChartRecords"
            height="800px"
            @on-legend-item-click="handleLegendItemClick"
        />
      </NCard>

      <NCard :bordered="false" class="h-full w-2/3 card-wrapper" title="Custom Component">
        <ListTable
            ref="customLayoutListTableRef"
            :options="customLayoutListTableOptions"
            :records="customLayoutListTableRecords"
            height="400px"
        >
          <!-- Order Number Column -->
          <ListColumn field="bloggerId" title="Order Number" width="100"/>

          <!-- Anchor Nickname Column with Custom Layout -->
          <ListColumn :width="330" field="bloggerName" title="Anchor Nickname">
            <template #customLayout="{ record, height, width }">
              <Group :height="height" :width="width" display="flex" flex-direction="row" flex-wrap="nowrap">
                <!-- Avatar Group -->
                <Group
                    :height="height"
                    :opacity="0.1"
                    :width="60"
                    align-items="center"
                    display="flex"
                    fill="red"
                    flex-direction="column"
                    justify-content="space-around"
                >
                  <Image id="icon0" :corner-radius="25" :height="50" :image="record.bloggerAvatar" :width="50"/>
                </Group>
                <!-- Blogger Info Group -->
                <Group :height="height" :width="width - 60" display="flex" flex-direction="column" flex-wrap="nowrap">
                  <Group
                      :height="height / 2"
                      :opacity="0.1"
                      :width="width - 60"
                      align-items="center"
                      display="flex"
                      fill="orange"
                      flex-wrap="wrap"
                  >
                    <Text
                        :bounds-padding="[0, 0, 0, 10]"
                        :font-size="13"
                        :text="record.bloggerName"
                        fill="black"
                        font-family="sans-serif"
                    />
                    <Image
                        id="location"
                        :bounds-padding="[0, 0, 0, 10]"
                        :height="15"
                        :width="15"
                        cursor="pointer"
                        image="https://lf9-dp-fe-cms-tos.byteorg.com/obj/bit-cloud/VTable/location.svg"
                    />
                    <Text :font-size="11" :text="record.city" fill="#6f7070" font-family="sans-serif"/>
                  </Group>
                  <!-- Tags Group -->
                  <Group
                      :height="height / 2"
                      :opacity="0.1"
                      :width="width - 60"
                      align-items="center"
                      display="flex"
                      fill="yellow"
                  >
                    <Tag
                        v-for="tag in record?.tags"
                        :key="tag"
                        :bounds-padding="[0, 0, 0, 5]"
                        :panel="{ visible: true, fill: '#f4f4f2', cornerRadius: 5 }"
                        :space="5"
                        :text="tag"
                        :text-style="{ fontSize: 10, fontFamily: 'sans-serif', fill: 'rgb(51, 101, 238)' }"
                    />
                  </Group>
                </Group>
              </Group>
            </template>
          </ListColumn>

          <!-- Other Columns -->
          <ListColumn
              :field-format="rec => rec.fansCount + 'w'"
              :style="customLayoutListTableColumnStyle"
              field="fansCount"
              title="Fans Count"
              width="120"
          />
          <ListColumn :style="customLayoutListTableColumnStyle" field="worksCount" title="Works Count" width="135"/>
          <ListColumn
              :field-format="rec => rec.viewCount + 'w'"
              :style="customLayoutListTableColumnStyle"
              field="viewCount"
              title="View Count"
              width="120"
          />
        </ListTable>
      </NCard>

      <NCard :bordered="false" class="h-full w-2/3 card-wrapper">
        <WebSiteLink label="More VTable Demos: " link="https://www.visactor.com/vtable/example"/>
      </NCard>
    </NSpace>
  </div>
</template>
