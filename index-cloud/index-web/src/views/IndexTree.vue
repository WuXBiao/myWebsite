<template>
  <div class="index-tree-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <el-select v-model="currentPeriod" placeholder="选择期间" @change="loadData" style="width: 150px">
        <el-option v-for="p in periods" :key="p" :label="formatPeriod(p)" :value="p" />
      </el-select>
      <el-button type="primary" @click="handleRefresh">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
      <el-button @click="toggleExpandAll">
        {{ isAllExpanded ? '全部收起' : '全部展开' }}
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon> 导出 Excel
      </el-button>
    </div>

    <!-- 树形表格 -->
    <el-table
      ref="tableRef"
      :data="treeData"
      row-key="id"
      border
      stripe
      :default-expand-all="false"
      :tree-props="{ children: 'children' }"
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="indexName" label="指标名称" min-width="200" fixed="left">
        <template #default="{ row }">
          <span :style="{ paddingLeft: (row.level - 1) * 10 + 'px', fontWeight: row.level === 1 ? 'bold' : 'normal' }">
            {{ row.indexName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="indexCode" label="指标编号" width="120" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="balance" label="余额(万元)" width="130" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.balance) }}
        </template>
      </el-table-column>
      <el-table-column prop="momIncrement" label="较上月增量" width="120" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.momIncrement)">{{ formatNumber(row.momIncrement) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="ytdIncrement" label="较年初增量" width="120" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.ytdIncrement)">{{ formatNumber(row.ytdIncrement) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="ytdGrowthRate" label="较年初增速(%)" width="130" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.ytdGrowthRate)">{{ formatPercent(row.ytdGrowthRate) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="depositRatio" label="占存款比重(%)" width="130" align="right">
        <template #default="{ row }">
          {{ formatPercent(row.depositRatio) }}
        </template>
      </el-table-column>
      <el-table-column prop="ratioVsYtd" label="占比较年初(%)" width="130" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.ratioVsYtd)">{{ formatPercent(row.ratioVsYtd) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="yearlyAvg" label="年日均(万元)" width="130" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.yearlyAvg) }}
        </template>
      </el-table-column>
      <el-table-column prop="interestIncome" label="利息收入(万元)" width="140" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.interestIncome) }}
        </template>
      </el-table-column>
      <el-table-column prop="interestExpense" label="利息支出(万元)" width="140" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.interestExpense) }}
        </template>
      </el-table-column>
      <el-table-column prop="interestRate" label="付息率(%)" width="110" align="right">
        <template #default="{ row }">
          {{ formatPercent(row.interestRate) }}
        </template>
      </el-table-column>
      <el-table-column prop="rateVsMom" label="付息率较上月" width="130" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.rateVsMom, true)">{{ formatPercent(row.rateVsMom) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="rateVsYoy" label="付息率较上年" width="130" align="right">
        <template #default="{ row }">
          <span :class="getValueClass(row.rateVsYoy, true)">{{ formatPercent(row.rateVsYoy) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import indexApi from '../api/index'

const tableRef = ref(null)
const loading = ref(false)
const treeData = ref([])
const periods = ref([])
const currentPeriod = ref('')
const isAllExpanded = ref(false)

// 加载期间列表
const loadPeriods = async () => {
  try {
    const data = await indexApi.getPeriods()
    periods.value = data || []
    if (periods.value.length > 0) {
      currentPeriod.value = periods.value[0]
      loadData()
    }
  } catch (error) {
    // 如果没有数据，使用当前月份
    const now = new Date()
    currentPeriod.value = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}`
    periods.value = [currentPeriod.value]
  }
}

// 加载指标数据
const loadData = async () => {
  if (!currentPeriod.value) return
  loading.value = true
  try {
    const data = await indexApi.getIndexTree(currentPeriod.value)
    treeData.value = data || []
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
    treeData.value = []
  } finally {
    loading.value = false
  }
}

// 刷新数据
const handleRefresh = () => {
  loadData()
}

// 全部展开/收起
const toggleExpandAll = () => {
  isAllExpanded.value = !isAllExpanded.value
  toggleExpand(treeData.value, isAllExpanded.value)
}

const toggleExpand = (data, expanded) => {
  data.forEach(item => {
    tableRef.value?.toggleRowExpansion(item, expanded)
    if (item.children && item.children.length > 0) {
      toggleExpand(item.children, expanded)
    }
  })
}

// 格式化期间显示
const formatPeriod = (period) => {
  if (!period || period.length !== 6) return period
  return `${period.substring(0, 4)}年${period.substring(4)}月`
}

// 格式化数字
const formatNumber = (value) => {
  if (value === null || value === undefined) return '-'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 格式化百分比
const formatPercent = (value) => {
  if (value === null || value === undefined) return '-'
  return Number(value).toFixed(2)
}

// 获取数值样式类
const getValueClass = (value, inverse = false) => {
  if (value === null || value === undefined) return ''
  const num = Number(value)
  if (inverse) {
    return num > 0 ? 'negative-value' : num < 0 ? 'positive-value' : ''
  }
  return num > 0 ? 'positive-value' : num < 0 ? 'negative-value' : ''
}

// 导出 Excel
const handleExport = () => {
  if (!currentPeriod.value) {
    ElMessage.warning('请先选择期间')
    return
  }
  const url = indexApi.exportExcel(currentPeriod.value)
  window.open(url, '_blank')
}

onMounted(() => {
  loadPeriods()
})
</script>

<style scoped>
.index-tree-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.positive-value {
  color: #67c23a;
}

.negative-value {
  color: #f56c6c;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
}
</style>
