<script lang="ts" setup>
import {computed, h} from 'vue';
import type {DropdownOption} from 'naive-ui';
import {NButton, NPopconfirm} from 'naive-ui';
import {$t} from '@/locales';

defineOptions({
  name: 'TableRowActions'
});

type ActionType = 'default' | 'primary' | 'info' | 'success' | 'warning' | 'error';

interface RowAction {
  key: string;
  label: string;
  type?: ActionType;
  loading?: boolean;
  disabled?: boolean;
  show?: boolean;
  confirmText?: string;
  onClick: () => void | Promise<void>;
}

interface Props {
  actions: RowAction[];
  maxVisible?: number;
}

const props = withDefaults(defineProps<Props>(), {
  maxVisible: 3
});

const visibleActions = computed(() => props.actions.filter(action => action.show !== false));
const directActions = computed(() => visibleActions.value.slice(0, props.maxVisible));
const overflowActions = computed(() => visibleActions.value.slice(props.maxVisible));
const dropdownActions = computed(() => overflowActions.value.filter(action => !action.confirmText));
const popoverActions = computed(() => overflowActions.value.filter(action => action.confirmText));

const dropdownOptions = computed<DropdownOption[]>(() =>
    dropdownActions.value.map(action => ({
      key: action.key,
      disabled: action.disabled,
      label: action.label
    }))
);

const dropdownActionMap = computed(() => new Map(dropdownActions.value.map(action => [action.key, action])));

function renderAction(action: RowAction) {
  const button = () =>
      h(
          NButton,
          {
            ghost: true,
            size: 'small',
            type: action.type || 'default',
            loading: action.loading,
            disabled: action.disabled,
            onClick: action.confirmText ? undefined : action.onClick
          },
          {default: () => action.label}
      );

  if (!action.confirmText) {
    return button();
  }

  return h(
      NPopconfirm,
      {onPositiveClick: action.onClick},
      {
        trigger: button,
        default: () => action.confirmText
      }
  );
}

function handleSelect(key: string) {
  const action = dropdownActionMap.value.get(key);

  if (action && !action.disabled) {
    action.onClick();
  }
}
</script>

<template>
  <NSpace :size="8" :wrap="false" justify="center">
    <template v-for="action in directActions" :key="action.key">
      <component :is="renderAction(action)"/>
    </template>

    <NPopover v-if="popoverActions.length" placement="bottom-end" trigger="click">
      <template #trigger>
        <NButton ghost size="small">
          {{ $t('common.more') }}
          <icon-mdi-chevron-down class="ml-4px text-icon"/>
        </NButton>
      </template>
      <div class="flex-col-stretch gap-8px">
        <template v-for="action in popoverActions" :key="action.key">
          <component :is="renderAction(action)"/>
        </template>
      </div>
    </NPopover>

    <NDropdown v-else-if="dropdownActions.length" :options="dropdownOptions" trigger="click" @select="handleSelect">
      <NButton ghost size="small">
        {{ $t('common.more') }}
        <icon-mdi-chevron-down class="ml-4px text-icon"/>
      </NButton>
    </NDropdown>
  </NSpace>
</template>
