<template>
  <div class="project-repository">
    <!-- GitHub风格页面头部 -->
    <div class="github-header">
      <div class="header-container">
        <div class="header-main">
          <div class="user-info">
            <div class="user-details">
              <h1 class="username">{{ userInfo.nickname || userInfo.username || '用户' }}</h1>
              <p class="user-bio">{{ userInfo.bio || '开源项目贡献者' }}</p>
            </div>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ myProjects.length }}</span>
            <span class="stat-label">项目</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ favorites.length }}</span>
            <span class="stat-label">收藏</span>
          </div>
        </div>
      </div>
    </div>

    <!-- GitHub风格导航标签 -->
    <div class="github-nav">
      <div class="nav-container">
        <div class="nav-tabs">
          <button 
            :class="['nav-tab', { active: activeTab === 'repositories' }]"
            @click="activeTab = 'repositories'"
          >
            <font-awesome-icon icon="book" />
            我的项目
            <span class="tab-count">{{ myProjects.length }}</span>
          </button>
          <button 
            :class="['nav-tab', { active: activeTab === 'starred' }]"
            @click="activeTab = 'starred'"
          >
            <font-awesome-icon icon="star" />
            收藏项目
            <span class="tab-count">{{ favorites.length }}</span>
          </button>
        </div>

      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 我的项目标签页 -->
      <div v-if="activeTab === 'repositories'" class="repositories-section">

          
          <div class="repo-list" v-loading="myProjectsLoading">
            <div v-if="myProjects.length === 0 && !myProjectsLoading" class="empty-state">
              <div class="empty-icon">
                <font-awesome-icon icon="folder-open" />
              </div>
              <h3 class="empty-title">创建你的第一个项目</h3>
              <p class="empty-description">与大家分享你的作品，创建一个项目开始吧。</p>
              <el-button type="success" @click="$router.push('/projects/upload')" class="empty-action-btn">
                <font-awesome-icon icon="plus" />
                创建项目
              </el-button>
            </div>
            
            <div v-else class="repo-items">
              <div
                v-for="project in myProjects"
                :key="project.id"
                class="repo-item"
              >
                <div class="repo-main">
                  <div class="repo-info">
                    <h3 class="repo-name" @click="viewProject(project.id)">
                      <font-awesome-icon icon="book" />
                      {{ project.title }}
                    </h3>
                    <span class="repo-visibility">
                      <font-awesome-icon icon="globe" />
                      公开
                    </span>
                  </div>
                  <div class="repo-actions">
                    <el-button 
                      size="small" 
                      @click="editProject(project)"
                      class="edit-btn"
                    >
                      <font-awesome-icon icon="edit" />
                      编辑
                    </el-button>
                    <el-button 
                      size="small" 
                      type="danger"
                      @click="deleteProject(project)"
                      class="delete-btn"
                    >
                      <font-awesome-icon icon="trash" />
                      删除
                    </el-button>
                  </div>
                </div>
                
                <p class="repo-description">{{ project.description || '暂无项目描述。' }}</p>
                
                <div class="repo-meta">
                  <div class="repo-stats">
                    <span class="language-color" :style="{ backgroundColor: getLanguageColor(project.category) }"></span>
                    <span class="language">{{ getProjectTypeLabel(project.category) }}</span>
                    <span class="stat-item" v-if="project.starCount">
                      <font-awesome-icon icon="star" />
                      {{ project.starCount }}
                    </span>
                    <span class="stat-item" v-if="project.viewCount">
                      <font-awesome-icon icon="eye" />
                      {{ project.viewCount }}
                    </span>
                  </div>
                  <span class="repo-updated">
                    更新于 {{ formatRelativeTime(project.createdAt) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 收藏项目标签页 -->
        <div v-if="activeTab === 'starred'" class="starred-section">

          
          <div class="starred-list" v-loading="favoritesLoading">
            <div v-if="favorites.length === 0 && !favoritesLoading" class="empty-state">
              <div class="empty-icon">
                <font-awesome-icon icon="star" />
              </div>
              <h3 class="empty-title">收藏感兴趣的项目</h3>
              <p class="empty-description">当你收藏一个项目时，它会显示在这里。</p>
              <el-button type="primary" @click="$router.push('/projects')" class="empty-action-btn">
                <font-awesome-icon icon="search" />
                探索项目
              </el-button>
            </div>
            
            <div v-else class="starred-items">
              <div
                v-for="favorite in favorites"
                :key="favorite.id"
                class="starred-item"
              >
                <div class="starred-main">
                  <div class="starred-info">
                    <div class="starred-title-section">
                      <div class="author-repo">
                        <span class="author-name">{{ favorite.authorName || 'unknown' }}</span>
                        <span class="repo-separator">/</span>
                        <h4 class="starred-title" @click="viewFavoriteProject(favorite)">
                          {{ favorite.resourceTitle }}
                        </h4>
                      </div>
                      <span class="starred-visibility">
                        <font-awesome-icon icon="globe" />
                        Public
                      </span>
                    </div>
                  </div>
                  <div class="starred-actions">
                    <el-button 
                      size="small" 
                      type="warning"
                      @click="removeFavorite(favorite)"
                      class="unstar-btn"
                    >
                      <font-awesome-icon icon="star" />
                      Unstar
                    </el-button>
                  </div>
                </div>
                
                <p class="starred-description">{{ favorite.resourceDescription || 'No description provided.' }}</p>
                
                <div class="starred-meta">
                  <div class="starred-stats">
                    <span class="language-color" :style="{ backgroundColor: getLanguageColor(favorite.category) }"></span>
                    <span class="language">{{ getProjectTypeLabel(favorite.category) }}</span>
                    <span class="stat-item" v-if="favorite.starCount">
                      <font-awesome-icon icon="star" />
                      {{ favorite.starCount }}
                    </span>
                    <span class="stat-item" v-if="favorite.viewCount">
                      <font-awesome-icon icon="eye" />
                      {{ favorite.viewCount }}
                    </span>
                  </div>
                  <span class="starred-date">
                    收藏于 {{ formatRelativeTime(favorite.createdAt) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import { getFavorites, removeFavoriteById } from '@/api/favorites'
import { getMyProjects, deleteProject as deleteProjectApi } from '@/api/projects'
import { config } from '@/utils/config'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const myProjects = ref([])
const favorites = ref([])
const myProjectsLoading = ref(false)
const favoritesLoading = ref(false)
const activeTab = ref('repositories')
const userInfo = ref({
  username: '',
  bio: '',
  avatar: '',
  repositoriesCount: 0,
  starredCount: 0
})

// 项目类型标签映射
const projectTypeLabels = {
  'software': '软件开发',
  'hardware': '硬件设计',
  'embedded': '嵌入式系统',
  'ai': '人工智能',
  'other': '其他'
}

// 计算属性
const filteredMyProjects = computed(() => {
  return myProjects.value
})

const filteredFavorites = computed(() => {
  return favorites.value
})

// 获取当前用户信息
const currentUser = computed(() => authStore.user)
const userAvatar = computed(() => {
  return currentUser.value?.avatar || '/default-avatar.png'
})

// 获取语言颜色
const getLanguageColor = (category) => {
  const colors = {
    'software': '#f1e05a',
    'hardware': '#e34c26',
    'embedded': '#563d7c',
    'ai': '#3572A5',
    'other': '#89e051'
  }
  return colors[category] || '#89e051'
}

// 格式化相对时间
const formatRelativeTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)
  
  if (diffInSeconds < 60) return 'just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`
  if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)} days ago`
  if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)} months ago`
  return `${Math.floor(diffInSeconds / 31536000)} years ago`
}

// 获取作者头像
const getAuthorAvatar = (avatar) => {
  return config.getAvatarUrl(avatar)
}

// 处理头像加载错误
const handleAvatarError = (event) => {
  event.target.src = '/default-avatar.png'
}



// 获取用户自己的项目
const fetchMyProjects = async () => {
  try {
    myProjectsLoading.value = true
    const response = await getMyProjects({ 
      page: 1, 
      size: 100
    })
    
    const data = response.data?.data || response.data
    myProjects.value = data?.data || []
    
    // 更新项目数量
    userInfo.value.repositoriesCount = myProjects.value.length
  } catch (error) {
    console.error('获取我的项目失败:', error)
    ElMessage.error('获取我的项目失败')
  } finally {
    myProjectsLoading.value = false
  }
}

// 获取收藏的项目
const fetchFavorites = async () => {
  try {
    favoritesLoading.value = true
    const response = await getFavorites({ page: 1, size: 100 })
    const data = response.data?.data || response.data
    favorites.value = data?.data || []
    userInfo.value.starredCount = favorites.value.length
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    favoritesLoading.value = false
  }
}

// 获取项目类型标签
const getProjectTypeLabel = (type) => {
  return projectTypeLabels[type] || type
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 查看项目详情
const viewProject = (projectId) => {
  router.push(`/projects/${projectId}`)
}

// 查看收藏的项目详情
const viewFavoriteProject = (favorite) => {
  let projectId = favorite.resourceId
  if (favorite.resourceUrl && !favorite.resourceUrl.startsWith('http')) {
    const match = favorite.resourceUrl.match(/\/(project|projects)\/(\d+)/)
    if (match) {
      projectId = match[2]
    }
  }
  if (projectId) {
    router.push(`/projects/${projectId}`)
  } else {
    ElMessage.warning('无法获取项目信息')
  }
}



// 编辑项目
const editProject = (project) => {
  router.push(`/project/edit/${project.id}`)
}

// 删除项目
const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目"${project.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await deleteProjectApi(project.id)
    ElMessage.success('项目删除成功')
    fetchMyProjects() // 重新加载项目列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除项目失败:', error)
      ElMessage.error('删除项目失败')
    }
  }
}

// 处理收藏项目操作
const handleFavoriteAction = (command) => {
  const { action, favorite } = command
  switch (action) {
    case 'view':
      viewFavoriteProject(favorite)
      break
    case 'remove':
      removeFavorite(favorite)
      break
  }
}

// 取消收藏
const removeFavorite = async (favorite) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏"${favorite.resourceTitle}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await removeFavoriteById(favorite.id)
    ElMessage.success('取消收藏成功')
    fetchFavorites() // 重新加载收藏列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 打开GitHub链接
const openGithub = (url) => {
  window.open(url, '_blank')
}

// 打开资源链接
const openResource = (url) => {
  window.open(url, '_blank')
}

// 初始化用户信息
const initUserInfo = async () => {
  try {
    // 首先从authStore获取基本信息
    const currentUser = authStore.user
    
    userInfo.value = {
      username: currentUser.username || 'developer',
      nickname: currentUser.nickname || '',
      bio: currentUser.bio || '暂无个人简介',
      avatar: currentUser.avatarUrl || '/default-avatar.png',
      repositoriesCount: 0,
      starredCount: 0
    }

    // 如果需要更完整的信息，调用API获取
    try {
      const { default: http } = await import('@/utils/http')
      const response = await http.get('/users/me')
      const userData = response.data?.data
      
      if (userData) {
        userInfo.value = {
          username: userData.username || userInfo.value.username,
          nickname: userData.nickname || userInfo.value.nickname,
          bio: userData.bio || userInfo.value.bio,
          avatar: userData.avatarUrl || userInfo.value.avatar,
          repositoriesCount: 0,
          starredCount: 0
        }
        
        // 同时更新authStore中的用户信息
        authStore.updateProfile(userData)
      }
    } catch (apiError) {
      console.warn('获取用户详细信息失败，使用基本信息:', apiError)
    }
  } catch (error) {
    console.error('初始化用户信息失败:', error)
    // 使用默认值
    userInfo.value = {
      username: 'developer',
      nickname: '',
      bio: '暂无个人简介',
      avatar: '/default-avatar.png',
      repositoriesCount: 0,
      starredCount: 0
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  authStore.loadFromStorage()
  if (!authStore.isLoggedIn) {
    // 未登录时静默处理，不跳转页面
    console.log('用户未登录，静默处理')
    return
  }
  
  // 检查token是否已过期
  if (authStore.isTokenExpired()) {
    authStore.logout()
    // 静默清除登录状态，不跳转页面
    console.log('Token已过期，静默清除登录状态')
    return
  }
  
  initUserInfo()
  fetchMyProjects()
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.project-repository {
  min-height: 100vh;
  background: #f6f8fa;
}

.github-header {
  background: #24292f;
  border-bottom: 1px solid #30363d;
  color: white;
  padding: 2rem 0;

  .header-container {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-main {
    display: flex;
    align-items: center;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 1rem;

    .user-avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      border: 2px solid #30363d;
    }

    .user-details {
      .username {
        font-size: 2rem;
        font-weight: 600;
        margin: 0 0 0.5rem 0;
        color: white;
      }

      .user-bio {
        color: #8b949e;
        margin: 0;
        font-size: 1rem;
      }
    }
  }

  .header-stats {
    display: flex;
    gap: 2rem;

    .stat-item {
      text-align: center;
      
      .stat-number {
        display: block;
        font-size: 1.5rem;
        font-weight: 600;
        color: white;
      }

      .stat-label {
        display: block;
        font-size: 0.875rem;
        color: #8b949e;
        margin-top: 0.25rem;
      }
    }
  }
}

.github-nav {
  background: white;
  border-bottom: 1px solid #d0d7de;
  position: sticky;
  top: 0;
  z-index: 100;

  .nav-container {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .nav-tabs {
    display: flex;
    gap: 0;

    .nav-tab {
      background: none;
      border: none;
      padding: 1rem 1.5rem;
      color: #656d76;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
      border-bottom: 2px solid transparent;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.2s ease;

      &:hover {
        color: #24292f;
        background: #f6f8fa;
      }

      &.active {
        color: #24292f;
        border-bottom-color: #fd7e14;
      }

      .tab-count {
        background: #d0d7de;
        color: #24292f;
        padding: 0.125rem 0.5rem;
        border-radius: 1rem;
        font-size: 0.75rem;
        font-weight: 500;
        min-width: 1.5rem;
        text-align: center;
      }

      &.active .tab-count {
        background: #fd7e14;
        color: white;
      }
    }
  }

  .nav-actions {
    .new-repo-btn {
      background: #238636;
      border-color: #238636;
      color: white;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 0.5rem;

      &:hover {
        background: #2ea043;
        border-color: #2ea043;
      }
    }
  }
}

.main-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  background: #f6f8fa;
  min-height: calc(100vh - 200px);
}

.repositories-section,
.starred-section {
  .repo-search,
  .starred-search {
    margin-bottom: 1.5rem;

    .search-input {
      max-width: 400px;

      :deep(.el-input__wrapper) {
        border: 1px solid #d0d7de;
        border-radius: 6px;
        background: white;

        &:hover {
          border-color: #0969da;
        }

        &.is-focus {
          border-color: #0969da;
          box-shadow: 0 0 0 3px rgba(9, 105, 218, 0.1);
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    background: white;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    margin: 2rem 0;

    .empty-icon {
      font-size: 4rem;
      color: #656d76;
      margin-bottom: 1.5rem;
    }

    .empty-title {
      font-size: 1.5rem;
      font-weight: 600;
      color: #24292f;
      margin-bottom: 0.5rem;
    }

    .empty-description {
      color: #656d76;
      margin-bottom: 2rem;
      font-size: 1rem;
      line-height: 1.5;
    }

    .empty-action-btn {
      background: #238636;
      border-color: #238636;
      color: white;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      text-decoration: none;
      transition: all 0.2s ease;

      &:hover {
        background: #2ea043;
        border-color: #2ea043;
        transform: translateY(-1px);
      }
    }
  }
}

.repo-items,
.starred-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.repo-item,
.starred-item {
  background: white;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  padding: 1.5rem;
  transition: all 0.2s ease;

  &:hover {
    border-color: #0969da;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .repo-main,
  .starred-main {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.5rem;

    .repo-info,
    .starred-info {
      flex: 1;

      .repo-name,
      .starred-title-section {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.5rem;

        .repo-name,
        .starred-title {
          font-size: 1.25rem;
          font-weight: 600;
          color: #0969da;
          margin: 0;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 0.5rem;

          &:hover {
            text-decoration: underline;
          }

          i {
            color: #656d76;
            font-size: 1rem;
          }
        }

        .author-repo {
          display: flex;
          align-items: center;
          gap: 0.5rem;

          .author-avatar-small {
            width: 20px;
            height: 20px;
            border-radius: 50%;
          }

          .author-name {
            color: #656d76;
            font-weight: 500;
          }

          .repo-separator {
            color: #656d76;
          }

          .starred-title {
            font-size: 1rem;
            margin: 0;
          }
        }
      }

      .repo-visibility,
      .starred-visibility {
        background: #f6f8fa;
        color: #656d76;
        padding: 0.125rem 0.5rem;
        border-radius: 1rem;
        font-size: 0.75rem;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;

        i {
          font-size: 0.625rem;
        }
      }
    }

    .repo-actions,
    .starred-actions {
      display: flex;
      gap: 0.5rem;
      align-items: center;

      .edit-btn,
      .delete-btn,
      .star-btn,
      .unstar-btn {
        background: #f6f8fa;
        border: 1px solid #d0d7de;
        color: #24292f;
        font-size: 0.75rem;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 0.25rem;
        padding: 0.375rem 0.75rem;
        border-radius: 6px;
        transition: all 0.2s ease;

        &:hover {
          background: #f3f4f6;
          border-color: #d0d7de;
        }

        i {
          font-size: 0.75rem;
        }
      }

      .edit-btn {
        background: #f0f9ff;
        border-color: #0969da;
        color: #0969da;

        &:hover {
          background: #dbeafe;
          border-color: #0969da;
        }
      }

      .delete-btn {
        background: #fff5f5;
        border-color: #f85149;
        color: #f85149;

        &:hover {
          background: #fef2f2;
          border-color: #f85149;
        }
      }

      .unstar-btn {
        background: #fff8e1;
        border-color: #ffc107;
        color: #f57c00;

        &:hover {
          background: #fff3cd;
        }
      }

      .more-btn {
        color: #656d76;
        padding: 0.375rem;
        border: none;
        background: none;

        &:hover {
          background: #f6f8fa;
          border-radius: 6px;
        }
      }
    }
  }

  .repo-description,
  .starred-description {
    color: #656d76;
    line-height: 1.5;
    margin: 0.75rem 0;
    font-size: 0.875rem;
  }

  .repo-meta,
  .starred-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.75rem;
    color: #656d76;
    margin-top: 1rem;

    .repo-stats,
    .starred-stats {
      display: flex;
      align-items: center;
      gap: 1rem;

      .language-color {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        display: inline-block;
      }

      .language {
        font-weight: 500;
      }

      .stat-item {
        display: flex;
        align-items: center;
        gap: 0.25rem;

        i {
          font-size: 0.75rem;
        }
      }
    }

    .repo-updated,
    .starred-date {
      color: #656d76;
    }
  }
}

.page-header {
  background: linear-gradient(135deg, #24292f 0%, #0d1117 100%);
  padding: 3rem 0 2rem;
  color: white;
  border-bottom: 1px solid #30363d;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-main {
    .page-title {
      font-size: 2.5rem;
      font-weight: 600;
      margin-bottom: 0.5rem;
      display: flex;
      align-items: center;
      gap: 1rem;

      i {
        color: #f78166;
      }
    }

    .page-subtitle {
      font-size: 1.1rem;
      opacity: 0.8;
      margin: 0;
    }
  }

  .header-stats {
    display: flex;
    gap: 2rem;

    .stat-item {
      text-align: center;
      
      .stat-number {
        display: block;
        font-size: 2rem;
        font-weight: 700;
        color: #58a6ff;
      }

      .stat-label {
        display: block;
        font-size: 0.875rem;
        opacity: 0.8;
        margin-top: 0.25rem;
      }
    }
  }
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 2rem;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}

.left-section,
.right-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #d1d9e0;

    .section-title-wrapper {
      display: flex;
      align-items: center;
      gap: 1rem;

      .section-title {
        font-size: 1.5rem;
        font-weight: 600;
        color: #24292f;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 0.5rem;

        i {
          color: #656d76;
        }
      }

      .project-count {
        background: #f6f8fa;
        color: #656d76;
        padding: 0.25rem 0.75rem;
        border-radius: 1rem;
        font-size: 0.875rem;
        font-weight: 500;
      }
    }

    .add-project-btn {
      background: #238636;
      color: white;
      padding: 0.5rem 1rem;
      border-radius: 0.375rem;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;
      gap: 0.5rem;

      &:hover {
        background: #2ea043;
        transform: translateY(-1px);
      }

      i {
        font-size: 0.875rem;
      }
    }
  }
}

.projects-container,
.favorites-container {
  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    background: white;
    border-radius: 0.75rem;
    border: 1px solid #d1d9e0;

    .empty-icon {
      font-size: 4rem;
      color: #656d76;
      margin-bottom: 1.5rem;
    }

    .empty-title {
      font-size: 1.5rem;
      font-weight: 600;
      color: #24292f;
      margin-bottom: 0.5rem;
    }

    .empty-description {
      color: #656d76;
      margin-bottom: 2rem;
      font-size: 1rem;
    }

    .empty-action-btn {
      background: #0969da;
      color: white;
      padding: 0.75rem 1.5rem;
      border-radius: 0.375rem;
      text-decoration: none;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.2s ease;

      &:hover {
        background: #0860ca;
        transform: translateY(-1px);
      }
    }
  }
}

.projects-grid {
  display: grid;
  gap: 1.5rem;
}

.project-card {
  background: white;
  border: 1px solid #d1d9e0;
  border-radius: 0.75rem;
  padding: 1.5rem;
  transition: all 0.2s ease;

  &:hover {
    border-color: #0969da;
    box-shadow: 0 4px 12px rgba(9, 105, 218, 0.1);
    transform: translateY(-2px);
  }

  .project-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;

    .project-title-section {
      flex: 1;

      .project-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #0969da;
        margin: 0 0 0.5rem 0;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        cursor: pointer;

        &:hover {
          text-decoration: underline;
        }

        i {
          color: #656d76;
          font-size: 1rem;
        }
      }

      .project-visibility {
        background: #f6f8fa;
        color: #656d76;
        padding: 0.125rem 0.5rem;
        border-radius: 1rem;
        font-size: 0.75rem;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;

        i {
          font-size: 0.625rem;
        }
      }
    }

    .project-actions {
      .action-btn {
        color: #656d76;
        padding: 0.25rem;
        border-radius: 0.25rem;

        &:hover {
          background: #f6f8fa;
          color: #24292f;
        }
      }
    }
  }

  .project-description {
    color: #656d76;
    line-height: 1.5;
    margin-bottom: 1rem;
    font-size: 0.875rem;
  }

  .project-tech {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;

    .tech-tag {
      background: #ddf4ff;
      color: #0969da;
      padding: 0.25rem 0.75rem;
      border-radius: 1rem;
      font-size: 0.75rem;
      font-weight: 500;
    }

    .github-link {
      color: #656d76;
      font-size: 0.875rem;
      display: flex;
      align-items: center;
      gap: 0.25rem;

      i {
        font-size: 1rem;
      }
    }
  }

  .project-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.875rem;

    .stat-group {
      display: flex;
      gap: 1rem;

      .stat-item {
        color: #656d76;
        display: flex;
        align-items: center;
        gap: 0.25rem;

        i {
          font-size: 0.75rem;
        }
      }
    }

    .project-date {
      color: #656d76;
      display: flex;
      align-items: center;
      gap: 0.25rem;

      i {
        font-size: 0.75rem;
      }
    }
  }
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-item {
  background: white;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  padding: 16px;
  transition: border-color 0.2s ease;

  &:hover {
    border-color: #0969da;
  }

  .favorite-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;

    .favorite-title-section {
      flex: 1;

      .favorite-title {
        font-size: 16px;
        font-weight: 600;
        color: #0969da;
        margin: 0 0 4px 0;
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;

        &:hover {
          text-decoration: underline;
        }

        i {
          color: #f85149;
          font-size: 14px;
        }
      }

      .favorite-author {
        color: #656d76;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          font-size: 12px;
        }
      }
    }

    .favorite-actions {
      margin-left: 16px;
      
      .action-btn {
        color: #656d76;
        padding: 4px;
        border-radius: 4px;

        &:hover {
          background: #f6f8fa;
          color: #24292f;
        }
      }
    }
  }

  .favorite-description {
    color: #656d76;
    line-height: 1.5;
    margin: 8px 0;
    font-size: 14px;
  }

  .favorite-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #656d76;
    margin-top: 12px;
    
    .favorite-date {
      display: flex;
      align-items: center;
      gap: 4px;

      i {
        font-size: 10px;
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #d1d9e0;

  :deep(.el-pagination) {
    .el-pager li {
      background: white;
      border: 1px solid #d1d9e0;
      color: #24292f;

      &:hover {
        background: #f6f8fa;
      }

      &.is-active {
        background: #0969da;
        border-color: #0969da;
        color: white;
      }
    }

    .btn-prev,
    .btn-next {
      background: white;
      border: 1px solid #d1d9e0;
      color: #24292f;

      &:hover {
        background: #f6f8fa;
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .page-header {
    .header-content {
      flex-direction: column;
      gap: 2rem;
      text-align: center;
    }

    .header-stats {
      justify-content: center;
    }
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 1rem;
  }

  .page-header {
    padding: 2rem 0 1rem;

    .header-main .page-title {
      font-size: 2rem;
    }

    .header-stats {
      gap: 1rem;

      .stat-item .stat-number {
        font-size: 1.5rem;
      }
    }
  }

  .left-section,
  .right-section {
    .section-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 1rem;

      .section-title-wrapper {
        width: 100%;
      }

      .add-project-btn {
        align-self: stretch;
        justify-content: center;
      }
    }
  }

  .project-card {
    padding: 1rem;

    .project-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 1rem;

      .project-actions {
        align-self: flex-end;
      }
    }

    .project-stats {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }
  }

  .favorite-item {
    padding: 1rem;

    .favorite-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 1rem;

      .favorite-actions {
        align-self: flex-end;
      }
    }
  }
}
</style>