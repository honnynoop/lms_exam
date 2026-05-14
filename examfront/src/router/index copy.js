import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: () => {
      const role = localStorage.getItem('role')
      return role === 'ADMIN' ? '/admin/dashboard' : '/user/exams'
    }
  },
  
  // 관리자 라우트
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/categories',
    name: 'CategoryManagement',
    component: () => import('../views/admin/CategoryManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/questions',
    name: 'QuestionManagement',
    component: () => import('../views/admin/QuestionManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/questions/create',
    name: 'QuestionCreate',
    component: () => import('../views/admin/QuestionCreate.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/exams',
    name: 'ExamManagement',
    component: () => import('../views/admin/ExamManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/exams/create',
    name: 'ExamCreate',
    component: () => import('../views/admin/ExamCreate.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/exams/users',
    name: 'UserManagement',
    component: () => import('../views/admin/UserManagement.vue')
  },
  // 사용자 라우트
  {
    path: '/user/exams',
    name: 'ExamList',
    component: () => import('../views/user/ExamList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/exam/:examId',
    name: 'TakeExam',
    component: () => import('../views/user/TakeExam.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/result/:userExamId',
    name: 'ExamResult',
    component: () => import('../views/user/ExamResult.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/my-exams',
    name: 'MyExams',
    component: () => import('../views/user/MyExams.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && role !== 'ADMIN') {
    alert('관리자 권한이 필요합니다.')
    next('/')
  } else {
    next()
  }
})

export default router
