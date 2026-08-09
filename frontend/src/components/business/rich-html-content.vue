<script lang="ts" setup>
import { computed } from 'vue';
import { sanitizeRichHtml } from '@/utils/rich-html';

defineOptions({
  name: 'RichHtmlContent'
});

const props = defineProps<{
  html: string | null | undefined;
}>();

const safeHtml = computed(() => sanitizeRichHtml(props.html));
</script>

<template>
  <!-- eslint-disable-next-line vue/no-v-html -->
  <div class="rich-html-content" v-html="safeHtml"></div>
</template>

<style scoped>
.rich-html-content {
  line-height: 1.7;
  word-break: break-word;
}

.rich-html-content :deep(p) {
  margin: 0 0 12px;
}

.rich-html-content :deep(ul),
.rich-html-content :deep(ol) {
  margin: 0 0 12px;
  padding-left: 20px;
}

.rich-html-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 12px;
}

.rich-html-content :deep(td),
.rich-html-content :deep(th) {
  border: 1px solid #e5e7eb;
  padding: 8px 10px;
}
</style>
