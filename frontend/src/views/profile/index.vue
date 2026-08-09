<script lang="ts" setup>
import {computed, reactive, ref} from 'vue';
import {genderOptions, statusRecord} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import {fetchProfile, fetchUpdateProfile, fetchUpdateProfileAvatar, fetchUpdateProfilePassword} from '@/service/api';
import {useAuthStore} from '@/store/modules/auth';
import {formatContextType} from '@/utils/context';
import {formatDateTime} from '@/utils/date-time';
import AuthenticatedAvatar from '@/components/business/authenticated-avatar.vue';

defineOptions({
  name: 'ProfilePage'
});

const authStore = useAuthStore();
const {createRequiredRule, createConfirmPwdRule, patternRules} = useFormRules();
const {formRef: basicFormRef, validate: validateBasic, restoreValidation: restoreBasicValidation} = useNaiveForm();
const {
  formRef: passwordFormRef,
  validate: validatePassword,
  restoreValidation: restorePasswordValidation
} = useNaiveForm();

const loading = ref(false);
const submittingBasic = ref(false);
const submittingPassword = ref(false);
const uploadingAvatar = ref(false);
const fileInputRef = ref<HTMLInputElement | null>(null);
const profile = ref<Api.Profile.Item | null>(null);
const activeTab = ref<'basic' | 'security' | 'account'>('basic');

const basicModel = reactive<Api.Profile.UpdateReq>({
  nickname: '',
  gender: null,
  phone: null,
  email: null,
  bio: null
});

const passwordModel = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const basicRules = {
  nickname: createRequiredRule($t('page.profile.form.nickname')),
  phone: {
    validator: (_rule: App.Global.FormRule, value: string | null) => !value || patternRules.phone.pattern.test(value),
    message: patternRules.phone.message,
    trigger: 'change'
  },
  email: {
    validator: (_rule: App.Global.FormRule, value: string | null) => !value || patternRules.email.pattern.test(value),
    message: patternRules.email.message,
    trigger: 'change'
  }
};

const passwordRules = {
  oldPassword: createRequiredRule($t('page.profile.form.oldPassword')),
  newPassword: [
    createRequiredRule($t('page.profile.form.newPassword')),
    {
      validator: (_rule: App.Global.FormRule, value: string) => value.length >= 8 && value.length <= 64,
      message: $t('page.profile.form.passwordRule'),
      trigger: 'input'
    }
  ],
  confirmPassword: createConfirmPwdRule(computed(() => passwordModel.newPassword))
};

const roleTags = computed(() => profile.value?.roles || []);
const statusLabel = computed(() => {
  const status = profile.value?.status;

  if (status === null || status === undefined) {
    return $t('common.noData');
  }

  return $t(statusRecord[status as keyof typeof statusRecord]?.label || 'common.noData');
});
const contextLabel = computed(() => formatContextType(profile.value?.contextType));
const contextSummary = computed(() => {
  if (profile.value?.contextType === 'PLATFORM') {
    return 'Platform';
  }

  return profile.value?.tenantName || 'Tenant';
});
const genderLabel = computed(() => {
  const gender = genderOptions.find(item => item.value === (profile.value?.gender || ''));
  return gender ? $t(gender.label) : $t('page.user.genderUnknown');
});

function resetPasswordModel() {
  passwordModel.oldPassword = '';
  passwordModel.newPassword = '';
  passwordModel.confirmPassword = '';
  restorePasswordValidation();
}

function fillBasicModel(data: Api.Profile.Item) {
  basicModel.nickname = data.nickname;
  basicModel.gender = data.gender;
  basicModel.phone = data.phone;
  basicModel.email = data.email;
  basicModel.bio = data.bio;
  restoreBasicValidation();
}

async function loadProfile() {
  loading.value = true;

  const {data, error} = await fetchProfile();

  loading.value = false;

  if (!error) {
    profile.value = data;
    fillBasicModel(data);
  }
}

function handleSelectAvatar() {
  fileInputRef.value?.click();
}

async function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  target.value = '';

  if (!file) {
    return;
  }

  uploadingAvatar.value = true;
  const {error} = await fetchUpdateProfileAvatar(file);
  uploadingAvatar.value = false;

  if (!error) {
    window.$message?.success($t('common.updateSuccess'));
    authStore.userInfo.avatar = '/api/profile/avatar';
    await authStore.getUserInfo();
    await loadProfile();
  }
}

async function handleSubmitBasic() {
  await validateBasic();
  submittingBasic.value = true;
  const payload: Api.Profile.UpdateReq = {
    nickname: basicModel.nickname,
    gender: basicModel.gender || null,
    phone: basicModel.phone || null,
    email: basicModel.email || null,
    bio: basicModel.bio || null
  };
  const {data, error} = await fetchUpdateProfile(payload);
  submittingBasic.value = false;

  if (!error) {
    profile.value = data;
    fillBasicModel(data);
    authStore.userInfo.nickname = data.nickname;
    authStore.userInfo.avatar = data.avatar;
    window.$message?.success($t('common.updateSuccess'));
  }
}

async function handleSubmitPassword() {
  await validatePassword();
  submittingPassword.value = true;

  const {error} = await fetchUpdateProfilePassword({
    oldPassword: passwordModel.oldPassword,
    newPassword: passwordModel.newPassword
  });

  submittingPassword.value = false;

  if (!error) {
    window.$message?.success($t('page.profile.passwordUpdated'));
    resetPasswordModel();
    await authStore.resetStore();
  }
}

void loadProfile();
</script>

<template>
  <div class="flex-col-stretch gap-16px">
    <NSpin :show="loading">
      <div class="grid gap-16px xl:grid-cols-[300px_minmax(0,1fr)]">
        <NCard :bordered="false" class="card-wrapper self-start" size="small">
          <div class="rounded-16px bg-gradient-to-br from-primary/12 via-white to-info/10 px-20px py-24px">
            <div class="flex flex-col items-center gap-12px text-center">
              <div
                class="group relative flex size-108px cursor-pointer items-center justify-center overflow-hidden rounded-full border-4 border-white bg-#f3f6fb shadow-sm"
                @click="handleSelectAvatar"
              >
                <AuthenticatedAvatar :src="profile?.avatar">
                  <template #default="{ src }">
                    <img v-if="src" :src="src" class="size-full object-cover" />
                    <SvgIcon v-else class="text-72px text-#8b95a7" icon="ph:user-circle" />
                  </template>
                </AuthenticatedAvatar>
                <div
                  class="absolute inset-0 flex items-center justify-center bg-black/45 text-12px text-white opacity-0 transition-opacity group-hover:opacity-100"
                >
                  {{ uploadingAvatar ? 'Uploading...' : $t('page.profile.uploadAvatar') }}
                </div>
              </div>
              <div>
                <div class="text-20px font-600">{{ profile?.nickname || '-' }}</div>
                <div class="mt-6px text-#666">{{ profile?.username || '-' }}</div>
              </div>
              <NSpace justify="center" size="small">
                <NTag round type="info">{{ contextLabel }}</NTag>
                <NTag :type="profile?.platformAdmin ? 'warning' : 'default'" round>
                  {{ profile?.platformAdmin ? $t('page.profile.platformAdmin') : $t('page.profile.tenantUser') }}
                </NTag>
                <NTag :type="profile?.status === 1 ? 'success' : 'warning'" round>{{ statusLabel }}</NTag>
              </NSpace>
              <input
                ref="fileInputRef" accept="image/jpeg,image/png,image/webp" class="hidden" type="file"
                @change="handleAvatarChange"
              />
            </div>
          </div>

          <NDescriptions :column="1" class="mt-16px" label-placement="top" size="small">
            <NDescriptionsItem :label="$t('page.profile.currentContext')">{{ contextSummary }}</NDescriptionsItem>
            <NDescriptionsItem :label="$t('page.profile.currentTenant')">{{ profile?.tenantName || '-' }}</NDescriptionsItem>
            <NDescriptionsItem :label="$t('page.profile.currentDepartment')">{{ profile?.departmentName || '-' }}</NDescriptionsItem>
            <NDescriptionsItem :label="$t('page.user.role')">
              <NSpace v-if="roleTags.length" size="small">
                <NTag v-for="role in roleTags" :key="role.id" size="small" type="default">{{ role.roleName }}</NTag>
              </NSpace>
              <span v-else>-</span>
            </NDescriptionsItem>
          </NDescriptions>
        </NCard>

        <NCard :bordered="false" class="card-wrapper min-w-0" size="small">
          <NTabs v-model:value="activeTab" animated type="line">
            <NTabPane name="basic" :tab="$t('page.profile.basicTitle')">
              <NForm ref="basicFormRef" :label-width="110" :model="basicModel" :rules="basicRules" label-placement="left">
                <NGrid :cols="24" :x-gap="16">
                  <NFormItemGi :label="$t('page.user.username')" span="24 m:12">
                    <NInput :value="profile?.username || ''" disabled />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.user.nickname')" path="nickname" span="24 m:12">
                    <NInput v-model:value="basicModel.nickname" />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.user.gender')" path="gender" span="24 m:12">
                    <NSelect
                      v-model:value="basicModel.gender"
                      :options="genderOptions.map(item => ({ label: $t(item.label), value: item.value }))"
                      clearable
                    />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.user.phone')" path="phone" span="24 m:12">
                    <NInput v-model:value="basicModel.phone" clearable />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.user.email')" path="email" span="24">
                    <NInput v-model:value="basicModel.email" clearable />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.user.bio')" path="bio" span="24">
                    <NInput
                      v-model:value="basicModel.bio" :autosize="{ minRows: 3, maxRows: 5 }" clearable
                      type="textarea"
                    />
                  </NFormItemGi>
                  <NFormItemGi span="24">
                    <NSpace class="w-full" justify="end">
                      <NButton @click="profile && fillBasicModel(profile)">{{ $t('common.reset') }}</NButton>
                      <NButton :loading="submittingBasic" type="primary" @click="handleSubmitBasic">
                        {{ $t('common.update') }}
                      </NButton>
                    </NSpace>
                  </NFormItemGi>
                </NGrid>
              </NForm>
            </NTabPane>

            <NTabPane name="security" :tab="$t('page.profile.securityTitle')">
              <NAlert :show-icon="false" class="mb-16px" type="warning">
                {{ $t('page.profile.passwordTip') }}
              </NAlert>

              <NForm
                ref="passwordFormRef" :label-width="110" :model="passwordModel" :rules="passwordRules"
                label-placement="left"
              >
                <NGrid :cols="24" :x-gap="16">
                  <NFormItemGi :label="$t('page.profile.oldPassword')" path="oldPassword" span="24 m:12">
                    <NInput v-model:value="passwordModel.oldPassword" show-password-on="click" type="password" />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.profile.newPassword')" path="newPassword" span="24 m:12">
                    <NInput v-model:value="passwordModel.newPassword" show-password-on="click" type="password" />
                  </NFormItemGi>
                  <NFormItemGi :label="$t('page.profile.confirmPassword')" path="confirmPassword" span="24 m:12">
                    <NInput v-model:value="passwordModel.confirmPassword" show-password-on="click" type="password" />
                  </NFormItemGi>
                  <NFormItemGi span="24 m:12">
                    <div class="flex h-full items-center text-13px text-#666">
                      {{ $t('page.profile.form.passwordRule') }}
                    </div>
                  </NFormItemGi>
                  <NFormItemGi span="24">
                    <NSpace class="w-full" justify="end">
                      <NButton @click="resetPasswordModel">{{ $t('common.reset') }}</NButton>
                      <NButton :loading="submittingPassword" type="primary" @click="handleSubmitPassword">
                        {{ $t('page.profile.updatePassword') }}
                      </NButton>
                    </NSpace>
                  </NFormItemGi>
                </NGrid>
              </NForm>
            </NTabPane>

            <NTabPane name="account" :tab="$t('page.profile.accountTitle')">
              <NDescriptions :column="2" bordered label-placement="left" size="small">
                <NDescriptionsItem :label="$t('page.profile.userId')">{{ profile?.userId || '-' }}</NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.username')">{{ profile?.username || '-' }}</NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.status')">{{ statusLabel }}</NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.gender')">{{ genderLabel }}</NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.profile.currentContext')">{{ contextLabel }}</NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.profile.currentTenant')">
                  {{
                    profile?.tenantName || '-'
                  }}
                </NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.profile.currentDepartment')">
                  {{
                    profile?.departmentName || '-'
                  }}
                </NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.role')">
                  <span v-if="roleTags.length">{{ roleTags.map(item => item.roleName).join(' / ') }}</span>
                  <span v-else>-</span>
                </NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.lastLoginTime')">
                  {{ formatDateTime(profile?.lastLoginTime) }}
                </NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.lastLoginIp')">
                  {{
                    profile?.lastLoginIp || '-'
                  }}
                </NDescriptionsItem>
                <NDescriptionsItem :label="$t('page.user.createTime')">
                  {{ formatDateTime(profile?.createTime) }}
                </NDescriptionsItem>
              </NDescriptions>
            </NTabPane>
          </NTabs>
        </NCard>
      </div>
    </NSpin>
  </div>
</template>
