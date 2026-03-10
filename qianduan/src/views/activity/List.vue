<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="活动名称"><el-input v-model="query.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="query.activityType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="文化" value="CULTURE" /><el-option label="体育" value="SPORT" />
            <el-option label="志愿" value="VOLUNTEER" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">发布活动</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="活动名称" />
        <el-table-column prop="activityType" label="类型" width="80">
          <template #default="{ row }">{{ typeMap[row.activityType] }}</template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">{{ getActivityStatus(row) }}</el-tag>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑活动' : '发布活动'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="form.activityType">
            <el-option label="文化" value="CULTURE" /><el-option label="体育" value="SPORT" />
            <el-option label="志愿" value="VOLUNTEER" /><el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动地点"><el-input v-model="form.location" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="活动内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="是否取消">
          <el-select v-model="form.isCancelled">
            <el-option label="正常" :value="0" />
            <el-option label="已取消" :value="1" />
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
import { activityApi } from '../../api'

const typeMap = { CULTURE: '文化', SPORT: '体育', VOLUNTEER: '志愿', OTHER: '其他' }
const query = ref({ pageNum: 1, pageSize: 10, title: '', activityType: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

// 计算活动状态
const getActivityStatus = (row) => {
  if (row.isCancelled === 1) {
    return '已取消'
  }
  
  const now = new Date()
  const startTime = new Date(row.startTime)
  const endTime = new Date(row.endTime)
  
  if (now < startTime) {
    return '未开始'
  } else if (now > endTime) {
    return '已结束'
  } else {
    return '进行中'
  }
}

// 获取状态标签类型
const getStatusType = (row) => {
  const status = getActivityStatus(row)
  switch (status) {
    case '未开始': return 'info'
    case '进行中': return 'success'
    case '已结束': return ''
    case '已取消': return 'danger'
    default: return 'info'
  }
}

const loadData = async () => {
  loading.value = true
  const res = await activityApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleAdd = () => {
  form.value = { isCancelled: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await activityApi.edit(form.value.id, form.value)
  } else {
    await activityApi.add(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该活动？', '提示', { type: 'warning' }).then(async () => {
    await activityApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', activityType: '' }
  loadData()
}

onMounted(loadData)
</script>
