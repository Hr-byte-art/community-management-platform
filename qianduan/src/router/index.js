import { ElMessage } from 'element-plus'
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import Home from '../views/Home.vue'
import Resident from '../views/resident/List.vue'
import Activity from '../views/activity/List.vue'
import WorkOrder from '../views/workorder/List.vue'
import WorkOrderOvertime from '../views/dashboard/OvertimeBoard.vue'
import Volunteer from '../views/volunteer/List.vue'
import Floating from '../views/floating/List.vue'
import Hazard from '../views/hazard/List.vue'
import Guide from '../views/guide/List.vue'
import Appointment from '../views/appointment/List.vue'
import NeighborHelp from '../views/neighborhelp/List.vue'
import PointsCenter from '../views/points/Center.vue'
import MessageCenter from '../views/message/Center.vue'
import Evaluation from '../views/evaluation/List.vue'
import Notice from '../views/notice/List.vue'
import User from '../views/user/List.vue'
import DispatchRule from '../views/dispatchrule/List.vue'
import Log from '../views/log/List.vue'
import Profile from '../views/Profile.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: Home, meta: { title: '首页', permission: 'menu.home' } },
      { path: 'resident', name: 'Resident', component: Resident, meta: { title: '居民管理', permission: 'menu.resident' } },
      { path: 'activity', name: 'Activity', component: Activity, meta: { title: '社区活动', permission: 'menu.activity' } },
      { path: 'workorder', name: 'WorkOrder', component: WorkOrder, meta: { title: '工单管理', permission: 'menu.workorder' } },
      { path: 'workorder-overtime', name: 'WorkOrderOvertime', component: WorkOrderOvertime, meta: { title: '工单超时看板', permission: 'menu.workorder.overtime' } },
      { path: 'volunteer', name: 'Volunteer', component: Volunteer, meta: { title: '志愿者管理', permission: 'menu.volunteer' } },
      { path: 'floating', name: 'Floating', component: Floating, meta: { title: '流动人口', permission: 'menu.floating' } },
      { path: 'hazard', name: 'Hazard', component: Hazard, meta: { title: '治安隐患', permission: 'menu.hazard' } },
      { path: 'guide', name: 'Guide', component: Guide, meta: { title: '办事指南', permission: 'menu.guide' } },
      { path: 'appointment', name: 'Appointment', component: Appointment, meta: { title: '服务预约', permission: 'menu.appointment' } },
      { path: 'neighborhelp', name: 'NeighborHelp', component: NeighborHelp, meta: { title: '邻里互助', permission: 'menu.neighborhelp' } },
      { path: 'points', name: 'PointsCenter', component: PointsCenter, meta: { title: '积分中心', permission: 'menu.points' } },
      { path: 'message', name: 'MessageCenter', component: MessageCenter, meta: { title: '消息中心', permission: 'menu.message' } },
      { path: 'evaluation', name: 'Evaluation', component: Evaluation, meta: { title: '服务评价', permission: 'menu.evaluation' } },
      { path: 'notice', name: 'Notice', component: Notice, meta: { title: '通知公告', permission: 'menu.notice' } },
      { path: 'user', name: 'User', component: User, meta: { title: '用户管理', permission: 'menu.user' } },
      { path: 'dispatch-rule', name: 'DispatchRule', component: DispatchRule, meta: { title: '派单规则', permission: 'menu.dispatch.rule' } },
      { path: 'log', name: 'Log', component: Log, meta: { title: '操作日志', permission: 'menu.log' } },
      { path: 'profile', name: 'Profile', component: Profile, meta: { title: '个人中心', permission: 'menu.profile' } }
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
