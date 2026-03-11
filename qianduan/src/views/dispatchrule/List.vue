<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>网格化自动派单规则</span>
          <el-button v-if="userStore.hasPerm('btn.dispatch.rule.add')" type="primary" @click="openAdd">新增规则</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="mb12">
        <el-form-item label="规则名称">
          <el-input v-model="query.gridName" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="query.orderType" clearable style="width: 140px">
            <el-option label="报修" value="REPAIR" />
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="建议" value="SUGGESTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select v-model="query.enabled" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="gridName" label="规则名称" min-width="160" />
        <el-table-column prop="orderType" label="工单类型" width="120" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">{{ formatPriority(row.priority) }}</template>
        </el-table-column>
        <el-table-column prop="assigneeId" label="责任人" min-width="220">
          <template #default="{ row }">{{ formatAssignee(row.assigneeId) }}</template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled === 1 ? 'success' : 'info'">{{ row.enabled === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('btn.dispatch.rule.edit')" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('btn.dispatch.rule.delete')" type="danger" link @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 16px"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑派单规则' : '新增派单规则'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="规则名称">
          <el-input v-model="form.gridName" maxlength="100" />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="form.orderType" clearable>
            <el-option label="报修" value="REPAIR" />
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="建议" value="SUGGESTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" clearable>
            <el-option label="低" :value="0" />
            <el-option label="中" :value="1" />
            <el-option label="高" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="责任人">
          <el-select v-model="form.assigneeId" filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="`${u.realName || u.username}(${u.id})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dispatchRuleApi, userApi } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const userOptions = ref([])
const query = ref({ pageNum: 1, pageSize: 10, gridName: '', orderType: '', enabled: null })

const dialogVisible = ref(false)
const form = ref({
  gridName: '',
  orderType: '',
  priority: null,
  assigneeId: null,
  enabled: 1,
  remark: ''
})

const loadUsers = async () => {
  const res = await userApi.list({ pageNum: 1, pageSize: 500 })
  userOptions.value = res?.data?.records || []
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...query.value,
      gridName: query.value.gridName?.trim() || undefined,
      orderType: query.value.orderType || undefined
    }
    const res = await dispatchRuleApi.list(params)
    tableData.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } finally {
    loading.value = false
  }
}

const formatPriority = (val) => {
  if (val === 0) return '低'
  if (val === 1) return '中'
  if (val === 2) return '高'
  return '通用'
}

const formatAssignee = (assigneeId) => {
  const user = userOptions.value.find((u) => u.id === assigneeId)
  return user ? `${user.realName || user.username}(${assigneeId})` : assigneeId
}

const openAdd = () => {
  form.value = { gridName: '', orderType: '', priority: null, assigneeId: null, enabled: 1, remark: '' }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.assigneeId) {
    ElMessage.warning('请选择责任人')
    return
  }
  if (form.value.id) {
    await dispatchRuleApi.edit(form.value.id, form.value)
  } else {
    await dispatchRuleApi.add(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const remove = async (row) => {
  await ElMessageBox.confirm('确认删除该派单规则吗？', '提示', { type: 'warning' })
  await dispatchRuleApi.del(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const resetQuery = () => {
  query.value = { pageNum: 1, pageSize: 10, gridName: '', orderType: '', enabled: null }
  loadData()
}

onMounted(async () => {
  await Promise.all([loadUsers(), loadData()])
})
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
