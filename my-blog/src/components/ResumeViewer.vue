<template>
  <div class="resume-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载简历中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-message">
      <div class="error-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="12"/>
          <line x1="12" y1="16" x2="12.01" y2="16"/>
        </svg>
      </div>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button @click="fetchResume" class="btn btn-primary">重试</button>
    </div>

    <!-- 成功状态 -->
    <div v-else-if="resumePath">
      <!-- PDF 预览区域 -->
      <div class="pdf-viewer">
        <object
          :data="resumePath"
          type="application/pdf"
          class="pdf-object"
        >
          <!-- 如果浏览器不支持 object，显示备用内容 -->
          <div class="fallback">
            <div class="fallback-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <polyline points="10 9 9 9 8 9"/>
              </svg>
            </div>
            <h3>无法预览 PDF</h3>
            <p>您的浏览器不支持内嵌 PDF 预览</p>
            <a :href="resumePath" download="resume.pdf" class="btn btn-primary">
              点击下载简历
            </a>
          </div>
        </object>
      </div>

      <!-- 操作按钮放到底部 -->
      <div class="action-buttons">
        <a 
          :href="resumePath" 
          :download="currentFileName"
          class="btn btn-primary"
        >
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/>
          </svg>
          下载简历
        </a>
        <a 
          :href="resumePath" 
          target="_blank"
          class="btn btn-secondary"
        >
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3"/>
          </svg>
          新窗口打开
        </a>
      </div>
    </div>

    <!-- 无文件状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/>
          <polyline points="10 9 9 9 8 9"/>
        </svg>
      </div>
      <h3>暂无简历</h3>
      <p>简历文件还未上传</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const loading = ref(true)
const error = ref(null)
const resumePath = ref(null)
const currentFileName = ref('resume.pdf')

// 后端服务地址（需要根据实际情况修改）
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// 加载简历
const fetchResume = async () => {
  loading.value = true
  error.value = null
  resumePath.value = null

  try {
    // 直接构建下载 URL，验证文件是否存在
    // 生产环境：API_BASE_URL 为 /api，所以路径是 /api/download/resume.pdf
    // 开发环境：API_BASE_URL 为 http://localhost:8080，所以路径是 http://localhost:8080/api/download/resume.pdf
    const resumeUrl = `${API_BASE_URL}/download/resume.pdf`
    
    // 发送 HEAD 请求验证文件存在
    const response = await fetch(resumeUrl, { method: 'HEAD' })
    
    if (!response.ok) {
      throw new Error(`文件不存在或无法访问 (HTTP ${response.status})`)
    }

    // 文件存在，设置下载 URL
    currentFileName.value = 'resume.pdf'
    resumePath.value = resumeUrl
  } catch (err) {
    error.value = `加载失败: ${err.message}`
    console.error('Failed to load resume:', err)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取简历
onMounted(() => {
  fetchResume()
})
</script>

<style scoped>
.resume-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.5rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-secondary {
  background: #6b7280;
  color: white;
}

.btn-secondary:hover {
  background: #4b5563;
}

.icon {
  width: 1.25rem;
  height: 1.25rem;
}

.pdf-viewer {
  background: #f3f4f6;
  border-radius: 0.75rem;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.pdf-object {
  width: 100%;
  height: 85vh;
  min-height: 600px;
  display: block;
}

.fallback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  min-height: 400px;
}

.fallback-icon {
  width: 80px;
  height: 80px;
  color: #9ca3af;
  margin-bottom: 1.5rem;
}

.fallback-icon svg {
  width: 100%;
  height: 100%;
}

.fallback h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.fallback p {
  color: #6b7280;
  margin-bottom: 1.5rem;
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 0.75rem;
  min-height: 400px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading p {
  color: #6b7280;
  font-size: 1rem;
}

/* 错误状态 */
.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 0.75rem;
  min-height: 400px;
}

.error-icon {
  width: 80px;
  height: 80px;
  color: #ef4444;
  margin-bottom: 1.5rem;
}

.error-icon svg {
  width: 100%;
  height: 100%;
}

.error-message h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #dc2626;
  margin-bottom: 0.5rem;
}

.error-message p {
  color: #991b1b;
  margin-bottom: 1.5rem;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 0.75rem;
  min-height: 400px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  color: #d1d5db;
  margin-bottom: 1.5rem;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #6b7280;
}

@media (max-width: 640px) {
  .resume-container {
    padding: 1rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .btn {
    justify-content: center;
  }
  
  .pdf-object {
    height: 70vh;
    min-height: 500px;
  }
}
</style>
