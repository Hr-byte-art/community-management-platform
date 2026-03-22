<template>
  <div>
    <el-row :gutter="20" class="summary-row">
      <el-col :xs="12" :lg="6">
        <el-card shadow="hover">
          <div class="metric-label">超时未结工单</div>
          <div class="metric-value danger">{{ overview.overtimeOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="hover">
          <div class="metric-label">今日待办工单</div>
          <div class="metric-value warning">{{ overview.todayTodoOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="hover">
          <div class="metric-label">待处理工单</div>
          <div class="metric-value">{{ overview.pendingOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="hover">
          <div class="metric-label">工单总量</div>
          <div class="metric-value">{{ overview.totalOrderCount ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-alert
      :title="boardInsight"
      type="warning"
      :closable="false"
      show-icon
      class="insight-alert"
    />

    <el-row :gutter="20">
      <el-col :xs="24" :lg="10">
        <el-card>
          <template #header>工单状态分布</template>
          <div ref="statusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card>
          <template #header>月度工单趋势</template>
          <div ref="trendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="detail-row">
      <el-col :xs="24" :lg="10">
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

      <el-col :xs="24" :lg="14">
        <el-card>
          <template #header>工单类型明细</template>
          <el-table :data="workorder.byType || []" stripe>
            <el-table-column prop="name" label="类型" min-width="140" />
            <el-table-column prop="count" label="数量" width="100" />
            <el-table-column label="占比" width="120">
              <template #default="{ row }">{{ calcTypePercent(row.count) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, nextTick } from 'vue'
import * as echarts from 'echarts'
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

const statusChartRef = ref(null)
const trendChartRef = ref(null)

let statusChart = null
let trendChart = null

const boardInsight = computed(() => {
  if (Number(overview.value.overtimeOrderCount || 0) > 0) {
    return `当前存在 ${overview.value.overtimeOrderCount} 条超时未结工单，建议优先处理已超时和今日待办事项。`
  }
  if (Number(overview.value.todayTodoOrderCount || 0) > 0) {
    return `当前暂无超时工单，但仍有 ${overview.value.todayTodoOrderCount} 条今日待办工单需要跟进。`
  }
  return '当前工单治理状态较稳定，暂无超时风险，可继续关注新增待处理工单。'
})

const initStatusChart = () => {
  if (!statusChartRef.value) return
  statusChart?.dispose()
  statusChart = echarts.init(statusChartRef.value)

  statusChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['50%', '44%'],
        label: { show: false },
        data: [
          { name: '待处理', value: Number(workorder.value.pending || 0), itemStyle: { color: '#e6a23c' } },
          { name: '处理中', value: Number(workorder.value.processing || 0), itemStyle: { color: '#409eff' } },
          { name: '已完成', value: Number(workorder.value.completed || 0), itemStyle: { color: '#67c23a' } },
          { name: '已关闭', value: Number(workorder.value.closed || 0), itemStyle: { color: '#909399' } }
        ]
      }
    ]
  })
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart?.dispose()
  trendChart = echarts.init(trendChartRef.value)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: (workorder.value.byMonth || []).map((item) => item.month),
      axisLine: { lineStyle: { color: '#ddd' } }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#eee' } }
    },
    grid: { left: '4%', right: '3%', bottom: '10%', top: '10%', containLabel: true },
    series: [
      {
        name: '工单数量',
        type: 'bar',
        data: (workorder.value.byMonth || []).map((item) => item.count),
        barWidth: '42%',
        itemStyle: {
          color: '#409eff',
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  })
}

const calcTypePercent = (count) => {
  const total = Number(overview.value.totalOrderCount || 0)
  if (total <= 0) return '0%'
  return `${((Number(count || 0) / total) * 100).toFixed(1)}%`
}

const handleResize = () => {
  statusChart?.resize()
  trendChart?.resize()
}

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

  await nextTick()
  initStatusChart()
  initTrendChart()
}

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.summary-row,
.detail-row {
  margin-bottom: 20px;
}

.insight-alert {
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

.chart-box {
  width: 100%;
  height: 300px;
}
</style>
