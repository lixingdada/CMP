import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 前后端分离：开发阶段前端 5173、后端 8080，通过 devServer 代理把 /api 转给后端，
// 这样浏览器看到的请求始终是同源 /api，不必依赖后端 CORS。
// 生产环境由 Nginx 接管同一件事（见 deploy/nginx.conf）。
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    chunkSizeWarningLimit: 800
  }
})
