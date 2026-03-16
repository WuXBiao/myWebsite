<template>
  <div class="camera-view">
    <h2>{{ deviceName }} - Live View</h2>
    
    <div class="camera-container">
      <video 
        ref="videoElement" 
        autoplay 
        playsinline 
        muted
        class="video-stream"
      ></video>
      <div v-if="!cameraActive" class="no-camera">
        <p>{{ cameraError || 'Initializing camera...' }}</p>
      </div>
    </div>

    <div class="controls">
      <el-button 
        v-if="!cameraActive" 
        type="primary" 
        @click="startCamera"
        :loading="loading"
      >
        Start Camera
      </el-button>
      <el-button 
        v-else 
        type="danger" 
        @click="stopCamera"
      >
        Stop Camera
      </el-button>
      <el-button @click="takeSnapshot">Take Snapshot</el-button>
      <el-button @click="goBack">Back</el-button>
    </div>

    <div v-if="snapshot" class="snapshot-container">
      <h3>Last Snapshot</h3>
      <img :src="snapshot" alt="Snapshot" class="snapshot-image" />
      <el-button @click="downloadSnapshot">Download</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const videoElement = ref<HTMLVideoElement | null>(null)
const cameraActive = ref(false)
const loading = ref(false)
const cameraError = ref('')
const snapshot = ref('')
const deviceName = ref('Camera')
let stream: MediaStream | null = null

onMounted(() => {
  deviceName.value = (route.query.name as string) || 'Camera'
})

onUnmounted(() => {
  stopCamera()
})

const startCamera = async () => {
  loading.value = true
  cameraError.value = ''
  
  try {
    // Check if getUserMedia is supported
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      throw new Error('Your browser does not support camera access')
    }

    console.log('Requesting camera access...')
    
    // Request camera access
    stream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1280 },
        height: { ideal: 720 }
      },
      audio: false
    })

    console.log('Camera access granted, stream:', stream)

    if (videoElement.value) {
      videoElement.value.srcObject = stream
      cameraActive.value = true
      ElMessage.success('Camera started successfully')
      console.log('Camera started')
    }
  } catch (error: any) {
    let errorMsg = 'Failed to access camera'
    
    if (error.name === 'NotAllowedError') {
      errorMsg = 'Camera access denied. Please allow camera access in browser settings.'
    } else if (error.name === 'NotFoundError') {
      errorMsg = 'No camera device found. Please check if your camera is connected.'
    } else if (error.name === 'NotReadableError') {
      errorMsg = 'Camera is in use by another application. Please close other apps using the camera.'
    } else if (error.message) {
      errorMsg = error.message
    }
    
    cameraError.value = errorMsg
    ElMessage.error(errorMsg)
    console.error('Camera error:', error)
  } finally {
    loading.value = false
  }
}

const stopCamera = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  cameraActive.value = false
  ElMessage.info('Camera stopped')
}

const takeSnapshot = () => {
  if (!videoElement.value || !cameraActive.value) {
    ElMessage.warning('Camera is not active')
    return
  }

  const canvas = document.createElement('canvas')
  canvas.width = videoElement.value.videoWidth
  canvas.height = videoElement.value.videoHeight
  
  const ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.drawImage(videoElement.value, 0, 0)
    snapshot.value = canvas.toDataURL('image/jpeg')
    ElMessage.success('Snapshot taken')
  }
}

const downloadSnapshot = () => {
  if (!snapshot.value) {
    ElMessage.warning('No snapshot available')
    return
  }

  const link = document.createElement('a')
  link.href = snapshot.value
  link.download = `snapshot-${Date.now()}.jpg`
  link.click()
}

const goBack = () => {
  stopCamera()
  router.back()
}
</script>

<style scoped>
.camera-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.camera-container {
  position: relative;
  width: 100%;
  max-width: 800px;
  margin: 20px 0;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
}

.video-stream {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-camera {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #333;
  color: #fff;
  font-size: 16px;
}

.controls {
  margin: 20px 0;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.snapshot-container {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.snapshot-image {
  max-width: 100%;
  max-height: 400px;
  margin: 10px 0;
  border-radius: 4px;
}
</style>
