<script lang="ts" setup>
import {toRaw} from 'vue';
import {jsonClone} from '@sa/utils';
import {enableStatusOptions} from '@/constants/business';
import {translateOptions} from '@/utils/common';
import {$t} from '@/locales';

defineOptions({
  name: 'RoleSearch'
});

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.RoleSearchParams>('model', {required: true});

const defaultModel = jsonClone(toRaw(model.value));

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" class="card-wrapper" size="small">
    <NCollapse :default-expanded-names="['role-search']">
      <NCollapseItem :title="$t('common.search')" name="role-search">
        <NForm :label-width="80" :model="model" label-placement="left">
          <NGrid item-responsive responsive="screen">
            <NFormItemGi :label="$t('page.manage.role.roleName')" class="pr-24px" path="roleName" span="24 s:12 m:6">
              <NInput v-model:value="model.roleName" :placeholder="$t('page.manage.role.form.roleName')"/>
            </NFormItemGi>
            <NFormItemGi :label="$t('page.manage.role.roleCode')" class="pr-24px" path="roleCode" span="24 s:12 m:6">
              <NInput v-model:value="model.roleCode" :placeholder="$t('page.manage.role.form.roleCode')"/>
            </NFormItemGi>
            <NFormItemGi :label="$t('page.manage.role.roleStatus')" class="pr-24px" path="status" span="24 s:12 m:6">
              <NSelect
                  v-model:value="model.status"
                  :options="translateOptions(enableStatusOptions)"
                  :placeholder="$t('page.manage.role.form.roleStatus')"
                  clearable
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6">
              <NSpace class="w-full" justify="end">
                <NButton @click="resetModel">
                  <template #icon>
                    <icon-ic-round-refresh class="text-icon"/>
                  </template>
                  {{ $t('common.reset') }}
                </NButton>
                <NButton ghost type="primary" @click="search">
                  <template #icon>
                    <icon-ic-round-search class="text-icon"/>
                  </template>
                  {{ $t('common.search') }}
                </NButton>
              </NSpace>
            </NFormItemGi>
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>

<style scoped></style>
