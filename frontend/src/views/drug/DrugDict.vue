<template>
  <div class="drug-dict">
    <el-card>
      <template #header>
        <div class="header-toolbar">
          <span>药品字典</span>
          <el-button type="primary" @click="openDialog()">新增药品</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="药品名称/编码" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="请选择" clearable style="width: 150px;">
            <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="drugCode" label="药品编码" width="120" />
        <el-table-column prop="genericName" label="通用名" />
        <el-table-column prop="tradeName" label="商品名" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="dosageForm" label="剂型" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="manufacturer" label="生产厂家" />
        <el-table-column prop="price" label="参考价格" width="100">
          <template #default="{ row }">￥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑药品' : '新增药品'" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品编码" prop="drugCode">
              <el-input v-model="form.drugCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通用名" prop="genericName">
              <el-input v-model="form.genericName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名">
              <el-input v-model="form.tradeName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="form.specification" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="剂型" prop="dosageForm">
              <el-input v-model="form.dosageForm" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产厂家">
              <el-input v-model="form.manufacturer" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准文号">
              <el-input v-model="form.approvalNumber" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品分类">
              <el-select v-model="form.categoryId" placeholder="请选择" style="width: 100%;">
                <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参考价格">
              <el-input-number v-model="form.price" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低库存">
              <el-input-number v-model="form.minimumStock" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高库存">
              <el-input-number v-model="form.maximumStock" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="贮存条件">
          <el-input v-model="form.storageCondition" />
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

const loading = ref(false)
const tableData = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryForm = reactive({
  keyword: '',
  categoryId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  drugCode: '',
  genericName: '',
  tradeName: '',
  specification: '',
  dosageForm: '',
  unit: '',
  manufacturer: '',
  approvalNumber: '',
  categoryId: null,
  storageCondition: '',
  minimumStock: 0,
  maximumStock: null,
  price: 0,
  remark: ''
})

const rules = {
  drugCode: [{ required: true, message: '请输入药品编码', trigger: 'blur' }],
  genericName: [{ required: true, message: '请输入通用名', trigger: 'blur' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  dosageForm: [{ required: true, message: '请输入剂型', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const loadCategories = async () => {
  const res = await drugApi.listCategory()
  categories.value = res.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await drugApi.listDict({
      current: pagination.current,
      size: pagination.size,
      keyword: queryForm.keyword,
      categoryId: queryForm.categoryId
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.categoryId = null
  loadData()
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'minimumStock' || key === 'price') {
        form[key] = 0
      } else if (key === 'id') {
        form[key] = null
      } else {
        form[key] = ''
      }
    })
    form.categoryId = null
    form.maximumStock = null
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await drugApi.updateDict(form)
    ElMessage.success('更新成功')
  } else {
    await drugApi.saveDict(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const deleteItem = async (row) => {
  await ElMessageBox.confirm('确定要删除该药品吗？', '提示', { type: 'warning' })
  await drugApi.deleteDict(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.header-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.query-form {
  margin-bottom: 20px;
}
</style>
