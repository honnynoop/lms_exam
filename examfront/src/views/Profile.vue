<template>
  <Layout>
    <div class="max-w-2xl mx-auto space-y-6">
      <h2 class="text-2xl font-bold text-gray-900">내 프로필</h2>
      
      <div class="card">
        <div v-if="loading" class="text-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        </div>
        
        <form v-else @submit.prevent="handleUpdate" class="space-y-6">
          <div>
            <label class="label">사용자명</label>
            <input v-model="user.username" type="text" disabled
                   class="input bg-gray-100 cursor-not-allowed">
            <p class="text-sm text-gray-500 mt-1">사용자명은 변경할 수 없습니다</p>
          </div>

          <div>
            <label class="label">이름</label>
            <input v-model="form.fullName" type="text" class="input">
          </div>

          <div>
            <label class="label">이메일</label>
            <input v-model="form.email" type="email" class="input">
          </div>
          
          <div>
            <label class="label">전화번호</label>
            <input v-model="form.phone" type="tel" class="input" placeholder="010-1234-5678">
          </div>
          
          <div class="pt-6 border-t">
            <h3 class="text-lg font-semibold mb-4">비밀번호 변경 (선택사항)</h3>
            
            <div class="space-y-4">
              <div>
                <label class="label">현재 비밀번호</label>
                <input v-model="form.currentPassword" type="password" class="input">
              </div>
              
              <div>
                <label class="label">새 비밀번호</label>
                <input v-model="form.newPassword" type="password" minlength="6" class="input">
              </div>
              
              <div>
                <label class="label">새 비밀번호 확인</label>
                <input v-model="form.newPasswordConfirm" type="password" class="input">
              </div>
            </div>
          </div>

          <div class="flex space-x-4 pt-4">
            <button type="submit" :disabled="updating" class="flex-1 btn btn-primary">
              {{ updating ? '수정 중...' : '저장' }}
            </button>
            <router-link to="/" class="flex-1 btn btn-secondary text-center">
              취소
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../components/Layout.vue'
import { authAPI } from '../api/auth'

const loading = ref(false)
const updating = ref(false)
const user = ref({})
const form = ref({
  fullName: '',
  email: '',
  phone: '',
  currentPassword: '',
  newPassword: '',
  newPasswordConfirm: ''
})

const loadProfile = async () => {
  loading.value = true
  try {
    const response = await authAPI.getCurrentUser()
    user.value = response.data.data
    form.value.fullName = user.value.fullName || ''
    form.value.email = user.value.email || ''
    form.value.phone = user.value.phone || ''
  } catch (error) {
    alert('프로필 조회 실패')
  } finally {
    loading.value = false
  }
}

const handleUpdate = async () => {
  if (form.value.currentPassword || form.value.newPassword) {
    if (!form.value.currentPassword || !form.value.newPassword) {
      alert('비밀번호를 변경하려면 현재 비밀번호와 새 비밀번호를 모두 입력해야 합니다')
      return
    }
    if (form.value.newPassword !== form.value.newPasswordConfirm) {
      alert('새 비밀번호가 일치하지 않습니다')
      return
    }
  }
  
  updating.value = true
  try {
    const updateData = {
      fullName: form.value.fullName,
      email: form.value.email,
      phone: form.value.phone
    }
    
    if (form.value.currentPassword) {
      updateData.currentPassword = form.value.currentPassword
      updateData.newPassword = form.value.newPassword
    }
    
    await authAPI.updateProfile(updateData)
    alert('프로필이 수정되었습니다')
    form.value.currentPassword = ''
    form.value.newPassword = ''
    form.value.newPasswordConfirm = ''
    loadProfile()
  } catch (error) {
    alert('수정 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>
