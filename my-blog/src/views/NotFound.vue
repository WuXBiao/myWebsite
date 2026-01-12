<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 min-h-[60vh] flex items-center">
      <div class="container mx-auto px-4 text-center">
        <h1 class="text-6xl md:text-8xl font-bold text-primary mb-4">404</h1>
        <h2 class="text-2xl md:text-3xl font-semibold mb-6 text-gray-800 dark:text-gray-200">
          页面未找到
        </h2>
        <p class="text-gray-600 dark:text-gray-400 mb-8 max-w-md mx-auto">
          您访问的页面不存在或已被删除。请检查 URL 是否正确，或返回首页。
        </p>
        <router-link 
          to="/" 
          class="inline-block bg-primary hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-medium transition-colors"
        >
          返回首页
        </router-link>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../components/layout/MainLayout.vue';
</script>
