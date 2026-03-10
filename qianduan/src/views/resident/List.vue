<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="姓名"><el-input v-model="query.name" placeholder="请输入姓名" clearable /></el-form-item>
        <el-form-item label="楼栋"><el-input v-model="query.buildingNo" placeholder="楼栋号" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="在住" :value="1" /><el-option label="已迁出" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增居民</el-button>
          <el-button type="warning" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="residenceType" label="居住类型" width="100">
          <template #default="{ row }">{{ row.residenceType === 'OWN' ? '自有' : '租住' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在住' : '已迁出' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑居民' : '新增居民'" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio :value="1">男</el-radio><el-radio :value="0">女</el-radio></el-radio-group></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="居住地址"><el-input v-model="form.address" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="楼栋号"><el-input v-model="form.buildingNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="单元号"><el-input v-model="form.unitNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="房间号"><el-input v-model="form.roomNo" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="居住类型"><el-select v-model="form.residenceType"><el-option label="自有" value="OWN" /><el-option label="租住" value="RENT" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="在住" :value="1" /><el-option label="已迁出" :value="0" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { residentApi, exportApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { idCardRule, phoneRule } from '../../utils/validation'

const userStore = useUserStore()
const query = ref({ pageNum: 1, pageSize: 10, name: '', buildingNo: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})
const formRef = ref()

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  idCard: [idCardRule],
  phone: [phoneRule]
}

const loadData = async () => {
  loading.value = true
  const res = await residentApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { gender: 1, status: 1, residenceType: 'OWN' }
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.value.id) {
      await residentApi.edit(form.value.id, form.value)
    } else {
      await residentApi.add(form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该居民信息？', '提示', { type: 'warning' }).then(async () => {
    await residentApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleExport = () => {
  const link = document.createElement('a')
  link.href = exportApi.resident() + '?token=' + userStore.token
  link.click()
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, name: '', buildingNo: '', status: null }
  loadData()
}

onMounted(loadData)
</script>
