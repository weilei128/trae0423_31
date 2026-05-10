<template>
  <div class="drug-category">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>药品分类</span>
          <el-button type="primary" @click="openDialog()">新增分类</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
import { drugApi } from '@/api'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  categoryName: '',
  parentId: 0,
  sortOrder: 0,
  remark: ''
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await drugApi.listCategory()
  tableData.value = res.data || []
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    form.id = null
    form.categoryName = ''
    form.sortOrder = 0
    form.remark = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await drugApi.updateCategory(form)
    ElMessage.success('更新成功')
  } else {
    await drugApi.saveCategory(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const deleteItem = async (row) => {
  await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
  await drugApi.deleteCategory(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.header-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
