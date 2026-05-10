<template>
  <div class="report">
    <el-card>
      <template #header>
        <span>采购金额分析</span>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="queryForm.startDate" type="date" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="queryForm.endDate" type="date" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
      
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="8">
          <el-card>
            <div class="stat">总采购金额</div>
            <div class="stat-value">￥{{ data.totalAmount || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <div class="stat">入库次数</div>
            <div class="stat-value">{{ data.totalCount || 0 }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { reportApi } from '@/api'

const data = ref({})
const queryForm = reactive({ startDate: '', endDate: '' })

const loadData = async () => {
  const res = await reportApi.getPurchase(queryForm)
  data.value = res.data || {}
}

onMounted(loadData)
</script>

<style scoped>
.query-form { margin-bottom: 20px; }
.stat { font-size: 14px; color: #909399; }
.stat-value { font-size: 28px; font-weight: bold; margin-top: 10px; }
</style>
