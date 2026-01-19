<template>
  <div class="upload-recording">
    <h2>上传录音</h2>
    <el-card class="upload-container">
      <el-form :model="form" :rules="rules" ref="uploadForm" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入录音标题"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入录音描述">
          </el-input>
        </el-form-item>
        <el-form-item label="上传者" prop="uploader">
          <el-input v-model="form.uploader" placeholder="请输入上传者姓名"></el-input>
        </el-form-item>
        <el-form-item label="录音文件" prop="file">
          <el-upload
            class="upload-demo"
            drag
            :auto-upload="false"
            :show-file-list="true"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            accept="audio/*"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">只能上传音频文件，且不超过100MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitUpload">立即上传</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 上传进度 -->
    <el-dialog v-model="dialogVisible" title="上传进度" width="50%">
      <el-progress :percentage="uploadProgress" :status="progressStatus"></el-progress>
      <p>{{ uploadMessage }}</p>
    </el-dialog>
  </div>
</template>

<script>
import { recordingApi } from '@/api/recordingApi';
import { UploadFilled } from '@element-plus/icons-vue';

export default {
  name: 'UploadRecording',
  components: {
    UploadFilled
  },
  data() {
    return {
      form: {
        title: '',
        description: '',
        uploader: '',
        file: null
      },
      rules: {
        title: [
          { required: true, message: '请输入录音标题', trigger: 'blur' }
        ],
        file: [
          { required: true, message: '请选择录音文件', trigger: 'change' }
        ]
      },
      dialogVisible: false,
      uploadProgress: 0,
      progressStatus: '',
      uploadMessage: ''
    }
  },
  methods: {
    handleFileChange(file) {
      this.form.file = file.raw;
    },
    beforeUpload(file) {
      const isAudio = file.type.startsWith('audio/');
      const isLt100M = file.size / 1024 / 1024 < 100;

      if (!isAudio) {
        this.$message.error('只能上传音频文件!');
      }
      if (!isLt100M) {
        this.$message.error('文件大小不能超过100MB!');
      }
      return isAudio && isLt100M;
    },
    async submitUpload() {
      // 验证表单
      if (!this.form.file) {
        this.$message.error('请选择录音文件');
        return;
      }
      if (!this.form.title) {
        this.$message.error('请输入录音标题');
        return;
      }

      const formData = new FormData();
      formData.append('file', this.form.file);
      formData.append('title', this.form.title);
      formData.append('description', this.form.description || '');
      formData.append('uploader', this.form.uploader || '');

      this.dialogVisible = true;
      this.uploadProgress = 0;
      this.progressStatus = '';
      this.uploadMessage = '正在上传...';

      try {
        await recordingApi.uploadRecording(formData, {
          onUploadProgress: (progressEvent) => {
            const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            this.uploadProgress = progress;
          }
        });

        this.uploadProgress = 100;
        this.progressStatus = 'success';
        this.uploadMessage = '上传成功！';

        setTimeout(() => {
          this.dialogVisible = false;
          this.resetForm();
          this.$message.success('录音上传成功！');
        }, 1000);
      } catch (error) {
        console.error('上传失败:', error);
        this.progressStatus = 'exception';
        this.uploadMessage = '上传失败：' + (error.response?.data?.message || error.message);
        this.$message.error('录音上传失败：' + (error.response?.data?.message || error.message));
      }
    },
    resetForm() {
      this.form = {
        title: '',
        description: '',
        uploader: '',
        file: null
      };
      this.uploadProgress = 0;
      this.progressStatus = '';
      this.uploadMessage = '';
    }
  }
}
</script>

<style scoped>
.upload-recording {
  padding: 20px;
}

.upload-container {
  max-width: 800px;
  margin: 0 auto;
}

.upload-demo {
  width: 100%;
}
</style>