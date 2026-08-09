<script lang="ts" setup>
import {computed} from 'vue';
import {useAppStore} from '@/store/modules/app';
import {$t} from '@/locales';
import pkg from '~/package.json';

const appStore = useAppStore();

const column = computed(() => (appStore.isMobile ? 1 : 2));

interface PkgJson {
  name: string;
  version: string;
  dependencies: PkgVersionInfo[];
  devDependencies: PkgVersionInfo[];
}

interface PkgVersionInfo {
  name: string;
  version: string;
}

const {name, version, dependencies, devDependencies} = pkg;

function transformVersionData(tuple: [string, string]): PkgVersionInfo {
  const [$name, $version] = tuple;
  return {
    name: $name,
    version: $version
  };
}

const pkgJson: PkgJson = {
  name,
  version,
  dependencies: Object.entries(dependencies).map(item => transformVersionData(item)),
  devDependencies: Object.entries(devDependencies).map(item => transformVersionData(item))
};

const latestBuildTime = BUILD_TIME;
</script>

<template>
  <NSpace :size="16" vertical>
    <NCard :bordered="false" :title="$t('page.about.title')" class="card-wrapper" segmented size="small">
      <p>{{ $t('page.about.introduction') }}</p>
    </NCard>
    <NCard :bordered="false" :title="$t('page.about.projectInfo.title')" class="card-wrapper" segmented size="small">
      <NDescriptions :column="column" bordered label-placement="left" size="small">
        <NDescriptionsItem :label="$t('page.about.projectInfo.version')">
          <NTag type="primary">{{ pkgJson.version }}</NTag>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.about.projectInfo.latestBuildTime')">
          <NTag type="primary">{{ latestBuildTime }}</NTag>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.about.projectInfo.githubLink')">
          <a :href="pkg.homepage" class="text-primary" rel="noopener noreferrer" target="_blank">
            {{ $t('page.about.projectInfo.githubLink') }}
          </a>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.about.projectInfo.previewLink')">
          <a :href="pkg.website" class="text-primary" rel="noopener noreferrer" target="_blank">
            {{ $t('page.about.projectInfo.previewLink') }}
          </a>
        </NDescriptionsItem>
      </NDescriptions>
    </NCard>
    <NCard :bordered="false" :title="$t('page.about.prdDep')" class="card-wrapper" segmented size="small">
      <NDescriptions :column="column" bordered label-placement="left" size="small">
        <NDescriptionsItem v-for="item in pkgJson.dependencies" :key="item.name" :label="item.name">
          {{ item.version }}
        </NDescriptionsItem>
      </NDescriptions>
    </NCard>
    <NCard :bordered="false" :title="$t('page.about.devDep')" class="card-wrapper" segmented size="small">
      <NDescriptions :column="column" bordered label-placement="left" size="small">
        <NDescriptionsItem v-for="item in pkgJson.devDependencies" :key="item.name" :label="item.name">
          {{ item.version }}
        </NDescriptionsItem>
      </NDescriptions>
    </NCard>
  </NSpace>
</template>

<style scoped></style>
