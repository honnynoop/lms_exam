// 위치: frontend/src/router/index.js
// 기존 파일에 아래 라우트를 추가하세요

import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  
  // 관리자
  {
    path: '/admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'exams',
        name: 'ExamManagement',
        component: () => import('../views/admin/ExamManagement.vue')
      },
      {
        path: 'exams/create',
        name: 'ExamCreate',
        component: () => import('../views/admin/ExamCreate.vue')
      },
      {
        path: 'questions',
        name: 'QuestionManagement',
        component: () => import('../views/admin/QuestionManagement.vue')
      },
      {
        path: 'questions/create',
        name: 'QuestionCreate',
        component: () => import('../views/admin/QuestionCreate.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('../views/admin/CategoryManagement.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue')
      }
    ]
  },
  
  // 사용자
  {
    path: '/user',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'exams',
        name: 'ExamList',
        component: () => import('../views/user/ExamList.vue')
      },
      {
        path: 'my-exams',
        name: 'MyExams',
        component: () => import('../views/user/MyExams.vue')
      },
      {
        path: 'exam/:examId',
        name: 'TakeExam',
        component: () => import('../views/user/TakeExam.vue')
      },
      {
        path: 'result/:userExamId',
        name: 'ExamResult',
        component: () => import('../views/user/ExamResult.vue')
      },
      // ✅ NEW: 상세 결과 보기
      {
        path: 'result-detail/:userExamId',
        name: 'ExamResultDetail',
        component: () => import('../views/user/ExamResultDetail.vue')
      }
    ]
  },
  
  // 기본 리다이렉트
  {
    path: '/',
    redirect: (to) => {
      const role = localStorage.getItem('role')
      return role === 'ADMIN' ? '/admin/dashboard' : '/user/exams'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && role !== 'ADMIN') {
    next('/user/exams')
  } else {
    next()
  }
})

export default router
