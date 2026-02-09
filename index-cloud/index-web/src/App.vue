<template>
  <div class="app-container">
    <el-container>
      <el-header class="app-header">
        <div class="logo">
          <el-icon><DataAnalysis /></el-icon>
          <span>指标管理平台</span>
        </div>
        <el-menu
          :default-active="currentRoute"
          mode="horizontal"
          :ellipsis="false"
          class="nav-menu"
          router
        >
          <el-menu-item index="/tree">
            <el-icon><Grid /></el-icon>树形视图
          </el-menu-item>
          <el-menu-item index="/list">
            <el-icon><List /></el-icon>列表视图
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const currentRoute = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
}

.app-container {
  height: 100%;
}

.el-container {
  height: 100%;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.logo .el-icon {
  font-size: 28px;
}

.nav-menu {
  background: transparent;
  border: none;
  flex: 1;
  justify-content: flex-end;
}

.nav-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.8);
  border-bottom: none;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  color: white;
  background: rgba(255, 255, 255, 0.1);
  border-bottom: none;
}

.el-main {
  background-color: #f0f2f5;
  padding: 0;
}
</style>
