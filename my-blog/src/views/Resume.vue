<template>
  <MainLayout>
    <div class="resume-page">
      <ResumeViewer />
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../components/layout/MainLayout.vue'
import ResumeViewer from '../components/ResumeViewer.vue'
</script>

<style scoped>
.resume-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 1rem 0;
}
</style>
