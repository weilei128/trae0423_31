<template>
  <div class="stock-out">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>出库管理</span>
          <el-button type="primary" @click="openDialog()">新增出库单</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="出库单号">
          <el-input v-model="queryForm.keyword" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="已出库" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="outNo" label="出库单号" width="180" />
        <el-table-column prop="applyDepartment" label="申请科室" width="120" />
        <el-table-column prop="applicant" label="申请人" width="100" />
        <el-table-column prop="reviewer" label="审核人" width="100" />
        <el-table-column prop="outTime" label="出库时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button type="warning" link v-if="row.status === 0" @click="review(row, 1)">审核通过</el-button>
            <el-button type="danger" link v-if="row.status === 0" @click="review(row, 3)">拒绝</el-button>
            <el-button type="success" link v-if="row.status === 1" @click="confirmOut(row)">确认出库</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="新增出库单" width="1000px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库类型">
              <el-select v-model="form.outType" style="width: 100%;">
                <el-option label="领用出库" :value="1" />
                <el-option label="销售出库" :value="2" />
                <el-option label="调拨出库" :value="3" />
                <el-option label="报损" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请科室">
              <el-select v-model="form.applyDepartment" placeholder="请选择" style="width: 100%;">
                <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.deptName" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="药品明细">
          <el-table :data="items" style="width: 100%;" border>
            <el-table-column label="药品" width="200">
              <template #default="{ row }">
                <el-select v-model="row.drugId" placeholder="请选择" style="width: 100%;" @change="onDrugChange(row)">
                  <el-option v-for="drug in drugs" :key="drug.id" :label="drug.genericName + '(' + drug.specification + ')'" :value="drug.id" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="批次号(可选)" width="130">
              <template #default="{ row }">
                <el-select v-model="row.batchNo" placeholder="自动分配" clearable style="width: 100%;">
                  <el-option v-for="s in row.stockList || []" :key="s.batchNo" :label="s.batchNo + '(剩' + s.quantity + ')' " :value="s.batchNo" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" />
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
    
    <el-dialog v-model="detailVisible" title="出库单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="出库单号">{{ detail.outNo }}</el-descriptions-item>
        <el-descriptions-item label="申请科室">{{ detail.applyDepartment }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detail.applicant }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detail.reviewer }}</el-descriptions-item>
        <el-descriptions-item label="出库时间">{{ detail.outTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(detail.status) }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-table :data="detailItems" style="width: 100%; margin-top: 10px;" border>
        <el-table-column prop="drugId" label="药品ID" />
        <el-table-column prop="batchNo" label="批次号" />
        <el-table-column prop="quantity" label="数量" />
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
const drugs = ref([])
const departments = ref([])
const items = ref([])
const detail = ref({})
const detailItems = ref([])

const queryForm = reactive({ keyword: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ outType: 1, applyDepartment: '', applicant: '', remark: '' })

const statusMap = { 0: '待审核', 1: '审核通过', 2: '已出库', 3: '已拒绝' }
const statusTypeMap = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'danger' }

const getStatusText = (s) => statusMap[s] || ''
const getStatusType = (s) => statusTypeMap[s] || 'info'

const loadOptions = async () => {
  const [d, dept] = await Promise.all([drugApi.listAllDict(), commonApi.listDepartment()])
  drugs.value = d.data || []
  departments.value = dept.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await stockApi.listOut({ ...pagination, ...queryForm })
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
  items.value.push({ drugId: null, batchNo: '', quantity: 1, stockList: [] })
}

const removeItem = (index) => items.value.splice(index, 1)

const onDrugChange = async (row) => {
  if (row.drugId) {
    const res = await stockApi.getByDrug(row.drugId)
    row.stockList = res.data || []
  }
}

const openDialog = () => {
  items.value = []
  dialogVisible.value = true
}

const submitForm = async () => {
  await stockApi.saveOut({ stockOut: form, items: items.value })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const viewDetail = async (row) => {
  detail.value = row
  const res = await stockApi.getOutItems(row.id)
  detailItems.value = res.data || []
  detailVisible.value = true
}

const review = async (row, status) => {
  await ElMessageBox.confirm(status === 1 ? '确定要审核通过吗？' : '确定要拒绝吗？', '提示', { type: 'warning' })
  await stockApi.reviewOut(row.id, status, 'admin')
  ElMessage.success('审核成功')
  loadData()
}

const confirmOut = async (row) => {
  await ElMessageBox.confirm('确定要确认出库吗？出库后库存将减少。', '提示', { type: 'warning' })
  await stockApi.confirmOut(row.id)
  ElMessage.success('出库成功')
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
