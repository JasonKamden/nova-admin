<script lang="ts" setup>
import WebSiteLink from './web-site-link.vue';

defineOptions({name: 'GithubLink'});

interface Props {
  /** github link */
  link: string;
}

defineProps<Props>();
</script>

<template>
  <WebSiteLink :link="link" label="github地址："/>
</template>

<style scoped></style>
