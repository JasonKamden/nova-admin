<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {fetchPlatformDashboard, fetchTenantDashboard} from '@/service/api';
import {useEcharts} from '@/hooks/common/echarts';
import {$t} from '@/locales';
import {useAppStore} from '@/store/modules/app';
import {useAuthStore} from '@/store/modules/auth';
import {useContextStore} from '@/store/modules/context';

defineOptions({
    name: 'HomePage'
});

const appStore = useAppStore();
const authStore = useAuthStore();
const contextStore = useContextStore();

const loading = ref(false);
const tenantDashboard = ref<Api.Dashboard.TenantDashboard | null>(null);
const platformDashboard = ref<Api.Dashboard.PlatformDashboard | null>(null);

const gap = computed(() => (appStore.isMobile ? 0 : 16));
const isPlatform = computed(() => contextStore.isPlatform);

const cards = computed(() => {
    if (isPlatform.value) {
        if (!platformDashboard.value) return [];

        return [
            {key: 'tenantCount', label: $t('page.home.tenantCount'), value: platformDashboard.value.tenantCount, icon: 'carbon:tenant'},
            {key: 'enabledTenantCount', label: $t('page.home.enabledTenantCount'), value: platformDashboard.value.enabledTenantCount, icon: 'carbon:checkmark-outline'},
            {key: 'disabledTenantCount', label: $t('page.home.disabledTenantCount'), value: platformDashboard.value.disabledTenantCount, icon: 'carbon:close-outline'},
            {key: 'platformUserCount', label: $t('page.home.platformUserCount'), value: platformDashboard.value.platformUserCount, icon: 'carbon:user-admin'},
            {key: 'todayLoginCount', label: $t('page.home.todayLoginCount'), value: platformDashboard.value.todayLoginCount, icon: 'carbon:login'}
        ];
    }

    if (!tenantDashboard.value) return [];

    return [
        {key: 'currentSpace', label: $t('page.home.currentSpace'), value: tenantDashboard.value.currentSpace || '-', icon: 'carbon:workspace'},
        {key: 'currentDepartment', label: $t('page.home.currentDepartment'), value: tenantDashboard.value.department || '-', icon: 'carbon:tree-view-alt'},
        {key: 'userCount', label: $t('page.home.userCount'), value: tenantDashboard.value.userCount, icon: 'carbon:user-multiple'},
        {key: 'departmentCount', label: $t('page.home.departmentCount'), value: tenantDashboard.value.departmentCount, icon: 'carbon:ibm-cloud-direct-link-1-connectivity'},
        {key: 'roleCount', label: $t('page.home.roleCount'), value: tenantDashboard.value.roleCount, icon: 'carbon:user-role'},
        {key: 'onlineUserCount', label: $t('page.home.onlineUserCount'), value: tenantDashboard.value.onlineUserCount, icon: 'carbon:user-online'}
    ];
});

const recentOperations = computed(() => tenantDashboard.value?.recentOperations || []);

const {domRef: lineChartRef, updateOptions: updateLineChart} = useEcharts(() => ({
    tooltip: {trigger: 'axis'},
    grid: {left: '3%', right: '4%', bottom: '3%', top: '14%', containLabel: true},
    xAxis: {type: 'category', data: [] as string[]},
    yAxis: {type: 'value', minInterval: 1},
    series: [
        {
            name: $t('page.home.loginTrend'),
            type: 'line',
            smooth: true,
            symbolSize: 8,
            itemStyle: {color: '#2f7df6'},
            areaStyle: {
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [
                        {offset: 0, color: 'rgba(47, 125, 246, 0.28)'},
                        {offset: 1, color: 'rgba(47, 125, 246, 0.02)'}
                    ]
                }
            },
            data: [] as number[]
        }
    ]
}));

const {domRef: pieChartRef, updateOptions: updatePieChart} = useEcharts(() => ({
    tooltip: {trigger: 'item'},
    legend: {bottom: '2%', left: 'center'},
    series: [
        {
            name: $t('page.home.userStatus'),
            type: 'pie',
            radius: ['45%', '75%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 12,
                borderColor: '#fff',
                borderWidth: 1
            },
            label: {show: false},
            emphasis: {label: {show: true, fontSize: 12}},
            data: [] as {name: string; value: number}[]
        }
    ]
}));

function updateCharts() {
    if (isPlatform.value) {
        updatePieChart(opts => {
            const dashboard = platformDashboard.value;
            opts.series[0].name = $t('page.home.platformOverview');
            opts.series[0].data = dashboard
                ? [
                      {name: $t('page.home.enabledTenantCount'), value: dashboard.enabledTenantCount},
                      {name: $t('page.home.disabledTenantCount'), value: dashboard.disabledTenantCount},
                      {name: $t('page.home.platformUserCount'), value: dashboard.platformUserCount},
                      {name: $t('page.home.todayLoginCount'), value: dashboard.todayLoginCount}
                  ]
                : [];
            return opts;
        });

        return;
    }

    updateLineChart(opts => {
        const trend = tenantDashboard.value?.loginTrend || [];
        opts.series[0].name = $t('page.home.loginTrend');
        opts.xAxis.data = trend.map(item => item.date);
        opts.series[0].data = trend.map(item => item.count);
        return opts;
    });

    updatePieChart(opts => {
        opts.series[0].name = $t('page.home.userStatus');
        opts.series[0].data = (tenantDashboard.value?.userStatus || []).map(item => ({name: item.name, value: item.value}));
        return opts;
    });
}

async function loadDashboard() {
    loading.value = true;

    if (isPlatform.value) {
        const {data, error} = await fetchPlatformDashboard();
        platformDashboard.value = error ? null : data;
        tenantDashboard.value = null;
    } else {
        const {data, error} = await fetchTenantDashboard();
        tenantDashboard.value = error ? null : data;
        platformDashboard.value = null;
    }

    loading.value = false;
    updateCharts();
}

watch(
    () => [contextStore.current.contextType, contextStore.current.tenantId],
    () => {
        loadDashboard();
    },
    {immediate: true}
);

watch(
    () => appStore.locale,
    () => {
        updateCharts();
    }
);
</script>

<template>
  <div class="flex-col-stretch gap-16px">
    <NCard :bordered="false" class="card-wrapper" size="small">
      <div class="flex flex-col gap-8px lg:flex-row lg:items-end lg:justify-between">
        <div>
          <div class="text-22px font-600">
            {{ $t('page.home.greeting', { userName: authStore.userInfo.nickname || authStore.userInfo.username }) }}
          </div>
          <div class="mt-8px text-#666">
            {{ isPlatform ? $t('page.home.platformDesc') : $t('page.home.tenantDesc', { tenantName: contextStore.current.tenantName || '-' }) }}
          </div>
        </div>
        <NTag type="info" round>
          {{ isPlatform ? 'PLATFORM' : `${contextStore.current.tenantName || 'TENANT'} / ${authStore.userInfo.departmentName || '-'}` }}
        </NTag>
      </div>
    </NCard>

    <NSpin :show="loading">
      <NGrid :x-gap="gap" :y-gap="16" cols="1 s:2 m:3 xl:5" responsive="screen">
        <NGi v-for="card in cards" :key="card.key">
          <NCard :bordered="false" class="card-wrapper" size="small">
            <div class="flex items-center justify-between gap-12px">
              <div class="min-w-0 flex-1">
                <div class="text-13px text-#666">{{ card.label }}</div>
                <div class="mt-10px break-all text-24px font-600 text-#1f2329">{{ card.value }}</div>
              </div>
              <div class="flex size-48px items-center justify-center rounded-12px bg-#f3f6fb text-24px text-#2f7df6">
                <SvgIcon :icon="card.icon" />
              </div>
            </div>
          </NCard>
        </NGi>
      </NGrid>

      <NGrid v-if="!isPlatform" :x-gap="gap" :y-gap="16" class="mt-16px" item-responsive responsive="screen">
        <NGi span="24 s:24 m:15">
          <NCard :bordered="false" :title="$t('page.home.loginTrend')" class="card-wrapper" size="small">
            <div ref="lineChartRef" class="h-320px"></div>
          </NCard>
        </NGi>
        <NGi span="24 s:24 m:9">
          <NCard :bordered="false" :title="$t('page.home.userStatus')" class="card-wrapper" size="small">
            <div ref="pieChartRef" class="h-320px"></div>
          </NCard>
        </NGi>
      </NGrid>

      <NGrid v-if="isPlatform" :x-gap="gap" :y-gap="16" class="mt-16px" item-responsive responsive="screen">
        <NGi span="24 s:24 m:10">
          <NCard :bordered="false" :title="$t('page.home.platformOverview')" class="card-wrapper" size="small">
            <div ref="pieChartRef" class="h-320px"></div>
          </NCard>
        </NGi>
        <NGi span="24 s:24 m:14">
          <NCard :bordered="false" :title="$t('page.home.platformSummary')" class="card-wrapper" size="small">
            <NDescriptions bordered label-placement="left" :column="1">
              <NDescriptionsItem :label="$t('page.home.tenantCount')">{{ platformDashboard?.tenantCount ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.home.enabledTenantCount')">{{ platformDashboard?.enabledTenantCount ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.home.disabledTenantCount')">{{ platformDashboard?.disabledTenantCount ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.home.platformUserCount')">{{ platformDashboard?.platformUserCount ?? '-' }}</NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.home.todayLoginCount')">{{ platformDashboard?.todayLoginCount ?? '-' }}</NDescriptionsItem>
            </NDescriptions>
          </NCard>
        </NGi>
      </NGrid>

      <NCard v-if="!isPlatform" :bordered="false" :title="$t('page.home.recentOperations')" class="mt-16px card-wrapper" size="small">
        <NEmpty v-if="recentOperations.length === 0" :description="$t('common.noData')" class="py-24px" />
        <NList v-else>
          <NListItem v-for="(item, index) in recentOperations" :key="`${item.operator}-${item.time}-${index}`">
            <div class="flex flex-col gap-6px md:flex-row md:items-center md:justify-between">
              <div class="min-w-0 flex-1">
                <div class="text-15px font-500">{{ item.description }}</div>
                <div class="mt-4px text-13px text-#666">{{ $t('page.home.operator') }}: {{ item.operator }}</div>
              </div>
              <NTag type="default" round>{{ item.time }}</NTag>
            </div>
          </NListItem>
        </NList>
      </NCard>
    </NSpin>
  </div>
</template>
