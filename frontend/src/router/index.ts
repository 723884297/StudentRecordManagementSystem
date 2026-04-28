import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('../layout/AppLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/Dashboard.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: 'students',
          name: 'Students',
          component: () => import('../views/StudentList.vue'),
          meta: { title: '学生管理' }
        },
        {
          path: 'archives',
          name: 'Archives',
          component: () => import('../views/ArchiveList.vue'),
          meta: { title: '档案管理' }
        },
        {
          path: 'colleges',
          name: 'Colleges',
          component: () => import('../views/CollegeList.vue'),
          meta: { title: '学院管理' }
        },
        {
          path: 'majors',
          name: 'Majors',
          component: () => import('../views/MajorList.vue'),
          meta: { title: '专业管理' }
        },
        {
          path: 'classes',
          name: 'Classes',
          component: () => import('../views/ClassList.vue'),
          meta: { title: '班级管理' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('../views/UserList.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'roles',
          name: 'Roles',
          component: () => import('../views/RoleList.vue'),
          meta: { title: '角色管理' }
        },
        {
          path: 'notifications',
          name: 'Notifications',
          component: () => import('../views/NotificationList.vue'),
          meta: { title: '通知中心' }
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token && to.path !== '/login') {
    return '/login'
  }
})

export default router
