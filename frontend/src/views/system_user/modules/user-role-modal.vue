<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { fetchUpdateUserRoles, fetchUserRoles } from '@/service/api';
import { $t } from '@/locales';
import BusinessFormContainer from '@/components/advanced/business-form-container.vue';
import RoleSelectorTable from '@/components/business/relation-selector/role-selector-table.vue';

defineOptions({
  name: 'UserRoleModal'
});

interface Props {
  userId: number | null;
  username: string;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();
const visible = defineModel<boolean>('visible', { default: false });

const state = reactive({
  loading: false,
  submitting: false
});

const roleIds = ref<number[]>([]);
async function loadData() {
  state.loading = true;

  const selectedResp = await fetchUserRoles(props.userId!);

  state.loading = false;

  if (!selectedResp.error) {
    roleIds.value = selectedResp.data.map(item => item.id);
  }
}

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  state.submitting = true;
  const {error} = await fetchUpdateUserRoles(props.userId!, roleIds.value);
  state.submitting = false;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    closeModal();
    emit('submitted');
  }
}

watch(
  () => visible.value,
  async show => {
    if (show && props.userId) {
      await loadData();
    }
  }
);
</script>

<template>
  <BusinessFormContainer v-model:visible="visible" :title="`${$t('page.user.roleTitle')} - ${username}`" :width="1080">
    <NSpin :show="state.loading">
      <RoleSelectorTable v-model:selected-ids="roleIds" />
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </BusinessFormContainer>
</template>
