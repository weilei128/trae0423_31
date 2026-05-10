<template>
  <div class="report">
    <el-card>
      <template #header>
        <span>库存周转率</span>
      </template>
      
      <el-button type="primary" @click="loadData">刷新</el-button>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="8">
          <el-card>
            <div class="stat">总入库数量</div>
            <div class="stat-value">{{ data.totalInCount || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <div class="stat">总出库数量</div>
            <div class="stat-value">{{ data.totalOutCount || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <div class="stat">总采购金额</div>
            <div class="stat-value">￥{{ data.totalInAmount || 0 }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { reportApi } from '@/api'

const data = ref({})

const loadData = async () => {
  const res = await reportApi.getTurnover()
  data.value = res.data || {}
}

onMounted(loadData)
</script>

<style scoped>
.stat { font-size: 14px; color: #909399; }
.stat-value { font-size: 28px; font-weight: bold; margin-top: 10px; }
</style>
