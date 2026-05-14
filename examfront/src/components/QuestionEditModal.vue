<!-- 위치: frontend/src/components/QuestionEditModal.vue -->

<template>
  <Modal :show="show" title="문제 점수 및 순서 수정" @close="$emit('close')" size="xl">
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>
    
    <div v-else class="space-y-6">
      <div class="p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p class="text-sm text-blue-800">
          💡 문제별 점수와 순서를 수정할 수 있습니다. 수정 후 "저장" 버튼을 눌러주세요.
        </p>
      </div>
      
      <!-- 문제 목록 -->
      <div class="space-y-3">
        <div v-for="(question, idx) in questions" :key="question.questionId" 
             class="border rounded-lg p-4 bg-white">
          <div class="grid grid-cols-12 gap-4 items-center">
            <!-- 순서 -->
            <div class="col-span-1">
              <label class="text-xs text-gray-600">순서</label>
              <input v-model.number="question.order" type="number" min="1" 
                     class="input text-center" :max="questions.length">
            </div>
            
            <!-- 문제 내용 -->
            <div class="col-span-8">
              <div class="flex items-center space-x-2 mb-1">
                <span class="text-sm font-semibold text-blue-600">문제 {{ idx + 1 }}</span>
                <span class="px-2 py-1 text-xs rounded-full" 
                      :class="getDifficultyClass(question.difficulty)">
                  {{ getDifficultyText(question.difficulty) }}
                </span>
              </div>
              <p class="text-sm text-gray-900">{{ question.questionText }}</p>
            </div>
            
            <!-- 점수 -->
            <div class="col-span-2">
              <label class="text-xs text-gray-600">점수</label>
              <input v-model.number="question.points" type="number" min="1" max="100" 
                     class="input text-center font-semibold">
            </div>
            
            <!-- 원래 값 표시 -->
            <div class="col-span-1 text-xs text-gray-500 text-center">
              <div>원래: {{ question.originalPoints }}점</div>
              <div>순서: {{ question.originalOrder }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 총 배점 -->
      <div class="p-4 bg-gray-50 rounded-lg">
        <div class="flex justify-between items-center">
          <span class="font-semibold text-gray-900">총 배점</span>
          <span class="text-2xl font-bold text-blue-600">{{ totalPoints }}점</span>
        </div>
      </div>
      
      <!-- 버튼 -->
      <div class="flex justify-end space-x-3">
        <button @click="$emit('close')" class="btn btn-secondary">
          취소
        </button>
        <button @click="handleSave" :disabled="saving" class="btn btn-primary">
          {{ saving ? '저장 중...' : '저장' }}
        </button>
      </div>
    </div>
  </Modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Modal from './Modal.vue'
import { examAPI } from '../api/exam'

const props = defineProps({
  show: Boolean,
  examId: Number
})

const emit = defineEmits(['close', 'updated'])

const questions = ref([])
const loading = ref(false)
const saving = ref(false)

const totalPoints = computed(() => {
  return questions.value.reduce((sum, q) => sum + (q.points || 0), 0)
})

const loadQuestions = async () => {
  if (!props.examId) return
  
  loading.value = true
  try {
    const response = await examAPI.getQuestions(props.examId)
    const data = response.data.data || []
    
    questions.value = data.map((q, idx) => ({
      questionId: q.questionId,
      questionText: q.questionText,
      difficulty: q.difficulty,
      points: q.points,
      order: idx + 1,
      originalPoints: q.points,
      originalOrder: idx + 1
    }))
  } catch (error) {
    console.error('문제 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  // 순서에 따라 정렬
  const sorted = [...questions.value].sort((a, b) => a.order - b.order)
  
  // 변경된 문제만 추출
  const updates = sorted.map((q, idx) => ({
    questionId: q.questionId,
    points: q.points,
    questionOrder: idx + 1
  }))
  
  saving.value = true
  try {
    await examAPI.updateQuestions(props.examId, updates)
    alert('문제가 수정되었습니다')
    emit('updated')
    emit('close')
  } catch (error) {
    alert('수정 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    saving.value = false
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

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadQuestions()
  }
})
</script>
