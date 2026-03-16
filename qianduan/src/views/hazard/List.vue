<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.hazardType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="消防" value="FIRE" /><el-option label="盗窃" value="THEFT" />
            <el-option label="交通" value="TRAFFIC" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" /><el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.hazard.add')" type="success" @click="handleAdd">上报隐患</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="hazardType" label="类型" width="80">
          <template #default="{ row }">{{ typeMap[row.hazardType] }}</template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上报时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status < 2 && userStore.hasPerm('btn.hazard.handle')" link type="primary" @click="handleHandle(row)">处理</el-button>
            <el-button v-if="userStore.hasPerm('btn.hazard.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="userStore.hasPerm('btn.hazard.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <div
        v-loading="aiLoading"
        element-loading-text="AI 正在完善，请稍候..."
        element-loading-background="rgba(255, 255, 255, 0.72)"
      >
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" :disabled="isView" /></el-form-item>
        <el-form-item label="隐患类型">
          <el-select v-model="form.hazardType" :disabled="isView">
            <el-option label="消防" value="FIRE" /><el-option label="盗窃" value="THEFT" />
            <el-option label="交通" value="TRAFFIC" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="隐患位置"><el-input v-model="form.location" :disabled="isView" /></el-form-item>
        <el-form-item label="隐患描述"><el-input v-model="form.content" type="textarea" :rows="4" :disabled="isView" /></el-form-item>
        <el-form-item label="AI辅助" v-if="!isView && !isHandle">
          <div style="width: 100%">
            <el-button type="primary" plain :loading="aiLoading" @click.stop="handleEnhance">{{ aiLoading ? '正在完善...' : '使用大模型完善信息' }}</el-button>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">AI 会帮你整理风险描述，不会自动捏造具体位置、照片和现场细节。</div>
          </div>
        </el-form-item>
        <template v-if="isHandle || isView || isEdit">
          <el-form-item label="处理状态">
            <el-select v-model="form.status" :disabled="isView">
              <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已解决" :value="2" /><el-option label="已关闭" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理结果"><el-input v-model="form.handleResult" type="textarea" :rows="3" :disabled="isView" /></el-form-item>
        </template>
      </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { hazardApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const typeMap = { FIRE: '消防', THEFT: '盗窃', TRAFFIC: '交通', OTHER: '其他' }
const statusMap = { 0: '待处理', 1: '处理中', 2: '已解决', 3: '已关闭' }
const statusType = { 0: 'danger', 1: 'warning', 2: 'success', 3: 'info' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', hazardType: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})
const aiLoading = ref(false)

const dialogTitle = computed(() => ({ add: '上报隐患', edit: '编辑隐患', handle: '处理隐患', view: '隐患详情' }[dialogMode.value]))
const isView = computed(() => dialogMode.value === 'view')
const isHandle = computed(() => dialogMode.value === 'handle')
const isEdit = computed(() => dialogMode.value === 'edit')

const loadData = async () => {
  loading.value = true
  const res = await hazardApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { hazardType: 'FIRE' }
  aiLoading.value = false
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleHandle = (row) => {
  form.value = { ...row, status: 1 }
  aiLoading.value = false
  dialogMode.value = 'handle'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  aiLoading.value = false
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleView = (row) => {
  form.value = { ...row }
  aiLoading.value = false
  dialogMode.value = 'view'
  dialogVisible.value = true
}

const hasEnhanceInput = () => Boolean(form.value.title?.trim() || form.value.content?.trim())

const handleEnhance = async () => {
  if (!hasEnhanceInput()) {
    ElMessage.warning('请先输入标题或隐患描述')
    return
  }
  aiLoading.value = true
  try {
    const res = await hazardApi.enhance({
      title: form.value.title,
      content: form.value.content,
      hazardType: form.value.hazardType,
      location: form.value.location
    })
    form.value = { ...form.value, ...res.data }
    ElMessage.success('AI 已完善主体描述，请继续核对位置和现场信息')
  } catch (error) {
    // 请求层已统一提示，这里只负责兜底，避免未捕获 Promise 影响页面切换
  } finally {
    aiLoading.value = false
  }
}

const handleSubmit = async () => {
  if (dialogMode.value === 'add') {
    await hazardApi.add(form.value)
  } else if (dialogMode.value === 'edit') {
    await hazardApi.edit(form.value.id, form.value)
  } else {
    await hazardApi.handle(form.value.id, form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await hazardApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', hazardType: '', status: null }
  loadData()
}

onMounted(loadData)
</script>

