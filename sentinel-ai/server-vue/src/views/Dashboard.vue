<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>Devices</template>
          <div class="stat-value">{{ devices.length }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Online</template>
          <div class="stat-value">{{ onlineDevices }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Alerts (Today)</template>
          <div class="stat-value">0</div>
        </el-card>
      </el-col>
    </el-row>

    <h3 style="margin-top: 20px;">Device List</h3>
    <el-table :data="devices" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="device_id" label="Device ID" />
      <el-table-column prop="status" label="Status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'online' ? 'success' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="last_seen" label="Last Seen" />
      <el-table-column label="Action">
        <template #default="scope">
          <el-button size="small" @click="viewStream(scope.row)">View</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Live Stream" width="60%" destroy-on-close>
      <VideoPlayer v-if="currentDevice" :device-id="currentDevice.device_id" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '../utils/request'
import VideoPlayer from '../components/VideoPlayer.vue'

const devices = ref<any[]>([])
const dialogVisible = ref(false)
const currentDevice = ref<any>(null)

const onlineDevices = computed(() => {
  return devices.value.filter(d => d.status === 'online').length
})

const fetchDevices = async () => {
  try {
    const res: any = await request.get('/devices')
    devices.value = res
  } catch (error) {
    console.error(error)
  }
}

const viewStream = (device: any) => {
  currentDevice.value = device
  dialogVisible.value = true
}

onMounted(() => {
  fetchDevices()
})
</script>

<style scoped>
.stat-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}
</style>
