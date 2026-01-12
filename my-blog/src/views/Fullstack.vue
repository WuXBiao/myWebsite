<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-4xl mx-auto">
          <h1 class="text-3xl md:text-4xl font-bold mb-6 text-gray-900 dark:text-white">
            全栈开发
          </h1>
          
          <p class="text-gray-600 dark:text-gray-300 mb-8">
            这里收集了关于全栈开发的文章、教程和资源，包括后端开发、数据库、API 设计等内容。
          </p>
          
          <div class="space-y-6">
            <ArticleCard 
              v-for="article in fullstackArticles" 
              :key="article.id" 
              :article="article" 
            />
          </div>
          
          <div v-if="fullstackArticles.length === 0" class="text-center py-12">
            <p class="text-gray-500 dark:text-gray-400">暂无相关文章</p>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { computed } from 'vue';
import MainLayout from '../components/layout/MainLayout.vue';
import ArticleCard from '../components/common/ArticleCard.vue';
import { useArticleStore } from '../store/article';

const articleStore = useArticleStore();

const fullstackArticles = computed(() => {
  return articleStore.getArticlesByCategory('fullstack');
});
</script>
