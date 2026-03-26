<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>社区活动</span>
          <el-button v-if="userStore.isAdmin()" type="warning" @click="handleExport">导出Excel</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="活动列表" name="activity">
          <el-form :inline="true" :model="query">
            <el-form-item label="活动名称">
              <el-input v-model="query.title" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item label="活动类型">
              <el-select v-model="query.activityType" placeholder="请选择" clearable style="width: 180px">
                <el-option label="文化" value="CULTURE" />
                <el-option label="体育" value="SPORT" />
                <el-option label="志愿" value="VOLUNTEER" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadData">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
              <el-button v-if="userStore.hasPerm('btn.activity.add')" type="success" @click="handleAdd">发布活动</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="tableData" v-loading="loading" stripe>
            <el-table-column prop="title" label="活动名称" min-width="180" />
            <el-table-column prop="activityType" label="类型" width="90">
              <template #default="{ row }">{{ typeMap[row.activityType] }}</template>
            </el-table-column>
            <el-table-column prop="location" label="地点" width="160" show-overflow-tooltip />
            <el-table-column prop="startTime" label="开始时间" width="170" />
            <el-table-column prop="endTime" label="结束时间" width="170" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row)">{{ getActivityStatus(row) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column v-if="!isAdmin" label="我的报名" width="110">
              <template #default="{ row }">
                <el-tag v-if="getRegistration(row.id)" :type="registrationTagType[getRegistration(row.id).status]">
                  {{ registrationStatusMap[getRegistration(row.id).status] }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column :width="isAdmin ? 220 : 260" label="操作" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleView(row)">查看</el-button>
                <template v-if="isAdmin">
                  <el-button link type="success" @click="openRegistrationDialog(row)">报名列表</el-button>
                  <el-button v-if="userStore.hasPerm('btn.activity.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
                  <el-button v-if="userStore.hasPerm('btn.activity.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
                </template>
                <template v-else>
                  <el-button
                    v-if="canRegister(row)"
                    link
                    type="success"
                    @click="handleRegister(row)"
                  >
                    报名
                  </el-button>
                  <el-button
                    v-if="canCancelRegistration(row)"
                    link
                    type="warning"
                    @click="handleCancelRegistration(getRegistration(row.id))"
                  >
                    取消报名
                  </el-button>
                  <el-button
                    v-if="canCheckin(row)"
                    link
                    type="success"
                    @click="handleCheckin(getRegistration(row.id))"
                  >
                    签到
                  </el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :total="total"
            layout="total, prev, pager, next"
            style="margin-top: 20px"
            @current-change="loadData"
          />
        </el-tab-pane>

        <el-tab-pane v-if="!isAdmin" label="我的报名" name="registration">
          <el-table :data="registrationTable" v-loading="registrationLoading" stripe>
            <el-table-column label="活动名称" min-width="220">
              <template #default="{ row }">{{ getRegistrationActivityTitle(row) }}</template>
            </el-table-column>
            <el-table-column label="活动类型" width="100">
              <template #default="{ row }">{{ getRegistrationActivityType(row) }}</template>
            </el-table-column>
            <el-table-column label="报名状态" width="110">
              <template #default="{ row }">
                <el-tag :type="registrationTagType[row.status]">{{ registrationStatusMap[row.status] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="registerTime" label="报名时间" width="180" />
            <el-table-column prop="checkInTime" label="签到时间" width="180" />
            <el-table-column label="活动状态" width="110">
              <template #default="{ row }">
                <el-tag :type="getRegistrationActivityStatusType(row)">
                  {{ getRegistrationActivityStatus(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleViewActivityById(row.activityId)">查看活动</el-button>
                <el-button
                  v-if="canCancelRegistrationByRecord(row)"
                  link
                  type="warning"
                  @click="handleCancelRegistration(row)"
                >
                  取消报名
                </el-button>
                <el-button
                  v-if="canCheckinByRecord(row)"
                  link
                  type="success"
                  @click="handleCheckin(row)"
                >
                  签到
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="registrationQuery.pageNum"
            v-model:page-size="registrationQuery.pageSize"
            :total="registrationTotal"
            layout="total, prev, pager, next"
            style="margin-top: 20px"
            @current-change="loadMyRegistrations"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称">
          <el-input v-model="form.title" :disabled="isView" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="form.activityType" :disabled="isView">
            <el-option label="文化" value="CULTURE" />
            <el-option label="体育" value="SPORT" />
            <el-option label="志愿" value="VOLUNTEER" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="form.location" :disabled="isView" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="form.startTime" type="datetime" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="form.endTime" type="datetime" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动内容">
          <el-input v-model="form.content" type="textarea" :rows="4" :disabled="isView" />
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUploadField v-model="form.coverImage" :disabled="isView" />
        </el-form-item>
        <el-form-item v-if="isAdmin" label="是否取消">
          <el-select v-model="form.isCancelled" :disabled="isView">
            <el-option label="正常" :value="0" />
            <el-option label="已取消" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isView" label="当前状态">
          <el-tag :type="getStatusType(form)">{{ getActivityStatus(form) }}</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="registrationDialogVisible" :title="registrationDialogTitle" width="760px">
      <el-table :data="activityRegistrationTable" v-loading="activityRegistrationLoading" stripe>
        <el-table-column prop="id" label="报名ID" width="90" />
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">{{ formatRegistrationUser(row.userId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="registrationTagType[row.status]">{{ registrationStatusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="报名时间" width="180" />
        <el-table-column prop="checkInTime" label="签到时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="activityRegistrationQuery.pageNum"
        v-model:page-size="activityRegistrationQuery.pageSize"
        :total="activityRegistrationTotal"
        layout="total, prev, pager, next"
        style="margin-top: 20px"
        @current-change="loadActivityRegistrations"
      />

      <template #footer>
        <el-button @click="registrationDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { activityApi, activityRegistrationApi, exportApi, userApi } from '../../api'
import { useUserStore } from '../../stores/user'
import ImageUploadField from '../../components/ImageUploadField.vue'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin())

const typeMap = {
  CULTURE: '文化',
  SPORT: '体育',
  VOLUNTEER: '志愿',
  OTHER: '其他'
}

const registrationStatusMap = {
  0: '已报名',
  1: '已签到',
  2: '已取消'
}

const registrationTagType = {
  0: 'warning',
  1: 'success',
  2: 'info'
}

const activeTab = ref('activity')
const query = ref({ pageNum: 1, pageSize: 10, title: '', activityType: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('add')
const form = ref({})

const registrationSummaryMap = ref({})
const registrationTable = ref([])
const registrationQuery = ref({ pageNum: 1, pageSize: 10 })
const registrationTotal = ref(0)
const registrationLoading = ref(false)

const activityCache = ref({})
const userMap = ref({})

const registrationDialogVisible = ref(false)
const currentRegistrationActivity = ref(null)
const activityRegistrationQuery = ref({ pageNum: 1, pageSize: 10 })
const activityRegistrationTable = ref([])
const activityRegistrationTotal = ref(0)
const activityRegistrationLoading = ref(false)

const dialogTitle = computed(() => ({ add: '发布活动', edit: '编辑活动', view: '活动详情' }[dialogMode.value]))
const registrationDialogTitle = computed(() => currentRegistrationActivity.value ? `活动报名列表 - ${currentRegistrationActivity.value.title}` : '活动报名列表')
const isView = computed(() => dialogMode.value === 'view')

const getActivityStatus = (row = {}) => {
  if (row.isCancelled === 1) {
    return '已取消'
  }
  if (!row.startTime || !row.endTime) {
    return '未知'
  }

  const now = new Date()
  const startTime = new Date(row.startTime)
  const endTime = new Date(row.endTime)

  if (now < startTime) return '未开始'
  if (now > endTime) return '已结束'
  return '进行中'
}

const getStatusType = (row = {}) => {
  const status = getActivityStatus(row)
  switch (status) {
    case '未开始':
      return 'info'
    case '进行中':
      return 'success'
    case '已取消':
      return 'danger'
    default:
      return ''
  }
}

const rememberActivities = (activities = []) => {
  if (!activities.length) return
  const nextCache = { ...activityCache.value }
  activities.forEach((item) => {
    if (item?.id) {
      nextCache[item.id] = item
    }
  })
  activityCache.value = nextCache
}

const ensureActivityDetails = async (activityIds = []) => {
  const ids = [...new Set(activityIds.filter(Boolean))].filter((id) => !activityCache.value[id])
  if (!ids.length) return

  const details = await Promise.all(ids.map(async (id) => {
    const res = await activityApi.get(id)
    return res?.data || null
  }))
  rememberActivities(details.filter(Boolean))
}

const loadUsers = async () => {
  if (!isAdmin.value) return
  const res = await userApi.list({ pageNum: 1, pageSize: 500 })
  const records = res?.data?.records || []
  userMap.value = records.reduce((map, item) => {
    map[item.id] = item.realName || item.username || `用户${item.id}`
    return map
  }, {})
}

const buildRegistrationSummary = (records = []) => {
  const map = {}
  records.forEach((item) => {
    if (!map[item.activityId]) {
      map[item.activityId] = item
    }
  })
  registrationSummaryMap.value = map
}

const loadRegistrationSummary = async () => {
  if (isAdmin.value) return
  const res = await activityRegistrationApi.my({ pageNum: 1, pageSize: 500 })
  const records = res?.data?.records || []
  buildRegistrationSummary(records)
  await ensureActivityDetails(records.map((item) => item.activityId))
}

const loadMyRegistrations = async () => {
  if (isAdmin.value) return
  registrationLoading.value = true
  try {
    const res = await activityRegistrationApi.my(registrationQuery.value)
    registrationTable.value = res?.data?.records || []
    registrationTotal.value = res?.data?.total || 0
    await ensureActivityDetails(registrationTable.value.map((item) => item.activityId))
  } finally {
    registrationLoading.value = false
  }
}

const loadActivityRegistrations = async () => {
  if (!currentRegistrationActivity.value?.id) return
  activityRegistrationLoading.value = true
  try {
    const res = await activityRegistrationApi.byActivity(currentRegistrationActivity.value.id, activityRegistrationQuery.value)
    activityRegistrationTable.value = res?.data?.records || []
    activityRegistrationTotal.value = res?.data?.total || 0
  } finally {
    activityRegistrationLoading.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await activityApi.list(query.value)
    tableData.value = res?.data?.records || []
    total.value = res?.data?.total || 0
    rememberActivities(tableData.value)
  } finally {
    loading.value = false
  }
}

const getRegistration = (activityId) => registrationSummaryMap.value[activityId] || null

const canRegister = (row) => {
  if (isAdmin.value) return false
  const activityStatus = getActivityStatus(row)
  const registration = getRegistration(row.id)
  return activityStatus !== '已取消'
    && activityStatus !== '已结束'
    && (!registration || registration.status === 2)
}

const canCancelRegistration = (row) => {
  const registration = getRegistration(row.id)
  if (!registration) return false
  return canCancelRegistrationByRecord(registration)
}

const canCancelRegistrationByRecord = (record) => {
  const activity = activityCache.value[record.activityId]
  const activityStatus = getActivityStatus(activity)
  return record.status === 0 && activityStatus !== '已结束' && activityStatus !== '已取消'
}

const canCheckin = (row) => {
  const registration = getRegistration(row.id)
  if (!registration) return false
  return canCheckinByRecord(registration)
}

const canCheckinByRecord = (record) => {
  const activity = activityCache.value[record.activityId]
  return record.status === 0 && getActivityStatus(activity) === '进行中'
}

const getRegistrationActivity = (record) => activityCache.value[record.activityId] || null

const getRegistrationActivityTitle = (record) => {
  const activity = getRegistrationActivity(record)
  return activity?.title || `活动#${record.activityId}`
}

const getRegistrationActivityType = (record) => {
  const activity = getRegistrationActivity(record)
  return activity ? typeMap[activity.activityType] || activity.activityType || '-' : '-'
}

const getRegistrationActivityStatus = (record) => {
  const activity = getRegistrationActivity(record)
  return activity ? getActivityStatus(activity) : '未知'
}

const getRegistrationActivityStatusType = (record) => {
  const activity = getRegistrationActivity(record)
  return activity ? getStatusType(activity) : 'info'
}

const formatRegistrationUser = (userId) => userMap.value[userId] || `用户${userId}`

const handleAdd = () => {
  form.value = { isCancelled: 0, activityType: 'CULTURE' }
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await activityApi.get(row.id)
  form.value = res?.data || row
  rememberActivities([form.value])
  dialogMode.value = 'view'
  dialogVisible.value = true
}

const handleViewActivityById = async (activityId) => {
  const activity = activityCache.value[activityId]
  if (activity) {
    await handleView(activity)
    return
  }
  const res = await activityApi.get(activityId)
  form.value = res?.data || { id: activityId }
  rememberActivities([form.value])
  dialogMode.value = 'view'
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
  await loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该活动？', '提示', { type: 'warning' }).then(async () => {
    await activityApi.del(row.id)
    ElMessage.success('删除成功')
    await loadData()
  })
}

const handleRegister = (row) => {
  ElMessageBox.confirm(`确定报名活动《${row.title}》吗？`, '提示', { type: 'info' }).then(async () => {
    const res = await activityRegistrationApi.add({ activityId: row.id })
    ElMessage.success(res?.message || '报名成功')
    await Promise.all([loadRegistrationSummary(), loadMyRegistrations()])
  })
}

const handleCancelRegistration = (record) => {
  ElMessageBox.confirm('确定取消报名吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await activityRegistrationApi.del(record.id)
    ElMessage.success(res?.message || '取消报名成功')
    await Promise.all([loadRegistrationSummary(), loadMyRegistrations()])
  })
}

const handleCheckin = (record) => {
  ElMessageBox.confirm('确认签到吗？签到后将发放活动积分。', '提示', { type: 'success' }).then(async () => {
    const res = await activityRegistrationApi.checkin(record.id)
    ElMessage.success(res?.message || '签到成功')
    await Promise.all([loadRegistrationSummary(), loadMyRegistrations()])
  })
}

const openRegistrationDialog = async (row) => {
  currentRegistrationActivity.value = row
  activityRegistrationQuery.value = { pageNum: 1, pageSize: 10 }
  registrationDialogVisible.value = true
  await loadActivityRegistrations()
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, title: '', activityType: '' }
  loadData()
}

const handleTabChange = async (name) => {
  if (name === 'registration' && !isAdmin.value) {
    await loadMyRegistrations()
  }
}

const handleExport = () => {
  const link = document.createElement('a')
  link.href = exportApi.activity() + '?token=' + userStore.token
  link.click()
}

onMounted(async () => {
  await loadData()
  if (isAdmin.value) {
    await loadUsers()
  } else {
    await Promise.all([loadRegistrationSummary(), loadMyRegistrations()])
  }
})
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
