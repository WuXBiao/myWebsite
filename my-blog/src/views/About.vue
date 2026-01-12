<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-3xl mx-auto">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 md:p-8">
            <div class="text-center mb-8">
              <div class="w-32 h-32 rounded-full overflow-hidden mx-auto mb-4">
                <img 
                  src="https://via.placeholder.com/128" 
                  alt="个人头像" 
                  class="w-full h-full object-cover"
                />
              </div>
              
              <h1 class="text-2xl md:text-3xl font-bold mb-2 text-gray-900 dark:text-white">
                关于我
              </h1>
              
              <p class="text-gray-600 dark:text-gray-300">
                Web 开发者 / 技术博主
              </p>
            </div>
            
            <div class="prose dark:prose-invert max-w-none">
              <h2>个人介绍</h2>
              <p>
                你好！我是一名热爱技术的 Web 开发者，专注于前端和全栈开发。我喜欢探索新技术，解决复杂问题，并分享我的知识和经验。
              </p>
              
              <h2>技能</h2>
              <ul>
                <li>前端开发：HTML, CSS, JavaScript, TypeScript</li>
                <li>前端框架：Vue, React, Angular</li>
                <li>后端开发：Node.js, Express, NestJS</li>
                <li>数据库：MongoDB, MySQL, PostgreSQL</li>
                <li>其他：Git, Docker, CI/CD, AWS</li>
              </ul>
              
              <h2>工作经历</h2>
              <p>
                我有多年的 Web 开发经验，曾在多家公司担任前端开发工程师和全栈开发工程师。我参与过各种规模的项目，从小型网站到大型企业应用。
              </p>
              
              <h2>教育背景</h2>
              <p>
                计算机科学与技术专业学士学位
              </p>
              
              <h2>联系方式</h2>
              <p>
                邮箱：your.email@example.com<br>
                GitHub：<a href="https://github.com/yourusername" target="_blank">github.com/yourusername</a><br>
                Twitter：<a href="https://twitter.com/yourusername" target="_blank">@yourusername</a>
              </p>
              
              <h2>关于本站</h2>
              <p>
                这个博客是我用来分享技术文章、教程和个人思考的平台。主要内容包括前端开发、全栈技术、性能优化等。
              </p>
              <p>
                本站使用 Vue 3、Vite 和 Tailwind CSS 构建，采用响应式设计，支持深色模式。
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../components/layout/MainLayout.vue';
</script>
