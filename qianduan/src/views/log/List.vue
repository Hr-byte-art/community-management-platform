<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="用户名"><el-input v-model="query.username" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="操作"><el-input v-model="query.operation" placeholder="请输入" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="danger" @click="handleClear">清空日志</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="operation" label="操作内容" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="method" label="方法" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="time" label="耗时(ms)" width="100" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logApi } from '../../api'

const query = ref({ pageNum: 1, pageSize: 10, username: '', operation: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 格式化时间为北京时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  // 转换为北京时间格式
  return date.toLocaleString('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

const loadData = async () => {
  loading.value = true
  const res = await logApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该日志？', '提示', { type: 'warning' }).then(async () => {
    await logApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleClear = () => {
  ElMessageBox.confirm('确定清空所有日志？此操作不可恢复！', '警告', { type: 'warning' }).then(async () => {
    await logApi.clear()
    ElMessage.success('清空成功')
    loadData()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, username: '', operation: '' }
  loadData()
}

onMounted(loadData)
</script>
