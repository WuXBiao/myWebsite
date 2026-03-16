<template>
  <div>
    <h2>Device Management</h2>
    <div style="margin-bottom: 20px;">
      <el-button type="primary" @click="handleScanUSB" :loading="scanning">
        {{ scanning ? 'Scanning USB...' : 'Scan USB Devices' }}
      </el-button>
      <el-button type="warning" @click="handleScanCamera" :loading="scanningCamera">
        {{ scanningCamera ? 'Scanning Camera...' : 'Scan Camera Devices' }}
      </el-button>
      <el-button type="info" @click="handleScan" :loading="scanningRegistered">
        {{ scanningRegistered ? 'Scanning...' : 'Scan Registered Devices' }}
      </el-button>
      <el-button type="success" @click="dialogVisible = true">Add Device Manually</el-button>
    </div>
    
    <el-table :data="devices" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="device_id" label="Device ID" />
      <el-table-column prop="ip" label="IP Address" />
      <el-table-column prop="status" label="Status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'online' ? 'success' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="last_seen" label="Last Seen" width="180" />
      <el-table-column label="Action" width="220">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleViewCamera(scope.row)">View</el-button>
          <el-popconfirm title="Are you sure to delete this?" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger">Delete</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Add Device Manually">
      <el-form :model="form">
        <el-form-item label="Name">
          <el-input v-model="form.name" placeholder="e.g., Living Room Camera" />
        </el-form-item>
        <el-form-item label="Device ID">
          <el-input v-model="form.device_id" placeholder="e.g., device_01, android_12345" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleAdd">Confirm</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="scanDialogVisible" :title="scanType === 'usb' ? 'USB Devices' : 'Scanned Devices'" width="700px">
      <el-empty v-if="scannedDevices.length === 0" description="No devices found" />
      <el-table v-else :data="scannedDevices" style="width: 100%;">
        <el-table-column prop="device_id" label="Device ID" min-width="150" />
        <el-table-column prop="name" label="Name" min-width="150" />
        <el-table-column v-if="scanType === 'usb'" prop="manufacturer" label="Manufacturer" min-width="120" />
        <el-table-column v-if="scanType === 'usb'" prop="model" label="Model" min-width="120" />
        <el-table-column v-if="scanType === 'registered'" prop="ip" label="IP Address" />
        <el-table-column v-if="scanType === 'registered'" prop="status" label="Status">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'online' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Action" width="120">
          <template #default="scope">
            <el-button 
              v-if="!scope.row.is_added" 
              size="small" 
              type="primary"
              @click="handleAddFromScan(scope.row)"
            >
              Add
            </el-button>
            <el-tag v-else type="success">Added</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scanDialogVisible = false">Close</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()

const devices = ref([])
const dialogVisible = ref(false)
const scanDialogVisible = ref(false)
const scanning = ref(false)
const scanningRegistered = ref(false)
const scanningCamera = ref(false)
const scannedDevices = ref([])
const scanType = ref('usb')
const form = ref({
  name: '',
  device_id: ''
})

const fetchDevices = async () => {
  try {
    const res: any = await request.get('/devices')
    devices.value = res
  } catch (error) {
    ElMessage.error('Failed to fetch devices')
  }
}

const handleScanUSB = async () => {
  scanning.value = true
  try {
    const res: any = await request.get('/devices/scan-usb')
    scannedDevices.value = res.devices || []
    scanType.value = 'usb'
    scanDialogVisible.value = true
    ElMessage.success(`Found ${scannedDevices.value.length} USB device(s)`)
  } catch (error) {
    ElMessage.error('Failed to scan USB devices')
  } finally {
    scanning.value = false
  }
}

const handleScanCamera = async () => {
  scanningCamera.value = true
  try {
    const res: any = await request.get('/devices/scan-camera')
    scannedDevices.value = res.devices || []
    scanType.value = 'camera'
    scanDialogVisible.value = true
    ElMessage.success(`Found ${scannedDevices.value.length} camera device(s)`)
  } catch (error) {
    ElMessage.error('Failed to scan camera devices')
  } finally {
    scanningCamera.value = false
  }
}

const handleScan = async () => {
  scanningRegistered.value = true
  try {
    const res: any = await request.get('/devices/scan')
    scannedDevices.value = res.devices || []
    scanType.value = 'registered'
    scanDialogVisible.value = true
    ElMessage.success(`Found ${scannedDevices.value.length} registered device(s)`)
  } catch (error) {
    ElMessage.error('Failed to scan devices')
  } finally {
    scanningRegistered.value = false
  }
}

const handleAdd = async () => {
  if (!form.value.device_id) {
    ElMessage.warning('Please enter Device ID')
    return
  }
  try {
    await request.post('/devices', form.value)
    ElMessage.success('Device added')
    dialogVisible.value = false
    form.value = { name: '', device_id: '' }
    fetchDevices()
  } catch (error) {
    ElMessage.error('Failed to add device')
  }
}

const handleAddFromScan = async (device: any) => {
  try {
    await request.post('/devices', {
      name: device.name || device.device_id,
      device_id: device.device_id
    })
    ElMessage.success('Device added from scan')
    device.is_added = true
    fetchDevices()
  } catch (error) {
    ElMessage.error('Failed to add device')
  }
}

const handleDelete = async (id: number) => {
  try {
    await request.delete(`/devices/${id}`)
    ElMessage.success('Device deleted')
    fetchDevices()
  } catch (error) {
    ElMessage.error('Failed to delete device')
  }
}

const handleViewCamera = (device: any) => {
  router.push({
    name: 'CameraView',
    query: {
      id: device.id,
      name: device.name,
      device_id: device.device_id
    }
  })
}

onMounted(() => {
  fetchDevices()
})
</script>
