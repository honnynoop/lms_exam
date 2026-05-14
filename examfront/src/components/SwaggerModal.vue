<template>
  <Modal :show="show" title="Swagger API 문서" @close="$emit('close')">
    <div class="space-y-6">
      <!-- JWT 토큰 -->
      <div>
        <label class="label">JWT 토큰</label>
        <div class="flex space-x-2">
          <input 
            ref="tokenInput"
            :value="token" 
            readonly 
            class="input flex-1 bg-gray-50 font-mono text-sm"
            placeholder="토큰이 없습니다"
            @click="selectToken"
          >
          <button @click="copyToken" class="btn btn-primary whitespace-nowrap">
            {{ copied ? '✓ 복사됨' : '토큰 복사' }}
          </button>
        </div>
        <p class="text-sm text-gray-500 mt-2">
          Swagger UI에서 Authorize 버튼을 클릭하고 이 토큰을 붙여넣으세요
        </p>
      </div>
      
      <!-- Swagger UI 링크 -->
      <div class="p-4 bg-blue-50 rounded-lg">
        <h3 class="font-semibold text-blue-900 mb-2">Swagger UI 접속</h3>
        <a :href="swaggerUrl" target="_blank" 
           class="text-blue-600 hover:underline font-medium">
          {{ swaggerUrl }}
        </a>
      </div>
      
      <!-- 사용 방법 -->
      <div class="p-4 bg-gray-50 rounded-lg">
        <h3 class="font-semibold text-gray-900 mb-3">사용 방법</h3>
        <ol class="list-decimal list-inside space-y-2 text-sm text-gray-700">
          <li>위의 "토큰 복사" 버튼을 클릭하여 JWT 토큰을 복사합니다</li>
          <li>Swagger UI 링크를 클릭하여 새 탭에서 엽니다</li>
          <li>Swagger UI 우측 상단의 "Authorize" 버튼을 클릭합니다</li>
          <li>값 입력란에 <code class="px-2 py-1 bg-gray-200 rounded">Bearer {복사한 토큰}</code> 형식으로 붙여넣습니다</li>
          <li>"Authorize" 버튼을 클릭하여 인증을 완료합니다</li>
          <li>이제 모든 API를 테스트할 수 있습니다</li>
        </ol>
      </div>
      
      <!-- API 엔드포인트 예시 -->
      <div class="p-4 bg-green-50 rounded-lg">
        <h3 class="font-semibold text-green-900 mb-3">주요 API 엔드포인트</h3>
        <div class="space-y-2 text-sm">
          <div class="flex items-center space-x-2">
            <span class="px-2 py-1 bg-green-600 text-white rounded text-xs">GET</span>
            <code>/exams</code>
            <span class="text-gray-600">- 시험 목록</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="px-2 py-1 bg-green-600 text-white rounded text-xs">GET</span>
            <code>/exams/active</code>
            <span class="text-gray-600">- 공개된 시험</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="px-2 py-1 bg-blue-600 text-white rounded text-xs">PUT</span>
            <code>/exams/{"{id}"}/activate</code>
            <span class="text-gray-600">- 시험 공개</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="px-2 py-1 bg-blue-600 text-white rounded text-xs">PUT</span>
            <code>/exams/{"{id}"}/publish</code>
            <span class="text-gray-600">- 결과 공개</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="px-2 py-1 bg-green-600 text-white rounded text-xs">GET</span>
            <code>/exams/{"{id}"}/statistics</code>
            <span class="text-gray-600">- 통계</span>
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<script setup>
import { ref, computed } from 'vue'
import Modal from './Modal.vue'

defineProps({
  show: Boolean
})

defineEmits(['close'])

const copied = ref(false)
const tokenInput = ref(null)
const swaggerUrl = `${import.meta.env.VITE_API_BASE_URL}/swagger-ui/index.html`

const token = computed(() => {
  return localStorage.getItem('token') || ''
})

// 토큰 입력창 선택
const selectToken = () => {
  if (tokenInput.value) {
    tokenInput.value.select()
  }
}

// 토큰 복사 (HTTP 환경에서도 작동)
const copyToken = async () => {
  if (!token.value) {
    alert('토큰이 없습니다. 다시 로그인해주세요.')
    return
  }
  
  try {
    // 방법 1: Clipboard API (HTTPS/localhost에서만 작동)
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(token.value)
      showCopied()
      return
    }
    
    // 방법 2: execCommand fallback (HTTP에서도 작동)
    const textArea = document.createElement('textarea')
    textArea.value = token.value
    textArea.style.position = 'fixed'
    textArea.style.left = '-9999px'
    textArea.style.top = '-9999px'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    
    const success = document.execCommand('copy')
    document.body.removeChild(textArea)
    
    if (success) {
      showCopied()
    } else {
      // 방법 3: input 직접 선택
      selectAndPrompt()
    }
  } catch (error) {
    console.error('복사 실패:', error)
    selectAndPrompt()
  }
}

// 복사 성공 표시
const showCopied = () => {
  copied.value = true
  setTimeout(() => {
    copied.value = false
  }, 2000)
}

// 수동 복사 안내
const selectAndPrompt = () => {
  if (tokenInput.value) {
    tokenInput.value.select()
    alert('토큰이 선택되었습니다. Ctrl+C (Mac: Cmd+C)로 복사해주세요.')
  }
}
</script>