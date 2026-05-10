<template>
  <div class="location">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>货位管理</span>
          <el-button type="primary" @click="openDialog()">新增货位</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="locationCode" label="货位编码" width="120" />
        <el-table-column prop="locationName" label="货位名称" />
        <el-table-column prop="warehouseName" label="仓库名称" />
        <el-table-column prop="area" label="区域" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑货位' : '新增货位'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="货位编码" prop="locationCode">
          <el-input v-model="form.locationCode" />
        </el-form-item>
        <el-form-item label="货位名称" prop="locationName">
          <el-input v-model="form.locationName" />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input v-model="form.warehouseName" />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="form.area" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { commonApi } from '@/api'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null, locationCode: '', locationName: '', warehouseName: '', area: '', sortOrder: 0, status: 1, remark: ''
})

const rules = {
  locationCode: [{ required: true, message: '请输入货位编码', trigger: 'blur' }],
  locationName: [{ required: true, message: '请输入货位名称', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await commonApi.listLocation()
  tableData.value = res.data || []
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => form[key] = key === 'id' ? null : key === 'status' ? 1 : key === 'sortOrder' ? 0 : '')
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await commonApi.updateLocation(form)
    ElMessage.success('更新成功')
  } else {
    await commonApi.saveLocation(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const deleteItem = async (row) => {
  await ElMessageBox.confirm('确定要删除该货位吗？', '提示', { type: 'warning' })
  await commonApi.deleteLocation(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
