<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-3xl mx-auto">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 md:p-8">
            <div class="text-center mb-8">
              <div class="w-32 h-32 rounded-full overflow-hidden mx-auto mb-4">
                <img 
                  src="@/assets/avatar.jpg" 
                  alt="个人头像" 
                  class="w-full h-full object-cover"
                />
              </div>
              
              <h1 class="text-2xl md:text-3xl font-bold mb-2 text-gray-900 dark:text-white">
                关于我
              </h1>
              
              <p class="text-gray-600 dark:text-gray-300">
                个人开发者（golang，java， python，vue） / 技术分享人
              </p>
            </div>
            
            <div class="prose dark:prose-invert max-w-none">
              <h2>个人介绍</h2>
              <p>
                你好！我是一名热爱技术的 Go 开发者，专注于服务端开发，大数据 SQL 开发。我喜欢探索新技术，解决复杂问题，并分享我的知识和经验。
              </p>
              <p>
                沉浸式碎片🧩：
                1.躺着刷 b 站，同城。
                2.挑选朋友聚餐地点。
                3.思考代码的实现方式。
                4.逛牛客博客，淘股吧。
                5.待发现...
              </p>
              
              <h2>技能</h2>
              <ul>
                <li>后端开发：Golang, Java, Python</li>
                <li>前端开发：HTML, CSS, JavaScript</li>
                <li>前端框架：Vue</li>
                <li>数据库：MySQL, Redis, Doris, Hive</li>
                <li>其他：Git, Docker, CI/CD</li>
              </ul>
              
              <h2>工作经历</h2>
              <p>
                我有多年的 Web 开发经验，曾在多家公司担任 Golang 开发工程师和SQL工程师。我参与过各种规模的项目，从小型系统到大型企业应用。
              </p>
              
              <h2>教育背景</h2>
              <p>
                软件工程专业学士学位
              </p>
              
              <h2>联系方式</h2>
              <p>
                邮箱：1472396277@qq.com<br>
                GitHub：<a href="https://github.com/WuXBiao/" target="_blank">github.com/WuXBiao/</a><br>
              </p>
              
              <h2>关于本站</h2>
              <p>
                这个博客是我用来分享技术文章、教程和个人思考的平台。主要内容包括开发知识、部署技能、性能优化等。
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
