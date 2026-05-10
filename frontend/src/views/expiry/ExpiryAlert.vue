<template>
  <div class="expiry-alert">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>近效期预警</span>
          <el-button type="primary" @click="generateAlerts">生成预警</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="待处理" :value="0" />
            <el-option label="已退货" :value="1" />
            <el-option label="已销毁" :value="2" />
            <el-option label="已使用" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警等级">
          <el-select v-model="queryForm.alertLevel" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="30天内" :value="1" />
            <el-option label="60天内" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="drugId" label="药品ID" width="100" />
        <el-table-column prop="batchNo" label="批次号" width="180" />
        <el-table-column prop="expiryDate" label="效期" width="120" />
        <el-table-column prop="remainingDays" label="剩余天数" width="100">
          <template #default="{ row }">
            <el-tag :type="row.remainingDays <= 30 ? 'danger' : 'warning'">
              {{ row.remainingDays }}天
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="alertLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.alertLevel === 1 ? 'danger' : 'warning'">
              {{ row.alertLevel === 1 ? '30天内' : '60天内' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="success" link v-if="row.status === 0" @click="handle(row, 1)">退货</el-button>
            <el-button type="danger" link v-if="row.status === 0" @click="handle(row, 2)">销毁</el-button>
            <el-button type="primary" link v-if="row.status === 0" @click="handle(row, 3)">已使用</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expiryApi } from '@/api'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({ status: null, alertLevel: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const statusMap = { 0: '待处理', 1: '已退货', 2: '已销毁', 3: '已使用' }
const getStatusText = (s) => statusMap[s] || ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await expiryApi.list({ ...pagination, ...queryForm })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.status = null
  queryForm.alertLevel = null
  loadData()
}

const generateAlerts = async () => {
  await ElMessageBox.confirm('确定要生成近效期预警吗？', '提示', { type: 'warning' })
  await expiryApi.generate()
  ElMessage.success('生成成功')
  loadData()
}

const handle = async (row, status) => {
  const msg = status === 1 ? '退货' : status === 2 ? '销毁' : '标记已使用'
  await ElMessageBox.confirm(`确定要${msg}吗？`, '提示', { type: 'warning' })
  await expiryApi.handle(row.id, { status, handler: 'admin' })
  ElMessage.success('处理成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
.query-form { margin-bottom: 20px; }
</style>
