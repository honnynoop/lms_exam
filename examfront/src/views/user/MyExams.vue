<template>
  <Layout>
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-gray-900">내 시험 기록</h2>
      
      <!-- 통계 요약 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div class="text-sm opacity-90">총 응시</div>
          <div class="text-3xl font-bold mt-2">{{ myExams.length }}회</div>
        </div>
        
        <div class="card bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div class="text-sm opacity-90">완료</div>
          <div class="text-3xl font-bold mt-2">{{ completedCount }}회</div>
        </div>
        
        <div class="card bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div class="text-sm opacity-90">평균 점수</div>
          <div class="text-3xl font-bold mt-2">{{ averageScore }}점</div>
        </div>
      </div>
      
      <!-- 로딩 -->
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- 시험 목록 -->
      <div v-else class="space-y-4">
        <div v-for="exam in myExams" :key="exam.userExamId" 
             class="card hover:shadow-lg transition-shadow">
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="text-lg font-semibold text-gray-900">{{ exam.examTitle }}</h3>
                <span class="px-2 py-1 text-xs rounded-full"
                      :class="getStatusClass(exam.status)">
                  {{ getStatusText(exam.status) }}
                </span>
              </div>
              
              <div class="flex items-center space-x-2 mb-3">
                <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
                  {{ exam.categoryName }}
                </span>
              </div>
              
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span class="text-gray-600">응시일:</span>
                  <div class="font-medium">{{ formatDate(exam.startedAt) }}</div>
                </div>
                
                <div v-if="exam.status === 'SUBMITTED'">
                  <span class="text-gray-600">제출일:</span>
                  <div class="font-medium">{{ formatDate(exam.submittedAt) }}</div>
                </div>
                
                <!-- ✅ 결과 공개 시에만 점수 표시 -->
                <div v-if="exam.status === 'SUBMITTED' && exam.isPublished && exam.score !== null">
                  <span class="text-gray-600">점수:</span>
                  <div class="font-medium text-blue-600">{{ exam.score }}점</div>
                </div>
                
                <!-- ✅ 결과 공개 시에만 정답률 표시 -->
                <div v-if="exam.status === 'SUBMITTED' && exam.isPublished && exam.correctAnswers !== null">
                  <span class="text-gray-600">정답률:</span>
                  <div class="font-medium">
                    {{ Math.round((exam.correctAnswers / exam.totalQuestions) * 100) }}%
                  </div>
                </div>
              </div>
              
              <!-- ✅ 결과 공개 시에만 정답/오답 표시 -->
              <div v-if="exam.status === 'SUBMITTED' && exam.isPublished && exam.correctAnswers !== null" 
                   class="mt-3 flex items-center space-x-4 text-sm">
                <div class="flex items-center">
                  <span class="text-green-600 mr-2">✓</span>
                  <span>정답 {{ exam.correctAnswers }}개</span>
                </div>
                <div class="flex items-center">
                  <span class="text-red-600 mr-2">✗</span>
                  <span>오답 {{ exam.wrongAnswers }}개</span>
                </div>
              </div>
              
              <!-- ✅ 결과 비공개 시 대기 메시지 -->
              <div v-if="exam.status === 'SUBMITTED' && !exam.isPublished" 
                   class="mt-3 text-sm text-yellow-600">
                ⏳ 결과 공개 대기중...
              </div>
            </div>
            
            <div class="ml-4">
              <!-- 진행중인 시험 -->
              <button v-if="exam.status === 'IN_PROGRESS'" 
                      @click="continueExam(exam.examId)"
                      class="btn btn-primary text-sm py-2 px-4 whitespace-nowrap">
                계속하기
              </button>
              
              <!-- ✅ 결과 공개 시에만 결과 보기 활성화 -->
              <button v-else-if="exam.status === 'SUBMITTED' && exam.isPublished" 
                      @click="viewResult(exam.userExamId)"
                      class="btn btn-success text-sm py-2 px-4 whitespace-nowrap">
                결과 보기
              </button>
              
              <!-- ✅ 결과 비공개 시 -->
              <div v-else-if="exam.status === 'SUBMITTED' && !exam.isPublished" 
                   class="text-sm text-gray-500 text-center px-4">
                결과 대기중
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && myExams.length === 0" class="card text-center py-12">
        <p class="text-gray-500 mb-4">아직 응시한 시험이 없습니다</p>
        <router-link to="/user/exams" class="btn btn-primary">
          시험 보러 가기
        </router-link>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../../components/Layout.vue'
import { userExamAPI } from '../../api/userExam'

const router = useRouter()
const myExams = ref([])
const loading = ref(false)

const completedCount = computed(() => {
  return myExams.value.filter(e => e.status === 'SUBMITTED').length
})

const averageScore = computed(() => {
  // ✅ 결과 공개된 시험만 평균 계산
  const completed = myExams.value.filter(e => 
    e.status === 'SUBMITTED' && e.isPublished && e.score != null
  )
  if (completed.length === 0) return 0
  const sum = completed.reduce((acc, e) => acc + e.score, 0)
  return Math.round(sum / completed.length)
})

const loadMyExams = async () => {
  loading.value = true
  try {
    const response = await userExamAPI.getMyExams()
    //console.log(response.data.data+"-------------------------------------------------")
   
    myExams.value = response.data.data || []
     //alert(myExams.value)
  } catch (error) {
    alert('시험 기록 조회 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const getStatusClass = (status) => {
  const classes = {
    'IN_PROGRESS': 'bg-yellow-100 text-yellow-800',
    'SUBMITTED': 'bg-green-100 text-green-800',
    'NOT_STARTED': 'bg-gray-100 text-gray-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getStatusText = (status) => {
  const texts = {
    'IN_PROGRESS': '진행중',
    'SUBMITTED': '제출완료',
    'NOT_STARTED': '시작 전'
  }
  return texts[status] || status
}

const continueExam = (examId) => {
  router.push(`/user/exam/${examId}`)
}

const viewResult = (userExamId) => {
  router.push(`/user/result/${userExamId}`)
}

const formatDate = (dateString) => {
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

onMounted(() => {
  loadMyExams()
})
</script>
