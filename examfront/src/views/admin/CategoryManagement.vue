<template>
  <Layout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">카테고리 관리</h2>
        <button @click="showModal = true" class="btn btn-primary">
          + 새 카테고리 추가
        </button>
      </div>
      
      <!-- 카테고리 목록 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="category in categories" :key="category.categoryId" 
             class="card hover:shadow-lg transition-shadow">
          <div class="flex justify-between items-start mb-3">
            <h3 class="text-lg font-semibold text-gray-900">{{ category.categoryName }}</h3>
            <span class="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
              {{ category.questionCount || 0 }}문제
            </span>
          </div>
          
          <p class="text-gray-600 text-sm mb-4">{{ category.description }}</p>
          
          <div class="flex space-x-2">
            <button @click="editCategory(category)" 
                    class="flex-1 btn btn-secondary text-sm py-1">
              수정
            </button>
            <button @click="deleteCategory(category.categoryId)" 
                    class="flex-1 btn btn-danger text-sm py-1">
              삭제
            </button>
          </div>
        </div>
      </div>
      
      <!-- 카테고리 추가/수정 모달 -->
      <Modal :show="showModal" :title="editMode ? '카테고리 수정' : '새 카테고리 추가'" 
             @close="closeModal">
        <form @submit.prevent="submitCategory" class="space-y-4">
          <div>
            <label class="label">카테고리 이름</label>
            <input v-model="form.categoryName" type="text" required 
                   class="input" placeholder="예: Java 프로그래밍">
          </div>
          
          <div>
            <label class="label">설명</label>
            <textarea v-model="form.description" rows="3" 
                      class="input" placeholder="카테고리 설명을 입력하세요"></textarea>
          </div>
          
          <div class="flex space-x-3">
            <button type="submit" class="flex-1 btn btn-primary">
              {{ editMode ? '수정' : '추가' }}
            </button>
            <button type="button" @click="closeModal" class="flex-1 btn btn-secondary">
              취소
            </button>
          </div>
        </form>
      </Modal>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../../components/Layout.vue'
import Modal from '../../components/Modal.vue'
import { categoryAPI } from '../../api/category'

const categories = ref([])
const showModal = ref(false)
const editMode = ref(false)
const form = ref({
  categoryId: null,
  categoryName: '',
  description: ''
})

const loadCategories = async () => {
  try {
    const response = await categoryAPI.getAllWithStats()
    categories.value = response.data.data
  } catch (error) {
    alert('카테고리 조회 실패: ' + error.message)
  }
}

const submitCategory = async () => {
  try {
    if (editMode.value) {
      await categoryAPI.update(form.value.categoryId, form.value)
      alert('카테고리가 수정되었습니다')
    } else {
      await categoryAPI.create(form.value)
      alert('카테고리가 추가되었습니다')
    }
    closeModal()
    loadCategories()
  } catch (error) {
    alert('저장 실패: ' + error.message)
  }
}

const editCategory = (category) => {
  editMode.value = true
  form.value = { ...category }
  showModal.value = true
}

const deleteCategory = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  
  try {
    await categoryAPI.delete(id)
    alert('카테고리가 삭제되었습니다')
    loadCategories()
  } catch (error) {
    alert('삭제 실패: ' + error.message)
  }
}

const closeModal = () => {
  showModal.value = false
  editMode.value = false
  form.value = {
    categoryId: null,
    categoryName: '',
    description: ''
  }
}

onMounted(() => {
  loadCategories()
})
</script>
