<!-- 위치: frontend/src/views/admin/UserManagement.vue -->

<template>
  <Layout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">사용자 관리</h2>
        <div class="text-sm text-gray-600">
          총 <strong class="text-blue-600">{{ users.length }}명</strong>의 사용자
        </div>
      </div>
      
      <!-- 필터 -->
      <div class="card">
        <div class="flex space-x-4">
          <button @click="filterRole = null" 
                  class="px-4 py-2 rounded-lg transition"
                  :class="filterRole === null ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
            전체 ({{ users.length }})
          </button>
          <button @click="filterRole = 'ADMIN'" 
                  class="px-4 py-2 rounded-lg transition"
                  :class="filterRole === 'ADMIN' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
            관리자 ({{ adminCount }})
          </button>
          <button @click="filterRole = 'USER'" 
                  class="px-4 py-2 rounded-lg transition"
                  :class="filterRole === 'USER' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
            사용자 ({{ userCount }})
          </button>
        </div>
      </div>
      
      <!-- 로딩 -->
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- 사용자 테이블 -->
      <div v-else class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  사용자
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  이메일
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  전화번호
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  권한
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  상태
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  가입일
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in filteredUsers" :key="user.userId" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center">
                      <span class="text-blue-600 font-semibold">
                        {{ user.fullName?.charAt(0) || user.username?.charAt(0) }}
                      </span>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{ user.fullName }}</div>
                      <div class="text-sm text-gray-500">@{{ user.username }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ user.email }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ user.phone || '-' }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 py-1 text-xs rounded-full"
                        :class="user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'">
                    {{ user.role === 'ADMIN' ? '관리자' : '사용자' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 py-1 text-xs rounded-full"
                        :class="user.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                    {{ user.isActive ? '활성' : '비활성' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ formatDate(user.createdAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                  <!-- 상태 변경 -->
                  <button v-if="user.isActive" 
                          @click="changeStatus(user.userId, false)"
                          :disabled="processing"
                          class="text-yellow-600 hover:text-yellow-900">
                    비활성
                  </button>
                  <button v-else 
                          @click="changeStatus(user.userId, true)"
                          :disabled="processing"
                          class="text-green-600 hover:text-green-900">
                    활성화
                  </button>
                  
                  <!-- 삭제 (관리자는 삭제 불가) -->
                  <button v-if="user.role !== 'ADMIN'" 
                          @click="deleteUser(user.userId)"
                          :disabled="processing"
                          class="text-red-600 hover:text-red-900">
                    삭제
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <div v-if="!loading && filteredUsers.length === 0" class="card text-center py-12 text-gray-500">
        해당 조건의 사용자가 없습니다
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Layout from '../../components/Layout.vue'
import { userAPI } from '../../api/user'

const users = ref([])
const filterRole = ref(null)
const loading = ref(false)
const processing = ref(false)

const filteredUsers = computed(() => {
  if (filterRole.value === null) {
    return users.value
  }
  return users.value.filter(u => u.role === filterRole.value)
})

const adminCount = computed(() => {
  return users.value.filter(u => u.role === 'ADMIN').length
})

const userCount = computed(() => {
  return users.value.filter(u => u.role === 'USER').length
})

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await userAPI.getAll()
    users.value = response.data.data || []
  } catch (error) {
    alert('사용자 목록 조회 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const changeStatus = async (userId, isActive) => {
  const action = isActive ? '활성화' : '비활성화'
  if (!confirm(`정말 ${action}하시겠습니까?`)) return
  
  processing.value = true
  try {
    await userAPI.updateStatus(userId, isActive)
    alert(`사용자가 ${action}되었습니다`)
    loadUsers()
  } catch (error) {
    alert(`상태 변경 실패: ${error.response?.data?.message || error.message}`)
  } finally {
    processing.value = false
  }
}

const deleteUser = async (userId) => {
  if (!confirm('정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return
  
  processing.value = true
  try {
    await userAPI.delete(userId)
    alert('사용자가 삭제되었습니다')
    loadUsers()
  } catch (error) {
    alert(`삭제 실패: ${error.response?.data?.message || error.message}`)
  } finally {
    processing.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR')
}

onMounted(() => {
  loadUsers()
})
</script>
