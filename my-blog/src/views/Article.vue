<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-4xl mx-auto">
          <h1 class="text-3xl md:text-4xl font-bold mb-6 text-gray-900 dark:text-white">
            所有文章
          </h1>
          
          <div class="mb-8">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索文章..."
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          
          <div class="space-y-6">
            <ArticleCard 
              v-for="article in filteredArticles" 
              :key="article.id" 
              :article="article" 
            />
          </div>
          
          <div v-if="filteredArticles.length === 0" class="text-center py-12">
            <p class="text-gray-500 dark:text-gray-400">没有找到相关文章</p>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import MainLayout from '../components/layout/MainLayout.vue';
import ArticleCard from '../components/common/ArticleCard.vue';
import { useArticleStore } from '../store/article';

const articleStore = useArticleStore();
const searchQuery = ref('');

const filteredArticles = computed(() => {
  const query = searchQuery.value.toLowerCase().trim();
  if (!query) {
    return articleStore.getAllArticles;
  }
  
  return articleStore.getAllArticles.filter(article => {
    return (
      article.title.toLowerCase().includes(query) || 
      article.summary.toLowerCase().includes(query)
    );
  });
});
</script>
