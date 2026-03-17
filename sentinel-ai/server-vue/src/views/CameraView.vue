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
      <canvas 
        ref="canvasElement" 
        class="canvas-overlay"
        :width="canvasWidth"
        :height="canvasHeight"
      ></canvas>
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
      <el-button 
        v-if="cameraActive"
        :type="aiEnabled ? 'success' : 'info'"
        @click="toggleAI"
      >
        {{ aiEnabled ? 'AI: ON' : 'AI: OFF' }}
      </el-button>
      <el-button @click="takeSnapshot">Take Snapshot</el-button>
      <el-button @click="goBack">Back</el-button>
    </div>

    <div v-if="snapshot" class="snapshot-container">
      <h3>Last Snapshot</h3>
      <img :src="snapshot" alt="Snapshot" class="snapshot-image" />
      <el-button @click="downloadSnapshot">Download</el-button>
    </div>

    <div v-if="aiResults" class="ai-results">
      <h3>AI Recognition Results</h3>
      <div class="results-content">
        <div v-if="aiResults.objects && aiResults.objects.length > 0" class="objects-section">
          <h4>Objects Detected:</h4>
          <div v-for="(obj, idx) in aiResults.objects" :key="`obj-${idx}`" class="result-item">
            <span class="label">{{ obj.class }}</span>
            <span class="confidence">{{ (obj.confidence * 100).toFixed(1) }}%</span>
          </div>
        </div>
        <div v-if="aiResults.gestures && aiResults.gestures.length > 0" class="gestures-section">
          <h4>Hand Gestures:</h4>
          <div v-for="(gesture, idx) in aiResults.gestures" :key="`gesture-${idx}`" class="result-item">
            <span class="label">{{ gesture.hand }}</span>
            <span class="confidence">{{ (gesture.confidence * 100).toFixed(1) }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()

const videoElement = ref<HTMLVideoElement | null>(null)
const canvasElement = ref<HTMLCanvasElement | null>(null)
const cameraActive = ref(false)
const loading = ref(false)
const cameraError = ref('')
const snapshot = ref('')
const deviceName = ref('Camera')
const aiEnabled = ref(false)
const aiResults = ref<any>(null)
const canvasWidth = ref(1280)
const canvasHeight = ref(720)
let stream: MediaStream | null = null
let aiIntervalId: number | null = null

onMounted(() => {
  deviceName.value = (route.query.name as string) || 'Camera'
})

onUnmounted(() => {
  stopCamera()
  stopAI()
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

const toggleAI = () => {
  if (aiEnabled.value) {
    stopAI()
  } else {
    startAI()
  }
}

const startAI = () => {
  if (!cameraActive.value || !videoElement.value) {
    ElMessage.warning('Camera is not active')
    return
  }

  aiEnabled.value = true
  ElMessage.success('AI recognition started')
  
  // Start analyzing frames every 500ms
  aiIntervalId = window.setInterval(() => {
    analyzeFrame()
  }, 500)
}

const stopAI = () => {
  if (aiIntervalId) {
    clearInterval(aiIntervalId)
    aiIntervalId = null
  }
  aiEnabled.value = false
  aiResults.value = null
  ElMessage.info('AI recognition stopped')
}

const analyzeFrame = async () => {
  if (!videoElement.value || !canvasElement.value) return

  try {
    const ctx = canvasElement.value.getContext('2d')
    if (!ctx) return

    // Set canvas size to match video
    canvasElement.value.width = videoElement.value.videoWidth
    canvasElement.value.height = videoElement.value.videoHeight
    canvasWidth.value = videoElement.value.videoWidth
    canvasHeight.value = videoElement.value.videoHeight

    // Create a temporary canvas for capturing the frame
    const tempCanvas = document.createElement('canvas')
    tempCanvas.width = videoElement.value.videoWidth
    tempCanvas.height = videoElement.value.videoHeight
    const tempCtx = tempCanvas.getContext('2d')
    if (!tempCtx) return

    tempCtx.drawImage(videoElement.value, 0, 0)

    // Convert temp canvas to blob for AI analysis
    tempCanvas.toBlob(async (blob) => {
      if (!blob) return

      const formData = new FormData()
      formData.append('image', blob, 'frame.jpg')

      try {
        // Send to backend for AI analysis
        const response = await request.post('/ai/analyze', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        // Handle response - axios wraps data in response.data
        const data = response.data || response
        
        if (data && canvasElement.value && videoElement.value) {
          aiResults.value = data
          
          // Clear canvas and redraw with results
          ctx.clearRect(0, 0, canvasElement.value.width, canvasElement.value.height)
          ctx.drawImage(videoElement.value, 0, 0)
          
          // Draw recognition results on canvas
          drawResults(ctx, data)
        }
      } catch (error) {
        console.error('AI analysis error:', error)
      }
    }, 'image/jpeg', 0.8)
  } catch (error) {
    console.error('Frame analysis error:', error)
  }
}

const drawResults = (ctx: CanvasRenderingContext2D, results: any) => {
  const width = canvasElement.value?.width || 1280
  const height = canvasElement.value?.height || 720

  // Draw objects
  if (results.objects && results.objects.length > 0) {
    ctx.strokeStyle = '#00FF00'
    ctx.lineWidth = 2
    ctx.font = '16px Arial'
    ctx.fillStyle = '#00FF00'

    results.objects.forEach((obj: any) => {
      const [x1, y1, x2, y2] = obj.bbox
      const boxWidth = x2 - x1
      const boxHeight = y2 - y1

      ctx.strokeRect(x1, y1, boxWidth, boxHeight)
      ctx.fillText(`${obj.class} ${(obj.confidence * 100).toFixed(1)}%`, x1, y1 - 5)
    })
  }

  // Draw hand landmarks
  if (results.gestures && results.gestures.length > 0) {
    ctx.fillStyle = '#FF0000'
    ctx.strokeStyle = '#FF0000'
    ctx.lineWidth = 2

    results.gestures.forEach((gesture: any) => {
      if (!gesture.landmarks || gesture.landmarks.length === 0) {
        return
      }
      
      // Draw landmarks as circles
      gesture.landmarks.forEach((landmark: any, idx: number) => {
        if (!landmark || typeof landmark.x === 'undefined' || typeof landmark.y === 'undefined') {
          return
        }
        
        const x = landmark.x * width
        const y = landmark.y * height

        ctx.beginPath()
        ctx.arc(x, y, 3, 0, Math.PI * 2)
        ctx.fill()

        // Draw connections between landmarks
        if (idx > 0) {
          const prevLandmark = gesture.landmarks[idx - 1]
          if (prevLandmark && typeof prevLandmark.x !== 'undefined' && typeof prevLandmark.y !== 'undefined') {
            const prevX = prevLandmark.x * width
            const prevY = prevLandmark.y * height
            ctx.beginPath()
            ctx.moveTo(prevX, prevY)
            ctx.lineTo(x, y)
            ctx.stroke()
          }
        }
      })

      // Draw hand label
      if (gesture.landmarks.length > 0) {
        const firstLandmark = gesture.landmarks[0]
        if (firstLandmark && typeof firstLandmark.x !== 'undefined' && typeof firstLandmark.y !== 'undefined') {
          const labelX = firstLandmark.x * width
          const labelY = firstLandmark.y * height
          ctx.font = '16px Arial'
          ctx.fillStyle = '#FF0000'
          ctx.fillText(`${gesture.hand} ${gesture.gesture || ''}`, labelX, labelY - 10)
        }
      }
    })
  }
}

const goBack = () => {
  stopCamera()
  stopAI()
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
  position: absolute;
  top: 0;
  left: 0;
}

.canvas-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
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

.ai-results {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.ai-results h3 {
  margin-top: 0;
  color: #333;
}

.results-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.objects-section,
.gestures-section {
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.objects-section h4,
.gestures-section h4 {
  margin-top: 0;
  color: #409eff;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.result-item:last-child {
  border-bottom: none;
}

.result-item .label {
  font-weight: 500;
  color: #333;
}

.result-item .confidence {
  color: #909399;
  font-size: 12px;
}
</style>
