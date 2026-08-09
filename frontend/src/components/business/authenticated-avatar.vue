<script lang="ts" setup>
import {computed, onBeforeUnmount, ref, watch} from 'vue';
import {getAuthorization} from '@/service/request/shared';
import {getServiceBaseURL} from '@/utils/service';

defineOptions({
  name: 'AuthenticatedAvatar'
});

interface Props {
  src?: string | null;
  alt?: string;
}

const props = withDefaults(defineProps<Props>(), {
  src: null,
  alt: 'avatar'
});

const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
const {baseURL} = getServiceBaseURL(import.meta.env, isHttpProxy);

const resolvedSrc = ref<string | null>(null);
const loading = ref(false);

const requestUrl = computed(() => {
  if (!props.src) {
    return null;
  }

  if (/^https?:\/\//.test(props.src) || props.src.startsWith('blob:') || props.src.startsWith('data:')) {
    return props.src;
  }

  return `${baseURL}${props.src}`;
});

function revokePreview() {
  if (resolvedSrc.value?.startsWith('blob:')) {
    URL.revokeObjectURL(resolvedSrc.value);
  }
}

async function loadAvatar() {
  revokePreview();
  resolvedSrc.value = null;

  if (!requestUrl.value) {
    return;
  }

  if (/^(https?:|blob:|data:)/.test(requestUrl.value) && !requestUrl.value.startsWith(baseURL)) {
    resolvedSrc.value = requestUrl.value;
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(requestUrl.value, {
      headers: {
        Authorization: getAuthorization() || ''
      }
    });

    if (!response.ok) {
      return;
    }

    const blob = await response.blob();
    resolvedSrc.value = URL.createObjectURL(blob);
  } catch {
    resolvedSrc.value = null;
  } finally {
    loading.value = false;
  }
}

watch(() => props.src, loadAvatar, {immediate: true});

onBeforeUnmount(() => {
  revokePreview();
});
</script>

<template>
  <slot :loading="loading" :src="resolvedSrc">
    <img v-if="resolvedSrc" :alt="alt" :src="resolvedSrc" class="size-full object-cover" />
    <div v-else class="flex size-full items-center justify-center bg-#f3f6fb text-#8b95a7">
      <SvgIcon class="text-70px" icon="ph:user-circle" />
    </div>
  </slot>
</template>
