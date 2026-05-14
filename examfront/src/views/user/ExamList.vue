<template>
  <Layout>
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-gray-900">시험 목록</h2>
      
      <!-- 카테고리 필터 -->
      <div class="flex space-x-4 overflow-x-auto pb-2">
        <button @click="selectedCategory = null" 
                class="px-4 py-2 rounded-full whitespace-nowrap transition-colors"
                :class="selectedCategory === null ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
          전체
        </button>
        <button v-for="cat in categories" :key="cat.categoryId"
                @click="selectedCategory = cat.categoryId"
                class="px-4 py-2 rounded-full whitespace-nowrap transition-colors"
                :class="selectedCategory === cat.categoryId ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
          {{ cat.categoryName }}
        </button>
      </div>
      
      <!-- 로딩 -->
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- ✅ 공개된 시험 카드 -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="exam in filteredExams" :key="exam.examId" 
             class="card hover:shadow-xl transition-shadow border-2 border-transparent hover:border-blue-500">
          <div class="mb-4">
            <div class="flex items-start justify-between mb-2">
              <h3 class="text-lg font-semibold text-gray-900">{{ exam.examTitle }}</h3>
              
              <!-- 결과 공개 표시 -->
              <span v-if="exam.isPublished" 
                    class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">
                결과공개
              </span>
            </div>
            
            <span class="inline-block px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800 mb-2">
              {{ exam.categoryName }}
            </span>
            
            <p v-if="exam.description" class="text-sm text-gray-600 mb-3">
              {{ exam.description }}
            </p>
          </div>
          
          <div class="space-y-2 text-sm text-gray-600 mb-4">
            <div class="flex items-center">
              <span class="w-20">📝 문제 수:</span>
              <span class="font-medium">{{ exam.totalQuestions }}개</span>
            </div>
            <div class="flex items-center">
              <span class="w-20">📊 총 배점:</span>
              <span class="font-medium">{{ exam.totalPoints }}점</span>
            </div>
            <div class="flex items-center">
              <span class="w-20">⏱ 시험시간:</span>
              <span class="font-medium">{{ exam.durationMinutes }}분</span>
            </div>
            <div class="flex items-center">
              <span class="w-20">✅ 합격점:</span>
              <span class="font-medium">{{ exam.passingScore }}점</span>
            </div>
            <div v-if="exam.examDate" class="flex items-center">
              <span class="w-20">📅 시험일:</span>
              <span class="font-medium">{{ formatDate(exam.examDate) }}</span>
            </div>
          </div>
          
          <!-- ✅ 결과 공개 시 버튼 비활성화 -->
          <button v-if="!exam.isPublished"
                  @click="goToExam(exam)" 
                  class="w-full btn btn-primary">
            시험 시작하기
          </button>
          <button v-else
                  disabled
                  class="w-full btn btn-secondary opacity-50 cursor-not-allowed">
            시험 종료됨
          </button>
        </div>
      </div>
      
      <div v-if="!loading && filteredExams.length === 0" class="card text-center py-12 text-gray-500">
        <p class="text-lg">공개된 시험이 없습니다</p>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { categoryAPI } from '../../api/category'
import { examAPI } from '../../api/exam'

const router = useRouter()
const categories = ref([])
const exams = ref([])
const selectedCategory = ref(null)
const loading = ref(false)

const filteredExams = computed(() => {
  if (selectedCategory.value === null) {
    return exams.value
  }
  return exams.value.filter(exam => exam.categoryId === selectedCategory.value)
})

const loadCategories = async () => {
  try {
    const response = await categoryAPI.getAll()
    categories.value = response.data.data
  } catch (error) {
    console.error('카테고리 조회 실패:', error)
  }
}

// ✅ 공개된 시험만 조회
const loadExams = async () => {
  loading.value = true
  try {
    const response = await examAPI.getActive()
    exams.value = response.data.data || []
  } catch (error) {
    alert('시험 목록 조회 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const goToExam = (exam) => {
  router.push(`/user/exam/${exam.examId}`)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR')
}

onMounted(() => {
  loadCategories()
  loadExams()
})
</script>
