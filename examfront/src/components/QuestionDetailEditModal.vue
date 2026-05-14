<!-- ============================================ -->
<!-- 파일 위치: frontend/src/components/QuestionDetailEditModal.vue -->
<!-- 설명: 관리자용 문제 상세 수정 모달 -->
<!-- ============================================ -->

<template>
  <Modal :show="show" :title="`문제 수정 #${question?.questionId || ''}`" @close="$emit('close')" size="xl">
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>
    
    <form v-else-if="question" @submit.prevent="handleUpdate" class="space-y-6">
      <!-- 문제 내용 -->
      <div>
        <label class="label">문제 내용 *</label>
        <textarea v-model="form.questionText" rows="4" required
                  class="input" placeholder="문제를 입력하세요"></textarea>
      </div>
      
      <!-- 배점 / 난이도 -->
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="label">배점 *</label>
          <input v-model.number="form.points" type="number" min="1" max="100" required
                 class="input" placeholder="5">
        </div>
        
        <div>
          <label class="label">난이도 *</label>
          <select v-model="form.difficulty" required class="input">
            <option value="EASY">쉬움</option>
            <option value="MEDIUM">보통</option>
            <option value="HARD">어려움</option>
          </select>
        </div>
      </div>
      
      <!-- 선택지 -->
      <div>
        <div class="flex justify-between items-center mb-3">
          <label class="label mb-0">선택지 *</label>
          <button type="button" @click="addOption" class="text-sm text-blue-600 hover:text-blue-800">
            + 선택지 추가
          </button>
        </div>
        
        <div class="space-y-3">
          <div v-for="(option, idx) in form.options" :key="idx" 
               class="flex items-start space-x-3 p-3 border rounded-lg"
               :class="option.isCorrect ? 'border-green-500 bg-green-50' : 'border-gray-200'">
            <!-- 번호 -->
            <div class="w-8 h-10 flex items-center justify-center bg-gray-100 rounded text-gray-600 font-semibold">
              {{ idx + 1 }}
            </div>
            
            <!-- 선택지 내용 -->
            <div class="flex-1">
              <input v-model="option.optionText" type="text" required
                     class="input" placeholder="선택지 내용">
            </div>
            
            <!-- 정답 체크 -->
            <div class="flex items-center space-x-2">
              <input type="checkbox" v-model="option.isCorrect" 
                     :id="`correct-${idx}`"
                     class="w-5 h-5 text-green-600 rounded focus:ring-green-500">
              <label :for="`correct-${idx}`" class="text-sm whitespace-nowrap">
                정답
              </label>
            </div>
            
            <!-- 삭제 버튼 -->
            <button type="button" @click="removeOption(idx)" 
                    v-if="form.options.length > 2"
                    class="text-red-600 hover:text-red-800">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
        
        <p v-if="!hasCorrectAnswer" class="text-sm text-red-600 mt-2">
          ⚠️ 정답을 최소 1개 선택해주세요
        </p>
      </div>
      
      <!-- 해설 -->
      <div>
        <label class="label">해설</label>
        <textarea v-model="form.explanation" rows="4"
                  class="input" placeholder="문제 해설을 입력하세요 (선택사항)"></textarea>
      </div>
      
      <!-- 버튼 -->
      <div class="flex space-x-3 pt-4">
        <button type="button" @click="$emit('close')" class="flex-1 btn btn-secondary">
          취소
        </button>
        <button type="submit" :disabled="updating || !hasCorrectAnswer" 
                class="flex-1 btn btn-primary"
                :class="{'opacity-50 cursor-not-allowed': updating || !hasCorrectAnswer}">
          {{ updating ? '저장 중...' : '저장' }}
        </button>
      </div>
    </form>
  </Modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Modal from './Modal.vue'
import { questionAPI } from '../api/question'

const props = defineProps({
  show: Boolean,
  questionId: Number
})

const emit = defineEmits(['close', 'updated'])

const question = ref(null)
const loading = ref(false)
const updating = ref(false)

const form = ref({
  questionText: '',
  explanation: '',
  points: 5,
  difficulty: 'MEDIUM',
  options: []
})

const hasCorrectAnswer = computed(() => {
  return form.value.options.some(opt => opt.isCorrect)
})

const loadQuestion = async () => {
  if (!props.questionId) return
  
  loading.value = true
  try {
    const response = await questionAPI.getById(props.questionId)
    question.value = response.data.data
    
    // 폼 초기화
    form.value = {
      questionText: question.value.questionText || '',
      explanation: question.value.explanation || '',
      points: question.value.points || 5,
      difficulty: question.value.difficulty || 'MEDIUM',
      options: (question.value.options || []).map(opt => ({
        optionId: opt.optionId,
        optionText: opt.optionText,
        isCorrect: opt.isCorrect
      }))
    }
    
    // 선택지가 없으면 기본 2개 추가
    if (form.value.options.length === 0) {
      form.value.options = [
        { optionText: '', isCorrect: false },
        { optionText: '', isCorrect: false }
      ]
    }
  } catch (error) {
    console.error('문제 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const addOption = () => {
  form.value.options.push({
    optionText: '',
    isCorrect: false
  })
}

const removeOption = (index) => {
  if (form.value.options.length <= 2) {
    alert('최소 2개의 선택지가 필요합니다')
    return
  }
  form.value.options.splice(index, 1)
}

const handleUpdate = async () => {
  if (!hasCorrectAnswer.value) {
    alert('정답을 최소 1개 선택해주세요')
    return
  }
  
  updating.value = true
  try {
    await questionAPI.update(props.questionId, form.value)
    alert('문제가 수정되었습니다')
    emit('updated')
    emit('close')
  } catch (error) {
    alert('수정 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    updating.value = false
  }
}

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadQuestion()
  }
})
</script>
