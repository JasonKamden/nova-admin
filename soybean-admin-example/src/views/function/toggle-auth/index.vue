<script lang="ts" setup>
import {computed, ref} from 'vue';
import {useRoute} from 'vue-router';
import {useLoading} from '@sa/hooks';
import {useAppStore} from '@/store/modules/app';
import {useAuthStore} from '@/store/modules/auth';
import {useTabStore} from '@/store/modules/tab';
import {useAuth} from '@/hooks/business/auth';
import {$t} from '@/locales';

const route = useRoute();
const appStore = useAppStore();
const authStore = useAuthStore();
const tabStore = useTabStore();
const {hasAuth} = useAuth();
const {loading, startLoading, endLoading} = useLoading();

type AccountKey = 'super' | 'admin' | 'user';

interface Account {
  key: AccountKey;
  label: string;
  userName: string;
  password: string;
}

const accounts = computed<Account[]>(() => [
  {
    key: 'super',
    label: $t('page.login.pwdLogin.superAdmin'),
    userName: 'Super',
    password: '123456'
  },
  {
    key: 'admin',
    label: $t('page.login.pwdLogin.admin'),
    userName: 'Admin',
    password: '123456'
  },
  {
    key: 'user',
    label: $t('page.login.pwdLogin.user'),
    userName: 'User',
    password: '123456'
  }
]);

const loginAccount = ref<AccountKey>('super');

async function handleToggleAccount(account: Account) {
  loginAccount.value = account.key;

  startLoading();
  await authStore.login(account.userName, account.password, false);
  tabStore.initTabStore(route);
  endLoading();
  appStore.reloadPage();
}
</script>

<template>
  <NSpace :size="16" vertical>
    <NCard :bordered="false" :title="$t('route.function_toggle-auth')" class="card-wrapper" segmented size="small">
      <NDescriptions :column="1" bordered>
        <NDescriptionsItem :label="$t('page.manage.user.userRole')">
          <NSpace>
            <NTag v-for="role in authStore.userInfo.roles" :key="role">{{ role }}</NTag>
          </NSpace>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.function.toggleAuth.toggleAccount')" ions-item>
          <NSpace>
            <NButton
                v-for="account in accounts"
                :key="account.key"
                :disabled="loading && loginAccount !== account.key"
                :loading="loading && loginAccount === account.key"
                @click="handleToggleAccount(account)"
            >
              {{ account.label }}
            </NButton>
          </NSpace>
        </NDescriptionsItem>
      </NDescriptions>
    </NCard>
    <NCard
        :bordered="false"
        :title="$t('page.function.toggleAuth.authHook')"
        class="card-wrapper"
        segmented
        size="small"
    >
      <NSpace>
        <NButton v-if="hasAuth('B_CODE1')">{{ $t('page.function.toggleAuth.superAdminVisible') }}</NButton>
        <NButton v-if="hasAuth('B_CODE2')">{{ $t('page.function.toggleAuth.adminVisible') }}</NButton>
        <NButton v-if="hasAuth('B_CODE3')">
          {{ $t('page.function.toggleAuth.adminOrUserVisible') }}
        </NButton>
      </NSpace>
    </NCard>
  </NSpace>
</template>

<style scoped></style>
