<template>
  <MainLayout>
    <div class="projects-page">
      <!-- 页面标题区 -->
      <section class="page-header">
        <div class="container">
          <h1 class="page-title">项目分享</h1>
          <p class="page-description">
            在这里，西电华为创新俱乐部成员可以展示和分享自己的项目成果，促进技术交流和合作
          </p>
        </div>
      </section>

      <!-- 筛选与搜索区 -->
      <section class="filter-section">
        <div class="container">
          <div class="filters">
            <div class="filter-left">
              <div class="filter-group">
                <label class="filter-label">技术领域</label>
                <el-select v-model="techCategory" placeholder="选择技术领域" clearable @change="handleFilter">
                  <el-option
                    v-for="item in techCategories"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </div>
            </div>
            
            <div class="filter-right">
              <div class="search-group">
                <label class="search-label">搜索项目</label>
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索项目标题或描述..." 
                  prefix-icon="Search"
                  clearable
                  @input="handleSearch"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 项目列表 -->
      <section class="projects-section">
        <div class="container">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 空状态 -->
          <div v-else-if="filteredProjects.length === 0" class="empty-container">
            <font-awesome-icon icon="folder-open" class="empty-icon" />
            <h2 class="empty-title">暂无项目</h2>
            <p class="empty-description">
              目前还没有符合条件的项目。您可以尝试更改筛选条件，或者分享您自己的项目！
            </p>
          </div>

          <!-- 项目列表 -->
          <div v-else class="projects-container">
            <div class="projects-count">
              共找到 <span class="count">{{ filteredProjects.length }}</span> 个项目
            </div>
            
            <div class="projects-list">
              <div 
                v-for="(project, index) in filteredProjects" 
                :key="index"
                class="project-card"
              >
                <div class="project-header">
                  <a @click.prevent="handleProjectClick(project.id)" href="#" class="project-title-link">
                    <h3 class="project-title">{{ project.title }}</h3>
                  </a>
                  <div class="project-author">
                    <img :src="project.authorAvatar" alt="作者头像" class="author-avatar" />
                    <span class="author-name">{{ project.authorName }}</span>
                  </div>
                </div>
                
                <p class="project-description">{{ project.description }}</p>
                
                <div class="project-meta">
                  <div class="meta-item">
                    <font-awesome-icon icon="calendar" />
                    <span>{{ formatDate(project.createdAt) }}</span>
                  </div>
                  <div class="meta-item">
                    <font-awesome-icon icon="tag" />
                    <span>{{ getCategoryLabel(project.category) }}</span>
                  </div>
                </div>
                
                <div class="project-tags">
                  <span 
                    v-for="(tag, tagIndex) in project.tags" 
                    :key="tagIndex"
                    class="project-tag"
                  >
                    {{ getTagLabel(tag) }}
                  </span>
                </div>
                
                <div class="project-stats">
                  <div class="stat-item">
                    <font-awesome-icon icon="eye" />
                    <span>{{ project.viewCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <font-awesome-icon icon="star" />
                    <span>{{ project.starCount || 0 }}</span>
                  </div>
                </div>
                
                <div class="project-actions">
                  <a @click.prevent="handleProjectClick(project.id)" href="#" class="action-link primary">
                    查看详情
                  </a>
                  <a :href="project.repoUrl" target="_blank" class="action-link secondary" v-if="project.repoUrl">
                    <font-awesome-icon :icon="['fab', 'github']" />
                    源码仓库
                  </a>
                </div>
              </div>
            </div>
            
            <!-- 分页 -->
            <div class="pagination-container" v-if="filteredProjects.length > 0">
              <el-pagination
                v-model="currentPage"
                :page-size="pageSize"
                :total="filteredProjects.length"
                layout="prev, pager, next"
                @current-change="handlePageChange"
              />
            </div>
          </div>
        </div>
      </section>

      <!-- 项目分享引导 -->
      <section class="contribute-section">
        <div class="container">
          <div class="contribute-card">
            <div class="contribute-content">
              <div class="contribute-text">
                <h3 class="contribute-title">分享你的项目</h3>
                <p class="contribute-description">
                  你的项目可能正是其他同学学习的好榜样，快来分享你的创新成果吧！
                </p>
              </div>
              <router-link to="/projects/upload">
                <el-button type="primary" size="large">
                  <font-awesome-icon icon="plus" />
                  分享我的项目
                </el-button>
              </router-link>
            </div>
          </div>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { getProjects, incrementProjectView } from '../api/projects'
import { config } from '../utils/config.js'

const router = useRouter()

// 状态变量
const loading = ref(true)
const techCategory = ref('')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const projects = ref([])

// 技术分类选项
const techCategories = [
  { value: 'all', label: '全部分类' },
  { value: 'software', label: '软件开发' },
  { value: 'hardware', label: '硬件设计' },
  { value: 'embedded', label: '嵌入式系统' },
  { value: 'ai', label: '人工智能' },
  { value: 'other', label: '其他' }
]

// 分类映射
const categoryMap = {
  'software': '软件开发',
  'hardware': '硬件设计', 
  'embedded': '嵌入式系统',
  'ai': '人工智能',
  'other': '其他'
}

// 标签映射
const tagMap = {
  'frontend': '前端',
  'backend': '后端',
  'fullstack': '全栈',
  'mobile': '移动端',
  'web': '网页',
  'app': '应用',
  'game': '游戏',
  'ai': '人工智能',
  'ml': '机器学习',
  'dl': '深度学习',
  'cv': '计算机视觉',
  'nlp': '自然语言处理',
  'iot': '物联网',
  'embedded': '嵌入式',
  'hardware': '硬件',
  'software': '软件',
  'algorithm': '算法',
  'database': '数据库',
  'network': '网络',
  'security': '安全',
  'blockchain': '区块链',
  'cloud': '云计算',
  'devops': '运维',
  'testing': '测试',
  'ui': '用户界面',
  'ux': '用户体验',
  'design': '设计',
  'prototype': '原型',
  'research': '研究',
  'innovation': '创新',
  'competition': '竞赛',
  'project': '项目',
  'team': '团队',
  'individual': '个人',
  'open-source': '开源',
  'commercial': '商业',
  'academic': '学术',
  'tutorial': '教程',
  'demo': '演示',
  'tool': '工具',
  'framework': '框架',
  'library': '库',
  'api': '接口',
  'service': '服务',
  'platform': '平台',
  'system': '系统',
  'application': '应用程序'
}

// 获取项目数据
const fetchProjects = async () => {
  try {
    loading.value = true
    
    const response = await getProjects({
      page: 1,
      size: 100, // 获取所有项目用于前端筛选
      category: '',
      keyword: '',
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
    
    if (response.data && response.data.success) {
      // 处理后端返回的数据结构
      const projectsData = response.data.data.data || response.data.data || []
      
      if (Array.isArray(projectsData)) {
        projects.value = projectsData.map(project => ({
          id: project.id,
          title: project.title,
          description: project.description,
          authorName: project.authorName || project.author?.nickname || project.author?.username || '匿名用户',
          authorAvatar: config.getAvatarUrl(project.authorAvatar),
          category: project.category,
          tags: project.tags ? (Array.isArray(project.tags) ? project.tags : project.tags.split(',')) : [],
          viewCount: project.viewCount || project.views || 0,
          starCount: project.starCount || project.stars || 0,
          createdAt: project.createdAt,
          repoUrl: project.githubUrl || project.repoUrl || '',
          demoUrl: project.demoUrl || ''
        }))
      } else {
        projects.value = []
      }
    } else {
      projects.value = []
    }
  } catch (error) {
    console.error('获取项目数据失败:', error)
    projects.value = []
  } finally {
    loading.value = false
  }
}

// 计算筛选后的项目
const filteredProjects = computed(() => {
  let result = [...projects.value]
  
  // 按技术分类筛选
  if (techCategory.value && techCategory.value !== 'all') {
    result = result.filter(p => 
      p.category === techCategory.value ||
      (p.tags && p.tags.some(tag => tag.includes(techCategory.value)))
    )
  }
  
  // 搜索
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(p => 
      p.title.toLowerCase().includes(query) || 
      p.description.toLowerCase().includes(query) ||
      (p.tags && p.tags.some(tag => tag.toLowerCase().includes(query)))
    )
  }
  
  // 默认按最新发布排序
  result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  
  return result
})

// 生命周期钩子
onMounted(() => {
  fetchProjects()
  
  // 监听页面可见性变化，当用户返回页面时重新获取数据
  const handleVisibilityChange = () => {
    if (!document.hidden) {
      // 页面变为可见时，重新获取项目数据以显示最新的浏览量
      fetchProjects()
    }
  }
  
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 组件卸载时移除监听器
  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange)
  })
})

// 方法
const handleFilter = () => {
  currentPage.value = 1 // 重置页码
}

const handleSearch = () => {
  currentPage.value = 1 // 重置页码
}

const handlePageChange = (page) => {
  currentPage.value = page
  // 滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 处理项目详情点击
const handleProjectClick = async (projectId) => {
  // 使用Vue Router跳转到项目详情页，避免页面刷新
  router.push(`/projects/${projectId}`)
}

// 获取分类标签
const getCategoryLabel = (category) => {
  return categoryMap[category] || category
}

// 获取标签标签
const getTagLabel = (tag) => {
  return tagMap[tag.toLowerCase()] || tag
}



// 辅助函数
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}
</script>

<style lang="scss" scoped>
// 导入变量文件
@import '../styles/variables';

.projects-page {
  padding-bottom: 0; // 移除底部padding，让contribute-section紧贴底部
}

/* 页面标题区 */
.page-header {
  background-color: #f0f2f5;
  padding: 60px 0;
  margin-bottom: 0; // 移除下方间隔
  text-align: center;
}

.page-title {
  font-size: 30px;
  color: #2d3748;
  margin-bottom: 1rem;
  font-weight: 700;
}

.page-description {
  max-width: 800px;
  color: $gray-dark-color;
  line-height: 1.6;
  text-align: center;
  margin: 0 auto;
}

/* 筛选部分 */
.filter-section {
  margin-bottom: $spacing-xl;
  background-color: white;
  padding: $spacing-lg;
  border-radius: $border-radius-lg;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: stretch;
  }
}

.filter-left {
  display: flex;
  gap: $spacing-lg;
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.filter-group {
  display: flex;
  flex-direction: column;
  min-width: 180px;
  
  @media (max-width: $breakpoint-sm) {
    margin-bottom: $spacing-md;
  }
}

.filter-label {
  font-weight: 500;
  margin-bottom: $spacing-sm;
  color: $gray-dark-color;
}

.filter-right {
  @media (max-width: $breakpoint-md) {
    margin-top: $spacing-md;
    width: 100%;
  }
}

.search-group {
  width: 300px;
  
  @media (max-width: $breakpoint-md) {
    width: 100%;
  }
  
  .search-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin-bottom: 8px;
    display: block;
  }
}

/* 项目列表 */
.projects-section {
  min-height: 500px;
  padding-bottom: $spacing-2xl;
}

.projects-count {
  margin-bottom: $spacing-lg;
  color: $gray-dark-color;
  
  .count {
    font-weight: 600;
    color: $dark-color;
  }
}

.projects-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xl;
  margin-bottom: $spacing-2xl;
  
  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.project-card {
  background-color: white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-md;
  gap: $spacing-md;
}

.project-title {
  font-size: $font-size-lg;
  margin: 0;
  line-height: 1.4;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-title-link {
  color: $dark-color;
  transition: color 0.2s;
  flex: 1;
  min-width: 0;
  
  &:hover {
    color: $primary-color;
  }
}

.project-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: $spacing-xs;
}

.author-name {
  font-size: $font-size-sm;
  color: $gray-dark-color;
}

.project-description {
  color: $gray-dark-color;
  margin-bottom: $spacing-lg;
  line-height: 1.5;
  height: 4.5em; // 固定高度，显示三行
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.project-meta {
  display: flex;
  margin-bottom: $spacing-md;
  
  .meta-item {
    display: flex;
    align-items: center;
    margin-right: $spacing-lg;
    color: $gray-dark-color;
    font-size: $font-size-sm;
    
    .svg-inline--fa {
      margin-right: 4px;
    }
  }
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;
  margin-bottom: $spacing-lg;
}

.project-tag {
  display: inline-block;
  padding: 4px 10px;
  background-color: rgba($secondary-color, 0.1);
  color: $secondary-color;
  border-radius: $border-radius-full;
  font-size: $font-size-xs;
}

.project-stats {
  display: flex;
  margin-bottom: $spacing-lg;
  
  .stat-item {
    display: flex;
    align-items: center;
    margin-right: $spacing-lg;
    color: $gray-dark-color;
    
    .svg-inline--fa {
      margin-right: 4px;
    }
  }
}

.project-actions {
  display: flex;
  gap: $spacing-md;
}

.action-link {
  display: inline-flex;
  align-items: center;
  padding: 8px 16px;
  border-radius: $border-radius-base;
  font-size: $font-size-sm;
  font-weight: 500;
  transition: all 0.2s;
  
  &.primary {
    color: white;
    background-color: $primary-color;
    
    &:hover {
      background-color: darken($primary-color, 10%);
    }
  }
  
  &.secondary {
    color: $dark-color;
    background-color: $gray-light-color;
    
    &:hover {
      background-color: darken($gray-light-color, 5%);
    }
    
    .svg-inline--fa {
      margin-right: 4px;
    }
  }
}

/* 空状态 */
.empty-container {
  text-align: center;
  padding: $spacing-3xl 0;
}

.empty-icon {
  font-size: 64px;
  color: $gray-color;
  margin-bottom: $spacing-lg;
}

.empty-title {
  font-size: $font-size-xl;
  margin-bottom: $spacing-md;
  color: $dark-color;
}

.empty-description {
  color: $gray-dark-color;
  max-width: 400px;
  margin: 0 auto;
  margin-bottom: $spacing-lg;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
}

/* 加载状态 */
.loading-container {
  padding: $spacing-xl 0;
}

/* 项目分享引导部分 */
.contribute-section {
  padding: 60px 0;
  background: white;
}

.contribute-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.contribute-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 30px;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    text-align: center;
  }
}

.contribute-text {
  flex: 1;
  text-align: left;
  
  @media (max-width: $breakpoint-md) {
    text-align: center;
  }
}

.contribute-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

.contribute-description {
  color: #718096;
  line-height: 1.6;
  margin: 0;
}
</style>