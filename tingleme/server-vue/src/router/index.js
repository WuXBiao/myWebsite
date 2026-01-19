import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import RecordingList from '../views/RecordingList.vue';
import UploadRecording from '../views/UploadRecording.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/recordings',
    name: 'RecordingList',
    component: RecordingList
  },
  {
    path: '/upload',
    name: 'UploadRecording',
    component: UploadRecording
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;