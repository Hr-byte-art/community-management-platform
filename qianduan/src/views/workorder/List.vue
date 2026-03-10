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
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">提交工单</el-button>
          <el-button type="warning" @click="handleExport">导出Excel</el-button>
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
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleHandle(row)" v-if="row.status < 2">处理</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
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
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" :disabled="isView" /></el-form-item>
        <template v-if="isHandle || isView || isEdit">
          <el-form-item label="处理状态">
            <el-select v-model="form.status" :disabled="isView">
              <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已关闭" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理结果"><el-input v-model="form.handleResult" type="textarea" :rows="3" :disabled="isView" /></el-form-item>
        </template>
      </el-form>
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
import { workOrderApi, exportApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const typeMap = { REPAIR: '报修', COMPLAINT: '投诉', SUGGESTION: '建议', OTHER: '其他' }
const statusMap = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const priorityMap = { 0: '低', 1: '中', 2: '高' }
const priorityType = { 0: 'info', 1: 'warning', 2: 'danger' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', orderType: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})

const dialogTitle = computed(() => ({ add: '提交工单', edit: '编辑工单', handle: '处理工单', view: '工单详情' }[dialogMode.value]))
const isView = computed(() => dialogMode.value === 'view')
const isHandle = computed(() => dialogMode.value === 'handle')
const isEdit = computed(() => dialogMode.value === 'edit')

const loadData = async () => {
  loading.value = true
  const res = await workOrderApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { priority: 1, orderType: 'REPAIR' }
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleHandle = (row) => {
  form.value = { ...row, status: 1 }
  dialogMode.value = 'handle'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleView = (row) => {
  form.value = { ...row }
  dialogMode.value = 'view'
  dialogVisible.value = true
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
  query.value = { pageNum: 1, pageSize: 10, title: '', orderType: '', status: null }
  loadData()
}

onMounted(loadData)
</script>
