<template>
  <MainLayout>
    <div class="articles-page">
      <!-- 页面标题区 -->
      <section class="page-header">
        <div class="container">
          <h1 class="page-title">华俱微信推文</h1>
          <p class="page-description">
            这里汇集了"XDU华创"微信公众号的精选文章，掌握社团最新动态
          </p>
        </div>
      </section>

      <!-- 搜索区 -->
      <section class="filter-section">
        <div class="container">
          <div class="filters">
            <div class="filter-right">
              <div class="search-group">
                <label class="search-label">搜索文章</label>
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索文章标题或摘要..." 
                  prefix-icon="Search"
                  clearable
                  @input="handleSearch"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 文章列表 -->
      <section class="articles-section">
        <div class="container">
          <div v-if="articles.length > 0" class="articles-container">
            <el-timeline>
              <el-timeline-item
                v-for="(article, index) in articles"
                :key="index"
                :timestamp="article.date"
                placement="top"
              >
                <el-card class="article-card" @click="openArticle(article)">
                  <div class="article-content">
                    <!-- 左侧图片 -->
                    <div v-if="article.image" class="article-image-left">
                      <img :src="article.image" :alt="article.title" @error="handleImageError">
                    </div>
                    
                    <!-- 右侧内容 -->
                    <div class="article-info">
                      <div class="article-header">
                        <h3 class="article-title">{{ article.title }}</h3>
                      </div>
                      
                      <p class="article-summary">{{ article.summary }}</p>
                      
                      <div class="article-meta">
                        <span class="meta-item">
                          <font-awesome-icon icon="eye" />
                          <span>{{ article.views }}次阅读</span>
                        </span>
                      </div>
                      
                      <el-button class="read-more-btn" type="primary" text>
                        阅读全文 <font-awesome-icon icon="arrow-right" />
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            
            <!-- 分页 -->
            <div class="pagination-container" v-if="totalArticles > 0">
              <el-pagination
                v-model="currentPage"
                :page-size="pageSize"
                :total="totalArticles"
                layout="prev, pager, next"
                @current-change="handlePageChange"
              />
            </div>
          </div>

          <!-- 无文章 -->
          <div v-else class="empty-container">
            <font-awesome-icon icon="newspaper" class="empty-icon" />
            <h2 class="empty-title">暂无文章</h2>
            <p class="empty-description">
              暂时还没有发布任何文章，请稍后再来查看。
            </p>
          </div>
        </div>
      </section>

      <!-- 订阅提示 -->
      <section class="subscription-section">
        <div class="container">
          <div class="subscription-card">
            <div class="subscription-content">
              <div class="qrcode">
                <img src="../assets/qrcode.jpg" alt="微信公众号二维码" class="qrcode-img">
              </div>
              <div class="subscription-text">
                <h3 class="subscription-title">关注公众号，获取最新内容</h3>
                <p class="subscription-description">
                  扫描二维码关注"XDU华创"微信公众号，及时获取华为俱乐部最新动态和技术分享。
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '../layouts/MainLayout.vue'
import { getArticles, incrementArticleView } from '../api/articles'
import { config } from '../utils/config.js'

// 状态变量
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(6)
const articles = ref([])
const totalArticles = ref(0)

// API调用函数
const fetchArticles = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      search: searchQuery.value || ''
    }
    const response = await getArticles(params)
    
    if (response.data.success) {
      const data = response.data.data
      const articleList = data.data || []
      
      // 转换数据格式以匹配前端期望的结构
      const transformedArticles = articleList.map(article => {
        const imageUrl = article.coverImage ? config.getUploadUrl(article.coverImage) : null
        return {
          id: article.id,
          title: article.title,
          summary: article.summary,
          date: new Date(article.publishTime).toLocaleDateString('zh-CN'),
          image: imageUrl,
          views: article.viewCount,
          url: article.linkUrl
        }
      })
      
      articles.value = transformedArticles
      totalArticles.value = data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取文章数据失败')
    }
  } catch (error) {
    console.error('获取文章数据失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 计算属性已移除，现在使用后端分页

// 搜索处理函数
const handleSearch = () => {
  currentPage.value = 1 // 重置页码
  fetchArticles()
}

// 分页处理函数
const handlePageChange = (page) => {
  currentPage.value = page
  fetchArticles()
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 图片加载错误处理
const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  // 可以设置默认图片
  // event.target.src = '/default-article-image.jpg'
}

// 生命周期钩子
onMounted(() => {
  fetchArticles()
})



const openArticle = async (article) => {
  try {
    // 调用后端API增加浏览量
    await incrementArticleView(article.id)
    // 更新本地显示的浏览量
    article.views = (article.views || 0) + 1
    
    // 打开文章链接
    if (article.url) {
      window.open(article.url, '_blank')
    } else {
      ElMessage.warning('该文章暂无可访问链接')
    }
  } catch (error) {
    console.error('增加浏览次数失败:', error)
    // 即使增加浏览次数失败，也要打开链接
    if (article.url) {
      window.open(article.url, '_blank')
    } else {
      ElMessage.warning('该文章暂无可访问链接')
    }
  }
}


</script>

<style lang="scss" scoped>
@import '../styles/variables.scss';

.articles-page {
  min-height: 100vh;
  background-color: #f8fafc;
}

/* 页面标题区 */
.page-header {
  background-color: #f0f2f5;
  padding: 60px 0;
  margin-bottom: 0; // 移除下方间隔
  text-align: center;
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
  margin-bottom: $spacing-xl;
}

.search-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  
  .search-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin-bottom: 4px;
  }
  
  .search-input {
    max-width: 300px;
    width: 100%;
  }
}

/* 文章列表 */
.articles-container {
  margin-bottom: $spacing-2xl;
}

.article-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  }
}

.article-content {
  display: flex;
  gap: $spacing-lg;
  align-items: flex-start;
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.article-image-left {
  flex-shrink: 0;
  width: 120px;
  height: 90px;
  
  @media (max-width: $breakpoint-sm) {
    width: 100%;
    height: 200px;
  }
  
  img {
    width: 100%;
    height: 100%;
    border-radius: $border-radius-base;
    object-fit: cover;
  }
}

.article-info {
  flex: 1;
  min-width: 0; // 防止flex子元素溢出
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-md;
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    gap: $spacing-sm;
  }
}

.article-title {
  font-size: $font-size-lg;
  margin: 0;
  flex: 1;
  margin-right: $spacing-md;
  line-height: 1.4;
}

.article-image {
  margin-bottom: $spacing-md;
  
  img {
    width: 100%;
    border-radius: $border-radius-base;
    object-fit: cover;
    max-height: 200px;
  }
}

.article-summary {
  color: $gray-dark-color;
  margin-bottom: $spacing-md;
}

.article-meta {
  display: flex;
  gap: $spacing-lg;
  margin-bottom: $spacing-md;
  
  .meta-item {
    display: flex;
    align-items: center;
    color: $gray-dark-color;
    font-size: $font-size-sm;
    
    .svg-inline--fa {
      margin-right: 6px;
    }
  }
}

.read-more-btn {
  .svg-inline--fa {
    margin-left: 4px;
    transition: transform 0.2s;
  }
  
  &:hover .svg-inline--fa {
    transform: translateX(3px);
  }
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: 20px 0;
}

.el-pagination {
  --el-pagination-button-color: #606266;
  --el-pagination-hover-color: #409eff;
  --el-pagination-button-bg-color: #fff;
  --el-pagination-button-disabled-color: #c0c4cc;
  --el-pagination-button-disabled-bg-color: #fff;
  --el-pagination-hover-bg-color: #f5f7fa;
}

/* 订阅提示 */
.subscription-section {
  margin-bottom: $spacing-2xl;
}

.subscription-card {
  background-color: #f6f9fe;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.subscription-content {
  display: flex;
  align-items: center;
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    text-align: center;
    gap: $spacing-lg;
  }
}

.qrcode {
  margin-right: $spacing-xl;
  
  @media (max-width: $breakpoint-sm) {
    margin-right: 0;
  }
  
  .qrcode-img {
    width: 120px;
    height: 120px;
    border-radius: $border-radius-base;
  }
}

.subscription-text {
  flex: 1;
}

.subscription-title {
  font-size: $font-size-lg;
  margin-bottom: $spacing-sm;
  color: $dark-color;
}

.subscription-description {
  color: $gray-dark-color;
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
</style>