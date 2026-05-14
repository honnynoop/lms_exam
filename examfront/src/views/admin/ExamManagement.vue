<template>
  <Layout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">시험 관리</h2>
        <router-link to="/admin/exams/create" class="btn btn-primary">
          + 새 시험 만들기
        </router-link>
      </div>
      
      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      </div>
      
      <!-- 시험 목록 -->
      <div v-else class="grid grid-cols-1 gap-6">
        <div v-for="exam in exams" :key="exam.examId" class="card hover:shadow-lg transition-shadow">
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="text-xl font-semibold text-gray-900">{{ exam.examTitle }}</h3>
                
                <!-- ✅ 시험 공개 상태 -->
                <span v-if="exam.isActive" 
                      class="px-3 py-1 text-sm rounded-full bg-blue-100 text-blue-800 font-medium">
                  👁 시험 공개됨
                </span>
                <span v-else 
                      class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-800">
                  🔒 시험 비공개
                </span>
                
                <!-- 결과 공개 상태 -->
                <span v-if="exam.isPublished" 
                      class="px-3 py-1 text-sm rounded-full bg-green-100 text-green-800 font-medium">
                  ✓ 결과 공개됨
                </span>
                <span v-else 
                      class="px-3 py-1 text-sm rounded-full bg-yellow-100 text-yellow-800">
                  ⏳ 결과 비공개
                </span>
              </div>
              
              <div class="flex items-center space-x-2 mb-3">
                <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
                  {{ exam.categoryName }}
                </span>
                <span class="text-sm text-gray-600">
                  {{ exam.totalQuestions }}문제 / {{ exam.totalPoints }}점
                </span>
                <span class="text-sm text-gray-600">
                  ⏱ {{ exam.durationMinutes }}분
                </span>
                <span class="text-sm text-gray-600">
                  합격: {{ exam.passingScore }}점
                </span>
                <span v-if="exam.examDate" class="text-sm text-gray-600">
                  📅 {{ formatDate(exam.examDate) }}
                </span>
              </div>
              
              <p v-if="exam.description" class="text-gray-600 mb-3">{{ exam.description }}</p>
            </div>
            
            <!-- ✅ 버튼 그룹 -->
            <div class="flex flex-col space-y-2 ml-4">
              <!-- 시험 공개/비공개 버튼 -->
              <button v-if="!exam.isActive" 
                      @click="activateExam(exam.examId)" 
                      :disabled="processing"
                      class="btn btn-primary text-sm py-2 px-4 whitespace-nowrap">
                {{ processing ? '처리 중...' : '👁 시험 공개' }}
              </button>
              <button v-else 
                      @click="deactivateExam(exam.examId)" 
                      :disabled="processing"
                      class="btn btn-secondary text-sm py-2 px-4 whitespace-nowrap">
                {{ processing ? '처리 중...' : '🔒 시험 비공개' }}
              </button>
              
              <!-- 결과 공개 버튼 -->
              <button v-if="!exam.isPublished" 
                      @click="publishResults(exam.examId)" 
                      :disabled="processing"
                      class="btn btn-success text-sm py-2 px-4 whitespace-nowrap">
                {{ processing ? '처리 중...' : '📊 결과 공개' }}
              </button>
              <button v-else 
                      class="btn btn-secondary text-sm py-2 px-4 whitespace-nowrap opacity-50 cursor-not-allowed"
                      disabled>
                결과 공개됨
              </button>
              
              <!-- 통계 버튼 -->
              <button @click="viewStatistics(exam)" 
                      class="btn btn-secondary text-sm py-2 px-4 whitespace-nowrap">
                📈 통계
              </button>
              
              <!-- 상세보기 버튼 -->
              <button @click="viewExamDetails(exam)" 
                      class="btn btn-secondary text-sm py-2 px-4 whitespace-nowrap">
                상세보기
              </button>
              
              <!-- 삭제 버튼 -->
              <button @click="deleteExam(exam.examId)" 
                      :disabled="processing"
                      class="btn btn-danger text-sm py-2 px-4 whitespace-nowrap">
                삭제
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && exams.length === 0" class="card text-center py-12 text-gray-500">
        등록된 시험이 없습니다
      </div>
      
      <!-- ✅ 시험 상세 모달 -->
      <ExamDetailModal 
        :show="showDetailModal" 
        :exam="selectedExam"
        @close="showDetailModal = false" />
      
      <!-- ✅ 통계 모달 -->
      <StatisticsModal 
        :show="showStatisticsModal" 
        :examId="selectedExamId"
        @close="showStatisticsModal = false" />
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../../components/Layout.vue'
import ExamDetailModal from '../../components/ExamDetailModal.vue'
import StatisticsModal from '../../components/StatisticsModal.vue'
import { examAPI } from '../../api/exam'

const exams = ref([])
const showDetailModal = ref(false)
const showStatisticsModal = ref(false)
const selectedExam = ref(null)
const selectedExamId = ref(null)
const loading = ref(false)
const processing = ref(false)

const loadExams = async () => {
  loading.value = true
  try {
    const response = await examAPI.getAll({ page: 1, size: 100 })
    const data = response.data.data
    exams.value = data.content || data || []
  } catch (error) {
    alert('시험 목록 조회 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// ✅ 시험 공개
const activateExam = async (examId) => {
  if (!confirm('시험을 공개하시겠습니까?\n공개 후 사용자들이 시험을 볼 수 있습니다.')) return
  
  processing.value = true
  try {
    await examAPI.activate(examId)
    alert('시험이 공개되었습니다!')
    loadExams()
  } catch (error) {
    alert('시험 공개 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    processing.value = false
  }
}

// ✅ 시험 비공개
const deactivateExam = async (examId) => {
  if (!confirm('시험을 비공개하시겠습니까?\n사용자들이 시험을 볼 수 없게 됩니다.')) return
  
  processing.value = true
  try {
    await examAPI.deactivate(examId)
    alert('시험이 비공개되었습니다')
    loadExams()
  } catch (error) {
    alert('시험 비공개 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    processing.value = false
  }
}

// 결과 공개
const publishResults = async (examId) => {
  if (!confirm('결과를 공개하시겠습니까?\n학생들이 점수와 해설을 볼 수 있습니다.')) return
  
  processing.value = true
  try {
    await examAPI.publish(examId)
    alert('결과가 공개되었습니다!')
    loadExams()
  } catch (error) {
    alert('결과 공개 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    processing.value = false
  }
}

const viewExamDetails = (exam) => {
  selectedExam.value = exam
  showDetailModal.value = true
}

const viewStatistics = (exam) => {
  selectedExamId.value = exam.examId
  showStatisticsModal.value = true
}

const deleteExam = async (examId) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  
  processing.value = true
  try {
    await examAPI.delete(examId)
    alert('시험이 삭제되었습니다')
    loadExams()
  } catch (error) {
    alert('삭제 실패: ' + (error.response?.data?.message || error.message))
  } finally {
    processing.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR')
}

onMounted(() => {
  loadExams()
})
</script>
