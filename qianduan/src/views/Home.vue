<template>
  <div class="home">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon blue"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.residentCount }}</div>
              <div class="stat-label">居民总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon green"><el-icon><Calendar /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activityCount }}</div>
              <div class="stat-label">社区活动</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon orange"><el-icon><Tickets /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrderCount }}</div>
              <div class="stat-label">待处理工单</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon red"><el-icon><Warning /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.overtimeOrderCount }}</div>
              <div class="stat-label">超时未结工单</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon yellow"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayTodoOrderCount }}</div>
              <div class="stat-label">今日待办工单</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="8" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon purple"><el-icon><DataLine /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCompletionRate }}</div>
              <div class="stat-label">工单完成率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header><span>工单类型分布</span></template>
          <div ref="workOrderTypeChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header><span>月度工单趋势</span></template>
          <div ref="workOrderMonthChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header><span>活动类型分布</span></template>
          <div ref="activityTypeChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header><span>综合数据统计</span></template>
          <div ref="summaryChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../api'

const stats = ref({
  residentCount: 0,
  activityCount: 0,
  pendingOrderCount: 0,
  overtimeOrderCount: 0,
  todayTodoOrderCount: 0,
  orderCompletionRate: '0%'
})

const workOrderTypeChartRef = ref(null)
const workOrderMonthChartRef = ref(null)
const activityTypeChartRef = ref(null)
const summaryChartRef = ref(null)

let workOrderTypeChart = null
let workOrderMonthChart = null
let activityTypeChart = null
let summaryChart = null

const calcCompletionRate = (overview) => {
  const total = Number(overview?.totalOrderCount || 0)
  const pending = Number(overview?.pendingOrderCount || 0)
  if (total <= 0) {
    return '0%'
  }
  return `${(((total - pending) / total) * 100).toFixed(1)}%`
}

const initWorkOrderTypeChart = (byType = []) => {
  if (!workOrderTypeChartRef.value) return
  workOrderTypeChart?.dispose()
  workOrderTypeChart = echarts.init(workOrderTypeChartRef.value)

  const colorMap = {
    REPAIR: '#409eff',
    COMPLAINT: '#f56c6c',
    SUGGESTION: '#67c23a',
    OTHER: '#909399'
  }

  workOrderTypeChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['50%', '44%'],
        label: { show: false },
        data: byType.length
          ? byType.map((item) => ({
              name: item.name,
              value: item.count,
              itemStyle: { color: colorMap[item.type] || '#409eff' }
            }))
          : [
              { name: '报修', value: 0 },
              { name: '投诉', value: 0 },
              { name: '建议', value: 0 },
              { name: '其他', value: 0 }
            ]
      }
    ]
  })
}

const initWorkOrderMonthChart = (byMonth = []) => {
  if (!workOrderMonthChartRef.value) return
  workOrderMonthChart?.dispose()
  workOrderMonthChart = echarts.init(workOrderMonthChartRef.value)

  workOrderMonthChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: byMonth.map((i) => i.month),
      axisLine: { lineStyle: { color: '#ddd' } }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#eee' } }
    },
    grid: { left: '4%', right: '3%', bottom: '8%', top: '10%', containLabel: true },
    series: [
      {
        type: 'bar',
        data: byMonth.map((i) => i.count),
        barWidth: '45%',
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
      }
    ]
  })
}

const initActivityTypeChart = (byType = []) => {
  if (!activityTypeChartRef.value) return
  activityTypeChart?.dispose()
  activityTypeChart = echarts.init(activityTypeChartRef.value)

  const colorMap = {
    CULTURE: '#409eff',
    SPORT: '#67c23a',
    VOLUNTEER: '#e6a23c',
    OTHER: '#909399'
  }

  activityTypeChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['50%', '44%'],
        label: { show: false },
        data: byType.length
          ? byType.map((item) => ({
              name: item.name,
              value: item.count,
              itemStyle: { color: colorMap[item.type] || '#409eff' }
            }))
          : [
              { name: '文化', value: 0 },
              { name: '体育', value: 0 },
              { name: '志愿', value: 0 },
              { name: '其他', value: 0 }
            ]
      }
    ]
  })
}

const initSummaryChart = (overview = {}) => {
  if (!summaryChartRef.value) return
  summaryChart?.dispose()
  summaryChart = echarts.init(summaryChartRef.value)

  const categories = ['居民', '志愿者', '流动人口', '活动', '工单', '通知', '待处理隐患']
  const values = [
    Number(overview.residentCount || 0),
    Number(overview.volunteerCount || 0),
    Number(overview.floatingCount || 0),
    Number(overview.activityCount || 0),
    Number(overview.totalOrderCount || 0),
    Number(overview.noticeCount || 0),
    Number(overview.pendingHazardCount || 0)
  ]

  summaryChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: categories, axisLine: { lineStyle: { color: '#ddd' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eee' } } },
    grid: { left: '4%', right: '3%', bottom: '10%', top: '10%', containLabel: true },
    series: [
      {
        type: 'bar',
        data: values,
        barWidth: '40%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: (params) => ['#409eff', '#67c23a', '#00c2ff', '#e6a23c', '#f56c6c', '#909399', '#a66bff'][params.dataIndex]
        }
      }
    ]
  })
}

const handleResize = () => {
  workOrderTypeChart?.resize()
  workOrderMonthChart?.resize()
  activityTypeChart?.resize()
  summaryChart?.resize()
}

const loadDashboard = async () => {
  const [overviewRes, workorderRes, activityRes] = await Promise.all([
    statisticsApi.overview(),
    statisticsApi.workorder(),
    statisticsApi.activity()
  ])

  const overview = overviewRes?.data || {}
  stats.value = {
    residentCount: Number(overview.residentCount || 0),
    activityCount: Number(overview.activityCount || 0),
    pendingOrderCount: Number(overview.pendingOrderCount || 0),
    overtimeOrderCount: Number(overview.overtimeOrderCount || 0),
    todayTodoOrderCount: Number(overview.todayTodoOrderCount || 0),
    orderCompletionRate: calcCompletionRate(overview)
  }

  await nextTick()
  initWorkOrderTypeChart(workorderRes?.data?.byType || [])
  initWorkOrderMonthChart(workorderRes?.data?.byMonth || [])
  initActivityTypeChart(activityRes?.data?.byType || [])
  initSummaryChart(overview)
}

onMounted(async () => {
  try {
    await loadDashboard()
  } finally {
    window.addEventListener('resize', handleResize)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  workOrderTypeChart?.dispose()
  workOrderMonthChart?.dispose()
  activityTypeChart?.dispose()
  summaryChart?.dispose()
})
</script>

<style scoped>
.home {
  padding: 0;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 60px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
}

.stat-icon.blue {
  background: #409eff;
}

.stat-icon.green {
  background: #67c23a;
}

.stat-icon.orange {
  background: #e6a23c;
}

.stat-icon.red {
  background: #f56c6c;
}

.stat-icon.yellow {
  background: #d9a404;
}

.stat-icon.purple {
  background: #7a60ff;
}

.stat-info {
  margin-left: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-box {
  width: 100%;
  height: 280px;
}
</style>
