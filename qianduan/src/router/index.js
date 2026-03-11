import { ElMessage } from 'element-plus'
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: '首页', permission: 'menu.home' } },
      { path: 'resident', name: 'Resident', component: () => import('../views/resident/List.vue'), meta: { title: '居民管理', permission: 'menu.resident' } },
      { path: 'activity', name: 'Activity', component: () => import('../views/activity/List.vue'), meta: { title: '社区活动', permission: 'menu.activity' } },
      { path: 'workorder', name: 'WorkOrder', component: () => import('../views/workorder/List.vue'), meta: { title: '工单管理', permission: 'menu.workorder' } },
      { path: 'workorder-overtime', name: 'WorkOrderOvertime', component: () => import('../views/dashboard/OvertimeBoard.vue'), meta: { title: '工单超时看板', permission: 'menu.workorder.overtime' } },
      { path: 'volunteer', name: 'Volunteer', component: () => import('../views/volunteer/List.vue'), meta: { title: '志愿者管理', permission: 'menu.volunteer' } },
      { path: 'floating', name: 'Floating', component: () => import('../views/floating/List.vue'), meta: { title: '流动人口', permission: 'menu.floating' } },
      { path: 'hazard', name: 'Hazard', component: () => import('../views/hazard/List.vue'), meta: { title: '治安隐患', permission: 'menu.hazard' } },
      { path: 'guide', name: 'Guide', component: () => import('../views/guide/List.vue'), meta: { title: '办事指南', permission: 'menu.guide' } },
      { path: 'appointment', name: 'Appointment', component: () => import('../views/appointment/List.vue'), meta: { title: '服务预约', permission: 'menu.appointment' } },
      { path: 'neighborhelp', name: 'NeighborHelp', component: () => import('../views/neighborhelp/List.vue'), meta: { title: '邻里互助', permission: 'menu.neighborhelp' } },
      { path: 'points', name: 'PointsCenter', component: () => import('../views/points/Center.vue'), meta: { title: '积分中心', permission: 'menu.points' } },
      { path: 'message', name: 'MessageCenter', component: () => import('../views/message/Center.vue'), meta: { title: '消息中心', permission: 'menu.message' } },
      { path: 'evaluation', name: 'Evaluation', component: () => import('../views/evaluation/List.vue'), meta: { title: '服务评价', permission: 'menu.evaluation' } },
      { path: 'notice', name: 'Notice', component: () => import('../views/notice/List.vue'), meta: { title: '通知公告', permission: 'menu.notice' } },
      { path: 'user', name: 'User', component: () => import('../views/user/List.vue'), meta: { title: '用户管理', permission: 'menu.user' } },
      { path: 'dispatch-rule', name: 'DispatchRule', component: () => import('../views/dispatchrule/List.vue'), meta: { title: '派单规则', permission: 'menu.dispatch.rule' } },
      { path: 'log', name: 'Log', component: () => import('../views/log/List.vue'), meta: { title: '操作日志', permission: 'menu.log' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '个人中心', permission: 'menu.profile' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')

  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
    return
  }

  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
    return
  }

  const requiredPermission = to.meta?.permission
  if (requiredPermission && !userStore.hasPerm(requiredPermission)) {
    ElMessage.warning('无权限访问该页面')
    next('/profile')
    return
  }

  next()
})

export default router
