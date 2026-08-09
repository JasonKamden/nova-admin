<script lang="ts" setup>
import {computed, toRaw} from 'vue';
import {jsonClone} from '@sa/utils';
import {enableStatusOptions, userGenderOptions} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {translateOptions} from '@/utils/common';
import {$t} from '@/locales';

defineOptions({
  name: 'UserSearch'
});

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const {formRef, validate, restoreValidation} = useNaiveForm();

const model = defineModel<Api.SystemManage.UserSearchParams>('model', {required: true});

type RuleKey = Extract<keyof Api.SystemManage.UserSearchParams, 'userEmail' | 'userPhone'>;

const rules = computed<Record<RuleKey, App.Global.FormRule>>(() => {
  const {patternRules} = useFormRules(); // inside computed to make locale reactive

  return {
    userEmail: patternRules.email,
    userPhone: patternRules.phone
  };
});

const defaultModel = jsonClone(toRaw(model.value));

function resetModel() {
  Object.assign(model.value, defaultModel);
}

async function reset() {
  await restoreValidation();
  resetModel();
}

async function search() {
  await validate();
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" class="card-wrapper" size="small">
    <NCollapse>
      <NCollapseItem :title="$t('common.search')" name="user-search">
        <NForm ref="formRef" :label-width="80" :model="model" :rules="rules" label-placement="left">
          <NGrid item-responsive responsive="screen">
            <NFormItemGi :label="$t('page.manage.user.userName')" class="pr-24px" path="userName" span="24 s:12 m:6">
              <NInput v-model:value="model.userName" :placeholder="$t('page.manage.user.form.userName')"/>
            </NFormItemGi>
            <NFormItemGi
                :label="$t('page.manage.user.userGender')"
                class="pr-24px"
                path="userGender"
                span="24 s:12 m:6"
            >
              <NSelect
                  v-model:value="model.userGender"
                  :options="translateOptions(userGenderOptions)"
                  :placeholder="$t('page.manage.user.form.userGender')"
                  clearable
              />
            </NFormItemGi>
            <NFormItemGi :label="$t('page.manage.user.nickName')" class="pr-24px" path="nickName" span="24 s:12 m:6">
              <NInput v-model:value="model.nickName" :placeholder="$t('page.manage.user.form.nickName')"/>
            </NFormItemGi>
            <NFormItemGi :label="$t('page.manage.user.userPhone')" class="pr-24px" path="userPhone" span="24 s:12 m:6">
              <NInput v-model:value="model.userPhone" :placeholder="$t('page.manage.user.form.userPhone')"/>
            </NFormItemGi>
            <NFormItemGi :label="$t('page.manage.user.userEmail')" class="pr-24px" path="userEmail" span="24 s:12 m:6">
              <NInput v-model:value="model.userEmail" :placeholder="$t('page.manage.user.form.userEmail')"/>
            </NFormItemGi>
            <NFormItemGi
                :label="$t('page.manage.user.userStatus')"
                class="pr-24px"
                path="userStatus"
                span="24 s:12 m:6"
            >
              <NSelect
                  v-model:value="model.status"
                  :options="translateOptions(enableStatusOptions)"
                  :placeholder="$t('page.manage.user.form.userStatus')"
                  clearable
              />
            </NFormItemGi>
            <NFormItemGi class="pr-24px" span="24 m:12">
              <NSpace class="w-full" justify="end">
                <NButton @click="reset">
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
