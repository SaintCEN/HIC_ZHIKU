<template>
  <header class="header">
    <div class="header-container container flex-between">
      <!-- Logo部分 -->
      <div class="logo-container">
        <router-link to="/portal" class="logo-link">
          <img src="../assets/about-image.jpg" alt="华创智库" class="logo-img" />
          <span class="logo-text">华创智库</span>
        </router-link>
      </div>
      
      <!-- 导航链接 - 桌面端 -->
      <nav class="nav-desktop hidden-xs-down">
        <ul class="nav-list">
          <li v-for="item in navItems" :key="item.id" class="nav-item">
            <router-link :to="item.path" class="nav-link" active-class="active">
              {{ item.title }}
            </router-link>
          </li>

        </ul>
      </nav>
      


      <!-- 认证入口 -->
      <div class="auth-actions hidden-xs-down">
        <template v-if="!authStore.isLoggedIn">
          <el-button type="primary" size="small" @click="router.push('/auth/login')">登录 / 注册</el-button>
        </template>
        <template v-else>
          <el-dropdown>
            <span class="el-dropdown-link user-entry">
              <img :src="avatarUrl" alt="avatar" class="avatar" />
              <span class="username">{{ authStore.user.nickname || authStore.user.username }}</span>
              <font-awesome-icon icon="caret-down" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/auth/profile')">
                  <font-awesome-icon icon="user" class="dropdown-icon" />
                  个人主页
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/repository')">
                  <font-awesome-icon icon="code-branch" class="dropdown-icon" />
                  项目仓库
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <font-awesome-icon icon="sign-out-alt" class="dropdown-icon" />
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
      
      <!-- 移动端菜单按钮 -->
      <button class="menu-toggle hidden-sm-up" @click="toggleMobileMenu">
        <font-awesome-icon :icon="isMobileMenuOpen ? 'xmark' : 'bars'" />
      </button>
    </div>
    
    <!-- 移动端导航菜单 -->
    <div class="mobile-menu" :class="{ 'is-open': isMobileMenuOpen }">
      <ul class="mobile-nav-list">
        <li v-for="item in navItems" :key="item.id" class="mobile-nav-item">
          <router-link 
            :to="item.path" 
            class="mobile-nav-link"
            active-class="active"
            @click="closeMobileMenu">
            <font-awesome-icon :icon="item.icon" />
            {{ item.title }}
          </router-link>
        </li>
        
        <!-- 移动端认证选项 -->
        <li class="mobile-nav-item mobile-auth-section">
          <template v-if="!authStore.isLoggedIn">
            <router-link 
              to="/auth/login" 
              class="mobile-nav-link mobile-auth-link"
              @click="closeMobileMenu">
              <font-awesome-icon icon="sign-in-alt" />
              登录 / 注册
            </router-link>
          </template>
          <template v-else>
            <div class="mobile-user-info">
              <div class="mobile-user-profile">
                <img :src="avatarUrl" alt="avatar" class="mobile-avatar" />
                <span class="mobile-username">{{ authStore.user.nickname || authStore.user.username }}</span>
              </div>
              <div class="mobile-user-actions">
                <router-link 
                  to="/auth/profile" 
                  class="mobile-nav-link"
                  @click="closeMobileMenu">
                  <font-awesome-icon icon="user" />
                  个人主页
                </router-link>
                <router-link 
                  to="/repository" 
                  class="mobile-nav-link"
                  @click="closeMobileMenu">
                  <font-awesome-icon icon="code-branch" />
                  项目仓库
                </router-link>
                <button 
                  class="mobile-nav-link mobile-logout-btn"
                  @click="handleLogout">
                  <font-awesome-icon icon="sign-out-alt" />
                  退出登录
                </button>
              </div>
            </div>
          </template>
        </li>
      </ul>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore, useAuthStore } from '../stores'
import { config } from '../utils/config.js'

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

onMounted(() => {
  authStore.loadFromStorage()
})

// 管理移动菜单状态
const isMobileMenuOpen = computed(() => appStore.isMobileMenuOpen)
const toggleMobileMenu = () => appStore.toggleMobileMenu()
const closeMobileMenu = () => {
  if (appStore.isMobileMenuOpen) appStore.toggleMobileMenu()
}



// 头像地址：将相对路径转换为后端绝对地址，缺省回退到本地logo
const avatarUrl = computed(() => {
  const url = authStore.user.avatarUrl
      const fallback = new URL('../assets/about-image.jpg', import.meta.url).href
  if (!url) return fallback
  // 已是绝对URL
  if (/^https?:\/\//i.test(url)) return url
  // 处理相对路径（兼容有无前导/）
  return config.getUploadUrl(url)
})

const handleLogout = () => {
  authStore.logout()
  router.push('/portal')
}

// 导航菜单项
const navItems = [
  { id: 1, title: '知识库', path: '/knowledge', icon: 'book' },
  { id: 2, title: '项目分享', path: '/projects', icon: 'code' },
  { id: 3, title: '华为竞赛', path: '/competitions', icon: 'trophy' },
  { id: 4, title: '实用工具', path: '/tools', icon: 'toolbox' },
  { id: 5, title: '华俱推文', path: '/articles', icon: 'newspaper' },
  { id: 6, title: '师兄师姐说', path: '/share', icon: 'comment' }
]
</script>

<style lang="scss" scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.header-container {
  height: 60px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  overflow: hidden;
  
  @media (max-width: 1200px) {
    gap: 8px;
    padding: 0 12px;
  }
  
  @media (max-width: 992px) {
    gap: 6px;
    padding: 0 8px;
  }
  
  @media (max-width: 768px) {
    gap: 4px;
    padding: 0 8px;
    justify-content: space-between;
  }
}

.logo-container {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  min-width: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: $primary-color;
  min-width: 0;
}

.logo-img {
  height: 36px;
  margin-right: 10px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 22px;
  font-weight: 600;
  white-space: nowrap;
  
  @media (max-width: 992px) {
    font-size: 18px;
  }
  
  @media (max-width: 768px) {
    display: none;
  }
}

.nav-desktop {
  flex: 1;
  display: flex;
  justify-content: center;
  min-width: 0;
  overflow: hidden;
  
  @media (max-width: 1200px) {
    flex: 0.8;
  }
  
  @media (max-width: 992px) {
    flex: 0.6;
  }
  
  @media (max-width: 768px) {
    display: none;
  }
}

.nav-list {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 8px;
  overflow: hidden;
  
  @media (max-width: 1200px) {
    gap: 4px;
  }
  
  @media (max-width: 992px) {
    gap: 2px;
  }
}

.nav-link {
  display: block;
  padding: 10px 16px;
  color: $text-color;
  font-weight: 500;
  transition: color 0.3s;
  white-space: nowrap;
  font-size: 16px;
  
  @media (max-width: 1200px) {
    padding: 8px 12px;
    font-size: 15px;
  }
  
  @media (max-width: 992px) {
    padding: 6px 8px;
    font-size: 14px;
  }
  
  &:hover, &.active {
    color: $primary-color;
  }
  
  &.active {
    font-weight: 600;
  }
}



.auth-actions {
  white-space: nowrap;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  
  .el-button {
    @media (max-width: 1200px) {
      font-size: 12px;
      padding: 6px 12px;
    }
    
    @media (max-width: 992px) {
      font-size: 11px;
      padding: 4px 8px;
    }
  }
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.dropdown-icon {
  margin-right: 8px;
  width: 14px;
}

.menu-toggle {
  font-size: 24px;
  color: $text-color;
  background: none;
  border: none;
  cursor: pointer;
  
  &:hover {
    color: $primary-color;
  }
}

.mobile-menu {
  display: none;
  position: fixed;
  top: 60px;
  left: 0;
  width: 100%;
  height: calc(100vh - 60px);
  background-color: white;
  z-index: 99;
  overflow-y: auto;
  transform: translateX(100%);
  transition: transform 0.3s ease;
  
  &.is-open {
    display: block;
    transform: translateX(0);
  }
}

.mobile-nav-list {
  padding: 16px;
}

.mobile-nav-item {
  margin-bottom: 8px;
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: $text-color;
  font-weight: 500;
  border-radius: 4px;
  
  .svg-inline--fa {
    margin-right: 12px;
    width: 18px;
  }
  
  &:hover, &.active {
    background-color: $gray-light-color;
    color: $primary-color;
  }
}

/* 移动端认证相关样式 */
.mobile-auth-section {
  border-top: 1px solid #e2e8f0;
  margin-top: 16px;
  padding-top: 16px;
}

.mobile-auth-link {
  background-color: $primary-color;
  color: white !important;
  
  &:hover {
    background-color: darken($primary-color, 10%);
    color: white !important;
  }
}

.mobile-user-info {
  padding: 0 16px;
}

.mobile-user-profile {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 8px;
}

.mobile-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 12px;
}

.mobile-username {
  font-weight: 600;
  color: $text-color;
}

.mobile-user-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mobile-logout-btn {
  background: none;
  border: none;
  text-align: left;
  width: 100%;
  cursor: pointer;
  
  &:hover {
    background-color: #fee2e2;
    color: #dc2626 !important;
  }
}
</style>