<template>
  <div class="space-y-8">
    <!-- 标题 -->
    <div class="text-center mb-12">
      <h2 class="text-4xl font-bold text-white mb-4">输入你的阴历生日信息</h2>
      <p class="text-purple-200 text-lg">根据出生年月日时辰，预测你的运势</p>
    </div>

    <!-- 输入表单 -->
    <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-purple-500 border-opacity-30 max-w-2xl mx-auto">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- 基本信息 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- 出生年份 -->
          <div>
            <label class="block text-white font-semibold mb-2">出生年份</label>
            <input 
              v-model.number="form.year" 
              type="number" 
              min="1900" 
              max="2024"
              placeholder="例如: 1990"
              class="w-full px-4 py-3 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white placeholder-purple-300 focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition"
              required
            />
          </div>

          <!-- 性别 -->
          <div>
            <label class="block text-white font-semibold mb-2">性别</label>
            <select 
              v-model="form.gender"
              class="w-full px-4 py-3 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition"
              required
            >
              <option value="" disabled>请选择</option>
              <option value="male">男</option>
              <option value="female">女</option>
            </select>
          </div>
        </div>

        <!-- 日期时间 -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div>
            <label class="block text-white font-semibold mb-2 text-sm">月</label>
            <input 
              v-model.number="form.month" 
              type="number" 
              min="1" 
              max="12"
              placeholder="1-12"
              class="w-full px-3 py-2 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white placeholder-purple-300 focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition text-sm"
              required
            />
          </div>

          <div>
            <label class="block text-white font-semibold mb-2 text-sm">日</label>
            <input 
              v-model.number="form.day" 
              type="number" 
              min="1" 
              max="31"
              placeholder="1-31"
              class="w-full px-3 py-2 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white placeholder-purple-300 focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition text-sm"
              required
            />
          </div>

          <div>
            <label class="block text-white font-semibold mb-2 text-sm">时</label>
            <input 
              v-model.number="form.hour" 
              type="number" 
              min="0" 
              max="23"
              placeholder="0-23"
              class="w-full px-3 py-2 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white placeholder-purple-300 focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition text-sm"
              required
            />
          </div>

          <div>
            <label class="block text-white font-semibold mb-2 text-sm">分</label>
            <input 
              v-model.number="form.minute" 
              type="number" 
              min="0" 
              max="59"
              placeholder="0-59"
              class="w-full px-3 py-2 rounded-lg bg-white bg-opacity-10 border border-purple-300 border-opacity-50 text-white placeholder-purple-300 focus:outline-none focus:border-purple-400 focus:bg-opacity-20 transition text-sm"
              required
            />
          </div>
        </div>

        <!-- 提交按钮 -->
        <button 
          type="submit"
          :disabled="loading"
          class="w-full py-3 px-6 rounded-lg bg-gradient-to-r from-purple-500 to-pink-500 text-white font-bold text-lg hover:from-purple-600 hover:to-pink-600 transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? '预测中...' : '开始预测' }}
        </button>
      </form>
    </div>

    <!-- 结果展示 -->
    <div v-if="result" class="space-y-6">
      <!-- 八字信息 -->
      <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-purple-500 border-opacity-30">
        <h3 class="text-2xl font-bold text-white mb-6">你的八字</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-purple-400 border-opacity-30">
            <p class="text-purple-300 text-sm mb-2">年柱</p>
            <p class="text-white text-2xl font-bold">{{ result.bazi.year }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-purple-400 border-opacity-30">
            <p class="text-purple-300 text-sm mb-2">月柱</p>
            <p class="text-white text-2xl font-bold">{{ result.bazi.month }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-purple-400 border-opacity-30">
            <p class="text-purple-300 text-sm mb-2">日柱</p>
            <p class="text-white text-2xl font-bold">{{ result.bazi.day }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-purple-400 border-opacity-30">
            <p class="text-purple-300 text-sm mb-2">时柱</p>
            <p class="text-white text-2xl font-bold">{{ result.bazi.hour }}</p>
          </div>
        </div>
      </div>

      <!-- 短期运势 -->
      <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-green-500 border-opacity-30">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-2xl font-bold text-white">短期运势</h3>
          <div class="text-center">
            <div class="text-5xl font-bold text-green-400">{{ result.fortune.shortTerm.score }}</div>
            <p class="text-green-300 text-sm">运势指数</p>
          </div>
        </div>
        <p class="text-purple-200 text-sm mb-4">{{ result.fortune.shortTerm.period }}</p>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-green-300 font-semibold mb-2">整体运势</p>
            <p class="text-white">{{ result.fortune.shortTerm.overall }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-green-300 font-semibold mb-2">事业运</p>
            <p class="text-white">{{ result.fortune.shortTerm.career }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-green-300 font-semibold mb-2">感情运</p>
            <p class="text-white">{{ result.fortune.shortTerm.love }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-green-300 font-semibold mb-2">健康运</p>
            <p class="text-white">{{ result.fortune.shortTerm.health }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-green-300 font-semibold mb-2">财运</p>
            <p class="text-white">{{ result.fortune.shortTerm.wealth }}</p>
          </div>
        </div>
      </div>

      <!-- 长期运势 -->
      <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-blue-500 border-opacity-30">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-2xl font-bold text-white">长期运势</h3>
          <div class="text-center">
            <div class="text-5xl font-bold text-blue-400">{{ result.fortune.longTerm.score }}</div>
            <p class="text-blue-300 text-sm">运势指数</p>
          </div>
        </div>
        <p class="text-purple-200 text-sm mb-4">{{ result.fortune.longTerm.period }}</p>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-blue-300 font-semibold mb-2">整体运势</p>
            <p class="text-white">{{ result.fortune.longTerm.overall }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-blue-300 font-semibold mb-2">事业运</p>
            <p class="text-white">{{ result.fortune.longTerm.career }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-blue-300 font-semibold mb-2">感情运</p>
            <p class="text-white">{{ result.fortune.longTerm.love }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-blue-300 font-semibold mb-2">健康运</p>
            <p class="text-white">{{ result.fortune.longTerm.health }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4">
            <p class="text-blue-300 font-semibold mb-2">财运</p>
            <p class="text-white">{{ result.fortune.longTerm.wealth }}</p>
          </div>
        </div>
      </div>

      <!-- 五行分析 -->
      <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-yellow-500 border-opacity-30">
        <h3 class="text-2xl font-bold text-white mb-6">五行分析</h3>
        <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-green-400 border-opacity-50">
            <p class="text-green-300 font-semibold mb-2">木</p>
            <p class="text-white text-lg">{{ result.fortune.fiveElements.wood }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-red-400 border-opacity-50">
            <p class="text-red-300 font-semibold mb-2">火</p>
            <p class="text-white text-lg">{{ result.fortune.fiveElements.fire }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-yellow-400 border-opacity-50">
            <p class="text-yellow-300 font-semibold mb-2">土</p>
            <p class="text-white text-lg">{{ result.fortune.fiveElements.earth }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-gray-400 border-opacity-50">
            <p class="text-gray-300 font-semibold mb-2">金</p>
            <p class="text-white text-lg">{{ result.fortune.fiveElements.metal }}</p>
          </div>
          <div class="bg-white bg-opacity-5 rounded-lg p-4 border border-blue-400 border-opacity-50">
            <p class="text-blue-300 font-semibold mb-2">水</p>
            <p class="text-white text-lg">{{ result.fortune.fiveElements.water }}</p>
          </div>
        </div>
      </div>

      <!-- 幸运信息 -->
      <div class="bg-white bg-opacity-10 backdrop-blur-md rounded-2xl p-8 border border-pink-500 border-opacity-30">
        <h3 class="text-2xl font-bold text-white mb-6">幸运信息</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <p class="text-pink-300 font-semibold mb-3">幸运颜色</p>
            <div class="flex gap-2">
              <span v-for="color in result.fortune.luckyColor" :key="color" class="px-4 py-2 rounded-lg bg-white bg-opacity-10 border border-pink-400 border-opacity-50 text-white">
                {{ color }}
              </span>
            </div>
          </div>
          <div>
            <p class="text-pink-300 font-semibold mb-3">幸运数字</p>
            <div class="flex gap-2">
              <span v-for="num in result.fortune.luckyNumber" :key="num" class="px-4 py-2 rounded-lg bg-white bg-opacity-10 border border-pink-400 border-opacity-50 text-white">
                {{ num }}
              </span>
            </div>
          </div>
          <div>
            <p class="text-pink-300 font-semibold mb-3">幸运方向</p>
            <p class="px-4 py-2 rounded-lg bg-white bg-opacity-10 border border-pink-400 border-opacity-50 text-white">
              {{ result.fortune.luckyDirection }}
            </p>
          </div>
        </div>
      </div>

      <!-- 重新预测按钮 -->
      <div class="text-center">
        <button 
          @click="resetForm"
          class="px-8 py-3 rounded-lg bg-white bg-opacity-10 border border-purple-400 border-opacity-50 text-white font-semibold hover:bg-opacity-20 transition"
        >
          重新预测
        </button>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="bg-red-500 bg-opacity-20 border border-red-500 rounded-lg p-4 text-red-200">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const form = ref({
  year: new Date().getFullYear(),
  month: 1,
  day: 1,
  hour: 12,
  minute: 0,
  gender: 'male'
})

const result = ref(null)
const loading = ref(false)
const error = ref('')

const handleSubmit = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.post('/api/fortune/predict', form.value)
    if (response.data.code === 0) {
      result.value = response.data.data
    } else {
      error.value = response.data.message || '预测失败'
    }
  } catch (err) {
    error.value = '网络错误，请检查后端服务是否运行'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  result.value = null
  error.value = ''
}
</script>
