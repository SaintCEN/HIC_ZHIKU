import axios from 'axios'
import { useAuthStore } from '../stores'
import { config } from './config'

const http = axios.create({
  baseURL: config.API_BASE_URL,  // 直接使用配置的 API 地址
  timeout: 15000,
  // 添加全局缓存控制，确保获取最新内容
  headers: {
    'Cache-Control': 'no-cache, no-store, must-revalidate',
    'Pragma': 'no-cache',
    'Expires': '0'
  }
})

// 请求拦截：自动附带 Authorization
http.interceptors.request.use((config) => {
  try {
    const auth = useAuthStore()
    if (auth?.accessToken) {
      config.headers.Authorization = `Bearer ${auth.accessToken}`
    }
  } catch (_) {}
  return config
})

// 响应拦截：统一错误提示/401处理
http.interceptors.response.use(
  (resp) => resp,
  (error) => {
    if (error.response && error.response.status === 401) {
      try {
        const auth = useAuthStore()
        auth.logout()
      } catch (_) {}
    }
    return Promise.reject(error)
  }
)

export default http


