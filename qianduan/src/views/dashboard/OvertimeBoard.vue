<template>
  <div>
    <el-row :gutter="20" class="summary-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="metric-label">超时未结工单</div>
          <div class="metric-value danger">{{ overview.overtimeOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="metric-label">今日待办工单</div>
          <div class="metric-value warning">{{ overview.todayTodoOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="metric-label">待处理工单</div>
          <div class="metric-value">{{ overview.pendingOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="metric-label">工单总量</div>
          <div class="metric-value">{{ overview.totalOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header>工单状态总览</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="待处理">{{ workorder.pending ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="处理中">{{ workorder.processing ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="已完成">{{ workorder.completed ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="已关闭">{{ workorder.closed ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="超时未结">{{ workorder.overtime ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="今日待办">{{ workorder.todayTodo ?? 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card>
          <template #header>工单类型分布</template>
          <el-table :data="workorder.byType || []" stripe>
            <el-table-column prop="name" label="类型" min-width="140" />
            <el-table-column prop="count" label="数量" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>月度工单趋势</template>
      <el-table :data="workorder.byMonth || []" stripe>
        <el-table-column prop="month" label="月份" width="120" />
        <el-table-column prop="count" label="数量" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { statisticsApi } from '../../api'

const overview = ref({
  overtimeOrderCount: 0,
  todayTodoOrderCount: 0,
  pendingOrderCount: 0,
  totalOrderCount: 0
})

const workorder = ref({
  pending: 0,
  processing: 0,
  completed: 0,
  closed: 0,
  overtime: 0,
  todayTodo: 0,
  byType: [],
  byMonth: []
})

const loadData = async () => {
  const [overviewRes, workorderRes] = await Promise.all([
    statisticsApi.overview(),
    statisticsApi.workorder()
  ])

  overview.value = overviewRes?.data || overview.value
  workorder.value = {
    ...workorder.value,
    ...(workorderRes?.data || {})
  }
}

onMounted(loadData)
</script>

<style scoped>
.summary-row {
  margin-bottom: 20px;
}
.metric-label {
  color: #909399;
  margin-bottom: 8px;
}
.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}
.metric-value.danger {
  color: #f56c6c;
}
.metric-value.warning {
  color: #e6a23c;
}
</style>
