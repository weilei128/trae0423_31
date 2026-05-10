<template>
  <div class="stock-in">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>入库管理</span>
          <el-button type="primary" @click="openDialog()">新增入库单</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="入库单号">
          <el-input v-model="queryForm.keyword" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="待入库" :value="0" />
            <el-option label="已入库" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="inNo" label="入库单号" width="180" />
        <el-table-column label="入库类型" width="100">
          <template #default="{ row }">{{ row.inType === 1 ? '采购入库' : '其他' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">￥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="warehouseKeeper" label="库管" width="100" />
        <el-table-column prop="inTime" label="入库时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'info'">
              {{ row.status === 0 ? '待入库' : row.status === 1 ? '已入库' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button type="success" link v-if="row.status === 0" @click="confirmIn(row)">确认入库</el-button>
            <el-button type="danger" link v-if="row.status === 0" @click="cancelIn(row)">取消</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="新增入库单" width="1000px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入库类型">
              <el-select v-model="form.inType" style="width: 100%;">
                <el-option label="采购入库" :value="1" />
                <el-option label="退货入库" :value="2" />
                <el-option label="调拨入库" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择" style="width: 100%;">
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库管">
              <el-input v-model="form.warehouseKeeper" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="药品明细">
          <el-table :data="items" style="width: 100%;" border>
            <el-table-column label="药品" width="200">
              <template #default="{ row }">
                <el-select v-model="row.drugId" placeholder="请选择" style="width: 100%;">
                  <el-option v-for="drug in drugs" :key="drug.id" :label="drug.genericName + '(' + drug.specification + ')'" :value="drug.id" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="批次号" width="130">
              <template #default="{ row }">
                <el-input v-model="row.batchNo" />
              </template>
            </el-table-column>
            <el-table-column label="数量" width="90">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.unitPrice" :min="0" :precision="2" />
              </template>
            </el-table-column>
            <el-table-column label="生产日期" width="130">
              <template #default="{ row }">
                <el-date-picker v-model="row.productionDate" type="date" style="width: 100%;" />
              </template>
            </el-table-column>
            <el-table-column label="效期" width="130">
              <template #default="{ row }">
                <el-date-picker v-model="row.expiryDate" type="date" style="width: 100%;" />
              </template>
            </el-table-column>
            <el-table-column label="货位" width="120">
              <template #default="{ row }">
                <el-select v-model="row.location" placeholder="请选择" style="width: 100%;">
                  <el-option v-for="loc in locations" :key="loc.id" :label="loc.locationName" :value="loc.locationCode" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ $index }">
                <el-button type="danger" link @click="removeItem($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" style="margin-top: 10px;" @click="addItem">+ 添加药品</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="入库单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入库单号">{{ detail.inNo }}</el-descriptions-item>
        <el-descriptions-item label="入库类型">{{ detail.inType === 1 ? '采购入库' : '其他' }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="库管">{{ detail.warehouseKeeper }}</el-descriptions-item>
        <el-descriptions-item label="入库时间">{{ detail.inTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === 0 ? '待入库' : detail.status === 1 ? '已入库' : '已取消' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-table :data="detailItems" style="width: 100%; margin-top: 10px;" border>
        <el-table-column prop="drugId" label="药品ID" />
        <el-table-column prop="batchNo" label="批次号" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="expiryDate" label="效期" />
        <el-table-column prop="location" label="货位" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { stockApi, drugApi, commonApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const drugs = ref([])
const suppliers = ref([])
const locations = ref([])
const items = ref([])
const detail = ref({})
const detailItems = ref([])

const queryForm = reactive({ keyword: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({ inType: 1, supplierId: null, warehouseKeeper: '', remark: '' })

const loadOptions = async () => {
  const [d, s, l] = await Promise.all([drugApi.listAllDict(), commonApi.listSupplier(), commonApi.listLocation()])
  drugs.value = d.data || []
  suppliers.value = s.data || []
  locations.value = l.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await stockApi.listIn({ ...pagination, ...queryForm })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.status = null
  loadData()
}

const addItem = () => {
  items.value.push({ drugId: null, batchNo: '', quantity: 1, unitPrice: 0, productionDate: null, expiryDate: null, location: '' })
}

const removeItem = (index) => items.value.splice(index, 1)

const openDialog = () => {
  items.value = []
  dialogVisible.value = true
}

const submitForm = async () => {
  await stockApi.saveIn({ stockIn: form, items: items.value })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const viewDetail = async (row) => {
  detail.value = row
  const res = await stockApi.getInItems(row.id)
  detailItems.value = res.data || []
  detailVisible.value = true
}

const confirmIn = async (row) => {
  await ElMessageBox.confirm('确定要确认入库吗？入库后库存将增加。', '提示', { type: 'warning' })
  await stockApi.confirmIn(row.id)
  ElMessage.success('入库成功')
  loadData()
}

const cancelIn = async (row) => {
  await ElMessageBox.confirm('确定要取消该入库单吗？', '提示', { type: 'warning' })
  await stockApi.cancelIn(row.id)
  ElMessage.success('取消成功')
  loadData()
}

onMounted(() => {
  loadOptions()
  loadData()
})
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
.query-form { margin-bottom: 20px; }
</style>
