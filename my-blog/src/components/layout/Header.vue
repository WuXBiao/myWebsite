<template>
  <header class="sticky top-0 z-50 bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700 shadow-sm">
    <div class="container mx-auto px-4 py-3 flex justify-between items-center">
      <!-- Logo -->
      <div class="flex items-center">
        <router-link to="/" class="text-xl font-bold text-primary dark:text-primary">
          biao 的空间站
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <nav class="hidden md:flex space-x-6">
        <router-link 
          v-for="item in navItems" 
          :key="item.path" 
          :to="item.path"
          class="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors"
          :class="{ 'text-primary dark:text-primary': isActive(item.path) }"
        >
          {{ item.name }}
        </router-link>
        
        <a 
          href="https://github.com/yourusername" 
          target="_blank" 
          class="text-gray-700 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors"
        >
          GitHub
        </a>
      </nav>

      <!-- 移动端菜单按钮和主题切换 -->
      <div class="flex items-center space-x-4">
        <button @click="toggleDarkMode" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
          <svg v-if="isDarkMode" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
          </svg>
        </button>
        
        <button @click="toggleMobileMenu" class="md:hidden p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-700 dark:text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path v-if="!isMobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <!-- 移动端菜单 -->
    <div 
      v-show="isMobileMenuOpen" 
      class="md:hidden bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700"
    >
      <div class="container mx-auto px-4 py-2">
        <div class="flex flex-col space-y-3 py-3">
          <router-link 
            v-for="item in navItems" 
            :key="item.path" 
            :to="item.path"
            class="px-3 py-2 rounded-md text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-primary dark:hover:text-primary"
            :class="{ 'bg-gray-100 dark:bg-gray-800 text-primary dark:text-primary': isActive(item.path) }"
            @click="isMobileMenuOpen = false"
          >
            {{ item.name }}
          </router-link>
          
          <a 
            href="https://github.com/yourusername" 
            target="_blank" 
            class="px-3 py-2 rounded-md text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-primary dark:hover:text-primary"
            @click="isMobileMenuOpen = false"
          >
            GitHub
          </a>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useThemeStore } from '../../store/theme';

const route = useRoute();
const themeStore = useThemeStore();

const isMobileMenuOpen = ref(false);
const isDarkMode = computed(() => themeStore.darkMode);

const navItems = [
  { name: '首页', path: '/' },
  { name: '前端', path: '/frontend' },
  { name: '全栈', path: '/fullstack' },
  { name: '文章', path: '/article' },
  { name: '关于', path: '/about' },
  { name: '简历', path: '/resume' },
];

const isActive = (path) => {
  if (path === '/') {
    return route.path === '/';
  }
  return route.path.startsWith(path);
};

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const toggleDarkMode = () => {
  themeStore.toggleDarkMode();
};
</script>
