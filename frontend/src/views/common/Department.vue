<template>
  <div class="department">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>科室管理</span>
          <el-button type="primary" @click="openDialog()">新增科室</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="deptCode" label="科室编码" width="120" />
        <el-table-column prop="deptName" label="科室名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
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
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑科室' : '新增科室'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="科室编码" prop="deptCode">
          <el-input v-model="form.deptCode" />
        </el-form-item>
        <el-form-item label="科室名称" prop="deptName">
          <el-input v-model="form.deptName" />
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
  id: null, deptCode: '', deptName: '', sortOrder: 0, status: 1, remark: ''
})

const rules = {
  deptCode: [{ required: true, message: '请输入科室编码', trigger: 'blur' }],
  deptName: [{ required: true, message: '请输入科室名称', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await commonApi.listDepartment()
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
    await commonApi.updateDepartment(form)
    ElMessage.success('更新成功')
  } else {
    await commonApi.saveDepartment(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const deleteItem = async (row) => {
  await ElMessageBox.confirm('确定要删除该科室吗？', '提示', { type: 'warning' })
  await commonApi.deleteDepartment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header-toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
