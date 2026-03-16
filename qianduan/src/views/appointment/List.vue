<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="服务类型">
          <el-select v-model="query.serviceType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="维修" value="REPAIR" /><el-option label="保洁" value="CLEAN" />
            <el-option label="医疗" value="MEDICAL" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="待确认" :value="0" /><el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.appointment.add')" type="success" @click="handleAdd">预约服务</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="预约标题" />
        <el-table-column prop="serviceType" label="服务类型" width="100">
          <template #default="{ row }">{{ typeMap[row.serviceType] }}</template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="appointmentTime" label="预约时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0 && userStore.hasPerm('btn.appointment.status.confirm')" link type="success" @click="handleStatus(row, 1)">确认</el-button>
            <el-button v-if="row.status === 1 && userStore.hasPerm('btn.appointment.status.complete')" link type="primary" @click="handleStatus(row, 2)">完成</el-button>
            <el-button v-if="row.status < 2 && userStore.hasPerm('btn.appointment.status.cancel')" link type="warning" @click="handleStatus(row, 3)">取消</el-button>
            <el-button v-if="userStore.hasPerm('btn.appointment.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('btn.appointment.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑预约' : '预约服务'" width="600px">
      <div
        v-loading="aiLoading"
        element-loading-text="AI 正在完善，请稍候..."
        element-loading-background="rgba(255, 255, 255, 0.72)"
      >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="预约标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType">
            <el-option label="维修" value="REPAIR" /><el-option label="保洁" value="CLEAN" />
            <el-option label="医疗" value="MEDICAL" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime"><el-date-picker v-model="form.appointmentTime" type="datetime" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="联系人" prop="contactName"><el-input v-model="form.contactName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="contactPhone"><el-input v-model="form.contactPhone" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="服务地址" prop="address"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="预约内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="AI辅助">
          <div style="width: 100%">
            <el-button type="primary" plain :loading="aiLoading" @click.stop="handleEnhance">{{ aiLoading ? '正在完善...' : '使用大模型完善信息' }}</el-button>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">AI 会帮你整理服务诉求，不会自动填写联系人、电话、详细地址和预约时间。</div>
          </div>
        </el-form-item>
        <el-form-item label="状态" v-if="form.id">
          <el-select v-model="form.status">
            <el-option label="待确认" :value="0" /><el-option label="已确认" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      </div>
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
import { appointmentApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { phoneRule } from '../../utils/validation'

const typeMap = { REPAIR: '维修', CLEAN: '保洁', MEDICAL: '医疗', OTHER: '其他' }
const userStore = useUserStore()
const statusMap = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const query = ref({ pageNum: 1, pageSize: 10, serviceType: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})
const formRef = ref()
const aiLoading = ref(false)

const rules = {
  title: [{ required: true, message: '请输入预约标题', trigger: 'blur' }],
  serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [phoneRule],
  address: [{ required: true, message: '请输入服务地址', trigger: 'blur' }],
  content: [{ required: true, message: '请输入预约内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  const res = await appointmentApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { serviceType: 'REPAIR' }
  aiLoading.value = false
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleEdit = async (row) => {
  const res = await appointmentApi.get(row.id)
  form.value = res.data
  aiLoading.value = false
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const hasEnhanceInput = () => Boolean(form.value.title?.trim() || form.value.content?.trim())

const handleEnhance = async () => {
  if (!hasEnhanceInput()) {
    ElMessage.warning('请先输入预约标题或预约内容')
    return
  }
  aiLoading.value = true
  try {
    const res = await appointmentApi.enhance({
      title: form.value.title,
      content: form.value.content,
      serviceType: form.value.serviceType,
      address: form.value.address,
      appointmentTime: form.value.appointmentTime,
      remark: form.value.remark
    })
    form.value = { ...form.value, ...res.data }
    nextTick(() => {
      formRef.value?.clearValidate(['title', 'content', 'serviceType'])
    })
    ElMessage.success('AI 已完善预约描述，请继续补充真实联系信息后提交')
  } catch (error) {
    // 请求层已统一提示，这里只负责兜底，避免未捕获 Promise 影响页面切换
  } finally {
    aiLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  if (form.value.id) {
    await appointmentApi.edit(form.value.id, form.value)
  } else {
    await appointmentApi.add(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleStatus = async (row, status) => {
  await appointmentApi.updateStatus(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await appointmentApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, serviceType: '', status: null }
  loadData()
}

onMounted(loadData)
</script>

