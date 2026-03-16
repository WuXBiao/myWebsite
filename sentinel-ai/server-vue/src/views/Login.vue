<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>SentinelAI Login</h2>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="Username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">Login</el-button>
          <el-button @click="handleRegister">Register</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '../store/user'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const form = ref({
  username: '',
  password: ''
})

const userStore = useUserStore()

const handleLogin = async () => {
  try {
    await userStore.login(form.value)
    ElMessage.success('Login successful')
  } catch (error) {
    // Error handled in request interceptor
  }
}

const handleRegister = async () => {
  try {
    await request.post('/register', form.value)
    ElMessage.success('Registration successful. Please login.')
  } catch (error) {
    // Error handled in request interceptor
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
</style>
