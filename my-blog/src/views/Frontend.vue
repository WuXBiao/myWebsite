<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-4xl mx-auto">
          <h1 class="text-3xl md:text-4xl font-bold mb-6 text-gray-900 dark:text-white">
            前端开发
          </h1>
          
          <p class="text-gray-600 dark:text-gray-300 mb-8">
            这里收集了关于前端开发的文章、教程和资源，包括 JavaScript、框架、性能优化等内容。
          </p>
          
          <div class="space-y-6">
            <ArticleCard 
              v-for="article in frontendArticles" 
              :key="article.id" 
              :article="article" 
            />
          </div>
          
          <div v-if="frontendArticles.length === 0" class="text-center py-12">
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

const frontendArticles = computed(() => {
  return articleStore.getArticlesByCategory('frontend');
});
</script>
