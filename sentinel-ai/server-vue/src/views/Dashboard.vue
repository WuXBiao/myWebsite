<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>Devices</template>
          <div class="stat-value">{{ devices.length }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Online</template>
          <div class="stat-value">{{ onlineDevices }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Alerts (Today)</template>
          <div class="stat-value">0</div>
        </el-card>
      </el-col>
    </el-row>

    <h3 style="margin-top: 20px;">Device List</h3>
    <el-table :data="devices" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="Name" width="120" />
      <el-table-column prop="device_id" label="Device ID" width="150" />
      <el-table-column prop="status" label="Status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'online' ? 'success' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="last_seen" label="Last Seen" />
      <el-table-column label="Action" width="100">
        <template #default="scope">
          <el-button 
            size="small" 
            type="primary"
            @click="toggleCamera(scope.row)"
          >
            {{ activeDevices[scope.row.id] ? 'Hide' : 'View' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog 
      v-model="previewDialogVisible" 
      :title="`${currentPreviewDevice?.name} - Camera Preview`" 
      width="40%"
      destroy-on-close
      @close="stopPreviewCamera"
    >
      <div class="preview-dialog-content">
        <div class="camera-preview-dialog">
          <video 
            ref="previewVideoElement" 
            autoplay 
            playsinline 
            muted
            class="preview-video-dialog"
          ></video>
          <div v-if="!previewActive" class="no-camera-dialog">
            <span>{{ previewError || 'Loading...' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">Close</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import request from '../utils/request'

const devices = ref<any[]>([])
const videoRefs = ref<Record<number, HTMLVideoElement>>({})
const activeDevices = ref<Record<number, boolean>>({})
const loadingDevices = ref<Record<number, boolean>>({})
const deviceErrors = ref<Record<number, string>>({})
const deviceStreams = ref<Record<number, MediaStream>>({})
const previewDialogVisible = ref(false)
const currentPreviewDevice = ref<any>(null)
const previewVideoElement = ref<HTMLVideoElement | null>(null)
const previewActive = ref(false)
const previewError = ref('')
let previewStream: MediaStream | null = null

const onlineDevices = computed(() => {
  return devices.value.filter(d => d.status === 'online').length
})

const fetchDevices = async () => {
  try {
    const res: any = await request.get('/devices')
    devices.value = res
  } catch (error) {
    console.error(error)
  }
}

const toggleCamera = async (device: any) => {
  currentPreviewDevice.value = device
  previewActive.value = false
  previewError.value = ''
  previewDialogVisible.value = true
  
  // Auto-start camera when dialog opens
  await startPreviewCamera()
}

const startPreviewCamera = async () => {
  if (!currentPreviewDevice.value) return
  
  try {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      throw new Error('Browser does not support camera')
    }

    previewStream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 640 },
        height: { ideal: 480 }
      },
      audio: false
    })

    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 50))
    
    if (previewVideoElement.value) {
      previewVideoElement.value.srcObject = previewStream
      previewActive.value = true
      console.log('Preview camera started')
    } else {
      throw new Error('Video element not available')
    }
  } catch (error: any) {
    let errorMsg = 'Failed to access camera'
    
    if (error.name === 'NotAllowedError') {
      errorMsg = 'Camera access denied'
    } else if (error.name === 'NotFoundError') {
      errorMsg = 'No camera found'
    } else if (error.name === 'NotReadableError') {
      errorMsg = 'Camera is in use'
    } else if (error.message) {
      errorMsg = error.message
    }
    
    previewError.value = errorMsg
    console.error('Preview camera error:', error)
  }
}

const stopPreviewCamera = () => {
  if (previewStream) {
    previewStream.getTracks().forEach(track => track.stop())
    previewStream = null
  }
  previewActive.value = false
}

const startDeviceCamera = async (device: any) => {
  const deviceId = device.id
  loadingDevices.value[deviceId] = true
  deviceErrors.value[deviceId] = ''
  
  try {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      throw new Error('Browser does not support camera')
    }

    const stream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 640 },
        height: { ideal: 480 }
      },
      audio: false
    })

    deviceStreams.value[deviceId] = stream
    
    // First set activeDevices to true to render the video element
    activeDevices.value[deviceId] = true
    
    // Wait for Vue to render the video element using nextTick
    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 50))
    
    const videoElement = videoRefs.value[deviceId]
    if (videoElement) {
      videoElement.srcObject = stream
      console.log('Camera started for device:', deviceId)
    } else {
      console.warn('Video element not found for device:', deviceId)
      deviceErrors.value[deviceId] = 'Video element not available'
    }
  } catch (error: any) {
    let errorMsg = 'Failed to access camera'
    
    if (error.name === 'NotAllowedError') {
      errorMsg = 'Camera access denied'
    } else if (error.name === 'NotFoundError') {
      errorMsg = 'No camera found'
    } else if (error.name === 'NotReadableError') {
      errorMsg = 'Camera is in use'
    } else if (error.message) {
      errorMsg = error.message
    }
    
    deviceErrors.value[deviceId] = errorMsg
    activeDevices.value[deviceId] = false
    console.error('Camera error:', error)
  } finally {
    loadingDevices.value[deviceId] = false
  }
}

const stopDeviceCamera = (deviceId: number) => {
  const stream = deviceStreams.value[deviceId]
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    delete deviceStreams.value[deviceId]
  }
  activeDevices.value[deviceId] = false
}

onMounted(() => {
  fetchDevices()
})
</script>

<style scoped>
.stat-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}

.preview-dialog-content {
  display: flex;
  justify-content: center;
}

.camera-preview-dialog {
  position: relative;
  width: 100%;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
}

.preview-video-dialog {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-camera-dialog {
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
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
