<script lang="ts" setup>
import { computed, ref } from 'vue';
import { $t } from '@/locales';

defineOptions({
  name: 'LogCodeViewer'
});

interface Props {
  code: string | null | undefined;
  language?: 'json' | 'text';
  maxChars?: number;
  collapsedHeight?: number;
}

const props = withDefaults(defineProps<Props>(), {
  language: 'json',
  maxChars: 4000,
  collapsedHeight: 240
});

const expanded = ref(false);
const copying = ref(false);

const normalizedCode = computed(() => {
  const raw = props.code?.trim();
  if (!raw) return '';

  if (props.language === 'text') return raw;

  try {
    return JSON.stringify(JSON.parse(raw), null, 2);
  } catch {
    return raw;
  }
});

const isEmpty = computed(() => !normalizedCode.value);
const isTruncated = computed(() => normalizedCode.value.length > props.maxChars);
const displayCode = computed(() => {
  if (!isTruncated.value || expanded.value) return normalizedCode.value;
  return normalizedCode.value.slice(0, props.maxChars);
});

async function handleCopy() {
  if (!normalizedCode.value) return;

  copying.value = true;

  try {
    await navigator.clipboard.writeText(normalizedCode.value);
    window.$message?.success($t('common.copySuccess'));
  } finally {
    copying.value = false;
  }
}
</script>

<template>
  <div class="rounded-12px border border-#e5e7eb bg-#fbfcfe dark:border-#334155 dark:bg-#0f172a">
    <div class="flex items-center justify-between gap-12px border-b border-#e5e7eb px-12px py-10px dark:border-#334155">
      <span class="text-13px text-text-secondary">{{ language === 'json' ? 'JSON' : 'TEXT' }}</span>
      <NSpace size="small">
        <NButton :disabled="isEmpty" :loading="copying" quaternary size="tiny" @click="handleCopy">
          {{ $t('common.copy') }}
        </NButton>
        <NButton v-if="isTruncated" quaternary size="tiny" @click="expanded = !expanded">
          {{ $t(expanded ? 'common.collapse' : 'common.expand') }}
        </NButton>
      </NSpace>
    </div>

    <div
      v-if="!isEmpty"
      class="overflow-auto"
      :style="expanded ? undefined : { maxHeight: `${collapsedHeight}px` }"
    >
      <NCode :code="displayCode" :language="language === 'json' ? 'json' : 'text'" show-line-numbers word-wrap />
    </div>

    <NEmpty v-else :description="$t('common.noData')" class="py-24px" />

    <div v-if="isTruncated && !expanded" class="border-t border-#e5e7eb px-12px py-8px text-12px text-text-secondary dark:border-#334155">
      {{ $t('page.monitor.contentTruncatedTip') }}
    </div>
  </div>
</template>
