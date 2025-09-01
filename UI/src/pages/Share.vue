<template>
  <MainLayout>
    <div class="shares-page">
      <!-- 页面标题区 -->
      <section class="page-header">
        <div class="container">
          <h1 class="page-title">师兄师姐说</h1>
          <p class="page-description">
            学长学姐学习就业的经验分享，为学弟学妹提供宝贵的成长指导
          </p>
        </div>
      </section>

      <!-- 优秀校友风采 -->
      <section class="contributors-section">
        <div class="container">
          <h2 class="section-title">优秀华创er</h2>
          <div class="contributor-cards-centered">
            <div 
              v-for="(contributor, index) in contributors" 
              :key="index"
              class="contributor-card"
              @click="filterByContributor(contributor.id)"
            >
              <div class="contributor-avatar">
                <img :src="contributor.avatar" :alt="contributor.name">
              </div>
              <h3 class="contributor-name">{{ contributor.name }}</h3>
              <p class="contributor-position">{{ contributor.position }}</p>
              <p class="contributor-company">{{ contributor.company }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 搜索区 -->
      <section class="filter-section">
        <div class="container">
          <div class="filters">
            <div class="filter-right">
              <div class="search-group">
                <label class="search-label">搜索经验</label>
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索经验标题或内容..." 
                  prefix-icon="Search"
                  clearable
                  @input="handleSearch"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 经验分享列表 -->
      <section class="shares-section">
        <div class="container">


          <!-- 加载状态 -->
          <div v-if="loading && currentPage === 1" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 分享列表 -->
          <div v-else-if="shares.length > 0" class="shares-container">
            <div class="shares-grid">
              <el-card 
                v-for="share in shares" 
                :key="share.id" 
                class="share-card"
                @click="openDetail(share.id)"
              >
                <div class="share-content">
                  <div class="share-header">
                    <h3 class="share-title">{{ share.title }}</h3>
                    <div class="share-tags">
                      <el-tag size="small" :type="getCategoryType(share.category)">
                        {{ share.categoryLabel }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <p class="share-summary">{{ share.summary }}</p>
                  
                  <div class="share-meta">
                    <span class="meta-item">
                      <font-awesome-icon icon="eye" />
                      <span>{{ share.views }}次浏览</span>
                    </span>
                    <span class="meta-item">
                      <font-awesome-icon icon="calendar" />
                      <span>{{ formatDate(share.date) }}</span>
                    </span>
                  </div>
                  
                  <el-button class="read-more-btn" type="primary" text>
                    查看详情 <font-awesome-icon icon="arrow-right" />
                  </el-button>
                </div>
              </el-card>
            </div>
            
            <!-- 分页 -->
            <div class="pagination-container" v-if="totalShares > 0">
              <el-pagination
                :current-page="currentPage"
                :page-size="pageSize"
                :total="totalShares"
                layout="prev, pager, next"
                @current-change="handlePageChange"
              />
            </div>
          </div>

          <!-- 无搜索结果 -->
          <div v-else class="empty-container">
            <font-awesome-icon icon="file-alt" class="empty-icon" />
            <h2 class="empty-title">暂无相关经验</h2>
            <p class="empty-description">
              没有找到符合条件的经验分享，请尝试调整筛选条件或搜索关键词。
            </p>
            <el-button @click="resetFilters">重置筛选</el-button>
          </div>
        </div>
      </section>

      <!-- 分享引导 -->
      <section class="contribute-section">
        <div class="container">
          <div class="contribute-card">
            <div class="contribute-content">
              <div class="contribute-text">
                <h3 class="contribute-title">分享你的经验</h3>
                <p class="contribute-description">
                  你的经验可能正是学弟学妹们需要的指导，快来分享你的成长故事吧！
                </p>
              </div>
              <el-button type="primary" size="large" @click="goToUpload">
                我要分享
              </el-button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { getShares } from '../api/shares'
// 使用 new URL() 方式确保开发和构建环境都能正确处理
const xy1 = new URL('../assets/xy1.jpg', import.meta.url).href
const xy2 = new URL('../assets/xy2.jpg', import.meta.url).href
const xy3 = new URL('../assets/xy3.jpg', import.meta.url).href
const xy4 = new URL('../assets/xy4.jpg', import.meta.url).href

const router = useRouter()

// 校友数据
const contributors = [
  {
    id: 1,
    name: '付友泉',
    avatar: xy1,
    position: '高瓴人工智能学院',
    company: '中国人民大学',
  },
  {
    id: 2,
    name: '许珈铭',
    avatar: xy2,
    position: 'DAI Lab',
    company: '上海交通大学'
  },
  {
    id: 3,
    name: '陈锦力',
    avatar: xy3,
    position: '硬件技术工程师',
    company: '华为技术有限公司'
  },
  {
    id: 4,
    name: '杨竣博',
    avatar: xy4,
    position: '人工智能学院',
    company: '西安电子科技大学'
  },
]

// 分类配置（仅用于显示，不用于筛选）
const categories = [
  { label: '学习经验', value: 'learn' },
  { label: '求职经验', value: 'job' },
  { label: '生涯规划', value: 'plan' },
  { label: '其他', value: 'others' }
]

// 筛选和搜索
const selectedContributor = ref(null)
const searchQuery = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(9)
const shares = ref([])
const totalShares = ref(0)
const totalPages = ref(0)

// 工具函数
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit' 
  })
}

// 将数据库的英文分类转换为中文显示
const getCategoryLabel = (category) => {
  const labelMap = {
    'learn': '学习经验',
    'job': '求职经验', 
    'plan': '生涯规划',
    'others': '其他'
  }
  return labelMap[category] || category
}

const getCategoryType = (category) => {
  const typeMap = {
    'learn': 'warning',
    'job': 'success',
    'plan': 'info',
    'others': 'primary'
  }
  return typeMap[category] || 'info'
}

// API调用函数
const fetchShares = async () => {
  loading.value = true
  try {
    const params = { 
      page: currentPage.value, 
      size: pageSize.value,
      search: searchQuery.value || ''
    }

    const response = await getShares(params)
    
    if (response.data && response.data.success) {
      const pageData = response.data.data || {}
      const list = pageData.data || []
      
      // 数据转换
      const normalizedShares = list.map(item => ({
        id: item.id,
        title: item.title,
        summary: item.content || '暂无摘要',
        category: item.category, // 保留原始分类值用于样式
        categoryLabel: getCategoryLabel(item.category), // 转换为中文显示
        date: item.createdAt,
        views: item.viewCount || 0
      }))
      
      shares.value = normalizedShares
      totalShares.value = pageData.total || 0
      totalPages.value = pageData.totalPages || 0
    }
  } catch (error) {
    console.error('获取经验分享失败:', error)
    shares.value = []
    totalShares.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

// 重置并重新获取数据
const resetAndFetch = () => {
  currentPage.value = 1
  fetchShares()
}

// 搜索处理函数
const handleSearch = () => {
  currentPage.value = 1 // 重置页码
  fetchShares()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  fetchShares()
  // 平滑滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 重置筛选
const resetFilters = () => {
  selectedContributor.value = null
  searchQuery.value = ''
  resetAndFetch()
}

// 按校友筛选
const filterByContributor = (contributorId) => {
  selectedContributor.value = contributorId
  currentPage.value = 1
  shares.value = []
  hasMore.value = true
  fetchShares()
}

// 打开详情页
const openDetail = (id) => {
  router.push(`/share/${id}`)
}

// 跳转到分享上传页面
const goToUpload = () => {
  router.push('/share/upload')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchShares()
})
</script>

<style lang="scss" scoped>
.shares-page {
  min-height: 100vh;
  background-color: #f8fafc;
}

.page-header {
  background-color: #f0f2f5;
  padding: 60px 0;
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
  color: #4a5568;
  line-height: 1.6;
  text-align: center;
  margin: 0 auto;
  font-size: 1.1rem;
}

/* 搜索部分 */
.filter-section {
  margin-bottom: 2rem;
  background-color: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.filters {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
}

.filter-right {
  width: 300px;
}

.search-group {
  display: flex;
  flex-direction: column;
}

.search-label {
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #4a5568;
}

.contributors-section {
  padding: 30px 0 40px 0;
  background: white;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
}

.contributor-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, 240px);
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
  justify-content: start;
}

.contributor-cards-centered {
  display: grid;
  grid-template-columns: repeat(4, 240px);
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
  justify-content: center;
  
  @media (max-width: 1024px) {
    grid-template-columns: repeat(2, 240px);
  }
  
  @media (max-width: 600px) {
    grid-template-columns: 1fr;
    justify-items: center;
  }
}

.contributor-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  width: 240px;
  height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
    border-color: #667eea;
  }
}

.contributor-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #e2e8f0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.contributor-name {
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

.contributor-position {
  font-size: 14px;
  color: #4a5568;
  margin-bottom: 6px;
}

.contributor-company {
  font-size: 13px;
  color: #718096;
}

.shares-section {
  padding: 40px 0;
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-left {
  width: 200px;
}

.filter-right {
  width: 300px;
  
  .search-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin-bottom: 8px;
    display: block;
  }
}

.loading-container {
  padding: 40px 0;
}

.shares-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 40px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.share-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: visible;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }
}

.share-content {
  padding: 20px;
  min-height: auto;
}

.share-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.share-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  flex: 1;
  margin-right: 12px;
  line-height: 1.4;
}

.share-tags {
  flex-shrink: 0;
}

.share-summary {
  color: #718096;
  line-height: 1.6;
  margin: 12px 0 16px;
  font-size: 0.95rem;
  word-wrap: break-word;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

.share-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: #a0aec0;
  font-size: 0.85rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.read-more-btn {
  font-size: 0.9rem;
  padding: 0;
  
  &:hover {
    transform: translateX(2px);
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin: 40px 0 20px 0;
}

.empty-container {
  text-align: center;
  padding: 80px 20px;
  color: #718096;
}

.empty-icon {
  font-size: 4rem;
  color: #cbd5e0;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 1.5rem;
  margin-bottom: 12px;
  color: #4a5568;
}

.empty-description {
  margin-bottom: 24px;
  line-height: 1.6;
}

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
}

.contribute-text {
  flex: 1;
  text-align: left;
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

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .filters {
    flex-direction: column;
    gap: 16px;
  }
  
  .filter-left,
  .filter-right {
    width: 100%;
  }
  
  .shares-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .contribute-content {
    flex-direction: column;
    text-align: center;
  }
  
  .contribute-text {
    text-align: center;
  }
}
</style>


