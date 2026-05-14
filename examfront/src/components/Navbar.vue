<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex items-center">
          <router-link to="/" class="flex items-center space-x-2">
            <span class="text-2xl">🎓</span>
            <span class="text-xl font-bold text-gray-900">LMS 시험 시스템</span>
          </router-link>
          
          <!-- 네비게이션 메뉴 -->
          <div class="hidden md:ml-10 md:flex md:space-x-4">
            <!-- 관리자 메뉴 -->
            <template v-if="role === 'ADMIN'">
              <router-link to="/admin/dashboard" 
                           class="nav-link"
                           active-class="nav-link-active">
                대시보드
              </router-link>
              <router-link to="/admin/exams" 
                           class="nav-link"
                           active-class="nav-link-active">
                시험 관리
              </router-link>
              <router-link to="/admin/questions" 
                           class="nav-link"
                           active-class="nav-link-active">
                문제 관리
              </router-link>
              <router-link to="/admin/categories" 
                           class="nav-link"
                           active-class="nav-link-active">
                카테고리
              </router-link>
              <!-- ✅ NEW: 사용자 관리 링크 -->
              <router-link to="/admin/users" 
                           class="nav-link"
                           active-class="nav-link-active">
                사용자 관리
              </router-link>
            </template>
            
            <!-- 사용자 메뉴 -->
            <template v-else>
              <router-link to="/user/exams" 
                           class="nav-link"
                           active-class="nav-link-active">
                시험 목록
              </router-link>
              <router-link to="/user/my-exams" 
                           class="nav-link"
                           active-class="nav-link-active">
                내 시험
              </router-link>
            </template>
          </div>
        </div>
        
        <!-- 우측 메뉴 -->
        <div class="flex items-center space-x-4">
          <span class="text-sm text-gray-700">
            {{ username }}
            <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800 ml-2">
              {{ role === 'ADMIN' ? '관리자' : '사용자' }}
            </span>
          </span>
          
          <router-link to="/profile" 
                       class="text-gray-600 hover:text-gray-900 transition">
            👤 프로필
          </router-link>
          
          <button v-if="role === 'ADMIN'"
                  @click="showSwaggerModal = true"
                  class="text-gray-600 hover:text-gray-900 transition">
            📖 Swagger
          </button>
          
          <button @click="logout" class="btn btn-secondary text-sm">
            로그아웃
          </button>
        </div>
      </div>
    </div>
    
    <SwaggerModal :show="showSwaggerModal" @close="showSwaggerModal = false" />
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import SwaggerModal from './SwaggerModal.vue'

const router = useRouter()
const showSwaggerModal = ref(false)

const username = computed(() => localStorage.getItem('username') || '')
const role = computed(() => localStorage.getItem('role') || '')

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.nav-link {
  @apply px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 transition;
}

.nav-link-active {
  @apply bg-blue-100 text-blue-700;
}
</style>