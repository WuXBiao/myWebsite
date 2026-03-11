import axios from 'axios';

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端 API 基础地址
  timeout: 30000, // 请求超时时间
});

// 获取当前工号
export const getCurrentEmployeeId = () => {
  return localStorage.getItem('employeeId') || '';
};

// 设置当前工号
export const setCurrentEmployeeId = (employeeId) => {
  localStorage.setItem('employeeId', employeeId);
};

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 添加工号到请求头
    const employeeId = getCurrentEmployeeId();
    if (employeeId) {
      config.headers['X-Employee-Id'] = employeeId;
    }
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
  },

  // 下载录音
  getDownloadUrl(id) {
    return `http://localhost:8080/api/recordings/download?id=${id}`;
  },

  // 下载录音（blob 方式，带请求头）
  async downloadRecording(id, defaultFileName = 'recording.mp3') {
    try {
      const response = await apiClient.get('/recordings/download', {
        params: { id },
        responseType: 'blob'
      });
      
      // 从响应头获取文件名
      let fileName = defaultFileName;
      const contentDisposition = response.headers['content-disposition'];
      if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
        if (fileNameMatch && fileNameMatch[1]) {
          fileName = decodeURIComponent(fileNameMatch[1]);
        }
      }
      
      // 创建 Blob URL 并下载
      const blob = new Blob([response.data], { type: response.headers['content-type'] });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      
      // 清理
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      return true;
    } catch (error) {
      console.error('下载录音失败:', error);
      throw error;
    }
  }
};

export default apiClient;