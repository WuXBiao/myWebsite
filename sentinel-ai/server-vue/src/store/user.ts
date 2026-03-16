import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

  async function login(form: any) {
    const res: any = await request.post('/login', form)
    token.value = res.token
    user.value = { username: res.username, id: res.user_id }
    
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    
    router.push('/')
  }

  function logout() {
    token.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }

  return { token, user, login, logout }
})
