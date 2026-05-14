<template>
  <Layout>
    <div v-if="!examStarted" class="max-w-2xl mx-auto card">
      <h2 class="text-2xl font-bold mb-4">{{ exam.examTitle }}</h2>
      <div class="space-y-3 mb-6 text-gray-700">
        <p><strong>카테고리:</strong> {{ exam.categoryName }}</p>
        <p><strong>문제 수:</strong> {{ exam.totalQuestions }}개</p>
        <p><strong>총 배점:</strong> {{ exam.totalPoints }}점</p>
        <p><strong>시험 시간:</strong> {{ exam.durationMinutes }}분</p>
        <p><strong>합격 점수:</strong> {{ exam.passingScore }}점</p>
        <p v-if="exam.description" class="pt-3 border-t">{{ exam.description }}</p>
      </div>
      
      <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
        <p class="text-sm text-yellow-800">
          ⚠️ 시험을 시작하면 타이머가 시작됩니다. 중간에 나가면 진행 상황이 저장되지 않습니다.
        </p>
      </div>
      
      <button @click="startExam" class="w-full btn btn-primary py-3 text-lg">
        시험 시작하기
      </button>
    </div>
    
    <div v-else class="max-w-4xl mx-auto space-y-6">
      <!-- 타이머 및 진행 상황 -->
      <div class="card bg-gradient-to-r from-blue-500 to-blue-600 text-white">
        <div class="flex justify-between items-center">
          <div>
            <h3 class="text-xl font-bold">{{ exam.examTitle }}</h3>
            <p class="text-blue-100">문제 {{ currentQuestionIndex + 1 }} / {{ questions.length }}</p>
          </div>
          
          <div class="text-center">
            <div class="text-3xl font-bold" :class="timeRemaining < 300 ? 'text-red-200' : ''">
              {{ formatTime(timeRemaining) }}
            </div>
            <div class="text-sm text-blue-100">남은 시간</div>
          </div>
        </div>
        
        <!-- 진행 바 -->
        <div class="mt-4 bg-blue-400 rounded-full h-2">
          <div class="bg-white rounded-full h-2 transition-all" 
               :style="{ width: progressPercentage + '%' }"></div>
        </div>
      </div>
      
      <!-- 문제 표시 -->
      <div class="card" v-if="currentQuestion">
        <div class="mb-6">
          <div class="flex items-center space-x-2 mb-3">
            <span class="px-3 py-1 text-sm rounded-full bg-blue-100 text-blue-800">
              문제 {{ currentQuestionIndex + 1 }}
            </span>
            <span class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-800">
              {{ currentQuestion.points }}점
            </span>
          </div>
          
          <h4 class="text-lg font-medium text-gray-900">
            {{ currentQuestion.questionText }}
          </h4>
        </div>
        
        <!-- 객관식 선택지 -->
        <div v-if="currentQuestion.options && currentQuestion.options.length > 0" 
             class="space-y-3">
          <div v-for="(option, idx) in currentQuestion.options" :key="option.optionId"
               class="border rounded-lg p-4 cursor-pointer transition-all"
               :class="answers[currentQuestionIndex]?.selectedOptionId === option.optionId 
                      ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-blue-300'"
               @click="selectOption(option.optionId)">
            <div class="flex items-center">
              <input type="radio" 
                     :name="'question-' + currentQuestion.questionId" 
                     :value="option.optionId"
                     :checked="answers[currentQuestionIndex]?.selectedOptionId === option.optionId"
                     class="w-5 h-5 text-blue-600 mr-3">
              <span class="flex-1 text-gray-900">{{ option.optionText }}</span>
            </div>
          </div>
        </div>
        
        <!-- 주관식 입력 -->
        <div v-else>
          <textarea v-model="answers[currentQuestionIndex].answerText" 
                    rows="5" 
                    class="input" 
                    placeholder="답안을 입력하세요"></textarea>
        </div>
      </div>
      
      <!-- 네비게이션 버튼 -->
      <div class="flex justify-between items-center">
        <button @click="previousQuestion" 
                :disabled="currentQuestionIndex === 0"
                class="btn btn-secondary"
                :class="{'opacity-50 cursor-not-allowed': currentQuestionIndex === 0}">
          ← 이전 문제
        </button>
        
        <div class="flex space-x-2">
          <button v-for="(q, idx) in questions" :key="idx"
                  @click="goToQuestion(idx)"
                  class="w-10 h-10 rounded-full font-medium"
                  :class="getQuestionButtonClass(idx)">
            {{ idx + 1 }}
          </button>
        </div>
        
        <button v-if="currentQuestionIndex < questions.length - 1"
                @click="nextQuestion" 
                class="btn btn-primary">
          다음 문제 →
        </button>
        
        <button v-else
                @click="submitExam" 
                class="btn btn-success">
          제출하기
        </button>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { examAPI } from '../../api/exam'
import { userExamAPI } from '../../api/userExam'

const route = useRoute()
const router = useRouter()

const exam = ref({})
const questions = ref([])
const examStarted = ref(false)
const currentQuestionIndex = ref(0)
const answers = ref([])
const timeRemaining = ref(0)
const userExamId = ref(null)
let timerInterval = null

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])

const progressPercentage = computed(() => {
  const answered = answers.value.filter(a => a.selectedOptionId || a.answerText).length
  return (answered / questions.value.length) * 100
})

const loadExam = async () => {
  try {
    // 시험 정보 조회
    const examResponse = await examAPI.getById(route.params.examId)
    exam.value = examResponse.data.data
    
    // 시험 문제 조회
    const questionsResponse = await examAPI.getQuestions(route.params.examId)
    questions.value = questionsResponse.data.data || []
    
    // 답안 초기화
    answers.value = questions.value.map(q => ({
      questionId: q.questionId,
      selectedOptionId: null,
      answerText: ''
    }))
  } catch (error) {
    console.error('시험 조회 실패:', error)
    alert('시험을 불러올 수 없습니다: ' + (error.response?.data?.message || error.message))
    router.push('/user/exams')
  }
}

const startExam = async () => {
  try {
    const response = await userExamAPI.start(route.params.examId)
    userExamId.value = response.data.data.userExamId
    examStarted.value = true
    timeRemaining.value = exam.value.durationMinutes * 60
    startTimer()
  } catch (error) {
    alert('시험 시작 실패: ' + (error.response?.data?.message || error.message))
  }
}

const startTimer = () => {
  timerInterval = setInterval(() => {
    timeRemaining.value--
    
    if (timeRemaining.value <= 0) {
      clearInterval(timerInterval)
      alert('시험 시간이 종료되었습니다. 자동 제출됩니다.')
      submitExam()
    }
  }, 1000)
}

const selectOption = (optionId) => {
  answers.value[currentQuestionIndex.value].selectedOptionId = optionId
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

const goToQuestion = (index) => {
  currentQuestionIndex.value = index
}

const getQuestionButtonClass = (idx) => {
  const answer = answers.value[idx]
  const isAnswered = answer.selectedOptionId || answer.answerText
  const isCurrent = idx === currentQuestionIndex.value
  
  if (isCurrent) {
    return 'bg-blue-600 text-white'
  } else if (isAnswered) {
    return 'bg-green-100 text-green-800'
  } else {
    return 'bg-gray-100 text-gray-600'
  }
}

const submitExam = async () => {
  const unanswered = answers.value.filter(a => !a.selectedOptionId && !a.answerText).length
  
  if (unanswered > 0) {
    if (!confirm(`${unanswered}개의 문제가 미응답 상태입니다. 제출하시겠습니까?`)) {
      return
    }
  }
  
  try {
    clearInterval(timerInterval)
    
    const submitData = {
      userExamId: userExamId.value,
      answers: answers.value
    }
    
    await userExamAPI.submit(submitData)
    alert('시험이 제출되었습니다!')
    router.push('/user/my-exams')
  } catch (error) {
    alert('제출 실패: ' + (error.response?.data?.message || error.message))
  }
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

onMounted(() => {
  loadExam()
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
})
</script>
