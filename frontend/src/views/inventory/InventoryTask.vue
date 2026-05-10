<template>
  <div class="inventory-task">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>库存盘点</span>
          <el-button type="primary" @click="openDialog()">新建盘点任务</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="任务编号/名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option label="待开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="taskNo" label="任务编号" width="180" />
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column label="盘点类型" width="100">
          <template #default="{ row }">{{ row.taskType === 1 ? '全盘' : '抽盘' }}</template>
        </el-table-column>
        <el-table-column prop="creator" label="创建人" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 0 ? '待开始' : row.status === 1 ? '进行中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewItems(row)">盘点明细</el-button>
            <el-button type="success" link v-if="row.status === 0" @click="startTask(row)">开始</el-button>
            <el-button type="warning" link v-if="row.status === 1" @click="completeTask(row)">完成</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="新建盘点任务" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="form.taskName" />
        </el-form-item>
        <el-form-item label="盘点类型">
          <el-select v-model="form.taskType" style="width: 100%;">
            <el-option label="全盘" :value="1" />
            <el-option label="抽盘" :value="2" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="创建人">
          <el-input v-model="form.creator" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">创建</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="itemsVisible" title="盘点明细" width="1000px">
      <el-table :data="inventoryItems" style="width: 100%;">
        <el-table-column prop="drugId" label="药品ID" width="100" />
        <el-table-column prop="batchNo" label="批次号" width="180" />
        <el-table-column prop="systemQuantity" label="系统数量" width="100" />
        <el-table-column label="实际数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.actualQuantity" :min="0" />
          </template>
        </el-table-column>
        <el-table-column prop="diffQuantity" label="差异" width="100">
          <template #default="{ row }">
            <span :class="row.diffQuantity > 0 ? 'text-success' : row.diffQuantity < 0 ? 'text-danger' : ''">
              {{ row.diffQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 0 ? '未盘点' : row.status === 1 ? '已盘点' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="updateItem(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { inventoryApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const itemsVisible = ref(false)
const inventoryItems = ref([])
const currentTaskId = ref(null)

const queryForm = reactive({ keyword: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ taskName: '', taskType: 1, creator: '', startDate: null, endDate: null, remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await inventoryApi.list({ ...pagination, ...queryForm })
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

const openDialog = () => {
  Object.keys(form).forEach(key => form[key] = key === 'taskType' ? 1 : '')
  form.startDate = null
  form.endDate = null
  dialogVisible.value = true
}

const submitForm = async () => {
  await inventoryApi.create(form)
  ElMessage.success('创建成功')
  dialogVisible.value = false
  loadData()
}

const startTask = async (row) => {
  await ElMessageBox.confirm('确定要开始该盘点任务吗？', '提示', { type: 'warning' })
  await inventoryApi.start(row.id)
  ElMessage.success('已开始')
  loadData()
}

const completeTask = async (row) => {
  await ElMessageBox.confirm('确定要完成该盘点任务吗？', '提示', { type: 'warning' })
  await inventoryApi.complete(row.id)
  ElMessage.success('已完成')
  loadData()
}

const viewItems = async (row) => {
  currentTaskId.value = row.id
  const res = await inventoryApi.getItems(row.id)
  inventoryItems.value = res.data || []
  itemsVisible.value = true
}

const updateItem = async (item) => {
  await inventoryApi.updateItem(item)
  ElMessage.success('保存成功')
}

onMounted(loadData)
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
.query-form { margin-bottom: 20px; }
.text-success { color: #67C23A; }
.text-danger { color: #F56C6C; }
</style>
