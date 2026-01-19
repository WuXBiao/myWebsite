<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>录音管理平台</h1>
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


export default {
  name: 'App'
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
  text-align: left;
  line-height: 60px;
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
