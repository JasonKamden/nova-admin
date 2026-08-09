<script lang="ts" setup>
defineOptions({name: 'WebSiteLink'});

interface Props {
  /** Web site name */
  label: string;
  /** Web site link */
  link: string;
}

defineProps<Props>();
</script>

<template>
  <p>
    <span>{{ label }}</span>
    <a :href="link" class="text-blue-500" target="#">
      {{ link }}
    </a>
  </p>
</template>

<style scoped></style>
