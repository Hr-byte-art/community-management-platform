<template>
  <div>
    <el-row :gutter="20" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="metric-label">我的总积分</div>
          <div class="metric-value">{{ account.totalPoints ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="metric-label">积分记录数</div>
          <div class="metric-value">{{ total }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="metric-label">更新时间</div>
          <div class="metric-value small">{{ account.updateTime || '-' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="header-row">
              <span>积分明细</span>
              <el-form :inline="true" :model="query" class="filter-form">
                <el-form-item label="变动类型">
                  <el-select v-model="query.changeType" clearable style="width: 120px">
                    <el-option label="增加" :value="1" />
                    <el-option label="扣减" :value="-1" />
                  </el-select>
                </el-form-item>
                <el-form-item label="业务类型">
                  <el-input v-model="query.businessType" clearable placeholder="如 ACTIVITY_SIGN" style="width: 180px" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadRecords">查询</el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </template>

          <el-table :data="records" v-loading="loading" stripe>
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column label="变动" width="90">
              <template #default="{ row }">
                <el-tag :type="row.changeType === 1 ? 'success' : 'danger'">
                  {{ row.changeType === 1 ? '+' : '-' }}{{ row.points || 0 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="businessType" label="业务类型" width="150" />
            <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
          </el-table>

          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :total="total"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadRecords"
          />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>积分排行榜（Top10）</template>
          <el-table :data="rankList" stripe>
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="realName" label="用户" min-width="120">
              <template #default="{ row }">
                {{ row.realName || row.username || `用户${row.userId ?? '-'}` }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPoints" label="积分" width="90" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { pointsApi } from '../../api'

const loading = ref(false)
const account = ref({ totalPoints: 0, updateTime: '' })
const records = ref([])
const total = ref(0)
const rankList = ref([])
const query = ref({
  pageNum: 1,
  pageSize: 10,
  changeType: null,
  businessType: ''
})

const loadAccount = async () => {
  const res = await pointsApi.my()
  account.value = res?.data || { totalPoints: 0, updateTime: '' }
}

const loadRank = async () => {
  const res = await pointsApi.rank(10)
  rankList.value = Array.isArray(res?.data) ? res.data : []
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      ...query.value,
      businessType: query.value.businessType?.trim() || undefined
    }
    const res = await pointsApi.records(params)
    records.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, changeType: null, businessType: '' }
  loadRecords()
}

onMounted(async () => {
  await Promise.all([loadAccount(), loadRecords(), loadRank()])
})
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
.metric-value.small {
  font-size: 14px;
  font-weight: 500;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.filter-form {
  margin-left: auto;
}
</style>
