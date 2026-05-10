<template>
  <div class="purchase-plan">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>采购计划</span>
          <el-button type="primary" @click="openDialog()">新增计划</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="计划编号/名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="planNo" label="计划编号" width="180" />
        <el-table-column prop="planName" label="计划名称" />
        <el-table-column prop="applyDepartment" label="申请部门" width="120" />
        <el-table-column prop="applicant" label="申请人" width="100" />
        <el-table-column prop="planDate" label="计划日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button link v-if="row.status === 0" @click="openDialog(row)">编辑</el-button>
            <el-button type="success" link v-if="row.status === 0" @click="submitPlan(row)">提交</el-button>
            <el-button type="warning" link v-if="row.status === 1" @click="audit(row, 2)">审核通过</el-button>
            <el-button type="danger" link v-if="row.status === 1" @click="audit(row, 3)">审核拒绝</el-button>
            <el-button type="info" link v-if="row.status < 2" @click="cancelPlan(row)">取消</el-button>
            <el-button type="danger" link v-if="row.status === 0" @click="deletePlan(row)">删除</el-button>
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
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑采购计划' : '新增采购计划'" width="900px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="form.planName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请部门">
              <el-input v-model="form.applyDepartment" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划日期">
              <el-date-picker v-model="form.planDate" type="date" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="药品明细">
          <el-table :data="items" style="width: 100%;" border>
            <el-table-column label="药品">
              <template #default="{ row }">
                <el-select v-model="row.drugId" placeholder="请选择" style="width: 100%;" @change="onDrugChange(row)">
                  <el-option v-for="drug in drugs" :key="drug.id" :label="drug.genericName + '(' + drug.specification + ')'" :value="drug.id" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" />
              </template>
            </el-table-column>
            <el-table-column label="预估单价" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.estimatedPrice" :min="0" :precision="2" />
              </template>
            </el-table-column>
            <el-table-column label="备注">
              <template #default="{ row }">
                <el-input v-model="row.remark" />
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
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="采购计划详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="计划编号">{{ detail.planNo }}</el-descriptions-item>
        <el-descriptions-item label="计划名称">{{ detail.planName }}</el-descriptions-item>
        <el-descriptions-item label="申请部门">{{ detail.applyDepartment }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detail.applicant }}</el-descriptions-item>
        <el-descriptions-item label="计划日期">{{ detail.planDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(detail.status) }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <div>药品明细：</div>
      <el-table :data="detailItems" style="width: 100%; margin-top: 10px;" border>
        <el-table-column prop="drugId" label="药品ID" width="100" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="estimatedPrice" label="预估单价" width="120" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { purchaseApi, drugApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const drugs = ref([])
const items = ref([])
const detail = ref({})
const detailItems = ref([])

const queryForm = reactive({ keyword: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({
  id: null, planNo: '', planName: '', applyDepartment: '', applicant: '', planDate: null, remark: ''
})

const rules = { planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }] }

const statusMap = { 0: '草稿', 1: '待审核', 2: '已审核', 3: '已取消' }
const statusTypeMap = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }

const getStatusText = (s) => statusMap[s] || ''
const getStatusType = (s) => statusTypeMap[s] || 'info'

const loadDrugs = async () => {
  const res = await drugApi.listAllDict()
  drugs.value = res.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await purchaseApi.list({ ...pagination, ...queryForm })
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
  items.value.push({ drugId: null, quantity: 1, estimatedPrice: 0, remark: '' })
}

const removeItem = (index) => {
  items.value.splice(index, 1)
}

const onDrugChange = (row) => {
  const drug = drugs.value.find(d => d.id === row.drugId)
  if (drug && drug.price) {
    row.estimatedPrice = drug.price
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => form[key] = key === 'id' ? null : '')
    form.planDate = null
  }
  items.value = []
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await purchaseApi.update({ plan: form, items: items.value })
    ElMessage.success('更新成功')
  } else {
    await purchaseApi.save({ plan: form, items: items.value })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const viewDetail = async (row) => {
  detail.value = row
  const res = await purchaseApi.getItems(row.id)
  detailItems.value = res.data || []
  detailVisible.value = true
}

const submitPlan = async (row) => {
  await ElMessageBox.confirm('确定要提交该采购计划吗？', '提示', { type: 'warning' })
  await purchaseApi.submit(row.id)
  ElMessage.success('提交成功')
  loadData()
}

const audit = async (row, status) => {
  await ElMessageBox.confirm(status === 2 ? '确定要审核通过吗？' : '确定要审核拒绝吗？', '提示', { type: 'warning' })
  await purchaseApi.audit(row.id, status)
  ElMessage.success('审核成功')
  loadData()
}

const cancelPlan = async (row) => {
  await ElMessageBox.confirm('确定要取消该采购计划吗？', '提示', { type: 'warning' })
  await purchaseApi.cancel(row.id)
  ElMessage.success('取消成功')
  loadData()
}

const deletePlan = async (row) => {
  await ElMessageBox.confirm('确定要删除该采购计划吗？', '提示', { type: 'warning' })
  await purchaseApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadDrugs()
  loadData()
})
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
.query-form { margin-bottom: 20px; }
</style>
