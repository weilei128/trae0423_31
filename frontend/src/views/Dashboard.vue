<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #409EFF"><MedicineBox /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.drugCount || 0 }}</div>
              <div class="stat-label">药品品种</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #67C23A"><Box /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.stockCount || 0 }}</div>
              <div class="stat-label">库存批次</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #E6A23C"><Warning /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.expiryCount || 0 }}</div>
              <div class="stat-label">近效期预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #F56C6C"><ShoppingCart /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.purchaseCount || 0 }}</div>
              <div class="stat-label">采购计划</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>近效期预警列表</span>
          </template>
          <el-table :data="expiryList" style="width: 100%">
            <el-table-column prop="batchNo" label="批次号" />
            <el-table-column prop="expiryDate" label="效期" />
            <el-table-column prop="remainingDays" label="剩余天数">
              <template #default="{ row }">
                <el-tag :type="row.remainingDays <= 30 ? 'danger' : 'warning'">
                  {{ row.remainingDays }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快速入口</span>
          </template>
          <el-row :gutter="10">
            <el-col :span="8" style="margin-bottom: 10px;">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/stock-in')">
                <el-icon><Upload /></el-icon>
                <div>入库管理</div>
              </el-card>
            </el-col>
            <el-col :span="8" style="margin-bottom: 10px;">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/stock-out')">
                <el-icon><Download /></el-icon>
                <div>出库管理</div>
              </el-card>
            </el-col>
            <el-col :span="8" style="margin-bottom: 10px;">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/stock-list')">
                <el-icon><Search /></el-icon>
                <div>库存查询</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/purchase-plan')">
                <el-icon><ShoppingCart /></el-icon>
                <div>采购计划</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/inventory-task')">
                <el-icon><DocumentChecked /></el-icon>
                <div>库存盘点</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="quick-card" @click="$router.push('/expiry-alert')">
                <el-icon><Warning /></el-icon>
                <div>近效期处理</div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { expiryApi, drugApi, stockApi, purchaseApi } from '@/api'

const stats = ref({})
const expiryList = ref([])

const loadData = async () => {
  try {
    const [drugRes, stockRes, expiryRes, purchaseRes] = await Promise.all([
      drugApi.listAllDict(),
      stockApi.list({ current: 1, size: 1 }),
      expiryApi.getPending(),
      purchaseApi.list({ current: 1, size: 1 })
    ])
    stats.value.drugCount = drugRes.data?.length || 0
    stats.value.stockCount = stockRes.data?.total || 0
    stats.value.expiryCount = expiryRes.data?.length || 0
    stats.value.purchaseCount = purchaseRes.data?.total || 0
    expiryList.value = expiryRes.data?.slice(0, 5) || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stat-card {
  height: 120px;
}
.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
}
.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.quick-card {
  text-align: center;
  cursor: pointer;
}
.quick-card .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}
</style>
