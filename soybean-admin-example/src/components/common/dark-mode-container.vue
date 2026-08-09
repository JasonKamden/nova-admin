<script lang="ts" setup>
defineOptions({name: 'DarkModeContainer'});

interface Props {
  inverted?: boolean;
}

defineProps<Props>();
</script>

<template>
  <div :class="{ 'bg-inverted text-#1f1f1f': inverted }" class="bg-container text-base-text transition-300">
    <slot></slot>
  </div>
</template>

<style scoped></style>
