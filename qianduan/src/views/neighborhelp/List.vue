<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.helpType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="求助" value="SEEK" /><el-option label="提供帮助" value="OFFER" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.category" placeholder="请选择" clearable style="width: 180px">
            <el-option label="日常" value="DAILY" /><el-option label="技能" value="SKILL" />
            <el-option label="物品" value="ITEM" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.neighborhelp.add')" type="success" @click="handleAdd">发布互助</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="helpType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.helpType === 'SEEK' ? 'danger' : 'success'">{{ typeMap[row.helpType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="80">
          <template #default="{ row }">{{ categoryMap[row.category] }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="userStore.hasPerm('btn.neighborhelp.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1 && userStore.hasPerm('btn.neighborhelp.status.complete')" link type="success" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 1 && userStore.hasPerm('btn.neighborhelp.status.close')" link type="warning" @click="handleClose(row)">关闭</el-button>
            <el-button v-if="userStore.hasPerm('btn.neighborhelp.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" :disabled="isView" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="helpType">
              <el-select v-model="form.helpType" :disabled="isView">
                <el-option label="求助" value="SEEK" /><el-option label="提供帮助" value="OFFER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" :disabled="isView">
                <el-option label="日常" value="DAILY" /><el-option label="技能" value="SKILL" />
                <el-option label="物品" value="ITEM" /><el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系方式" prop="contactInfo"><el-input v-model="form.contactInfo" :disabled="isView" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" :disabled="isView" /></el-form-item>
        <el-form-item label="AI辅助" v-if="!isView">
          <div style="width: 100%">
            <el-button type="primary" plain :loading="aiLoading" @click.stop="handleEnhance">{{ aiLoading ? '正在完善...' : '使用大模型完善信息' }}</el-button>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">AI 会帮你润色互助内容，但不会自动填写手机号、微信号等联系方式。</div>
          </div>
        </el-form-item>
        <el-form-item label="状态" v-if="form.id">
          <el-select v-model="form.status" :disabled="isView">
            <el-option label="已关闭" :value="0" /><el-option label="进行中" :value="1" /><el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { neighborHelpApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { contactRule } from '../../utils/validation'

const userStore = useUserStore()
const typeMap = { SEEK: '求助', OFFER: '提供帮助' }
const categoryMap = { DAILY: '日常', SKILL: '技能', ITEM: '物品', OTHER: '其他' }
const statusMap = { 0: '已关闭', 1: '进行中', 2: '已完成' }
const statusType = { 0: 'info', 1: 'success', 2: 'primary' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', helpType: '', category: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})
const formRef = ref()
const aiLoading = ref(false)

const isView = computed(() => dialogMode.value === 'view')
const dialogTitle = computed(() => ({ add: '发布互助', edit: '编辑互助', view: '互助详情' }[dialogMode.value]))

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  helpType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  contactInfo: [contactRule],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  const res = await neighborHelpApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { helpType: 'SEEK', category: 'DAILY' }
  aiLoading.value = false
  dialogMode.value = 'add'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleEdit = async (row) => {
  const res = await neighborHelpApi.get(row.id)
  form.value = res.data
  aiLoading.value = false
  dialogMode.value = 'edit'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleView = async (row) => {
  // 使用preview接口，不增加浏览次数
  const res = await neighborHelpApi.preview(row.id)
  form.value = res.data
  aiLoading.value = false
  dialogMode.value = 'view'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 专门用于增加浏览次数的查看函数
const handleDetailView = async (id) => {
  const res = await neighborHelpApi.get(id)
  return res.data
}

const hasEnhanceInput = () => Boolean(form.value.title?.trim() || form.value.content?.trim())

const handleEnhance = async () => {
  if (!hasEnhanceInput()) {
    ElMessage.warning('请先输入互助标题或互助内容')
    return
  }
  aiLoading.value = true
  try {
    const res = await neighborHelpApi.enhance({
      title: form.value.title,
      content: form.value.content,
      helpType: form.value.helpType,
      category: form.value.category
    })
    form.value = { ...form.value, ...res.data }
    nextTick(() => {
      formRef.value?.clearValidate(['title', 'content', 'helpType', 'category'])
    })
    ElMessage.success('AI 已完善互助内容，请补充真实联系方式后发布')
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
    await neighborHelpApi.edit(form.value.id, form.value)
  } else {
    await neighborHelpApi.add(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleStatus = async (row, status) => {
  await neighborHelpApi.updateStatus(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

const handleComplete = async (row) => {
  ElMessageBox.confirm(`确定标记为已完成吗？`, '提示', { type: 'warning' }).then(async () => {
    await handleStatus(row, 2)
  })
}

const handleClose = async (row) => {
  ElMessageBox.confirm(`确定关闭此互助请求吗？关闭后将不再接受帮助。`, '提示', { type: 'warning' }).then(async () => {
    await handleStatus(row, 0)
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await neighborHelpApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', helpType: '', category: '' }
  loadData()
}

onMounted(loadData)
</script>


