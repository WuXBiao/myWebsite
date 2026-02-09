<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-3xl mx-auto">
          <div v-if="article" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 md:p-8">
            <div class="mb-6">
              <h1 class="text-2xl md:text-3xl font-bold mb-4 text-gray-900 dark:text-white">
                {{ article.title }}
              </h1>
              
              <div class="flex items-center text-sm text-gray-500 dark:text-gray-400 mb-6">
                <span class="mr-4">{{ formatDate(article.date) }}</span>
                <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs">
                  {{ getCategoryName(article.category) }}
                </span>
              </div>
              
              <div class="h-px bg-gray-200 dark:bg-gray-700 w-full"></div>
            </div>
            
            <div class="markdown-body prose dark:prose-invert max-w-none" v-html="articleContent"></div>
            
            <div class="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
              <div class="flex justify-between">
                <router-link 
                  to="/article" 
                  class="text-primary dark:text-primary hover:underline"
                >
                  ← 返回文章列表
                </router-link>
                <router-link 
                  :to="`/article/${article.id}/edit`" 
                  class="text-primary dark:text-primary hover:underline"
                >
                  编辑文章 →
                </router-link>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center py-16">
            <p class="text-gray-500 dark:text-gray-400 text-lg">文章不存在或已被删除</p>
            <router-link 
              to="/article" 
              class="mt-4 inline-block text-primary dark:text-primary hover:underline"
            >
              返回文章列表
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import MainLayout from '../components/layout/MainLayout.vue';
import { useArticleStore } from '../store/article';
import { formatDate, markdownToHtml } from '../utils/format';

const route = useRoute();
const articleStore = useArticleStore();

const article = computed(() => {
  return articleStore.getArticleById(route.params.id);
});

const articleContent = computed(() => {
  if (!article.value) return '';
  return markdownToHtml(article.value.content);
});

const getCategoryName = (categoryId) => {
  const category = articleStore.getCategories.find(cat => cat.id === categoryId);
  return category ? category.name : categoryId;
};
</script>

<style>
/* 引入 highlight.js 样式 */
@import 'highlight.js/styles/github.css';
/* 暗模式下使用暗色主题 */
.dark .hljs {
  background: #1e1e1e;
}
</style>
