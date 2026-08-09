<script lang="ts" setup>
interface Props {
  url: string;
}

defineProps<Props>();
</script>

<template>
  <div class="h-full">
    <iframe id="iframePage" :src="url" class="size-full"></iframe>
  </div>
</template>

<style scoped></style>
