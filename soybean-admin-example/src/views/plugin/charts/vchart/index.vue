<script lang="ts" setup>
import {useVChart} from '@/hooks/common/vchart';
import {
  barMarkPointSpec,
  circularProgressTickSpec,
  histogramDifferentBinSpec,
  liquidChartSmartInvertSpec,
  rankingBarSpec,
  shapeWordCloudSpec,
  stackedDashAreaSpec
} from './data';

const {domRef: stackedDashAreaRef} = useVChart(() => stackedDashAreaSpec);
const {domRef: barMarkPointRef} = useVChart(() => barMarkPointSpec);
const {domRef: histogramDifferentBinRef} = useVChart(() => histogramDifferentBinSpec);
const {domRef: rankingBarRef} = useVChart(() => rankingBarSpec);
const {domRef: shapeWordCloudRef} = useVChart(() => shapeWordCloudSpec);
const {domRef: circularProgressTickRef} = useVChart(() => circularProgressTickSpec);
const {domRef: liquidChartSmartInvertRef} = useVChart(() => liquidChartSmartInvertSpec);
</script>

<template>
  <NSpace :size="16" vertical>
    <NCard :bordered="false" class="h-full card-wrapper" title="VChart">
      <WebSiteLink label="More Demos: " link="https://www.visactor.com/vchart/example"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Stacked Dash Area Chart">
      <div ref="stackedDashAreaRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Bar Mark Point Chart">
      <div ref="barMarkPointRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Histogram Different Bin Chart">
      <div ref="histogramDifferentBinRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Ranking Bar Chart">
      <div ref="rankingBarRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Circular Progress Tick Chart">
      <div ref="circularProgressTickRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Liquid Chart Smart Invert Chart">
      <div ref="liquidChartSmartInvertRef" class="h-400px"/>
    </NCard>
    <NCard :bordered="false" class="h-full card-wrapper" title="Shape Word Cloud Chart">
      <div ref="shapeWordCloudRef" class="h-400px"/>
    </NCard>
  </NSpace>
</template>

<style scoped></style>
