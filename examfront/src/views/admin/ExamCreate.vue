<template>
  <Layout>
    <div class="max-w-6xl mx-auto space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">새 시험 만들기</h2>
        <router-link to="/admin/exams" class="btn btn-secondary">
          목록으로
        </router-link>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 시험 기본 정보 -->
        <div class="lg:col-span-1 space-y-6">
          <div class="card sticky top-4">
            <h3 class="text-lg font-semibold mb-4">시험 정보</h3>
            
            <div class="space-y-4">
              <div>
                <label class="label">시험 제목 *</label>
                <input v-model="exam.examTitle" type="text" required 
                       class="input" placeholder="예: Java 기초 테스트">
              </div>
              
              <div>
                <label class="label">카테고리 *</label>
                <select v-model="exam.categoryId" @change="loadQuestions" required class="input">
                  <option value="">선택하세요</option>
                  <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                    {{ cat.categoryName }}
                  </option>
                </select>
              </div>
              
              <div>
                <label class="label">설명</label>
                <textarea v-model="exam.description" rows="3" 
                          class="input" placeholder="시험 설명"></textarea>
              </div>
              
              <div>
                <label class="label">시험 시간 (분)</label>
                <input v-model.number="exam.durationMinutes" type="number" min="1" 
                       class="input" placeholder="60">
              </div>
              
              <div>
                <label class="label">합격 점수</label>
                <input v-model.number="exam.passingScore" type="number" min="0" max="100" 
                       class="input" placeholder="60">
              </div>
              
              <!-- ✅ NEW: 시험 날짜 -->
              <div>
                <label class="label">시험 날짜</label>
                <input v-model="exam.examDate" type="date" 
                       class="input">
              </div>
              
              <div class="pt-4 border-t">
                <div class="text-sm text-gray-600 space-y-1">
                  <div>선택된 문제: <strong>{{ selectedQuestions.length }}개</strong></div>
                  <div>총 배점: <strong>{{ totalPoints }}점</strong></div>
                </div>
              </div>
              
              <button @click="createExam" 
                      :disabled="selectedQuestions.length === 0 || loading" 
                      class="w-full btn btn-primary"
                      :class="{'opacity-50 cursor-not-allowed': selectedQuestions.length === 0 || loading}">
                {{ loading ? '생성 중...' : '시험 생성' }}
              </button>
            </div>
          </div>
        </div>
        
        <!-- 문제 선택 -->
        <div class="lg:col-span-2 card">
          <h3 class="text-lg font-semibold mb-4">문제 선택</h3>
          
          <div v-if="!exam.categoryId" class="text-center py-12 text-gray-500">
            먼저 카테고리를 선택하세요
          </div>
          
          <div v-else-if="loadingQuestions" class="text-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
          
          <div v-else-if="questions.length === 0" class="text-center py-12 text-gray-500">
            해당 카테고리에 문제가 없습니다
          </div>
          
          <div v-else class="space-y-3">
            <div v-for="q in questions" :key="q.questionId" 
                 class="p-4 border rounded-lg cursor-pointer transition-all"
                 :class="isSelected(q.questionId) ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-blue-300'"
                 @click="toggleQuestion(q)">
              <div class="flex items-start">
                <input type="checkbox" 
                       :checked="isSelected(q.questionId)" 
                       class="mt-1 mr-3 w-5 h-5 text-blue-600">
                <div class="flex-1">
                  <div class="flex items-center space-x-2 mb-2">
                    <span class="px-2 py-1 text-xs rounded-full" 
                          :class="getDifficultyClass(q.difficulty)">
                      {{ getDifficultyText(q.difficulty) }}
                    </span>
                    <span class="text-sm text-gray-600">{{ q.points }}점</span>
                  </div>
                  
                  <p class="font-medium text-gray-900">{{ q.questionText }}</p>
                  
                  <div v-if="q.options && q.options.length > 0" class="mt-2 space-y-1">
                    <div v-for="(opt, idx) in q.options" :key="opt.optionId" 
                         class="text-sm text-gray-600">
                      {{ idx + 1 }}. {{ opt.optionText }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { categoryAPI } from '../../api/category'
import { questionAPI } from '../../api/question'
import { examAPI } from '../../api/exam'

const router = useRouter()
const categories = ref([])
const questions = ref([])
const selectedQuestions = ref([])
const loading = ref(false)
const loadingQuestions = ref(false)

const exam = ref({
  examTitle: '',
  categoryId: '',
  description: '',
  durationMinutes: 60,
  passingScore: 60,
  examDate: '',  // ✅ NEW
  questionIds: []
})

const totalPoints = computed(() => {
  return selectedQuestions.value.reduce((sum, q) => sum + (q.points || 0), 0)
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
  if (!exam.value.categoryId) return
  
  loadingQuestions.value = true
  try {
    const params = { page: 1, size: 100 }
    const response = await questionAPI.getAll(params)
    
    const allQuestions = response.data.data.content || []
    questions.value = allQuestions.filter(q => q.categoryId === exam.value.categoryId)
    selectedQuestions.value = []
  } catch (error) {
    alert('문제 조회 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loadingQuestions.value = false
  }
}

const isSelected = (questionId) => {
  return selectedQuestions.value.some(q => q.questionId === questionId)
}

const toggleQuestion = (question) => {
  const index = selectedQuestions.value.findIndex(q => q.questionId === question.questionId)
  if (index > -1) {
    selectedQuestions.value.splice(index, 1)
  } else {
    selectedQuestions.value.push(question)
  }
}

const createExam = async () => {
  if (selectedQuestions.value.length === 0) {
    alert('최소 1개 이상의 문제를 선택해야 합니다')
    return
  }
  
  if (!exam.value.examTitle) {
    alert('시험 제목을 입력하세요')
    return
  }
  
  loading.value = true
  try {
    const data = {
      ...exam.value,
      questionIds: selectedQuestions.value.map(q => q.questionId)
    }
    
    await examAPI.create(data)
    alert('시험이 생성되었습니다!')
    router.push('/admin/exams')
  } catch (error) {
    alert('시험 생성 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  loadCategories()
})
</script>
