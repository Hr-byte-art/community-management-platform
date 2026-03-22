<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.orderType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="报修" value="REPAIR" /><el-option label="投诉" value="COMPLAINT" />
            <el-option label="建议" value="SUGGESTION" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" /><el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="超时状态">
          <el-select v-model="query.isOvertime" placeholder="请选择" clearable style="width: 140px">
            <el-option label="正常" :value="0" />
            <el-option label="已超时" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="query.deadlineRange"
            type="daterange"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item v-if="canAssign" label="责任人">
          <el-select v-model="query.assigneeId" placeholder="请选择" clearable style="width: 180px">
            <el-option v-for="u in userOptions" :key="u.id" :label="`${u.realName || u.username}(${u.id})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.workorder.add')" type="success" @click="handleAdd">提交工单</el-button>
          <el-button v-if="userStore.hasPerm('btn.workorder.export')" type="warning" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="orderType" label="类型" width="80">
          <template #default="{ row }">{{ typeMap[row.orderType] }}</template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="priorityType[row.priority]" size="small">{{ priorityMap[row.priority] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeId" label="责任人" width="140">
          <template #default="{ row }">{{ row.assigneeName || formatAssignee(row.assigneeId) }}</template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="超时" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isOvertime === 1 ? 'danger' : 'success'">{{ row.isOvertime === 1 ? '已超时' : '正常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleHandle(row)" v-if="row.status < 2 && userStore.hasPerm('btn.workorder.handle')">处理</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="userStore.hasPerm('btn.workorder.edit')">编辑</el-button>
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="userStore.hasPerm('btn.workorder.delete')">删除</el-button>
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
        <el-form-item label="类型">
          <el-select v-model="form.orderType" :disabled="isView">
            <el-option label="报修" value="REPAIR" /><el-option label="投诉" value="COMPLAINT" />
            <el-option label="建议" value="SUGGESTION" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" :disabled="isView">
            <el-option label="低" :value="0" /><el-option label="中" :value="1" /><el-option label="高" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canAssign" label="责任人">
          <el-select v-model="form.assigneeId" clearable :disabled="isView" style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="`${u.realName || u.username}(${u.id})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" v-if="canSetDeadline || isView">
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            placeholder="请选择截止时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled="isView"
          />
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" :disabled="isView" /></el-form-item>
        <el-form-item label="AI辅助" v-if="!isView && !isHandle">
          <div style="width: 100%">
            <el-button type="primary" plain :loading="aiLoading" @click.stop="handleEnhance">{{ aiLoading ? '正在完善...' : '使用大模型完善信息' }}</el-button>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">AI 会帮你梳理问题现象和优先级，不会自动填写门牌号、联系方式等隐私信息。</div>
          </div>
        </el-form-item>
        <template v-if="isHandle || isView || isEdit">
          <el-form-item label="处理状态">
            <el-select v-model="form.status" :disabled="isView">
              <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已关闭" :value="3" />
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
import { workOrderApi, exportApi, userApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const typeMap = { REPAIR: '报修', COMPLAINT: '投诉', SUGGESTION: '建议', OTHER: '其他' }
const statusMap = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const priorityMap = { 0: '低', 1: '中', 2: '高' }
const priorityType = { 0: 'info', 1: 'warning', 2: 'danger' }
const userOptions = ref([])
const query = ref({ pageNum: 1, pageSize: 10, title: '', orderType: '', status: null, isOvertime: null, deadlineRange: [], assigneeId: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})
const aiLoading = ref(false)

const dialogTitle = computed(() => ({ add: '提交工单', edit: '编辑工单', handle: '处理工单', view: '工单详情' }[dialogMode.value]))
const isView = computed(() => dialogMode.value === 'view')
const isHandle = computed(() => dialogMode.value === 'handle')
const isEdit = computed(() => dialogMode.value === 'edit')
const canAssign = computed(() => userStore.hasPerm('btn.workorder.handle'))
const canSetDeadline = computed(() => canAssign.value || isEdit.value)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: query.value.pageNum,
      pageSize: query.value.pageSize,
      title: query.value.title?.trim() || undefined,
      orderType: query.value.orderType || undefined,
      status: query.value.status,
      isOvertime: query.value.isOvertime,
      assigneeId: query.value.assigneeId || undefined,
      deadlineStart: query.value.deadlineRange?.[0] || undefined,
      deadlineEnd: query.value.deadlineRange?.[1] || undefined
    }
    const res = await workOrderApi.list(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadUsers = async () => {
  if (!canAssign.value) {
    userOptions.value = []
    return
  }
  const res = await userApi.list({ pageNum: 1, pageSize: 500 })
  userOptions.value = res?.data?.records || []
}

const normalizeDateTime = (value) => {
  if (!value) return ''
  if (typeof value === 'string') {
    return value.replace('T', ' ').slice(0, 19)
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const formatAssignee = (assigneeId) => {
  if (!assigneeId) return '-'
  const user = userOptions.value.find((item) => item.id === assigneeId)
  return user ? `${user.realName || user.username}(${assigneeId})` : `用户${assigneeId}`
}

const handleAdd = () => {
  form.value = { priority: 1, orderType: 'REPAIR', assigneeId: null, deadline: '' }
  aiLoading.value = false
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleHandle = (row) => {
  form.value = { ...row, status: 1, deadline: normalizeDateTime(row.deadline) }
  aiLoading.value = false
  dialogMode.value = 'handle'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row, deadline: normalizeDateTime(row.deadline) }
  aiLoading.value = false
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleView = (row) => {
  form.value = { ...row, deadline: normalizeDateTime(row.deadline) }
  aiLoading.value = false
  dialogMode.value = 'view'
  dialogVisible.value = true
}

const hasEnhanceInput = () => Boolean(form.value.title?.trim() || form.value.content?.trim())

const handleEnhance = async () => {
  if (!hasEnhanceInput()) {
    ElMessage.warning('请先输入标题或问题描述')
    return
  }
  aiLoading.value = true
  try {
    const res = await workOrderApi.enhance({
      title: form.value.title,
      content: form.value.content,
      orderType: form.value.orderType,
      priority: form.value.priority
    })
    form.value = { ...form.value, ...res.data }
    ElMessage.success('AI 已完善主体描述，请核对隐私信息后再提交')
  } catch (error) {
    // 请求层已统一提示，这里只负责兜底，避免未捕获 Promise 影响页面切换
  } finally {
    aiLoading.value = false
  }
}

const handleSubmit = async () => {
  if (dialogMode.value === 'add') {
    await workOrderApi.add(form.value)
  } else if (dialogMode.value === 'edit') {
    await workOrderApi.edit(form.value.id, form.value)
  } else {
    await workOrderApi.handle(form.value.id, form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该工单？', '提示', { type: 'warning' }).then(async () => {
    await workOrderApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleExport = () => {
  const link = document.createElement('a')
  link.href = exportApi.workorder() + '?token=' + userStore.token
  link.click()
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', orderType: '', status: null, isOvertime: null, deadlineRange: [], assigneeId: null }
  loadData()
}

onMounted(async () => {
  await loadUsers()
  await loadData()
})
</script>
