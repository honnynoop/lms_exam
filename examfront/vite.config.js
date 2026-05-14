import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: true, // 외부 접속 허용
    // 로컬 및 서버 IP에서 접근 가능하도록 설정
    proxy: {
      '/api': {
        target: 'http://172.30.1.71:8080',
        changeOrigin: true,
        secure: false
      }
    }
  }
})