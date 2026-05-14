<template>
  <Layout>
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      <p class="mt-4 text-gray-600">결과를 불러오는 중...</p>
    </div>
    
    <div v-else class="max-w-4xl mx-auto space-y-6">
      <!-- 결과 요약 -->
      <div class="card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
        <div class="text-center">
          <h2 class="text-3xl font-bold mb-2">{{ result.examTitle }}</h2>
          <p class="text-blue-100 mb-6">{{ result.categoryName }}</p>
          
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <div class="bg-white/10 rounded-lg p-4">
              <div class="text-4xl font-bold">{{ result.score }}</div>
              <div class="text-sm text-blue-100">획득 점수</div>
            </div>
            
            <div class="bg-white/10 rounded-lg p-4">
              <div class="text-4xl font-bold text-green-300">{{ result.correctAnswers }}</div>
              <div class="text-sm text-blue-100">정답</div>
            </div>
            
            <div class="bg-white/10 rounded-lg p-4">
              <div class="text-4xl font-bold text-red-300">{{ result.wrongAnswers }}</div>
              <div class="text-sm text-blue-100">오답</div>
            </div>
            
            <div class="bg-white/10 rounded-lg p-4">
              <div class="text-4xl font-bold">{{ accuracyRate }}%</div>
              <div class="text-sm text-blue-100">정답률</div>
            </div>
          </div>
          
          <div v-if="result.passingScore" 
               class="text-2xl font-bold py-4 px-8 rounded-full inline-block"
               :class="result.score >= result.passingScore ? 'bg-green-500' : 'bg-red-500'">
            {{ result.score >= result.passingScore ? '✓ 합격' : '✗ 불합격' }}
          </div>
        </div>
      </div>
      
      <!-- 답안 상세 -->
      <div class="space-y-4">
        <h3 class="text-xl font-bold text-gray-900">답안 상세</h3>
        
        <div v-for="(answer, idx) in result.answers" :key="answer.answerId" 
             class="card"
             :class="answer.isCorrect ? 'border-l-4 border-green-500' : 'border-l-4 border-red-500'">
          <div class="flex items-start justify-between mb-3">
            <div class="flex items-center space-x-2">
              <span class="font-bold text-lg">문제 {{ idx + 1 }}</span>
              <span v-if="answer.isCorrect" 
                    class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">
                ✓ 정답
              </span>
              <span v-else 
                    class="px-2 py-1 text-xs rounded-full bg-red-100 text-red-800">
                ✗ 오답
              </span>
              <span class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800">
                {{ answer.points }}점
              </span>
            </div>
          </div>
          
          <p class="text-gray-900 font-medium mb-4">{{ answer.questionText }}</p>
          
          <div class="space-y-2 mb-4">
            <div v-if="answer.selectedOptionText" 
                 class="p-3 rounded-lg"
                 :class="answer.isCorrect ? 'bg-green-50' : 'bg-red-50'">
              <span class="text-sm font-medium" :class="answer.isCorrect ? 'text-green-800' : 'text-red-800'">
                내 답안:
              </span>
              <span class="ml-2 text-gray-900">{{ answer.selectedOptionText }}</span>
            </div>
            
            <div v-if="!answer.isCorrect && answer.correctOptionText" 
                 class="p-3 bg-green-50 rounded-lg">
              <span class="text-sm font-medium text-green-800">정답:</span>
              <span class="ml-2 text-gray-900">{{ answer.correctOptionText }}</span>
            </div>
            
            <div v-if="answer.answerText" 
                 class="p-3 bg-gray-50 rounded-lg">
              <span class="text-sm font-medium text-gray-800">주관식 답안:</span>
              <p class="mt-2 text-gray-900">{{ answer.answerText }}</p>
            </div>
          </div>
          
          <div v-if="answer.explanation" class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p class="text-sm font-medium text-yellow-900 mb-1">💡 해설</p>
            <p class="text-sm text-gray-700">{{ answer.explanation }}</p>
          </div>
        </div>
      </div>
      
      <!-- 버튼 -->
      <div class="flex space-x-4">
        <router-link to="/user/my-exams" class="flex-1 btn btn-secondary text-center">
          내 시험 목록
        </router-link>
        <router-link to="/user/exams" class="flex-1 btn btn-primary text-center">
          다른 시험 보기
        </router-link>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { userExamAPI } from '../../api/userExam'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const result = ref({
  examTitle: '',
  categoryName: '',
  score: 0,
  correctAnswers: 0,
  wrongAnswers: 0,
  totalQuestions: 0,
  passingScore: 60,
  answers: []
})

const accuracyRate = computed(() => {
  if (result.value.totalQuestions === 0) return 0
  return Math.round((result.value.correctAnswers / result.value.totalQuestions) * 100)
})

const loadResult = async () => {
  loading.value = true
  try {
    const response = await userExamAPI.getResult(route.params.userExamId)
    result.value = response.data.data
  } catch (error) {
    console.error('결과 조회 실패:', error)
    if (error.response?.status === 403 || error.response?.data?.message?.includes('공개')) {
      alert('아직 결과가 공개되지 않았습니다.')
    } else {
      alert('결과 조회 실패: ' + (error.response?.data?.message || error.message))
    }
    router.push('/user/my-exams')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadResult()
})
</script>
