<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="姓名"><el-input v-model="query.name" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.volunteer.apply')" type="success" @click="handleApply">申请成为志愿者</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="skills" label="技能特长" />
        <el-table-column prop="serviceHours" label="服务时长(h)" width="100" />
        <el-table-column prop="joinDate" label="加入日期" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button v-if="userStore.hasPerm('btn.volunteer.audit')" link type="success" @click="handleAudit(row, 1)">通过</el-button>
              <el-button v-if="userStore.hasPerm('btn.volunteer.audit')" link type="danger" @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
            <el-button v-if="userStore.hasPerm('btn.volunteer.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('btn.volunteer.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑志愿者' : '申请成为志愿者'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="技能特长" prop="skills"><el-input v-model="form.skills" type="textarea" /></el-form-item>
        <el-form-item label="个人照片"><ImageUploadField v-model="form.photo" /></el-form-item>
        <el-form-item label="服务时长" v-if="form.id"><el-input-number v-model="form.serviceHours" :min="0" :precision="1" /></el-form-item>
        <el-form-item label="状态" v-if="form.id">
          <el-select v-model="form.status">
            <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
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
import { volunteerApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { phoneRule } from '../../utils/validation'
import ImageUploadField from '../../components/ImageUploadField.vue'

const userStore = useUserStore()
const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
const statusType = { 0: 'warning', 1: 'success', 2: 'danger' }
const query = ref({ pageNum: 1, pageSize: 10, name: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})
const formRef = ref()

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [phoneRule],
  skills: [{ required: true, message: '请输入技能特长', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  const res = await volunteerApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleApply = () => {
  form.value = {}
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
  if (!formRef.value) return
  await formRef.value.validate()
  
  if (form.value.id) {
    await volunteerApi.edit(form.value.id, form.value)
  } else {
    await volunteerApi.apply(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleAudit = async (row, status) => {
  await volunteerApi.audit(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await volunteerApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, name: '', status: null }
  loadData()
}

onMounted(loadData)
</script>
