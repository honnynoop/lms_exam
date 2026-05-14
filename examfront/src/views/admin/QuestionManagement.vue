<template>
  <Layout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">문제 관리</h2>
        <router-link to="/admin/questions/create" class="btn btn-primary">
          + 새 문제 만들기
        </router-link>
      </div>
      
      <!-- 필터 -->
      <div class="card">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="label">카테고리</label>
            <select v-model="filter.categoryId" @change="loadQuestions" class="input">
              <option value="">전체</option>
              <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                {{ cat.categoryName }}
              </option>
            </select>
          </div>
          
          <div>
            <label class="label">난이도</label>
            <select v-model="filter.difficulty" @change="loadQuestions" class="input">
              <option value="">전체</option>
              <option value="EASY">쉬움</option>
              <option value="MEDIUM">보통</option>
              <option value="HARD">어려움</option>
            </select>
          </div>
          
          <div>
            <label class="label">문제 유형</label>
            <select v-model="filter.questionType" @change="loadQuestions" class="input">
              <option value="">전체</option>
              <option value="MULTIPLE_CHOICE">객관식</option>
              <option value="TRUE_FALSE">참/거짓</option>
              <option value="SHORT_ANSWER">주관식</option>
            </select>
          </div>
        </div>
      </div>
      
      <!-- 로딩 -->
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- 문제 목록 -->
      <div v-else class="space-y-4">
        <div v-for="q in questions" :key="q.questionId" class="card hover:shadow-lg transition-shadow">
          <div class="flex justify-between items-start mb-3">
            <div class="flex-1">
              <div class="flex items-center space-x-2 mb-2">
                <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
                  {{ q.categoryName }}
                </span>
                <span class="px-2 py-1 text-xs rounded-full" 
                      :class="getDifficultyClass(q.difficulty)">
                  {{ getDifficultyText(q.difficulty) }}
                </span>
                <span class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800">
                  {{ q.points }}점
                </span>
              </div>
              
              <h3 class="text-lg font-medium text-gray-900 mb-2">{{ q.questionText }}</h3>
              
              <div v-if="q.options && q.options.length > 0" class="space-y-1">
                <div v-for="(opt, idx) in q.options" :key="opt.optionId" 
                     class="text-sm flex items-center">
                  <span class="w-6">{{ idx + 1 }}.</span>
                  <span :class="opt.isCorrect ? 'text-green-600 font-medium' : 'text-gray-600'">
                    {{ opt.optionText }}
                    <span v-if="opt.isCorrect" class="ml-2">✓ 정답</span>
                  </span>
                </div>
              </div>
              
              <div v-if="q.explanation" class="mt-3 p-3 bg-yellow-50 rounded-lg">
                <p class="text-sm text-gray-700"><strong>해설:</strong> {{ q.explanation }}</p>
              </div>
            </div>
            
            <div class="flex space-x-2 ml-4">
              <button @click="viewQuestion(q)" class="btn btn-secondary text-sm py-1 px-3">
                상세
              </button>
              <button @click="deleteQuestion(q.questionId)" class="btn btn-danger text-sm py-1 px-3">
                삭제
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && questions.length === 0" class="card text-center py-12 text-gray-500">
        등록된 문제가 없습니다
      </div>
      
      <!-- 페이징 -->
      <div v-if="totalPages > 1" class="flex justify-center space-x-2">
        <button v-for="p in totalPages" :key="p" 
                @click="currentPage = p; loadQuestions()"
                class="px-4 py-2 rounded-md"
                :class="p === currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
          {{ p }}
        </button>
      </div>
      
      <!-- 문제 상세 모달 -->
      <Modal :show="showDetailModal" title="문제 상세" @close="showDetailModal = false">
        <div v-if="selectedQuestion" class="space-y-4">
          <div>
            <strong>카테고리:</strong> {{ selectedQuestion.categoryName }}
          </div>
          <div>
            <strong>문제:</strong> {{ selectedQuestion.questionText }}
          </div>
          <div>
            <strong>유형:</strong> {{ getQuestionTypeText(selectedQuestion.questionType) }}
          </div>
          <div>
            <strong>난이도:</strong> {{ getDifficultyText(selectedQuestion.difficulty) }}
          </div>
          <div>
            <strong>배점:</strong> {{ selectedQuestion.points }}점
          </div>
          <div v-if="selectedQuestion.explanation">
            <strong>해설:</strong> {{ selectedQuestion.explanation }}
          </div>
        </div>
      </Modal>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../../components/Layout.vue'
import Modal from '../../components/Modal.vue'
import { categoryAPI } from '../../api/category'
import { questionAPI } from '../../api/question'

const categories = ref([])
const questions = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const showDetailModal = ref(false)
const selectedQuestion = ref(null)
const loading = ref(false)

const filter = ref({
  categoryId: '',
  difficulty: '',
  questionType: ''
})

const loadCategories = async () => {
  try {
    const response = await categoryAPI.getAll()
    categories.value = response.data.data
  } catch (error) {
    console.error('카테고리 조회 실패:', error)
  }
}

const loadQuestions = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: 20
    }
    
    const response = await questionAPI.getAll(params)
    const data = response.data.data
    
    if (data && data.content) {
      questions.value = data.content
      totalPages.value = data.totalPages || 1
    } else {
      questions.value = []
      totalPages.value = 1
    }
  } catch (error) {
    console.error('문제 조회 실패:', error)
    alert('문제 목록을 불러올 수 없습니다: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const deleteQuestion = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  
  try {
    await questionAPI.delete(id)
    alert('문제가 삭제되었습니다')
    loadQuestions()
  } catch (error) {
    alert('삭제 실패: ' + (error.response?.data?.message || error.message))
  }
}

const viewQuestion = (question) => {
  selectedQuestion.value = question
  showDetailModal.value = true
}

const getDifficultyClass = (difficulty) => {
  const classes = {
    'EASY': 'bg-green-100 text-green-800',
    'MEDIUM': 'bg-yellow-100 text-yellow-800',
    'HARD': 'bg-red-100 text-red-800'
  }
  return classes[difficulty] || 'bg-gray-100 text-gray-800'
}

const getDifficultyText = (difficulty) => {
  const texts = {
    'EASY': '쉬움',
    'MEDIUM': '보통',
    'HARD': '어려움'
  }
  return texts[difficulty] || difficulty
}

const getQuestionTypeText = (type) => {
  const texts = {
    'MULTIPLE_CHOICE': '객관식',
    'TRUE_FALSE': '참/거짓',
    'SHORT_ANSWER': '주관식'
  }
  return texts[type] || type
}

onMounted(() => {
  loadCategories()
  loadQuestions()
})
</script>
