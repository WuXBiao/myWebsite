<script setup>
import { onMounted } from 'vue';
import { useThemeStore } from './store/theme';
import { useArticleStore } from './store/article';
import Live2dKanban from './components/common/Live2dKanban.vue';

const themeStore = useThemeStore();
const articleStore = useArticleStore();

onMounted(() => {
  themeStore.init();
  articleStore.init();
});
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <Live2dKanban />
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
