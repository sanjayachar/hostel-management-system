import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/auth-service-api": "http://localhost:8081",
      "/accomm-service-api": "http://localhost:8082",
      "/student-service-api": "http://localhost:8083",
      "/staff-service-api": "http://localhost:8084",
      "/other-candidate-service-api": "http://localhost:8085",
      "/audit-log-service-api": "http://localhost:8086",
      "/chat-service-api": {
        target: "http://localhost:8087",
        changeOrigin: true,
        ws: true
      }
    }
  }
})
