import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 10000
})

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    return Promise.reject(error)
  }
)

export default {
  // 获取指标树形结构
  getIndexTree(period) {
    return api.get('/index/tree', { params: { period } })
  },

  // 获取指标数据列表
  getIndexList(period) {
    return api.get('/index/data/list', { params: { period } })
  },

  // 分页查询指标数据
  getIndexPage(period, category, page, size) {
    return api.get('/index/data/page', { params: { period, category, page, size } })
  },

  // 获取所有期间
  getPeriods() {
    return api.get('/index/periods')
  },

  // 获取所有分类
  getCategories() {
    return api.get('/index/categories')
  },

  // 保存指标
  saveIndex(data) {
    return api.post('/index', data)
  },

  // 批量保存
  saveAllIndexes(data) {
    return api.post('/index/batch', data)
  },

  // 删除指标
  deleteIndex(id) {
    return api.delete(`/index/${id}`)
  },

  // 导出 Excel
  exportExcel(period, category) {
    const params = { period }
    if (category) params.category = category
    return `http://localhost:8081/api/index/export?period=${period}${category ? '&category=' + category : ''}`
  }
}
