<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.category" placeholder="请选择" clearable style="width: 180px">
            <el-option label="证件办理" value="CERTIFICATE" /><el-option label="社保" value="SOCIAL" />
            <el-option label="住房" value="HOUSING" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">发布指南</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">{{ categoryMap[row.category] }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80" />
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" :disabled="isView" /></el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" :disabled="isView">
            <el-option label="证件办理" value="CERTIFICATE" /><el-option label="社保" value="SOCIAL" />
            <el-option label="住房" value="HOUSING" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="3" :disabled="isView" /></el-form-item>
        <el-form-item label="所需材料"><el-input v-model="form.requiredMaterials" type="textarea" :rows="3" :disabled="isView" /></el-form-item>
        <el-form-item label="办理流程"><el-input v-model="form.processSteps" type="textarea" :rows="3" :disabled="isView" /></el-form-item>
        <el-form-item label="联系方式" prop="contactInfo"><el-input v-model="form.contactInfo" :disabled="isView" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { guideApi } from '../../api'
import { contactRule } from '../../utils/validation'

const categoryMap = { CERTIFICATE: '证件办理', SOCIAL: '社保', HOUSING: '住房', OTHER: '其他' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', category: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})
const formRef = ref()

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  contactInfo: [contactRule]
}

const dialogTitle = computed(() => ({ add: '发布指南', edit: '编辑指南', view: '指南详情' }[dialogMode.value]))
const isView = computed(() => dialogMode.value === 'view')

const loadData = async () => {
  loading.value = true
  const res = await guideApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { category: 'CERTIFICATE' }
  dialogMode.value = 'add'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogMode.value = 'edit'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleView = async (row) => {
  const res = await guideApi.get(row.id)
  form.value = res.data
  dialogMode.value = 'view'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  if (form.value.id) {
    await guideApi.edit(form.value.id, form.value)
  } else {
    await guideApi.add(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await guideApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', category: '' }
  loadData()
}

onMounted(loadData)
</script>
