<template>
  <div class="report">
    <el-card>
      <template #header>
        <span>消耗量排行</span>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="queryForm.startDate" type="date" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="queryForm.endDate" type="date" />
        </el-form-item>
        <el-form-item label="TOP N">
          <el-input-number v-model="queryForm.topN" :min="1" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="排名" width="80" />
        <el-table-column prop="drugId" label="药品ID" width="100" />
        <el-table-column prop="quantity" label="消耗量" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { reportApi } from '@/api'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({ startDate: '', endDate: '', topN: 10 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await reportApi.getConsumption(queryForm)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.query-form { margin-bottom: 20px; }
</style>
