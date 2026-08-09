<script lang="ts" setup>
import { computed, nextTick, ref, watch } from 'vue';
import { $t } from '@/locales';

defineOptions({
  name: 'RichTextEditor'
});

const props = withDefaults(
  defineProps<{
    disabled?: boolean;
    placeholder?: string;
    minHeight?: number;
  }>(),
  {
    disabled: false,
    placeholder: '',
    minHeight: 260
  }
);

const model = defineModel<string>({ default: '' });
const editorRef = ref<HTMLDivElement | null>(null);
const focused = ref(false);

const placeholderText = computed(() => props.placeholder || $t('page.message.editorPlaceholder'));

function syncEditorHtml(html: string) {
  if (editorRef.value && editorRef.value.innerHTML !== html) {
    editorRef.value.innerHTML = html;
  }
}

function updateValue() {
  model.value = editorRef.value?.innerHTML || '';
}

function runCommand(command: string, value?: string) {
  if (props.disabled) return;
  editorRef.value?.focus();
  document.execCommand(command, false, value);
  updateValue();
}

function handleInsertLink() {
  const value = window.prompt($t('page.message.linkPlaceholder'));
  if (value) {
    runCommand('createLink', value);
  }
}

function handleInsertTable() {
  const tableHtml = `
    <table>
      <thead>
        <tr><th>Header 1</th><th>Header 2</th></tr>
      </thead>
      <tbody>
        <tr><td>Value 1</td><td>Value 2</td></tr>
      </tbody>
    </table>
  `;

  runCommand('insertHTML', tableHtml);
}

watch(
  () => model.value,
  async value => {
    if (focused.value) return;
    await nextTick();
    syncEditorHtml(value || '');
  },
  { immediate: true }
);
</script>

<template>
  <div class="overflow-hidden rounded-12px border border-#dbe3ef bg-white dark:border-#334155 dark:bg-#0f172a">
    <div class="flex flex-wrap gap-8px border-b border-#e5e7eb p-12px dark:border-#334155">
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('bold')">B</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('italic')">I</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('formatBlock', '<h2>')">H2</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('insertUnorderedList')">• List</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('insertOrderedList')">1. List</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="handleInsertLink">Link</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="handleInsertTable">Table</NButton>
      <NButton :disabled="disabled" quaternary size="small" @click="runCommand('removeFormat')">
        {{ $t('common.reset') }}
      </NButton>
    </div>

    <div
      ref="editorRef"
      :contenteditable="!disabled"
      :data-placeholder="placeholderText"
      :style="{ minHeight: `${minHeight}px` }"
      class="rich-editor p-16px"
      @blur="focused = false"
      @focus="focused = true"
      @input="updateValue"
    ></div>
  </div>
</template>

<style scoped>
.rich-editor {
  outline: none;
  line-height: 1.7;
  word-break: break-word;
}

.rich-editor:empty::before {
  content: attr(data-placeholder);
  color: rgb(148 163 184);
}

.rich-editor :deep(p) {
  margin: 0 0 12px;
}
</style>
