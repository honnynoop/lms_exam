<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600">
    <div class="max-w-md w-full mx-4">
      <div class="bg-white rounded-2xl shadow-2xl p-8">
        <div class="text-center mb-8">
          <div class="text-5xl mb-4">🎓</div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">LMS 시험 관리</h2>
          <p class="text-gray-600">Learning Management System</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label class="label">사용자명</label>
            <input v-model="form.username" type="text" required 
                   class="input" placeholder="사용자명을 입력하세요">
          </div>

          <div>
            <label class="label">비밀번호</label>
            <input v-model="form.password" type="password" required 
                   class="input" placeholder="비밀번호를 입력하세요">
          </div>

          <button type="submit" :disabled="loading" 
                  class="w-full btn btn-primary py-3 text-lg"
                  :class="{'opacity-50 cursor-not-allowed': loading}">
            {{ loading ? '로그인 중...' : '로그인' }}
          </button>
        </form>

        <div class="mt-8 pt-6 border-t border-gray-200">
          <div class="space-y-2 text-sm">
            <!-- 회원가입 링크 -->
            <div class="register-link">
              계정이 없으신가요? 
              <router-link to="/register">회원가입</router-link>
            </div>
          </div>
        </div>
      </div>
      
      <p class="text-center text-white text-sm mt-6">
        © 2024 LMS Exam System. All rights reserved.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store'
import { authAPI } from '../api/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  
  try {
    const response = await authAPI.login(form.value)
    const data = response.data.data
    
    // 토큰과 사용자 정보 저장
    authStore.setToken(data.accessToken)
    localStorage.setItem('role', data.role)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    
    // 역할에 따라 리다이렉트
    if (data.role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else {
      router.push('/user/exams')
    }
  } catch (error) {
    alert('로그인 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>
