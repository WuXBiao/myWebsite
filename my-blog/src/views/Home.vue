<template>
  <MainLayout>
    <!-- 英雄区域 -->
    <section class="bg-gradient-to-r from-blue-500 to-purple-600 text-white py-16">
      <div class="container mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">欢迎来到我的空间</h1>
        <p class="text-xl md:text-2xl opacity-90 mb-8 max-w-3xl mx-auto">
          在这里，我分享技术交流和个人思考~
        </p>
        <div class="flex justify-center space-x-4">
          <router-link 
            to="/frontend" 
            class="bg-white text-blue-600 hover:bg-blue-50 px-6 py-3 rounded-lg font-medium transition-colors"
          >
            浏览文章
          </router-link>
          <router-link 
            to="/about" 
            class="bg-transparent border border-white text-white hover:bg-white hover:bg-opacity-10 px-6 py-3 rounded-lg font-medium transition-colors"
          >
            关于我
          </router-link>
        </div>
      </div>
    </section>

    <!-- 最新文章 -->
    <section class="py-12 bg-gray-50 dark:bg-gray-900">
      <div class="container mx-auto px-4">
        <h2 class="text-2xl md:text-3xl font-bold text-center mb-8 text-gray-900 dark:text-white">
          最新文章
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <ArticleCard 
            v-for="article in latestArticles" 
            :key="article.id" 
            :article="article" 
          />
        </div>
        
        <div class="text-center mt-10">
          <router-link 
            to="/article" 
            class="inline-block bg-primary hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-medium transition-colors"
          >
            查看全部文章
          </router-link>
        </div>
      </div>
    </section>

    <!-- 分类 -->
    <section class="py-12 bg-white dark:bg-gray-800">
      <div class="container mx-auto px-4">
        <h2 class="text-2xl md:text-3xl font-bold text-center mb-8 text-gray-900 dark:text-white">
          内容分类
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div 
            v-for="category in categories" 
            :key="category.id"
            class="bg-gray-50 dark:bg-gray-700 rounded-lg p-6 text-center hover:shadow-md transition-shadow"
          >
            <router-link :to="`/${category.id}`">
              <h3 class="text-xl font-semibold mb-3 text-gray-900 dark:text-white">
                {{ category.name }}
              </h3>
              <p class="text-gray-600 dark:text-gray-300">
                探索{{ category.name }}相关的文章和教程
              </p>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- 关于 -->
    <section class="py-12 bg-gray-50 dark:bg-gray-900">
      <div class="container mx-auto px-4">
        <div class="max-w-3xl mx-auto">
          <h2 class="text-2xl md:text-3xl font-bold text-center mb-6 text-gray-900 dark:text-white">
            关于本站
          </h2>
          
          <p class="text-gray-600 dark:text-gray-300 mb-6 text-center">
            这是我的个人博客网站，主要分享前端开发、全栈技术和个人思考。希望这些内容对你有所帮助！
          </p>
          
          <div class="text-center">
            <router-link 
              to="/about" 
              class="inline-block text-primary hover:text-blue-600 font-medium transition-colors"
            >
              了解更多 →
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { computed } from 'vue';
import MainLayout from '../components/layout/MainLayout.vue';
import ArticleCard from '../components/common/ArticleCard.vue';
import { useArticleStore } from '../store/article';

const articleStore = useArticleStore();

const latestArticles = computed(() => {
  return articleStore.getAllArticles.slice(0, 3);
});

const categories = computed(() => {
  return articleStore.getCategories;
});
</script>
