<!-- ============================================ -->
<!-- 파일 위치: frontend/src/components/UserExamDetailModal.vue -->
<!-- 설명: 사용자용 시험 상세보기 모달 (결과 공개 후) -->
<!-- ============================================ -->

<template>
  <Modal :show="show" :title="`시험 상세: ${exam?.examTitle || ''}`" @close="$emit('close')" size="xl">
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>
    
    <div v-else-if="exam" class="space-y-6">
      <!-- 시험 정보 -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 bg-gray-50 rounded-lg">
        <div>
          <div class="text-sm text-gray-600">카테고리</div>
          <div class="font-semibold text-gray-900">{{ exam.categoryName }}</div>
        </div>
        <div>
          <div class="text-sm text-gray-600">문제 수</div>
          <div class="font-semibold text-gray-900">{{ exam.totalQuestions }}개</div>
        </div>
        <div>
          <div class="text-sm text-gray-600">총 배점</div>
          <div class="font-semibold text-gray-900">{{ exam.totalPoints }}점</div>
        </div>
        <div>
          <div class="text-sm text-gray-600">시험 시간</div>
          <div class="font-semibold text-gray-900">{{ exam.durationMinutes }}분</div>
        </div>
        <div>
          <div class="text-sm text-gray-600">합격 점수</div>
          <div class="font-semibold text-gray-900">{{ exam.passingScore }}점</div>
        </div>
        <div v-if="exam.examDate">
          <div class="text-sm text-gray-600">시험 날짜</div>
          <div class="font-semibold text-gray-900">{{ formatDate(exam.examDate) }}</div>
        </div>
        <div>
          <div class="text-sm text-gray-600">공개 상태</div>
          <span class="px-2 py-1 text-xs rounded-full font-semibold"
                :class="exam.isActive ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800'">
            {{ exam.isActive ? '공개' : '비공개' }}
          </span>
        </div>
        <div>
          <div class="text-sm text-gray-600">결과 공개</div>
          <span class="px-2 py-1 text-xs rounded-full font-semibold"
                :class="exam.isPublished ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">
            {{ exam.isPublished ? '공개됨' : '비공개' }}
          </span>
        </div>
      </div>
      
      <!-- 설명 -->
      <div v-if="exam.description" class="p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p class="text-sm text-blue-900"><strong>설명:</strong> {{ exam.description }}</p>
      </div>
      
      <!-- 문제 목록 -->
      <div class="border-t pt-4">
        <h3 class="text-lg font-semibold mb-4">문제 목록 ({{ questions.length }}개)</h3>
        
        <div v-if="loadingQuestions" class="text-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
        </div>
        
        <div v-else-if="questions.length === 0" class="text-center py-8 text-gray-500">
          문제가 없습니다
        </div>
        
        <div v-else class="space-y-4">
          <div v-for="(q, idx) in questions" :key="q.questionId" 
               class="border rounded-lg p-5 hover:shadow-md transition-shadow cursor-pointer"
               @click="toggleQuestion(idx)">
            
            <!-- 문제 헤더 -->
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center space-x-3 mb-3">
                  <span class="text-lg font-bold text-blue-600">문제 {{ idx + 1 }}</span>
                  <span class="px-3 py-1 text-xs rounded-full" 
                        :class="getDifficultyClass(q.difficulty)">
                    {{ getDifficultyText(q.difficulty) }}
                  </span>
                  <span class="px-3 py-1 text-xs rounded-full bg-gray-100 text-gray-800">
                    {{ q.points }}점
                  </span>
                </div>
                
                <p class="text-gray-900 font-medium mb-3">{{ q.questionText }}</p>
                
                <!-- 펼쳐진 상태 -->
                <div v-if="expandedQuestionIndex === idx" class="mt-4 space-y-3">
                  <!-- 선택지 -->
                  <div v-if="q.options && q.options.length > 0" class="space-y-2">
                    <div v-for="(opt, optIdx) in q.options" :key="opt.optionId"
                         class="p-3 rounded-lg border"
                         :class="opt.isCorrect ? 'bg-green-100 border-green-500' : 'bg-white border-gray-200'">
                      <div class="flex items-center">
                        <span class="w-8 text-gray-600 font-medium">{{ optIdx + 1 }}.</span>
                        <span class="flex-1">{{ opt.optionText }}</span>
                        <span v-if="opt.isCorrect" class="text-green-600 font-semibold ml-3">
                          ✓ 정답
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- 해설 -->
                  <div v-if="q.explanation && exam.isPublished" 
                       class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <p class="text-sm font-semibold text-yellow-900 mb-2">💡 해설</p>
                    <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ q.explanation }}</p>
                  </div>
                  
                  <!-- 결과 비공개 시 -->
                  <div v-if="!exam.isPublished" class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <p class="text-sm text-yellow-800">
                      ⏳ 결과가 공개되면 해설을 확인할 수 있습니다
                    </p>
                  </div>
                </div>
              </div>
              
              <!-- 펼치기 아이콘 -->
              <div class="ml-4 text-gray-400">
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
  examId: Number
})

defineEmits(['close'])

const exam = ref(null)
const questions = ref([])
const loading = ref(false)
const loadingQuestions = ref(false)
const expandedQuestionIndex = ref(null)

const loadExam = async () => {
  if (!props.examId) return
  
  loading.value = true
  try {
    const response = await examAPI.getById(props.examId)
    exam.value = response.data.data
    await loadQuestions()
  } catch (error) {
    console.error('시험 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const loadQuestions = async () => {
  loadingQuestions.value = true
  try {
    const response = await examAPI.getQuestions(props.examId)
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
    loadExam()
    expandedQuestionIndex.value = null
  }
})
</script>
