<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">社区治理系统</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="resident-menu">
          <template #title>
            <el-icon><User /></el-icon>
            <span>居民管理</span>
          </template>
          <el-menu-item index="/resident">居民信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="affair-menu">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>社区事务</span>
          </template>
          <el-menu-item index="/activity">社区活动</el-menu-item>
          <el-menu-item index="/volunteer">志愿者管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="growth-menu">
          <template #title>
            <el-icon><Service /></el-icon>
            <span>服务成长</span>
          </template>
          <el-menu-item index="/guide">办事指南</el-menu-item>
          <el-menu-item index="/appointment">服务预约</el-menu-item>
          <el-menu-item index="/neighborhelp">邻里互助</el-menu-item>
          <el-menu-item index="/points">积分中心</el-menu-item>
          <el-menu-item index="/message">消息中心</el-menu-item>
          <el-menu-item index="/evaluation">服务评价</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="governance-menu">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>工单治理</span>
          </template>
          <el-menu-item index="/workorder">工单管理</el-menu-item>
          <el-menu-item index="/workorder-overtime">工单超时看板</el-menu-item>
          <el-menu-item index="/dispatch-rule" v-if="userStore.isAdmin()">派单规则</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="security-menu">
          <template #title>
            <el-icon><Warning /></el-icon>
            <span>治安管理</span>
          </template>
          <el-menu-item index="/floating">流动人口</el-menu-item>
          <el-menu-item index="/hazard">治安隐患</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <span>通知公告</span>
        </el-menu-item>

        <el-sub-menu index="system-menu" v-if="userStore.isAdmin()">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/log">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar" />
              <span class="username">{{ userStore.user?.realName || userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
    return
  }

  if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
.header {
  background: #fff;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.username {
  margin-left: 10px;
  color: #333;
}
.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
