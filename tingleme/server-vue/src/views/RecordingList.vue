<template>
  <div class="recording-list">
    <h2>录音列表</h2>
    
    <!-- 筛选表单 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filters" class="demo-form-inline">
        <el-form-item label="标题">
          <el-input v-model="filters.title" placeholder="按标题搜索"></el-input>
        </el-form-item>
        <el-form-item label="上传者">
          <el-input v-model="filters.uploader" placeholder="按上传者搜索"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRecordings">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 录音表格 -->
    <el-table
      :data="recordings"
      stripe
      style="width: 100%"
      @row-dblclick="handleRowDoubleClick"
    >
      <el-table-column prop="title" label="标题" width="200"></el-table-column>
      <el-table-column prop="description" label="描述" width="250"></el-table-column>
      <el-table-column prop="fileName" label="文件名" width="200"></el-table-column>
      <el-table-column prop="duration" label="时长" width="100"></el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="120">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="uploader" label="上传者" width="120"></el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.uploadTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="playRecording(row)">播放</el-button>
          <el-button size="small" type="danger" @click="deleteRecording(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.currentPage"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.totalItems"
      style="margin-top: 20px; text-align: center;"
    >
    </el-pagination>

    <!-- 音频播放器 -->
    <audio ref="audioPlayer" controls style="display: none;"></audio>
  </div>
</template>

<script>
import { recordingApi } from '@/api/recordingApi';

export default {
  name: 'RecordingList',
  data() {
    return {
      recordings: [],
      filters: {
        title: '',
        uploader: ''
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        totalItems: 0
      }
    }
  },
  mounted() {
    this.fetchRecordings();
  },
  methods: {
    async fetchRecordings() {
      try {
        const params = {
          page: this.pagination.currentPage - 1,
          size: this.pagination.pageSize,
          title: this.filters.title || undefined,
          uploader: this.filters.uploader || undefined
        };
        
        const response = await recordingApi.getRecordings(params);
        this.recordings = response.data.data;
        this.pagination.totalItems = response.data.totalItems;
        this.pagination.currentPage = response.data.currentPage + 1;
      } catch (error) {
        console.error('获取录音列表失败:', error);
        this.$message.error('获取录音列表失败');
      }
    },
    async searchRecordings() {
      this.pagination.currentPage = 1;
      await this.fetchRecordings();
    },
    async resetFilters() {
      this.filters.title = '';
      this.filters.uploader = '';
      this.pagination.currentPage = 1;
      await this.fetchRecordings();
    },
    handleSizeChange(newSize) {
      this.pagination.pageSize = newSize;
      this.fetchRecordings();
    },
    handleCurrentChange(newPage) {
      this.pagination.currentPage = newPage;
      this.fetchRecordings();
    },
    async playRecording(recording) {
      try {
        const audioUrl = `http://localhost:8080/api/recordings/play/${recording.id}?t=${new Date().getTime()}`;
        this.$refs.audioPlayer.src = audioUrl;
        this.$refs.audioPlayer.load(); // 加载音频
        await this.$refs.audioPlayer.play();
        this.$message.success('开始播放');
      } catch (error) {
        console.error('播放录音失败:', error);
        this.$message.error('播放失败: ' + error.message);
      }
    },
    async deleteRecording(id) {
      try {
        await recordingApi.deleteRecording(id);
        this.$message.success('删除成功');
        await this.fetchRecordings();
      } catch (error) {
        console.error('删除录音失败:', error);
        this.$message.error('删除失败');
      }
    },
    handleRowDoubleClick(row) {
      this.playRecording(row);
    },
    formatFileSize(size) {
      if (size === undefined || size === null) return '未知';
      
      const units = ['B', 'KB', 'MB', 'GB'];
      let index = 0;
      let fileSize = size;
      
      while (fileSize >= 1024 && index < units.length - 1) {
        fileSize /= 1024;
        index++;
      }
      
      return `${fileSize.toFixed(2)} ${units[index]}`;
    },
    formatDate(dateString) {
      if (!dateString) return '未知';
      
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
    }
  }
}
</script>

<style scoped>
.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
}
</style>