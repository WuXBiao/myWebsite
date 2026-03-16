<template>
  <div class="video-player">
    <div v-if="loading" class="loading">Connecting...</div>
    <img ref="videoImg" class="video-feed" alt="Live Stream" />
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps<{
  deviceId: string
}>()

const videoImg = ref<HTMLImageElement | null>(null)
const loading = ref(true)
const error = ref('')
let ws: WebSocket | null = null
let lastUrl: string | null = null

const connect = () => {
  loading.value = true
  error.value = ''
  
  // Use relative path for proxy to work, but WS needs absolute URL usually or protocol change
  // If we use `ws://` it won't use the proxy config in vite.config.ts (which is for http)
  // But we can use `window.location.host`
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = window.location.host // localhost:5173
  // Since we proxy /api -> localhost:8080, but WS is usually not proxied by Vite dev server easily unless configured?
  // Vite dev server does proxy WS if configured.
  // My vite config: proxy /api -> http://localhost:8080.
  // My backend WS route: /ws/watch/:id. It's not under /api.
  // I should probably move WS route under /api or configure proxy for /ws as well.
  
  // Let's configure proxy for /ws in vite.config.ts later.
  // For now, let's assume /ws is proxied.
  const url = `${protocol}//${host}/ws/watch/${props.deviceId}`
  
  ws = new WebSocket(url)
  ws.binaryType = 'blob'

  ws.onopen = () => {
    loading.value = false
    console.log('WS Connected')
  }

  ws.onmessage = (event) => {
    if (lastUrl) {
      URL.revokeObjectURL(lastUrl)
    }
    lastUrl = URL.createObjectURL(event.data)
    if (videoImg.value) {
      videoImg.value.src = lastUrl
    }
  }

  ws.onerror = (e) => {
    console.error('WS Error', e)
    error.value = 'Connection error'
    loading.value = false
  }

  ws.onclose = () => {
    console.log('WS Closed')
    // loading.value = false
  }
}

watch(() => props.deviceId, () => {
  if (ws) ws.close()
  connect()
})

onMounted(() => {
  connect()
})

onUnmounted(() => {
  if (ws) ws.close()
  if (lastUrl) URL.revokeObjectURL(lastUrl)
})
</script>

<style scoped>
.video-player {
  width: 100%;
  height: 100%;
  background: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}
.video-feed {
  max-width: 100%;
  max-height: 100%;
}
.loading, .error {
  position: absolute;
  color: white;
}
</style>
