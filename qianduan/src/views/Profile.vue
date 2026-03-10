<template>
  <div class="profile">
    <el-card>
      <template #header><span>个人信息</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 500px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top: 20px">
      <template #header><span>修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" style="max-width: 500px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { phoneRule } from '../utils/validation'

const userStore = useUserStore()
const form = ref({})
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const formRef = ref()

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [phoneRule]
}

onMounted(async () => {
  const res = await userApi.getInfo()
  form.value = res.data
})

const handleUpdate = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  await userApi.update(form.value)
  userStore.user.realName = form.value.realName
  userStore.user.phone = form.value.phone
  userStore.user.email = form.value.email
  localStorage.setItem('user', JSON.stringify(userStore.user))
  ElMessage.success('修改成功')
}

const handleUpdatePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    return ElMessage.warning('请填写完整')
  }
  await userApi.updatePassword(pwdForm.value)
  ElMessage.success('密码修改成功')
  pwdForm.value = { oldPassword: '', newPassword: '' }
}
</script>
