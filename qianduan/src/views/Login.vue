<template>
  <div class="login-container" :style="containerStyle">
    <div class="login-mask"></div>
    <div class="login-box">
      <div class="login-header">
        <h2>社区综合治理服务系统</h2>
        <p>欢迎登录智慧社区服务平台</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="submit-btn">登录</el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi, userApi } from '../api'
import { useUserStore } from '../stores/user'
import backgroundImage from '../public/bk.png'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const containerStyle = computed(() => ({
  backgroundImage: `linear-gradient(135deg, rgba(18, 52, 96, 0.46) 0%, rgba(38, 91, 160, 0.36) 45%, rgba(255, 255, 255, 0.12) 100%), url(${backgroundImage})`
}))
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(form.value)
    const loginPermissions = Array.isArray(res.data.permissions) ? res.data.permissions : null
    userStore.setUser(res.data.user, res.data.token, loginPermissions)

    try {
      const infoRes = await userApi.getInfo()
      const infoPermissions = Array.isArray(infoRes.data.permissions) ? infoRes.data.permissions : loginPermissions
      userStore.setUser(infoRes.data.user || res.data.user, res.data.token, infoPermissions)
    } catch (error) {
      userStore.setUser(res.data.user, res.data.token, loginPermissions)
    }

    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #6e87b8;
}
.login-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.18) 0%, rgba(255, 255, 255, 0.06) 100%);
}
.login-box {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  box-shadow: 0 18px 45px rgba(25, 54, 104, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.45);
}
.login-header {
  margin-bottom: 28px;
}
.login-box h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #1f2d3d;
  letter-spacing: 1px;
}
.login-header p {
  text-align: center;
  color: #5f6b7a;
  font-size: 14px;
}
.submit-btn {
  width: 100%;
  height: 42px;
  border-radius: 10px;
}
.register-link {
  text-align: center;
  color: #666;
}
.register-link a {
  color: #409eff;
}
</style>
