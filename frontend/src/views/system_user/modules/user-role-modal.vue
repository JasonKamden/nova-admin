<script lang="ts" setup>
import {reactive, ref, watch} from 'vue';
import {fetchRoleOptions, fetchUpdateUserRoles, fetchUserRoles} from '@/service/api';
import {$t} from '@/locales';

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
const visible = defineModel<boolean>('visible', {default: false});

const state = reactive({
  loading: false,
  submitting: false
});

const roleIds = ref<number[]>([]);
const roleOptions = ref<Array<{ label: string; value: number }>>([]);

async function loadData() {
  state.loading = true;

  const [rolesResp, selectedResp] = await Promise.all([fetchRoleOptions(null), fetchUserRoles(props.userId!)]);

  state.loading = false;

  if (!rolesResp.error) {
    roleOptions.value = rolesResp.data.map(item => ({
      label: `${item.roleName} (${item.roleCode})`,
      value: item.id
    }));
  }

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
  <NModal v-model:show="visible" :mask-closable="false" class="w-560px" preset="card">
    <template #header>
      <div class="text-16px font-600">{{ $t('page.user.roleTitle') }} - {{ username }}</div>
    </template>

    <NSpin :show="state.loading">
      <NForm :label-width="80" label-placement="left">
        <NFormItem :label="$t('page.user.role')">
          <NSelect v-model:value="roleIds" :options="roleOptions" clearable filterable multiple/>
        </NFormItem>
      </NForm>
    </NSpin>

    <template #action>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton :loading="state.submitting" type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
