<template>
  <div class="index-list-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <el-select v-model="currentPeriod" placeholder="选择期间" @change="loadData" style="width: 150px">
        <el-option v-for="p in periods" :key="p" :label="formatPeriod(p)" :value="p" />
      </el-select>
      <el-select v-model="currentCategory" placeholder="全部分类" clearable @change="loadData" style="width: 120px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="handleRefresh">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon> 导出 Excel
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="indexName" label="指标名称" min-width="180" fixed="left" />
      <el-table-column prop="indexCode" label="指标编号" width="120" />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column prop="level" label="层级" width="70" align="center" />
      <el-table-column prop="balance" label="余额(万元)" width="130" align="right">
        <template #default="{ row }">{{ formatNumber(row.balance) }}</template>
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
        <template #default="{ row }">{{ formatPercent(row.depositRatio) }}</template>
      </el-table-column>
      <el-table-column prop="interestRate" label="付息率(%)" width="110" align="right">
        <template #default="{ row }">{{ formatPercent(row.interestRate) }}</template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import indexApi from '../api/index'

const loading = ref(false)
const tableData = ref([])
const periods = ref([])
const categories = ref([])
const currentPeriod = ref('')
const currentCategory = ref('')

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 加载期间和分类列表
const loadOptions = async () => {
  try {
    const [periodsData, categoriesData] = await Promise.all([
      indexApi.getPeriods(),
      indexApi.getCategories()
    ])
    periods.value = periodsData || []
    categories.value = categoriesData || []
    if (periods.value.length > 0) {
      currentPeriod.value = periods.value[0]
      loadData()
    }
  } catch (error) {
    const now = new Date()
    currentPeriod.value = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}`
    periods.value = [currentPeriod.value]
  }
}

// 加载分页数据
const loadData = async () => {
  if (!currentPeriod.value) return
  loading.value = true
  try {
    const data = await indexApi.getIndexPage(
      currentPeriod.value,
      currentCategory.value,
      pagination.page - 1,
      pagination.size
    )
    tableData.value = data.content || []
    pagination.total = data.totalElements || 0
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const handleRefresh = () => {
  loadData()
}

const handleSizeChange = () => {
  pagination.page = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

// 导出 Excel
const handleExport = () => {
  if (!currentPeriod.value) {
    ElMessage.warning('请先选择期间')
    return
  }
  const url = indexApi.exportExcel(currentPeriod.value, currentCategory.value)
  window.open(url, '_blank')
}

const formatPeriod = (period) => {
  if (!period || period.length !== 6) return period
  return `${period.substring(0, 4)}年${period.substring(4)}月`
}

const formatNumber = (value) => {
  if (value === null || value === undefined) return '-'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatPercent = (value) => {
  if (value === null || value === undefined) return '-'
  return Number(value).toFixed(2)
}

const getValueClass = (value) => {
  if (value === null || value === undefined) return ''
  const num = Number(value)
  return num > 0 ? 'positive-value' : num < 0 ? 'negative-value' : ''
}

onMounted(() => {
  loadOptions()
})
</script>

<style scoped>
.index-list-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
