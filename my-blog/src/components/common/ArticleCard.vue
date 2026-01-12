<template>
  <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
    <router-link :to="`/article/${article.id}`" class="block">
      <div class="p-5">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2 line-clamp-2">
          {{ article.title }}
        </h3>
        
        <div class="flex items-center text-sm text-gray-500 dark:text-gray-400 mb-3">
          <span class="mr-3">{{ formatDate(article.date) }}</span>
          <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs">
            {{ getCategoryName(article.category) }}
          </span>
        </div>
        
        <p class="text-gray-600 dark:text-gray-300 text-sm line-clamp-3 mb-4">
          {{ article.summary }}
        </p>
        
        <div class="flex justify-between items-center">
          <span class="text-primary dark:text-primary text-sm font-medium">阅读全文</span>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useArticleStore } from '../../store/article';
import { formatDate } from '../../utils/format';

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
});

const articleStore = useArticleStore();

const getCategoryName = (categoryId) => {
  const category = articleStore.getCategories.find(cat => cat.id === categoryId);
  return category ? category.name : categoryId;
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
