import axios from 'axios';

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端 API 基础地址
  timeout: 30000, // 请求超时时间
});

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 可以在这里添加认证 token
    // config.headers.Authorization = `Bearer ${getToken()}`;
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    // 处理响应错误
    if (error.response) {
      // 服务器返回错误状态码
      console.error('API Error:', error.response.status, error.response.data);
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('Network Error:', error.request);
    } else {
      // 其他错误
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

// 录音相关 API
export const recordingApi = {
  // 获取录音列表（带分页和筛选）
  getRecordings(params) {
    return apiClient.get('/recordings', { params });
  },

  // 根据 ID 获取录音详情
  getRecordingById(id) {
    return apiClient.get(`/recordings/${id}`);
  },

  // 上传录音
  uploadRecording(formData, options = {}) {
    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    };
    
    if (options.onUploadProgress) {
      config.onUploadProgress = options.onUploadProgress;
    }
    
    return apiClient.post('/recordings/upload', formData, config);
  },

  // 删除录音
  deleteRecording(id) {
    return apiClient.delete(`/recordings/${id}`);
  },

  // 播放录音
  playRecording(id) {
    return apiClient.get(`/recordings/play/${id}`, {
      responseType: 'blob' // 用于处理音频流
    });
  }
};

export default apiClient;