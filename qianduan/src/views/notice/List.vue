<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.noticeType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="通知" value="NOTICE" /><el-option label="公告" value="ANNOUNCEMENT" /><el-option label="新闻" value="NEWS" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /><el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.notice.add')" type="success" @click="handleAdd">发布通知</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题">
          <template #default="{ row }">
            <el-tag type="danger" size="small" v-if="row.isTop">置顶</el-tag> {{ row.title }}
          </template>
        </el-table-column>
        <el-table-column prop="noticeType" label="类型" width="80">
          <template #default="{ row }">{{ typeMap[row.noticeType] }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="userStore.hasPerm('btn.notice.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('btn.notice.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" :disabled="isView" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="form.noticeType" :disabled="isView">
                <el-option label="通知" value="NOTICE" /><el-option label="公告" value="ANNOUNCEMENT" /><el-option label="新闻" value="NEWS" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" :disabled="isView">
                <el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /><el-option label="已下架" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" :disabled="isView" />
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" :disabled="isView" /></el-form-item>
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
import { noticeApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const typeMap = { NOTICE: '通知', ANNOUNCEMENT: '公告', NEWS: '新闻' }
const statusMap = { 0: '草稿', 1: '已发布', 2: '已下架' }
const statusType = { 0: 'info', 1: 'success', 2: 'warning' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', noticeType: '', status: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})

const dialogTitle = computed(() => ({ add: '发布通知', edit: '编辑通知', view: '通知详情' }[dialogMode.value]))
const isView = computed(() => dialogMode.value === 'view')

const loadData = async () => {
  loading.value = true
  const res = await noticeApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { noticeType: 'NOTICE', status: 1, isTop: 0 }
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await noticeApi.get(row.id)
  form.value = res.data
  dialogMode.value = 'view'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await noticeApi.edit(form.value.id, form.value)
  } else {
    await noticeApi.add(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await noticeApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', noticeType: '', status: '' }
  loadData()
}

onMounted(loadData)
</script>
