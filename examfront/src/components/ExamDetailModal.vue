<template>
  <Modal :show="show" :title="exam?.examTitle || '시험 상세'" @close="$emit('close')" size="large">
    <div v-if="exam" class="space-y-6">
      <!-- 시험 정보 -->
      <div class="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-lg">
        <div>
          <strong class="text-gray-700">카테고리:</strong>
          <span class="ml-2">{{ exam.categoryName }}</span>
        </div>
        <div>
          <strong class="text-gray-700">문제 수:</strong>
          <span class="ml-2">{{ exam.totalQuestions }}개</span>
        </div>
        <div>
          <strong class="text-gray-700">총 배점:</strong>
          <span class="ml-2">{{ exam.totalPoints }}점</span>
        </div>
        <div>
          <strong class="text-gray-700">시험 시간:</strong>
          <span class="ml-2">{{ exam.durationMinutes }}분</span>
        </div>
        <div>
          <strong class="text-gray-700">합격 점수:</strong>
          <span class="ml-2">{{ exam.passingScore }}점</span>
        </div>
        <!-- ✅ 시험 날짜 -->
        <div v-if="exam.examDate">
          <strong class="text-gray-700">시험 날짜:</strong>
          <span class="ml-2">{{ formatDate(exam.examDate) }}</span>
        </div>
        <div>
          <strong class="text-gray-700">시험 공개:</strong>
          <span class="ml-2" :class="exam.isActive ? 'text-blue-600' : 'text-gray-600'">
            {{ exam.isActive ? '공개됨' : '비공개' }}
          </span>
        </div>
        <div>
          <strong class="text-gray-700">결과 공개:</strong>
          <span class="ml-2" :class="exam.isPublished ? 'text-green-600' : 'text-yellow-600'">
            {{ exam.isPublished ? '공개됨' : '비공개' }}
          </span>
        </div>
      </div>
      
      <div v-if="exam.description" class="p-4 bg-blue-50 rounded-lg">
        <strong class="text-gray-700">설명:</strong>
        <p class="mt-2 text-gray-900">{{ exam.description }}</p>
      </div>
      
      <!-- ✅ 문제 리스트 -->
      <div class="border-t pt-4">
        <h3 class="text-lg font-semibold mb-4">문제 목록 ({{ questions.length }}개)</h3>
        
        <div v-if="loadingQuestions" class="text-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
        </div>
        
        <div v-else-if="questions.length === 0" class="text-center py-8 text-gray-500">
          문제가 없습니다
        </div>
        
        <div v-else class="space-y-3">
          <div v-for="(q, idx) in questions" :key="q.questionId" 
               class="border rounded-lg p-4 cursor-pointer hover:bg-gray-50 transition"
               @click="toggleQuestion(idx)">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center space-x-2 mb-2">
                  <span class="font-bold text-blue-600">문제 {{ idx + 1 }}</span>
                  <span class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800">
                    {{ q.points }}점
                  </span>
                  <span class="px-2 py-1 text-xs rounded-full" 
                        :class="getDifficultyClass(q.difficulty)">
                    {{ getDifficultyText(q.difficulty) }}
                  </span>
                </div>
                
                <p class="text-gray-900 font-medium">{{ q.questionText }}</p>
                
                <!-- ✅ 문제 클릭 시 상세 표시 -->
                <div v-if="expandedQuestionIndex === idx" class="mt-4 space-y-3">
                  <!-- 선택지 -->
                  <div v-if="q.options && q.options.length > 0" class="space-y-2">
                    <div v-for="(opt, optIdx) in q.options" :key="opt.optionId"
                         class="p-3 rounded-lg"
                         :class="opt.isCorrect ? 'bg-green-50 border border-green-200' : 'bg-gray-50'">
                      <div class="flex items-center">
                        <span class="w-6 text-gray-600">{{ optIdx + 1 }}.</span>
                        <span class="flex-1">{{ opt.optionText }}</span>
                        <span v-if="opt.isCorrect" class="text-green-600 font-medium ml-2">
                          ✓ 정답
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- ✅ 해설 -->
                  <div v-if="q.explanation" class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <p class="text-sm font-medium text-yellow-900 mb-1">💡 해설</p>
                    <p class="text-sm text-gray-700">{{ q.explanation }}</p>
                  </div>
                </div>
              </div>
              
              <div class="text-gray-400 ml-4">
                {{ expandedQuestionIndex === idx ? '▼' : '▶' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<script setup>
import { ref, watch } from 'vue'
import Modal from './Modal.vue'
import { examAPI } from '../api/exam'

const props = defineProps({
  show: Boolean,
  exam: Object
})

defineEmits(['close'])

const questions = ref([])
const loadingQuestions = ref(false)
const expandedQuestionIndex = ref(null)

const loadQuestions = async () => {
  if (!props.exam?.examId) return
  
  loadingQuestions.value = true
  try {
    const response = await examAPI.getQuestions(props.exam.examId)
    questions.value = response.data.data || []
  } catch (error) {
    console.error('문제 조회 실패:', error)
  } finally {
    loadingQuestions.value = false
  }
}

const toggleQuestion = (index) => {
  expandedQuestionIndex.value = expandedQuestionIndex.value === index ? null : index
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR')
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

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadQuestions()
    expandedQuestionIndex.value = null
  }
})
</script>
