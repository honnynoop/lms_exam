<template>
  <Layout>
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-gray-900">관리자 대시보드</h2>
      
      <!-- 통계 카드 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <!-- 전체 사용자 -->
        <div class="card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm opacity-90">전체 사용자</p>
              <p class="text-3xl font-bold mt-2">{{ stats.totalUsers }}</p>
            </div>
            <div class="text-5xl opacity-50">👥</div>
          </div>
        </div>
        
        <!-- 전체 시험 -->
        <div class="card bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm opacity-90">전체 시험</p>
              <p class="text-3xl font-bold mt-2">{{ stats.totalExams }}</p>
            </div>
            <div class="text-5xl opacity-50">📝</div>
          </div>
        </div>
        
        <!-- 공개된 시험 -->
        <div class="card bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm opacity-90">공개된 시험</p>
              <p class="text-3xl font-bold mt-2">{{ stats.activeExams }}</p>
            </div>
            <div class="text-5xl opacity-50">👁</div>
          </div>
        </div>
        
        <!-- 전체 문제 -->
        <div class="card bg-gradient-to-br from-orange-500 to-orange-600 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm opacity-90">전체 문제</p>
              <p class="text-3xl font-bold mt-2">{{ stats.totalQuestions }}</p>
            </div>
            <div class="text-5xl opacity-50">❓</div>
          </div>
        </div>
      </div>
      
      <!-- 최근 활동 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 최근 시험 -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-4">최근 시험</h3>
          <div v-if="recentExams.length === 0" class="text-center py-8 text-gray-500">
            등록된 시험이 없습니다
          </div>
          <div v-else class="space-y-3">
            <div v-for="exam in recentExams" :key="exam.examId" 
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
              <div class="flex-1">
                <p class="font-medium text-gray-900">{{ exam.examTitle }}</p>
                <p class="text-sm text-gray-600">{{ exam.categoryName }} • {{ exam.totalQuestions }}문제</p>
              </div>
              <div class="flex items-center space-x-2">
                <span v-if="exam.isActive" class="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
                  공개
                </span>
                <span v-if="exam.isPublished" class="px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full">
                  결과공개
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 카테고리별 문제 수 -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-4">카테고리별 문제</h3>
          <div v-if="categoryStats.length === 0" class="text-center py-8 text-gray-500">
            카테고리가 없습니다
          </div>
          <div v-else class="space-y-3">
            <div v-for="cat in categoryStats" :key="cat.categoryId" 
                 class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
              <div>
                <p class="font-medium text-gray-900">{{ cat.categoryName }}</p>
                <p class="text-sm text-gray-600">{{ cat.questionCount }}개 문제</p>
              </div>
              <div class="text-2xl">📚</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 빠른 액션 -->
      <div class="card">
        <h3 class="text-lg font-semibold mb-4">빠른 작업</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <router-link to="/admin/exams/create" 
                       class="flex flex-col items-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition">
            <div class="text-3xl mb-2">➕</div>
            <span class="text-sm font-medium text-gray-900">시험 생성</span>
          </router-link>
          
          <router-link to="/admin/questions/create" 
                       class="flex flex-col items-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition">
            <div class="text-3xl mb-2">❓</div>
            <span class="text-sm font-medium text-gray-900">문제 생성</span>
          </router-link>
          
          <router-link to="/admin/categories" 
                       class="flex flex-col items-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition">
            <div class="text-3xl mb-2">📁</div>
            <span class="text-sm font-medium text-gray-900">카테고리</span>
          </router-link>
          
          <router-link to="/admin/exams" 
                       class="flex flex-col items-center p-4 bg-orange-50 rounded-lg hover:bg-orange-100 transition">
            <div class="text-3xl mb-2">📊</div>
            <span class="text-sm font-medium text-gray-900">시험 관리</span>
          </router-link>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../../components/Layout.vue'
import { examAPI } from '../../api/exam'
import { categoryAPI } from '../../api/category'
import { userAPI } from '../../api/user'
import { questionAPI } from '../../api/question'

const stats = ref({
  totalUsers: 0,
  totalExams: 0,
  activeExams: 0,
  totalQuestions: 0
})

const recentExams = ref([])
const categoryStats = ref([])

const loadStats = async () => {
  try {
    // 전체 사용자 수
    const usersResponse = await userAPI.getAll()
    stats.value.totalUsers = usersResponse.data.data.length || 0
    
    // 전체 시험 수 및 공개된 시험 수
    const examsResponse = await examAPI.getAll({ page: 1, size: 1000 })
    const allExams = examsResponse.data.data.content || examsResponse.data.data || []
    stats.value.totalExams = allExams.length
    stats.value.activeExams = allExams.filter(e => e.isActive).length
    
    // 최근 시험 5개
    recentExams.value = allExams.slice(0, 5)
    
    // 전체 문제 수
    const questionsResponse = await questionAPI.getAll({ page: 1, size: 1000 })
    const allQuestions = questionsResponse.data.data.content || questionsResponse.data.data || []
    stats.value.totalQuestions = allQuestions.length
    
    // 카테고리별 문제 수
    const categoriesResponse = await categoryAPI.getAllWithStats()
    categoryStats.value = categoriesResponse.data.data || []
  } catch (error) {
    console.error('통계 조회 실패:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>
