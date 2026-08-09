<script lang="ts" setup>
import {computed, onBeforeUnmount, reactive, ref} from 'vue';
import dayjs from 'dayjs';
import {genderOptions, statusRecord} from '@/constants/business';
import {useFormRules, useNaiveForm} from '@/hooks/common/form';
import {$t} from '@/locales';
import {fetchProfile, fetchUpdateProfile, fetchUpdateProfileAvatar, fetchUpdateProfilePassword} from '@/service/api';
import {getAuthorization} from '@/service/request/shared';
import {useAuthStore} from '@/store/modules/auth';
import {getServiceBaseURL} from '@/utils/service';

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

const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
const {baseURL} = getServiceBaseURL(import.meta.env, isHttpProxy);

const loading = ref(false);
const submittingBasic = ref(false);
const submittingPassword = ref(false);
const uploadingAvatar = ref(false);
const fileInputRef = ref<HTMLInputElement | null>(null);
const profile = ref<Api.Profile.Item | null>(null);
const avatarObjectUrl = ref<string | null>(null);

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
const roleSummary = computed(() => `${roleTags.value.length} role${roleTags.value.length === 1 ? '' : 's'}`);
const statusLabel = computed(() => {
  const status = profile.value?.status;

  if (status === null || status === undefined) {
    return $t('common.noData');
  }

  return $t(statusRecord[status as keyof typeof statusRecord]?.label || 'common.noData');
});
const contextLabel = computed(() => (profile.value?.contextType === 'PLATFORM' ? 'PLATFORM' : 'TENANT'));
const contextSummary = computed(() => {
  if (profile.value?.contextType === 'PLATFORM') {
    return 'PLATFORM';
  }

  return profile.value?.tenantName || 'TENANT';
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

async function refreshAvatarPreview() {
  if (avatarObjectUrl.value) {
    URL.revokeObjectURL(avatarObjectUrl.value);
    avatarObjectUrl.value = null;
  }

  if (!profile.value?.avatar) {
    return;
  }

  try {
    const response = await fetch(`${baseURL}${profile.value.avatar}`, {
      headers: {
        Authorization: getAuthorization() || ''
      }
    });

    if (!response.ok) {
      return;
    }

    const blob = await response.blob();
    avatarObjectUrl.value = URL.createObjectURL(blob);
  } catch {
    avatarObjectUrl.value = null;
  }
}

async function loadProfile() {
  loading.value = true;

  const {data, error} = await fetchProfile();

  loading.value = false;

  if (!error) {
    profile.value = data;
    fillBasicModel(data);
    await refreshAvatarPreview();
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

onBeforeUnmount(() => {
  if (avatarObjectUrl.value) {
    URL.revokeObjectURL(avatarObjectUrl.value);
  }
});
</script>

<template>
  <div class="flex-col-stretch gap-16px">
    <NSpin :show="loading">
      <div class="grid gap-16px xl:grid-cols-[300px_minmax(0,1fr)]">
        <NCard :bordered="false" class="card-wrapper self-start" size="small">
          <div class="rounded-16px bg-gradient-to-br from-primary/12 via-white to-info/10 px-20px py-24px">
            <div class="flex flex-col items-center gap-12px text-center">
              <div
                class="flex size-108px items-center justify-center overflow-hidden rounded-full border-4 border-white bg-#f3f6fb shadow-sm"
              >
                <img v-if="avatarObjectUrl" :src="avatarObjectUrl" class="size-full object-cover" />
                <SvgIcon v-else class="text-72px text-#8b95a7" icon="ph:user-circle" />
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
              <NButton :loading="uploadingAvatar" secondary type="primary" @click="handleSelectAvatar">
                {{ $t('page.profile.uploadAvatar') }}
              </NButton>
              <input
                ref="fileInputRef" accept="image/jpeg,image/png,image/webp" class="hidden" type="file"
                @change="handleAvatarChange"
              />
            </div>
          </div>

          <div class="mt-16px flex-col-stretch gap-12px">
            <div class="text-14px font-600">{{ $t('page.profile.identityTitle') }}</div>
            <div class="grid grid-cols-2 gap-12px">
              <div class="rounded-12px bg-#f7f9fc px-14px py-12px">
                <div class="text-12px text-#7b8798">{{ $t('page.profile.currentContext') }}</div>
                <div class="mt-6px text-14px font-600">{{ contextSummary }}</div>
              </div>
              <div class="rounded-12px bg-#f7f9fc px-14px py-12px">
                <div class="text-12px text-#7b8798">{{ $t('page.user.role') }}</div>
                <div class="mt-6px text-14px font-600">{{ roleSummary }}</div>
              </div>
            </div>

            <div class="rounded-12px bg-#f7f9fc px-14px py-12px">
              <div class="text-12px text-#7b8798">{{ $t('page.profile.currentDepartment') }}</div>
              <div class="mt-6px text-14px font-600">{{ profile?.departmentName || '-' }}</div>
            </div>

            <div class="rounded-12px bg-#f7f9fc px-14px py-12px">
              <div class="text-12px text-#7b8798">{{ $t('page.user.role') }}</div>
              <NSpace v-if="roleTags.length" class="mt-8px" size="small">
                <NTag v-for="role in roleTags" :key="role.id" size="small" type="default">{{ role.roleName }}</NTag>
              </NSpace>
              <div v-else class="mt-6px text-14px font-600">-</div>
            </div>
          </div>
        </NCard>

        <div class="min-w-0 flex-col-stretch gap-16px">
          <NCard :bordered="false" :title="$t('page.profile.basicTitle')" class="card-wrapper" size="small">
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
          </NCard>

          <NCard :bordered="false" :title="$t('page.profile.accountTitle')" class="card-wrapper" size="small">
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
                {{ profile?.lastLoginTime ? dayjs(profile.lastLoginTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
              </NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.user.lastLoginIp')">
                {{
                  profile?.lastLoginIp || '-'
                }}
              </NDescriptionsItem>
              <NDescriptionsItem :label="$t('page.user.createTime')">
                {{ profile?.createTime ? dayjs(profile.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
              </NDescriptionsItem>
            </NDescriptions>
          </NCard>

          <NCard :bordered="false" :title="$t('page.profile.securityTitle')" class="card-wrapper" size="small">
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
          </NCard>
        </div>
      </div>
    </NSpin>
  </div>
</template>
