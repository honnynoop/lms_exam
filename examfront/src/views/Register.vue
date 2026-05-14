<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600">
    <div class="max-w-md w-full mx-4">
      <div class="bg-white rounded-2xl shadow-2xl p-8">
        <div class="text-center mb-8">
          <div class="text-5xl mb-4">🎓</div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">회원가입</h2>
          <p class="text-gray-600">LMS 시험 관리 시스템</p>
        </div>
        
        <form @submit.prevent="handleRegister" class="space-y-4">
          <div>
            <label class="label">사용자명 *</label>
            <input v-model="form.username" type="text" required minlength="3"
                   class="input" placeholder="3자 이상">
          </div>

          <div>
            <label class="label">비밀번호 *</label>
            <input v-model="form.password" type="password" required minlength="6"
                   class="input" placeholder="6자 이상">
          </div>
          
          <div>
            <label class="label">비밀번호 확인 *</label>
            <input v-model="form.passwordConfirm" type="password" required
                   class="input" placeholder="비밀번호 재입력">
          </div>

          <div>
            <label class="label">이름 *</label>
            <input v-model="form.fullName" type="text" required 
                   class="input" placeholder="홍길동">
          </div>

          <div>
            <label class="label">이메일 *</label>
            <input v-model="form.email" type="email" required 
                   class="input" placeholder="email@example.com">
          </div>
          
          <div>
            <label class="label">전화번호</label>
            <input v-model="form.phone" type="tel" 
                   class="input" placeholder="010-1234-5678">
          </div>

          <button type="submit" :disabled="loading" 
                  class="w-full btn btn-primary py-3 text-lg mt-6">
            {{ loading ? '가입 중...' : '회원가입' }}
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            이미 계정이 있으신가요?
            <router-link to="/login" class="text-blue-600 hover:underline font-medium">
              로그인
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  passwordConfirm: '',
  fullName: '',
  email: '',
  phone: ''
})

const handleRegister = async () => {
  if (form.value.password !== form.value.passwordConfirm) {
    alert('비밀번호가 일치하지 않습니다')
    return
  }
  
  loading.value = true
  try {
    const { passwordConfirm, ...registerData } = form.value
    await authAPI.register(registerData)
    alert('회원가입이 완료되었습니다! 로그인해주세요.')
    router.push('/login')
  } catch (error) {
    alert('회원가입 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>
