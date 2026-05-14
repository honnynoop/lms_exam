<!-- 위치: frontend/src/views/user/ExamResultDetail.vue -->

<template>
  <Layout>
    <div class="max-w-5xl mx-auto space-y-6">
      <!-- 로딩 -->
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- 결과 표시 -->
      <div v-else-if="result">
        <!-- 헤더 -->
        <div class="flex justify-between items-center">
          <h2 class="text-2xl font-bold text-gray-900">{{ result.examTitle }}</h2>
          <router-link to="/user/my-exams" class="btn btn-secondary">
            목록으로
          </router-link>
        </div>
        
        <!-- 점수 카드 -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <!-- 총 점수 -->
          <div class="card text-center"
               :class="result.score >= result.passingScore ? 'bg-gradient-to-br from-green-500 to-green-600' : 'bg-gradient-to-br from-red-500 to-red-600'">
            <div class="text-white">
              <div class="text-sm opacity-90">총 점수</div>
              <div class="text-4xl font-bold mt-2">{{ result.score }}</div>
              <div class="text-sm mt-1">/ {{ exam.totalPoints }}점</div>
            </div>
          </div>
          
          <!-- 정답 수 -->
          <div class="card bg-gradient-to-br from-blue-500 to-blue-600 text-white text-center">
            <div class="text-sm opacity-90">정답</div>
            <div class="text-4xl font-bold mt-2">{{ result.correctAnswers }}</div>
            <div class="text-sm mt-1">개</div>
          </div>
          
          <!-- 오답 수 -->
          <div class="card bg-gradient-to-br from-orange-500 to-orange-600 text-white text-center">
            <div class="text-sm opacity-90">오답</div>
            <div class="text-4xl font-bold mt-2">{{ result.wrongAnswers }}</div>
            <div class="text-sm mt-1">개</div>
          </div>
          
          <!-- 정답률 -->
          <div class="card bg-gradient-to-br from-purple-500 to-purple-600 text-white text-center">
            <div class="text-sm opacity-90">정답률</div>
            <div class="text-4xl font-bold mt-2">{{ correctRate }}%</div>
            <div class="text-sm mt-1">{{ result.correctAnswers }}/{{ result.totalQuestions }}</div>
          </div>
        </div>
        
        <!-- 합격/불합격 -->
        <div class="card text-center py-6"
             :class="result.score >= result.passingScore ? 'bg-green-50 border-2 border-green-500' : 'bg-red-50 border-2 border-red-500'">
          <div class="text-4xl mb-2">
            {{ result.score >= result.passingScore ? '🎉' : '😢' }}
          </div>
          <div class="text-2xl font-bold"
               :class="result.score >= result.passingScore ? 'text-green-700' : 'text-red-700'">
            {{ result.score >= result.passingScore ? '합격' : '불합격' }}
          </div>
          <div class="text-sm text-gray-600 mt-2">
            합격 기준: {{ result.passingScore }}점
          </div>
        </div>
        
        <!-- 시험 정보 -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-4">시험 정보</h3>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
            <div>
              <span class="text-gray-600">카테고리:</span>
              <span class="ml-2 font-medium">{{ result.categoryName }}</span>
            </div>
            <div>
              <span class="text-gray-600">시작 시간:</span>
              <span class="ml-2 font-medium">{{ formatDateTime(result.startedAt) }}</span>
            </div>
            <div>
              <span class="text-gray-600">제출 시간:</span>
              <span class="ml-2 font-medium">{{ formatDateTime(result.submittedAt) }}</span>
            </div>
            <div>
              <span class="text-gray-600">소요 시간:</span>
              <span class="ml-2 font-medium">{{ getDuration(result.startedAt, result.submittedAt) }}</span>
            </div>
            <div>
              <span class="text-gray-600">총 문제 수:</span>
              <span class="ml-2 font-medium">{{ result.totalQuestions }}개</span>
            </div>
            <div>
              <span class="text-gray-600">총 배점:</span>
              <span class="ml-2 font-medium">{{ exam.totalPoints }}점</span>
            </div>
          </div>
        </div>
        
        <!-- 문제별 결과 -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-4">문제별 결과</h3>
          
          <div class="space-y-6">
            <div v-for="(answer, idx) in result.answers" :key="answer.answerId" 
                 class="border rounded-lg p-6"
                 :class="answer.isCorrect ? 'border-green-300 bg-green-50' : 'border-red-300 bg-red-50'">
              
              <!-- 문제 헤더 -->
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-3">
                  <span class="text-lg font-bold text-gray-900">문제 {{ idx + 1 }}</span>
                  <span class="px-3 py-1 text-sm rounded-full"
                        :class="answer.isCorrect ? 'bg-green-600 text-white' : 'bg-red-600 text-white'">
                    {{ answer.isCorrect ? '✓ 정답' : '✗ 오답' }}
                  </span>
                  <span class="text-sm text-gray-600">{{ answer.points }}점</span>
                </div>
              </div>
              
              <!-- 문제 내용 -->
              <div class="mb-4">
                <p class="text-gray-900 font-medium mb-3">{{ answer.questionText }}</p>
                
                <!-- 선택지 -->
                <div v-if="answer.options && answer.options.length > 0" class="space-y-2">
                  <div v-for="(opt, optIdx) in answer.options" :key="opt.optionId"
                       class="p-3 rounded-lg border"
                       :class="{
                         'bg-green-100 border-green-500': opt.isCorrect,
                         'bg-red-100 border-red-500': !opt.isCorrect && opt.optionId === answer.selectedOptionId,
                         'bg-white border-gray-200': !opt.isCorrect && opt.optionId !== answer.selectedOptionId
                       }">
                    <div class="flex items-center">
                      <span class="w-8 text-gray-600">{{ optIdx + 1 }}.</span>
                      <span class="flex-1">{{ opt.optionText }}</span>
                      
                      <!-- 정답 표시 -->
                      <span v-if="opt.isCorrect" class="text-green-600 font-medium ml-2">
                        ✓ 정답
                      </span>
                      
                      <!-- 내가 선택한 답 -->
                      <span v-if="opt.optionId === answer.selectedOptionId && !opt.isCorrect" 
                            class="text-red-600 font-medium ml-2">
                        ✗ 내 답
                      </span>
                      
                      <span v-if="opt.optionId === answer.selectedOptionId && opt.isCorrect" 
                            class="text-green-600 font-medium ml-2">
                        ✓ 내 답
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 해설 -->
              <div v-if="answer.explanation" class="mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p class="text-sm font-medium text-yellow-900 mb-2">💡 해설</p>
                <p class="text-sm text-gray-700">{{ answer.explanation }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 에러 -->
      <div v-else-if="error" class="card text-center py-12">
        <p class="text-red-600 mb-4">{{ error }}</p>
        <router-link to="/user/my-exams" class="btn btn-primary">
          목록으로 돌아가기
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
import { examAPI } from '../../api/exam'

const route = useRoute()
const router = useRouter()

const result = ref(null)
const exam = ref(null)
const loading = ref(false)
const error = ref('')

const correctRate = computed(() => {
  if (!result.value) return 0
  return Math.round((result.value.correctAnswers / result.value.totalQuestions) * 100)
})

const loadResult = async () => {
  loading.value = true
  try {
    const userExamId = route.params.userExamId
    const response = await userExamAPI.getResult(userExamId)
    result.value = response.data.data
    
    // 시험 정보 조회
    const examResponse = await examAPI.getById(result.value.examId)
    exam.value = examResponse.data.data
  } catch (err) {
    console.error('결과 조회 실패:', err)
    error.value = err.response?.data?.message || '결과를 불러올 수 없습니다'
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getDuration = (start, end) => {
  if (!start || !end) return '-'
  const startDate = new Date(start)
  const endDate = new Date(end)
  const diff = Math.floor((endDate - startDate) / 1000 / 60)
  return `${diff}분`
}

onMounted(() => {
  loadResult()
})
</script>
