<template>
  <Layout>
    <div class="max-w-4xl mx-auto space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">문제 만들기</h2>
        <router-link to="/admin/questions" class="btn btn-secondary">
          목록으로
        </router-link>
      </div>
      
      <div class="card">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- 카테고리 선택 -->
          <div>
            <label class="label">카테고리 *</label>
            <select v-model="question.categoryId" required class="input">
              <option value="">카테고리를 선택하세요</option>
              <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                {{ cat.categoryName }}
              </option>
            </select>
          </div>
          
          <!-- 문제 내용 -->
          <div>
            <label class="label">문제 내용 *</label>
            <textarea v-model="question.questionText" rows="4" required 
                      class="input" 
                      placeholder="문제를 입력하세요"></textarea>
          </div>
          
          <!-- 문제 유형 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="label">문제 유형 *</label>
              <select v-model="question.questionType" required class="input">
                <option value="MULTIPLE_CHOICE">객관식</option>
                <option value="TRUE_FALSE">참/거짓</option>
                <option value="SHORT_ANSWER">주관식</option>
              </select>
            </div>
            
            <div>
              <label class="label">난이도</label>
              <select v-model="question.difficulty" class="input">
                <option value="EASY">쉬움</option>
                <option value="MEDIUM">보통</option>
                <option value="HARD">어려움</option>
              </select>
            </div>
            
            <div>
              <label class="label">배점</label>
              <input v-model.number="question.points" type="number" min="1" 
                     class="input" placeholder="10">
            </div>
          </div>
          
          <!-- 선택지 (객관식일 때만) -->
          <div v-if="question.questionType === 'MULTIPLE_CHOICE' || question.questionType === 'TRUE_FALSE'">
            <label class="label">선택지 *</label>
            <div class="space-y-3">
              <div v-for="(option, index) in question.options" :key="index" 
                   class="flex items-center space-x-3">
                <input type="radio" :name="'correct'" :value="index" 
                       v-model="correctIndex" 
                       class="w-5 h-5 text-blue-600">
                <input v-model="option.optionText" type="text" required 
                       class="flex-1 input" 
                       :placeholder="'선택지 ' + (index + 1)">
                <span class="text-sm text-gray-500 w-16">
                  {{ index === correctIndex ? '정답' : '' }}
                </span>
              </div>
            </div>
            <p class="text-sm text-gray-500 mt-2">* 라디오 버튼을 선택하여 정답을 지정하세요</p>
          </div>
          
          <!-- 해설 -->
          <div>
            <label class="label">해설</label>
            <textarea v-model="question.explanation" rows="3" 
                      class="input" 
                      placeholder="문제 해설을 입력하세요 (선택사항)"></textarea>
          </div>
          
          <!-- 버튼 그룹 -->
          <div class="flex space-x-3 pt-4 border-t">
            <button type="submit" class="flex-1 btn btn-success">
              💾 저장
            </button>
            <button type="button" @click="saveAndNext" class="flex-1 btn btn-primary">
              ➕ 다음 문제 만들기
            </button>
            <button type="button" @click="finish" class="flex-1 btn btn-secondary">
              ✅ 문제 만들기 끝
            </button>
          </div>
        </form>
        
        <!-- 저장된 문제 수 표시 -->
        <div v-if="savedCount > 0" class="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
          <p class="text-green-800 text-center font-medium">
            ✅ 총 {{ savedCount }}개의 문제가 저장되었습니다
          </p>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { categoryAPI } from '../../api/category'
import { questionAPI } from '../../api/question'

const router = useRouter()
const categories = ref([])
const savedCount = ref(0)
const correctIndex = ref(0)

const question = ref({
  categoryId: '',
  questionText: '',
  questionType: 'MULTIPLE_CHOICE',
  difficulty: 'MEDIUM',
  points: 10,
  explanation: '',
  options: [
    { optionText: '', optionOrder: 1, isCorrect: false },
    { optionText: '', optionOrder: 2, isCorrect: false },
    { optionText: '', optionOrder: 3, isCorrect: false },
    { optionText: '', optionOrder: 4, isCorrect: false }
  ]
})

const loadCategories = async () => {
  try {
    const response = await categoryAPI.getAll()
    categories.value = response.data.data
  } catch (error) {
    alert('카테고리 조회 실패')
  }
}

const submitQuestion = async () => {
  // 정답 설정
  question.value.options.forEach((opt, idx) => {
    opt.isCorrect = idx === correctIndex.value
  })
  
  try {
    await questionAPI.create(question.value)
    savedCount.value++
    return true
  } catch (error) {
    alert('문제 저장 실패: ' + error.message)
    return false
  }
}

const handleSubmit = async () => {
  const success = await submitQuestion()
  if (success) {
    alert('문제가 저장되었습니다!')
  }
}

const saveAndNext = async () => {
  const success = await submitQuestion()
  if (success) {
    // 폼 초기화 (카테고리는 유지)
    const currentCategoryId = question.value.categoryId
    question.value = {
      categoryId: currentCategoryId,
      questionText: '',
      questionType: 'MULTIPLE_CHOICE',
      difficulty: 'MEDIUM',
      points: 10,
      explanation: '',
      options: [
        { optionText: '', optionOrder: 1, isCorrect: false },
        { optionText: '', optionOrder: 2, isCorrect: false },
        { optionText: '', optionOrder: 3, isCorrect: false },
        { optionText: '', optionOrder: 4, isCorrect: false }
      ]
    }
    correctIndex.value = 0
    
    alert('문제가 저장되었습니다! 다음 문제를 입력하세요.')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const finish = async () => {
  if (savedCount.value === 0) {
    const confirmSave = confirm('저장된 문제가 없습니다. 현재 문제를 저장하고 종료하시겠습니까?')
    if (confirmSave) {
      await submitQuestion()
    }
  }
  
  const confirmFinish = confirm(`총 ${savedCount.value}개의 문제가 저장되었습니다. 문제 목록으로 이동하시겠습니까?`)
  if (confirmFinish) {
    router.push('/admin/questions')
  }
}

onMounted(() => {
  loadCategories()
})
</script>
