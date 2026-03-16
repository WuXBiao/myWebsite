<template>
  <div>
    <h2>Device Management</h2>
    <el-button type="primary" @click="dialogVisible = true">Add Device</el-button>
    
    <el-table :data="devices" style="width: 100%; margin-top: 20px;">
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
          <el-popconfirm title="Are you sure to delete this?" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger">Delete</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Add Device">
      <el-form :model="form">
        <el-form-item label="Name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Device ID">
          <el-input v-model="form.device_id" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleAdd">Confirm</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const devices = ref([])
const dialogVisible = ref(false)
const form = ref({
  name: '',
  device_id: ''
})

const fetchDevices = async () => {
  const res: any = await request.get('/devices')
  devices.value = res
}

const handleAdd = async () => {
  try {
    await request.post('/devices', form.value)
    ElMessage.success('Device added')
    dialogVisible.value = false
    fetchDevices()
  } catch (error) {
    // handled
  }
}

const handleDelete = async (id: number) => {
  try {
    await request.delete(`/devices/${id}`)
    ElMessage.success('Device deleted')
    fetchDevices()
  } catch (error) {
    // handled
  }
}

onMounted(() => {
  fetchDevices()
})
</script>
