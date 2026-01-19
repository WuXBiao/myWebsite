<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>录音管理平台</h1>
          <div class="employee-selector">
            <span class="label">当前工号：</span>
            <el-select 
              v-model="currentEmployeeId" 
              placeholder="选择工号" 
              size="small"
              @change="onEmployeeChange"
              style="width: 180px;"
            >
              <el-option
                v-for="emp in employees"
                :key="emp.id"
                :label="`${emp.id} (${emp.role})`"
                :value="emp.id"
              />
            </el-select>
            <el-input
              v-model="customEmployeeId"
              placeholder="或输入工号"
              size="small"
              style="width: 120px; margin-left: 10px;"
              @keyup.enter="setCustomEmployee"
            />
            <el-button size="small" type="primary" @click="setCustomEmployee" style="margin-left: 5px;">确定</el-button>
          </div>
        </div>
      </el-header>
      <el-menu
        :default-active="$route.path"
        mode="horizontal"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/recordings">录音列表</el-menu-item>
        <el-menu-item index="/upload">上传录音</el-menu-item>
      </el-menu>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { getCurrentEmployeeId, setCurrentEmployeeId } from '@/api/recordingApi';

export default {
  name: 'App',
  data() {
    return {
      currentEmployeeId: '',
      customEmployeeId: '',
      // 预设的测试工号
      employees: [
        { id: 'admin001', role: '管理员' },
        { id: 'uploader001', role: '上传者' },
        { id: 'user001', role: '用户' },
        { id: 'multi001', role: '多角色' }
      ]
    };
  },
  mounted() {
    // 加载保存的工号
    this.currentEmployeeId = getCurrentEmployeeId();
  },
  methods: {
    onEmployeeChange(value) {
      setCurrentEmployeeId(value);
      this.$message.success(`已切换到工号: ${value}`);
    },
    setCustomEmployee() {
      if (this.customEmployeeId) {
        this.currentEmployeeId = this.customEmployeeId;
        setCurrentEmployeeId(this.customEmployeeId);
        this.$message.success(`已设置工号: ${this.customEmployeeId}`);
        this.customEmployeeId = '';
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

.el-header {
  background-color: #545c64;
  color: #fff;
  padding: 0 20px;
  height: auto !important;
  min-height: 60px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
}

.employee-selector {
  display: flex;
  align-items: center;
}

.employee-selector .label {
  margin-right: 10px;
  font-size: 14px;
}

.el-menu {
  border-bottom: none;
}

.el-main {
  background-color: #f0f2f5;
  color: #333;
  flex: 1;
  padding: 20px;
}
</style>
