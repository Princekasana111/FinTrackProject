import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy: any request starting with /api is forwarded to the Spring Boot
    // backend on :8080. This avoids CORS issues during development.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
