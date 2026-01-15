import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/fullstack',
    name: 'Fullstack',
    component: () => import('../views/Fullstack.vue'),
    meta: { title: '全栈' }
  },
  {
    path: '/article',
    name: 'Article',
    component: () => import('../views/Article.vue'),
    meta: { title: '文章' }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue'),
    meta: { title: '文章详情' }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue'),
    meta: { title: '关于我' }
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('../views/Resume.vue'),
    meta: { title: '个人简历' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
];

const router = createRouter({
  // history: createWebHistory(),
  history: createHashHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

// 全局前置守卫，设置页面标题
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} | biao 的空间站` : '我的个人博客';
  next();
});

export default router;
