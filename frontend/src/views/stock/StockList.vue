<template>
  <div class="stock-list">
    <el-card>
      <template #header>
        <span>库存查询</span>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="批次号">
          <el-input v-model="queryForm.keyword" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="药品ID">
          <el-input v-model.number="queryForm.drugId" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="drugId" label="药品ID" width="100" />
        <el-table-column prop="batchNo" label="批次号" width="180" />
        <el-table-column prop="quantity" label="库存数量" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">￥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column prop="expiryDate" label="效期" width="120">
          <template #default="{ row }">
            <el-tag :type="getExpiryType(row.expiryDate)" size="small">{{ row.expiryDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="货位" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
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
import { stockApi } from '@/api'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({ keyword: '', drugId: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const getExpiryType = (date) => {
  if (!date) return 'info'
  const now = new Date()
  const expiry = new Date(date)
  const diff = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
  if (diff <= 30) return 'danger'
  if (diff <= 60) return 'warning'
  return 'success'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await stockApi.list({ ...pagination, ...queryForm })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.drugId = null
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.query-form { margin-bottom: 20px; }
</style>
