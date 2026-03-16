<template>
  <div class="register-container" :style="containerStyle">
    <div class="register-mask"></div>
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>加入社区综合治理服务平台</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn">注册</el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'
import { phoneRule } from '../utils/validation'
import backgroundImage from '../public/bk.png'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '', realName: '', phone: '' })
const containerStyle = computed(() => ({
  backgroundImage: `linear-gradient(135deg, rgba(18, 52, 96, 0.48) 0%, rgba(38, 91, 160, 0.36) 45%, rgba(255, 255, 255, 0.14) 100%), url(${backgroundImage})`
}))
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [phoneRule]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authApi.register(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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
.register-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.16) 0%, rgba(255, 255, 255, 0.04) 100%);
}
.register-box {
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
.register-header {
  margin-bottom: 28px;
}
.register-box h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #1f2d3d;
  letter-spacing: 1px;
}
.register-header p {
  text-align: center;
  color: #5f6b7a;
  font-size: 14px;
}
.submit-btn {
  width: 100%;
  height: 42px;
  border-radius: 10px;
}
.login-link {
  text-align: center;
  color: #666;
}
.login-link a {
  color: #409eff;
}
</style>
