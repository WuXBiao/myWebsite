<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900">
    <!-- 导航栏 -->
    <nav class="bg-black bg-opacity-30 backdrop-blur-md border-b border-purple-500 border-opacity-30">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center space-x-2">
            <span class="text-2xl">🔮</span>
            <h1 class="text-2xl font-bold text-white">卦云</h1>
            <span class="text-sm text-purple-300">运势预测</span>
          </div>
          <div class="flex space-x-6">
            <router-link to="/" class="text-purple-200 hover:text-white transition">首页</router-link>
            <router-link to="/history" class="text-purple-200 hover:text-white transition">历史记录</router-link>
            <router-link to="/about" class="text-purple-200 hover:text-white transition">关于</router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="bg-black bg-opacity-30 backdrop-blur-md border-t border-purple-500 border-opacity-30 mt-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 text-center text-purple-300 text-sm">
        <p>© 2024 卦云运势预测 | 仅供娱乐参考</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
</style>
