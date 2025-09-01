<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import { onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from './stores'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

// 定期检查token有效性
let tokenCheckInterval = null

onMounted(async () => {
  // 初始化时加载认证状态
  auth.loadFromStorage()
  
  // 如果已登录，立即检查token有效性
  if (auth.isLoggedIn) {
    checkTokenValidity()
  }
  
  // 检查当前路径，如果是根路径且已登录，则跳转到portal
  await nextTick()
  if (router.currentRoute.value.path === '/' && auth.isLoggedIn) {
    router.replace('/portal')
  }
  
  // 设置定期检查token有效性（每5分钟检查一次）
  tokenCheckInterval = setInterval(() => {
    if (auth.isLoggedIn) {
      checkTokenValidity()
    }
  }, 5 * 60 * 1000)
})

onUnmounted(() => {
  if (tokenCheckInterval) {
    clearInterval(tokenCheckInterval)
  }
})

async function checkTokenValidity() {
  try {
    // 首先检查本地token是否已过期
    if (auth.isTokenExpired()) {
      console.log('Token已过期，静默清除登录状态')
      auth.logout()
      // 不跳转页面，让用户继续浏览
      return
    }
    
    // 定期验证token有效性（减少频率，避免过多请求）
    const now = Date.now()
    const lastCheck = auth.lastTokenCheck || 0
    const checkInterval = 10 * 60 * 1000 // 10分钟检查一次
    
    if (now - lastCheck > checkInterval) {
      const isValid = await auth.checkTokenValidity()
      if (!isValid) {
        auth.logout()
      }
      auth.lastTokenCheck = now
    }
  } catch (error) {

  }
}
</script>

<style lang="scss">
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style> 