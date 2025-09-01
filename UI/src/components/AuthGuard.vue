<template>
  <div v-if="isAuthenticated">
    <slot />
  </div>
  <div v-else class="auth-guard-loading">
    <el-card class="loading-card">
      <div class="loading-content">
        <el-icon class="is-loading" size="24">
          <Loading />
        </el-icon>
        <p>正在验证登录状态...</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores'
import { Loading } from '@element-plus/icons-vue'

const router = useRouter()
const auth = useAuthStore()
const isAuthenticated = ref(false)

onMounted(async () => {
  // 加载认证状态
  auth.loadFromStorage()
  
  if (!auth.isLoggedIn) {
    console.log('用户未登录，跳转到登录页面')
    router.push('/auth/login')
    return
  }
  
  // 检查token是否已过期
  if (auth.isTokenExpired()) {
    console.log('Token已过期，静默清除登录状态')
    auth.logout()
    // 不跳转页面，让用户继续浏览
    return
  }
  
  // 验证token有效性
  try {
    const isValid = await auth.checkTokenValidity()
    if (!isValid) {
      console.log('Token验证失败，静默清除登录状态')
      auth.logout()
      // 不跳转页面，让用户继续浏览
      return
    }
    
    // 认证通过
    isAuthenticated.value = true
  } catch (error) {
    console.error('Token验证出错:', error)
    auth.logout()
    // 不跳转页面，让用户继续浏览
  }
})
</script>

<style lang="scss" scoped>
.auth-guard-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
  
  .loading-card {
    max-width: 400px;
    width: 100%;
    
    .loading-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
      padding: 2rem;
      
      p {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }
  }
}
</style>




