<template>
  <div class="h-screen flex flex-col bg-gray-50 overflow-hidden">
    <!-- Navbar (고정 높이) -->
    <Navbar class="flex-shrink-0" />
    
    <!-- Main 컨텐츠 영역 (남은 공간 차지 + 내부 스크롤) -->
    <main class="flex-1 overflow-hidden">
      <div class="h-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup>
import Navbar from './Navbar.vue'
</script>

<style scoped>
/* 슬롯 내부 컴포넌트가 전체 높이를 사용할 수 있도록 */
main > div {
  display: flex;
  flex-direction: column;
}

/* 슬롯 내부의 직계 자식이 flex-1을 사용할 수 있도록 */
main > div > :deep(*) {
  flex: 1;
  min-height: 0;
}
</style>