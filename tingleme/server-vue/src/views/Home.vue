<template>
  <div class="home">
    <h1>欢迎使用录音管理平台</h1>
    <p>这是一个功能齐全的录音管理平台，您可以：</p>
    <ul>
      <li><router-link to="/recordings">浏览录音列表</router-link></li>
      <li><router-link to="/upload">上传新录音</router-link></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'HomeView'
}
</script>

<style scoped>
.home {
  padding: 20px;
  text-align: center;
}
</style>