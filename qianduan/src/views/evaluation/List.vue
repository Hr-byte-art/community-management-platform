<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>服务评价闭环</span>
          <el-button @click="loadAll">刷新</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane label="待评价工单" name="todo">
          <el-form :inline="true" :model="todoQuery" class="mb12">
            <el-form-item label="标题">
              <el-input v-model="todoQuery.title" clearable style="width: 220px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadTodo">查询</el-button>
              <el-button @click="resetTodo">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="todoList" v-loading="loading" stripe>
            <el-table-column prop="title" label="工单标题" min-width="220" />
            <el-table-column prop="orderType" label="类型" width="120" />
            <el-table-column prop="createTime" label="提交时间" width="170" />
            <el-table-column prop="evaluated" label="评价状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.evaluated === 1 ? 'success' : 'warning'">
                  {{ row.evaluated === 1 ? '已评价' : '未评价' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button v-if="userStore.hasPerm('btn.evaluation.submit')" type="primary" link @click="openEvaluation(row)">去评价</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="todoQuery.pageNum"
            v-model:page-size="todoQuery.pageSize"
            :total="todoTotal"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadTodo"
          />
        </el-tab-pane>

        <el-tab-pane label="我的评价" name="mine">
          <el-table :data="myList" v-loading="loading" stripe>
            <el-table-column prop="workOrderTitle" label="工单标题" min-width="220" />
            <el-table-column prop="score" label="评分" width="100">
              <template #default="{ row }">
                <el-rate :model-value="row.score" disabled show-score text-color="#ff9900" />
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
            <el-table-column prop="createTime" label="评价时间" width="170" />
          </el-table>

          <el-pagination
            v-model:current-page="myQuery.pageNum"
            v-model:page-size="myQuery.pageSize"
            :total="myTotal"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadMy"
          />
        </el-tab-pane>

        <el-tab-pane v-if="isAdmin" label="评价统计" name="stats">
          <el-row :gutter="16" class="mb12">
            <el-col :span="6"><el-card>总评价数：{{ stats.total || 0 }}</el-card></el-col>
            <el-col :span="6"><el-card>平均分：{{ stats.avgScore || 0 }}</el-card></el-col>
            <el-col :span="6"><el-card>满意率：{{ stats.satisfactionRate || 0 }}%</el-card></el-col>
            <el-col :span="6"><el-card>5星数：{{ stats.score5 || 0 }}</el-card></el-col>
          </el-row>

          <el-table :data="adminList" v-loading="loading" stripe>
            <el-table-column prop="userId" label="用户ID" width="90" />
            <el-table-column prop="workOrderTitle" label="工单标题" min-width="220" />
            <el-table-column prop="score" label="评分" width="80" />
            <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
            <el-table-column prop="createTime" label="评价时间" width="170" />
          </el-table>

          <el-pagination
            v-model:current-page="adminQuery.pageNum"
            v-model:page-size="adminQuery.pageSize"
            :total="adminTotal"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadAdmin"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" title="工单服务评价" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="工单标题">
          <el-input :model-value="currentWorkOrder?.title" disabled />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="form.score" :max="5" show-score />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.content" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="userStore.hasPerm('btn.evaluation.submit')" type="primary" @click="submitEvaluation">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { evaluationApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.hasAnyPerm(['btn.evaluation.admin_list', 'btn.evaluation.stats']))

const activeTab = ref('todo')
const loading = ref(false)

const todoQuery = ref({ pageNum: 1, pageSize: 10, title: '' })
const todoList = ref([])
const todoTotal = ref(0)

const myQuery = ref({ pageNum: 1, pageSize: 10 })
const myList = ref([])
const myTotal = ref(0)

const adminQuery = ref({ pageNum: 1, pageSize: 10 })
const adminList = ref([])
const adminTotal = ref(0)
const stats = ref({})

const dialogVisible = ref(false)
const currentWorkOrder = ref(null)
const form = ref({ score: 5, content: '' })

const loadTodo = async () => {
  loading.value = true
  try {
    const params = { ...todoQuery.value, title: todoQuery.value.title?.trim() || undefined }
    const res = await evaluationApi.myWorkOrders(params)
    todoList.value = res?.data?.records || []
    todoTotal.value = res?.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadMy = async () => {
  loading.value = true
  try {
    const res = await evaluationApi.my(myQuery.value)
    myList.value = res?.data?.records || []
    myTotal.value = res?.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadAdmin = async () => {
  if (!isAdmin.value) return
  loading.value = true
  try {
    const [listRes, statsRes] = await Promise.all([
      evaluationApi.adminList(adminQuery.value),
      evaluationApi.stats()
    ])
    adminList.value = listRes?.data?.records || []
    adminTotal.value = listRes?.data?.total || 0
    stats.value = statsRes?.data || {}
  } finally {
    loading.value = false
  }
}

const openEvaluation = async (row) => {
  currentWorkOrder.value = row
  form.value = { score: 5, content: '' }
  const res = await evaluationApi.getByWorkOrder(row.id)
  if (res?.data) {
    form.value = {
      score: res.data.score || 5,
      content: res.data.content || ''
    }
  }
  dialogVisible.value = true
}

const submitEvaluation = async () => {
  if (!currentWorkOrder.value?.id) {
    return
  }
  if (!form.value.score) {
    ElMessage.warning('请选择评分')
    return
  }

  await evaluationApi.submit(currentWorkOrder.value.id, form.value)
  ElMessage.success('评价提交成功')
  dialogVisible.value = false
  await Promise.all([loadTodo(), loadMy()])
  if (isAdmin.value) {
    await loadAdmin()
  }
}

const resetTodo = () => {
  todoQuery.value = { pageNum: 1, pageSize: 10, title: '' }
  loadTodo()
}

const onTabChange = () => {
  if (activeTab.value === 'todo') {
    loadTodo()
  } else if (activeTab.value === 'mine') {
    loadMy()
  } else {
    loadAdmin()
  }
}

const loadAll = async () => {
  await Promise.all([loadTodo(), loadMy()])
  if (isAdmin.value) {
    await loadAdmin()
  }
}

onMounted(loadAll)
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mb12 {
  margin-bottom: 12px;
}
</style>

