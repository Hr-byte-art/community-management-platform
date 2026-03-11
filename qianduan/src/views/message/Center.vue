<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>消息中心</span>
          <div class="header-actions">
            <el-tag type="warning">未读 {{ unreadCount }}</el-tag>
            <el-button v-if="userStore.hasPerm('btn.message.read_all')" type="primary" plain @click="markAllRead">全部标记已读</el-button>
            <el-button @click="loadCurrent">刷新</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="loadCurrent">
        <el-tab-pane label="我的消息" name="my">
          <el-form :inline="true" :model="myQuery" class="filter-row">
            <el-form-item label="阅读状态">
              <el-select v-model="myQuery.isRead" clearable style="width: 120px">
                <el-option label="未读" :value="0" />
                <el-option label="已读" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="消息类型">
              <el-input v-model="myQuery.messageType" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadMyMessages">查询</el-button>
              <el-button @click="resetMyQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="myList" v-loading="loading" stripe>
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="messageType" label="类型" width="120" />
            <el-table-column prop="createTime" label="发送时间" width="170" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.isRead === 1 ? 'success' : 'danger'">
                  {{ row.isRead === 1 ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button v-if="userStore.hasPerm('btn.message.read')" link type="primary" :disabled="row.isRead === 1" @click="markRead(row)">已读</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="myQuery.pageNum"
            v-model:page-size="myQuery.pageSize"
            :total="myTotal"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadMyMessages"
          />
        </el-tab-pane>

        <el-tab-pane v-if="isAdmin" label="管理列表" name="admin">
          <el-form :inline="true" :model="adminQuery" class="filter-row">
            <el-form-item label="用户ID">
              <el-input-number v-model="adminQuery.userId" :min="1" :controls="false" style="width: 120px" />
            </el-form-item>
            <el-form-item label="阅读状态">
              <el-select v-model="adminQuery.isRead" clearable style="width: 120px">
                <el-option label="未读" :value="0" />
                <el-option label="已读" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="消息类型">
              <el-input v-model="adminQuery.messageType" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadAdminMessages">查询</el-button>
              <el-button @click="resetAdminQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="adminList" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="90" />
            <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
            <el-table-column prop="messageType" label="类型" width="110" />
            <el-table-column prop="isRead" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.isRead === 1 ? 'success' : 'warning'">{{ row.isRead === 1 ? '已读' : '未读' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发送时间" width="170" />
          </el-table>

          <el-pagination
            v-model:current-page="adminQuery.pageNum"
            v-model:page-size="adminQuery.pageSize"
            :total="adminTotal"
            layout="total, prev, pager, next"
            style="margin-top: 16px"
            @current-change="loadAdminMessages"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { messageApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.hasPerm('btn.message.admin_list'))

const loading = ref(false)
const unreadCount = ref(0)
const activeTab = ref('my')

const myList = ref([])
const myTotal = ref(0)
const myQuery = ref({ pageNum: 1, pageSize: 10, isRead: null, messageType: '' })

const adminList = ref([])
const adminTotal = ref(0)
const adminQuery = ref({ pageNum: 1, pageSize: 10, userId: null, isRead: null, messageType: '' })

const loadUnreadCount = async () => {
  const res = await messageApi.unreadCount()
  unreadCount.value = Number(res?.data || 0)
}

const loadMyMessages = async () => {
  loading.value = true
  try {
    const params = {
      ...myQuery.value,
      messageType: myQuery.value.messageType?.trim() || undefined
    }
    const res = await messageApi.my(params)
    myList.value = res?.data?.records || []
    myTotal.value = res?.data?.total || 0
    await loadUnreadCount()
  } finally {
    loading.value = false
  }
}

const loadAdminMessages = async () => {
  if (!isAdmin.value) {
    return
  }
  loading.value = true
  try {
    const params = {
      ...adminQuery.value,
      userId: adminQuery.value.userId || undefined,
      messageType: adminQuery.value.messageType?.trim() || undefined
    }
    const res = await messageApi.adminList(params)
    adminList.value = res?.data?.records || []
    adminTotal.value = res?.data?.total || 0
  } finally {
    loading.value = false
  }
}

const markRead = async (row) => {
  if (!row?.id || row.isRead === 1) {
    return
  }
  await messageApi.read(row.id)
  ElMessage.success('已标记为已读')
  await loadMyMessages()
}

const markAllRead = async () => {
  await messageApi.readAll()
  ElMessage.success('全部消息已标记为已读')
  await loadMyMessages()
}

const resetMyQuery = () => {
  myQuery.value = { pageNum: 1, pageSize: 10, isRead: null, messageType: '' }
  loadMyMessages()
}

const resetAdminQuery = () => {
  adminQuery.value = { pageNum: 1, pageSize: 10, userId: null, isRead: null, messageType: '' }
  loadAdminMessages()
}

const loadCurrent = () => {
  if (activeTab.value === 'admin') {
    loadAdminMessages()
  } else {
    loadMyMessages()
  }
}

onMounted(async () => {
  await loadUnreadCount()
  await loadMyMessages()
})
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.filter-row {
  margin-bottom: 12px;
}
</style>

