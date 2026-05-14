<template>
  <Modal :show="show" title="문제별 통계" @close="$emit('close')" size="large">
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>
    
    <div v-else-if="statistics.length === 0" class="text-center py-12 text-gray-500">
      통계 데이터가 없습니다
    </div>
    
    <div v-else class="space-y-4">
      <div v-for="(stat, idx) in statistics" :key="stat.questionId" 
           class="border rounded-lg p-4">
        <div class="flex items-start justify-between mb-3">
          <div class="flex-1">
            <div class="flex items-center space-x-2 mb-2">
              <span class="font-bold text-blue-600">문제 {{ idx + 1 }}</span>
              <span class="px-2 py-1 text-xs rounded-full" 
                    :class="getDifficultyClass(stat.difficulty)">
                {{ getDifficultyText(stat.difficulty) }}
              </span>
            </div>
            <p class="text-gray-900 font-medium">{{ stat.questionText }}</p>
          </div>
        </div>
        
        <!-- 통계 정보 -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4">
          <div class="text-center p-3 bg-blue-50 rounded-lg">
            <div class="text-2xl font-bold text-blue-600">{{ stat.totalAttempts || 0 }}</div>
            <div class="text-sm text-gray-600 mt-1">총 응시</div>
          </div>
          
          <div class="text-center p-3 bg-green-50 rounded-lg">
            <div class="text-2xl font-bold text-green-600">{{ stat.correctCount || 0 }}</div>
            <div class="text-sm text-gray-600 mt-1">정답</div>
          </div>
          
          <div class="text-center p-3 bg-red-50 rounded-lg">
            <div class="text-2xl font-bold text-red-600">{{ stat.wrongCount || 0 }}</div>
            <div class="text-sm text-gray-600 mt-1">오답</div>
          </div>
          
          <div class="text-center p-3 bg-purple-50 rounded-lg">
            <div class="text-2xl font-bold text-purple-600">{{ formatRate(stat.correctRate) }}%</div>
            <div class="text-sm text-gray-600 mt-1">정답률</div>
          </div>
        </div>
        
        <!-- 정답률 바 -->
        <div class="mt-4">
          <div class="flex items-center justify-between text-sm mb-1">
            <span class="text-gray-600">정답률</span>
            <span class="font-medium">{{ formatRate(stat.correctRate) }}%</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-3">
            <div class="h-3 rounded-full transition-all"
                 :class="getRateClass(stat.correctRate)"
                 :style="{ width: formatRate(stat.correctRate) + '%' }"></div>
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

const statistics = ref([])
const loading = ref(false)

const loadStatistics = async () => {
  if (!props.examId) return
  
  loading.value = true
  try {
    const response = await examAPI.getStatistics(props.examId)
    statistics.value = response.data.data || []
  } catch (error) {
    console.error('통계 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const formatRate = (rate) => {
  if (rate == null) return 0
  return Math.round(rate * 10) / 10
}

const getRateClass = (rate) => {
  if (rate >= 80) return 'bg-green-500'
  if (rate >= 60) return 'bg-yellow-500'
  return 'bg-red-500'
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
    loadStatistics()
  }
})
</script>
