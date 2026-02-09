<template>
  <div>
    <div
      v-if="chatOpen"
      class="fixed right-[180px] bottom-24 z-50 w-[320px] max-w-[calc(100vw-2rem)] bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg shadow-lg overflow-hidden"
    >
      <div class="px-3 py-2 border-b border-gray-200 dark:border-gray-700 space-y-2">
        <div class="flex items-center justify-between">
          <div class="text-sm font-medium text-gray-900 dark:text-gray-100">看板娘 AI</div>
          <button
            type="button"
            class="text-sm text-gray-600 dark:text-gray-300 hover:underline"
            @click="chatOpen = false"
          >
            关闭
          </button>
        </div>
        <select
          v-model="selectedModel"
          class="w-full px-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
        >
          <option v-for="m in models" :key="m.value" :value="m.value">
            {{ m.label }}
          </option>
        </select>
      </div>

      <div class="p-3 space-y-2 max-h-[280px] overflow-auto">
        <div v-if="messages.length === 0" class="text-sm text-gray-500 dark:text-gray-400">
          请输入一句话开始对话。
        </div>

        <div v-for="(m, idx) in messages" :key="idx" class="text-sm">
          <div
            v-if="m.role === 'user'"
            class="ml-auto w-fit max-w-[90%] px-3 py-2 rounded-lg bg-blue-600 text-white"
          >
            {{ m.content }}
          </div>
          <div
            v-else
            class="mr-auto w-fit max-w-[90%] px-3 py-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-gray-100"
          >
            {{ m.content }}
          </div>
        </div>
      </div>

      <form class="p-3 border-t border-gray-200 dark:border-gray-700" @submit.prevent="send">
        <div class="flex items-center gap-2">
          <input
            v-model="input"
            type="text"
            class="flex-1 px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
            placeholder="说点什么..."
            :disabled="loading"
          />
          <button
            type="submit"
            class="px-3 py-2 rounded-lg bg-primary text-white hover:bg-blue-600 disabled:opacity-60"
            :disabled="loading"
          >
            {{ loading ? '发送中' : '发送' }}
          </button>
        </div>
        <div v-if="error" class="mt-2 text-xs text-red-600">{{ error }}</div>
      </form>
    </div>

    <div
      v-if="enabled"
      id="live2d-click-overlay"
      class="fixed"
      style="right: 0; bottom: 0; width: 200px; height: 350px; cursor: pointer; z-index: 40;"
      @click="playMeow"
    />

    <div class="fixed right-4 bottom-4 z-50 flex items-center gap-2">
      <button
        type="button"
        class="px-3 py-2 rounded-lg bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 text-gray-800 dark:text-gray-200 shadow-md hover:shadow-lg"
        @click="chatOpen = !chatOpen"
      >
        {{ chatOpen ? '收起 AI' : 'AI 对话' }}
      </button>

      <button
        type="button"
        class="px-3 py-2 rounded-lg bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 text-gray-800 dark:text-gray-200 shadow-md hover:shadow-lg"
        @click="toggle"
      >
        {{ enabled ? '隐藏看板娘' : '显示看板娘' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';

const enabled = ref(true);
const chatOpen = ref(false);
const input = ref('');
const loading = ref(false);
const error = ref('');
const messages = ref([]);
const selectedModel = ref('Qwen/Qwen2.5-7B-Instruct');
const models = [
  { value: 'Qwen/Qwen2.5-7B-Instruct', label: '通用模型（推荐）' },
  { value: 'Qwen/Qwen3-8B', label: 'Qwen3-8B（更强）' },
  { value: 'deepseek-ai/DeepSeek-R1-Distill-Qwen-7B', label: 'DeepSeek-R1（推理强）' },
  { value: 'deepseek-ai/DeepSeek-R1-0528-Qwen3-8B', label: 'DeepSeek-R1-Qwen3（推理最强）' },
  { value: 'Qwen/Qwen2.5-Coder-7B-Instruct', label: '代码能力强' },
  { value: 'THUDM/GLM-4-9B-0414', label: 'GLM-4（多模态）' },
  { value: 'THUDM/GLM-Z1-9B-0414', label: 'GLM-Z1（思维链）' },
  { value: 'tencent/Hunyuan-MT-7B', label: '腾讯混元（翻译强）' },
  { value: 'internlm/internlm2_5-7b-chat', label: 'InternLM2.5' },
  { value: 'Qwen/Qwen2-7B-Instruct', label: 'Qwen2-7B' }
];

const ensureScript = (src) => {
  return new Promise((resolve, reject) => {
    const existing = document.querySelector(`script[src="${src}"]`);
    if (existing) {
      resolve();
      return;
    }

    const s = document.createElement('script');
    s.src = src;
    s.async = true;
    s.onload = () => resolve();
    s.onerror = (e) => reject(e);
    document.head.appendChild(s);
  });
};

const send = async () => {
  error.value = '';
  const text = input.value.trim();
  if (!text) return;

  messages.value.push({ role: 'user', content: text });
  input.value = '';
  loading.value = true;

  try {
    const resp = await axios.post('/api/ai/chat', {
      message: text,
      model: selectedModel.value
    });
    const reply = resp?.data?.reply;
    if (!reply) {
      throw new Error('empty reply');
    }
    messages.value.push({ role: 'assistant', content: reply });
  } catch (e) {
    const resp = e?.response;
    const data = resp?.data;
    if (data && typeof data === 'object') {
      const msg = data.error || '请求失败';
      const detail = data.detail ? `\n${data.detail}` : '';
      const upstream = data.upstream_status ? `\nupstream_status=${data.upstream_status}` : '';
      error.value = `${msg}${detail}${upstream}`;
    } else {
      error.value = '请求失败：请确认 server-go 已启动且已配置 AI_API_KEY';
    }
  } finally {
    loading.value = false;
  }
};

let audioContext = null;

const getAudioContext = () => {
  if (!audioContext) {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;
    if (AudioContextClass) {
      audioContext = new AudioContextClass();
    }
  }
  return audioContext;
};

const playMeow = () => {
  const ctx = getAudioContext();
  if (!ctx) return;
  
  if (ctx.state === 'suspended') {
    ctx.resume();
  }
  
  try {
    const now = ctx.currentTime;
    
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    const filter = ctx.createBiquadFilter();
    
    osc.connect(filter);
    filter.connect(gain);
    gain.connect(ctx.destination);
    
    filter.type = 'lowpass';
    filter.frequency.setValueAtTime(2000, now);
    
    osc.type = 'sine';
    osc.frequency.setValueAtTime(500, now);
    osc.frequency.exponentialRampToValueAtTime(350, now + 0.4);
    osc.frequency.exponentialRampToValueAtTime(280, now + 1);
    
    gain.gain.setValueAtTime(0.25, now);
    gain.gain.exponentialRampToValueAtTime(0.15, now + 0.4);
    gain.gain.exponentialRampToValueAtTime(0.05, now + 1);
    
    osc.start(now);
    osc.stop(now + 1);
  } catch (e) {
    console.error('音频播放出错:', e);
  }
  
  if (window.L2Dwidget && window.L2Dwidget.chatAPI && typeof window.L2Dwidget.chatAPI.addMessage === 'function') {
    window.L2Dwidget.chatAPI.addMessage('喵～');
  } else if (window.L2Dwidget && window.L2Dwidget.message) {
    window.L2Dwidget.message.show('喵～');
  }
};

const initWidget = () => {
  if (!enabled.value) return;
  if (!window.L2Dwidget || typeof window.L2Dwidget.init !== 'function') return;

  window.L2Dwidget.init({
    model: {
      jsonPath: 'https://unpkg.com/live2d-widget-model-hijiki@1.0.5/assets/hijiki.model.json'
    },
    display: {
      position: 'right',
      width: 150,
      height: 300,
      hOffset: 0,
      vOffset: -20
    },
    mobile: {
      show: true,
      scale: 0.8
    },
    react: {
      opacityDefault: 0.9,
      opacityOnHover: 0.2
    }
  });
  
  const bindClickEvent = (retries = 0) => {
    if (retries > 20) {
      console.error('❌ 无法找到 Live2D canvas 元素');
      return;
    }
    
    let canvas = document.getElementById('live2dcanvas');
    
    if (!canvas) {
      const allCanvas = document.querySelectorAll('canvas');
      if (allCanvas.length > 0) {
        canvas = allCanvas[allCanvas.length - 1];
      }
    }
    
    if (canvas && canvas.offsetParent !== null) {
      canvas.addEventListener('click', playMeow);
      canvas.addEventListener('touchend', playMeow);
      canvas.style.cursor = 'pointer';
      console.log('✅ 点击事件已绑定到看板娘');
    } else {
      setTimeout(() => bindClickEvent(retries + 1), 200);
    }
  };
  
  bindClickEvent();
};

const showCanvas = () => {
  const c = document.getElementById('live2dcanvas');
  if (c) c.style.display = '';
};

const hideCanvas = () => {
  const c = document.getElementById('live2dcanvas');
  if (c) c.style.display = 'none';
};

const toggle = () => {
  enabled.value = !enabled.value;
  if (enabled.value) {
    if (document.getElementById('live2dcanvas')) {
      showCanvas();
      return;
    }
    initWidget();
    return;
  }
  hideCanvas();
};

onMounted(async () => {
  try {
    await ensureScript('https://unpkg.com/live2d-widget@3.1.4/lib/L2Dwidget.min.js');
    initWidget();
  } catch (e) {
    enabled.value = false;
  }
});
</script>
