<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="姓名"><el-input v-model="query.name" placeholder="请输入姓名" clearable /></el-form-item>
        <el-form-item label="楼栋"><el-input v-model="query.buildingNo" placeholder="楼栋号" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="在住" :value="1" /><el-option label="已迁出" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="userStore.hasPerm('btn.resident.add')" type="success" @click="handleAdd">新增居民</el-button>
          <el-button v-if="userStore.hasPerm('btn.resident.export')" type="warning" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="residenceType" label="居住类型" width="100">
          <template #default="{ row }">{{ row.residenceType === 'OWN' ? '自有' : '租住' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在住' : '已迁出' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="userStore.hasAnyPerm(['btn.resident.family.add', 'btn.resident.family.delete'])"
              link
              type="success"
              @click="openFamilyDialog(row)"
            >
              家庭关系
            </el-button>
            <el-button v-if="userStore.hasPerm('btn.resident.edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('btn.resident.delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 20px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑居民' : '新增居民'" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio :value="1">男</el-radio><el-radio :value="0">女</el-radio></el-radio-group></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="居住地址"><el-input v-model="form.address" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="楼栋号"><el-input v-model="form.buildingNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="单元号"><el-input v-model="form.unitNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="房间号"><el-input v-model="form.roomNo" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="居住类型"><el-select v-model="form.residenceType"><el-option label="自有" value="OWN" /><el-option label="租住" value="RENT" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="在住" :value="1" /><el-option label="已迁出" :value="0" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="居民照片">
          <ImageUploadField v-model="form.photo" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="familyDialogVisible" :title="`家庭关系 - ${currentResident?.name || ''}`" width="760px">
      <el-form :inline="true" :model="familyForm">
        <el-form-item label="关联居民">
          <el-select v-model="familyForm.relatedResidentId" filterable style="width: 220px">
            <el-option
              v-for="item in familyResidentOptions"
              :key="item.id"
              :label="`${item.name}（${item.idCard || item.id}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关系">
          <el-select v-model="familyForm.relation" style="width: 160px">
            <el-option label="配偶" value="SPOUSE" />
            <el-option label="父母" value="PARENT" />
            <el-option label="子女" value="CHILD" />
            <el-option label="兄弟姐妹" value="SIBLING" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button v-if="userStore.hasPerm('btn.resident.family.add')" type="primary" @click="handleAddFamily">添加关系</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="familyTable" v-loading="familyLoading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="关联居民" min-width="200">
          <template #default="{ row }">{{ formatResidentName(row.relatedResidentId) }}</template>
        </el-table-column>
        <el-table-column label="关系" width="120">
          <template #default="{ row }">{{ relationMap[row.relation] || row.relation || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="userStore.hasPerm('btn.resident.family.delete')"
              link
              type="danger"
              @click="handleDeleteFamily(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="familyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { residentApi, exportApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { idCardRule, phoneRule } from '../../utils/validation'
import ImageUploadField from '../../components/ImageUploadField.vue'

const userStore = useUserStore()
const query = ref({ pageNum: 1, pageSize: 10, name: '', buildingNo: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})
const formRef = ref()
const familyDialogVisible = ref(false)
const familyLoading = ref(false)
const familyTable = ref([])
const currentResident = ref(null)
const residentOptions = ref([])
const familyForm = ref({ relatedResidentId: null, relation: 'SPOUSE' })

const relationMap = {
  SPOUSE: '配偶',
  PARENT: '父母',
  CHILD: '子女',
  SIBLING: '兄弟姐妹'
}

const familyResidentOptions = computed(() => {
  return residentOptions.value.filter((item) => item.id !== currentResident.value?.id)
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  idCard: [idCardRule],
  phone: [phoneRule]
}

const loadData = async () => {
  loading.value = true
  const res = await residentApi.list(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const loadResidentOptions = async () => {
  const res = await residentApi.list({ pageNum: 1, pageSize: 500 })
  residentOptions.value = res?.data?.records || []
}

const handleAdd = () => {
  form.value = { gender: 1, status: 1, residenceType: 'OWN' }
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.value.id) {
      await residentApi.edit(form.value.id, form.value)
    } else {
      await residentApi.add(form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该居民信息？', '提示', { type: 'warning' }).then(async () => {
    await residentApi.del(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleExport = () => {
  const link = document.createElement('a')
  link.href = exportApi.resident() + '?token=' + userStore.token
  link.click()
}

const formatResidentName = (residentId) => {
  const resident = residentOptions.value.find((item) => item.id === residentId)
  return resident ? `${resident.name}${resident.idCard ? `（${resident.idCard}）` : ''}` : `居民${residentId}`
}

const loadFamilyRelations = async () => {
  if (!currentResident.value?.id) return
  familyLoading.value = true
  try {
    const res = await residentApi.getFamily(currentResident.value.id)
    familyTable.value = res?.data || []
  } finally {
    familyLoading.value = false
  }
}

const openFamilyDialog = async (row) => {
  currentResident.value = row
  familyForm.value = { relatedResidentId: null, relation: 'SPOUSE' }
  familyDialogVisible.value = true
  await Promise.all([loadResidentOptions(), loadFamilyRelations()])
}

const handleAddFamily = async () => {
  if (!currentResident.value?.id) return
  if (!familyForm.value.relatedResidentId) {
    ElMessage.warning('请选择关联居民')
    return
  }
  await residentApi.addFamily({
    residentId: currentResident.value.id,
    relatedResidentId: familyForm.value.relatedResidentId,
    relation: familyForm.value.relation
  })
  ElMessage.success('添加成功')
  familyForm.value = { relatedResidentId: null, relation: 'SPOUSE' }
  await loadFamilyRelations()
}

const handleDeleteFamily = (row) => {
  ElMessageBox.confirm('确定删除该家庭关系吗？', '提示', { type: 'warning' }).then(async () => {
    await residentApi.delFamily(row.id)
    ElMessage.success('删除成功')
    await loadFamilyRelations()
  })
}

const handleReset = () => {
  query.value = { pageNum: 1, pageSize: 10, name: '', buildingNo: '', status: null }
  loadData()
}

onMounted(loadData)
</script>
