import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/tree'
  },
  {
    path: '/tree',
    name: 'IndexTree',
    component: () => import('../views/IndexTree.vue'),
    meta: { title: '树形视图' }
  },
  {
    path: '/list',
    name: 'IndexList',
    component: () => import('../views/IndexList.vue'),
    meta: { title: '列表视图' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
