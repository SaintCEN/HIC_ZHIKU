<template>
  <MainLayout>
    <div class="announcements-page">
      <div class="page-header">
        <div class="header-cartoon header-cartoon--left">
          <img src="/src/assets/cartoon1.jpg" alt="cartoon decoration" />
        </div>
        <div class="header-content">
          <h1 class="page-title">全部公告</h1>
          <h2 class="page-subtitle">省流版的...都在这儿？</h2>
        </div>
        <div class="header-cartoon header-cartoon--right">
          <img src="/src/assets/cartoon2.png" alt="cartoon decoration" />
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载公告...</p>
      </div>

      <div v-else class="content-container">
        <!-- 公告列表 -->
        <div v-if="announcements.length > 0" class="announcements-list">
          <div v-for="a in announcements" :key="a.id" class="announcement-card announcement-card--media">
            <div class="announcement-cover">
              <img :src="getAnnouncementCoverUrl(a)" alt="announcement cover" />
            </div>
            <div class="announcement-content">
              <div class="announcement-meta">
                <span class="announcement-date">{{ formatDate(a.publish_time || a.publishTime || a.createdAt || a.date) }}</span>
                <span v-if="a.type" :class="['announcement-tag', `tag-${a.type}`]">{{ getAnnouncementTypeName(a.type) }}</span>
                <span class="announcement-views"><i class="fas fa-eye"></i> {{ (a.viewCount || a.view_count || a.views || 0) }} 次浏览</span>
              </div>
              <h3 class="announcement-title">{{ a.title }}</h3>
              <p class="announcement-excerpt">{{ a.summary || a.excerpt || a.content }}</p>
              <router-link :to="`/announcements/${a.id}`" class="announcement-link" @click.native="handleReadMore(a)">阅读更多</router-link>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty-state">
          <p>暂无公告数据</p>
        </div>

        <!-- 简化的分页组件 -->
        <div v-if="announcements.length > 0" class="simple-pagination">
          <!-- 上一页 -->
          <button 
            class="pagination-btn" 
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            上一页
          </button>
          
          <!-- 页面信息 -->
          <span class="page-info">
            第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
          </span>
          
          <!-- 下一页 -->
          <button 
            class="pagination-btn" 
            :disabled="currentPage >= totalPages || totalPages <= 1"
            @click="goToPage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import axios from 'axios'
import { config } from '../utils/config.js'

const announcements = ref([])
const loading = ref(true)

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const totalPages = ref(0)

const handleReadMore = (a) => {
  // 可在此处做点击统计上报（若后端提供接口），当前浏览量由详情接口自增
}

// 获取公告数据
const fetchAnnouncements = async (page = 1, size = pageSize.value) => {
  loading.value = true
  try {
    const { data } = await axios.get('/api/portal/announcements', { 
      params: { page: page, size: size } 
    })
    
    console.log('Announcements API response:', data)
    
    // 处理响应数据
    if (data?.data) {
      // 如果数据在 data 字段中
      announcements.value = Array.isArray(data.data) ? data.data : []
    } else if (Array.isArray(data)) {
      // 如果直接返回数组
      announcements.value = data
    } else {
      // 其他情况
      announcements.value = []
    }
    
    // 设置分页信息
    totalCount.value = data?.total || announcements.value.length
    totalPages.value = Math.max(1, Math.ceil(totalCount.value / size))
    
    currentPage.value = page
  } catch (e) {
    console.error('获取公告失败', e)
    announcements.value = []
    totalCount.value = 0
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

// 跳转到指定页面
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value && page !== currentPage.value) {
    fetchAnnouncements(page, pageSize.value)
  }
}

// 初始化时使用新的函数名
const fetchAll = () => fetchAnnouncements(1, pageSize.value)

const formatDate = (d) => {
  if (!d) return ''
  const date = new Date(d)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

const getAnnouncementTypeName = (type) => {
  const m = { competition: '竞赛通知', activity: '活动公告', resource: '资源更新', system: '系统通知', notice: '一般通知' }
  return m[type] || '一般通知'
}

const getAnnouncementCoverUrl = (a) => {
  const url = a.cover || a.cover_url || a.coverUrl || a.image || a.image_url || a.thumbnail || a.thumbnail_url || ''
  if (!url) return '/src/assets/banner1.jpg'
  return config.getUploadUrl(url) || '/src/assets/banner1.jpg'
}

onMounted(fetchAll)
</script>

<style lang="scss" scoped>
@import '../styles/variables.scss';

.announcements-page { min-height: 100vh; background-color: $background-color; }

.content-container {
  padding: $spacing-xl;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 60vh;
}
.page-header { 
  background: linear-gradient(135deg, #e8f4f8 0%, #f0f7ff 50%, #faf6ff 100%);
  color: #2d3748; 
  padding: 50px 0 60px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  position: relative;
  min-height: 200px;
  border-radius: 0 0 30px 30px;
  overflow: hidden;
  z-index: 10;
  margin-bottom: 0;
  
  // 添加背景装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 80%, rgba(79, 172, 254, 0.08) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(139, 69, 247, 0.06) 0%, transparent 50%);
    pointer-events: none;
  }
  
  // 添加几何装饰元素
  &::after {
    content: '';
    position: absolute;
    top: -30px;
    right: -30px;
    width: 120px;
    height: 120px;
    background: rgba(79, 172, 254, 0.08);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }
  
  .header-content {
    text-align: center;
    z-index: 2;
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-width: 700px;
    padding: 0 20px;
    position: relative;
  }
  
  .page-title { 
    font-size: clamp(1.8rem, 4vw, 2.8rem); 
    font-weight: 700; 
    color: #2d3748;
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
    letter-spacing: -0.01em;
    line-height: 1.3;
    text-shadow: 0 2px 8px rgba(79, 172, 254, 0.1);
    animation: titleFadeIn 1s ease-out;
    position: relative;
    
    // 添加装饰性下划线
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
      width: 60px;
      height: 3px;
      background: linear-gradient(90deg, transparent, rgba(79, 172, 254, 0.4), transparent);
      border-radius: 2px;
      animation: underlineGrow 1s ease-out 0.5s both;
    }
  }

  .page-subtitle{
    font-size: 1.1rem;
    font-weight: 400;
    color: #64748b;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
    margin: 0;
    letter-spacing: -0.01em;
    opacity: 0.9;
    animation: metaFadeIn 1s ease-out 0.3s both;
  }
  
  .header-cartoon {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    z-index: 1;
    animation: float 3s ease-in-out infinite;
    
    img {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border-radius: 50%;
      box-shadow: 0 6px 20px rgba(79, 172, 254, 0.2);
      border: 2px solid rgba(255, 255, 255, 0.8);
      transition: all 0.3s ease;
      filter: brightness(1.05);
      
      &:hover {
        transform: scale(1.05);
        box-shadow: 0 8px 25px rgba(79, 172, 254, 0.3);
      }
    }
    
    &--left {
      left: 8%;
      animation-delay: -1s;
    }
    
    &--right {
      right: 8%;
      animation-delay: -2s;
    }
  }
  
  @media (max-width: 768px) {
    padding: 40px 0 50px;
    border-radius: 0 0 20px 20px;
    
    .header-cartoon {
      display: none;
    }
  }
}
.loading-container { display:flex; flex-direction:column; align-items:center; padding: $spacing-xl; }
.loading-spinner { width:32px;height:32px;border:3px solid rgba(0,0,0,0.1);border-top-color:$primary-color;border-radius:50%;animation:spin 1s linear infinite; margin-bottom: $spacing-sm; }
@keyframes spin { to { transform: rotate(360deg); } }

// 添加AnnouncementDetail页面的动画keyframes
@keyframes float {
  0%, 100% { transform: translateY(-50%) translateX(0); }
  50% { transform: translateY(-50%) translateX(10px); }
}

@keyframes titleFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes metaFadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes underlineGrow {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 60px;
    opacity: 1;
  }
}

.announcements-list { display: flex; flex-direction: column; gap: $spacing-md; }
.announcement-card { background:white; border-radius:$border-radius-lg; box-shadow:0 2px 8px rgba(0,0,0,0.05); overflow:hidden; border-left:4px solid $primary-color; }
.announcement-card--media { display:flex; align-items:stretch; gap:$spacing-md; }
.announcement-cover { flex:0 0 260px; width:260px; height:160px; margin:$spacing-md; border-radius:8px; overflow:hidden; background:#f5f5f5; }
.announcement-cover img { width:100%; height:100%; object-fit:cover; display:block; }
.announcement-content { flex:1; padding:$spacing-lg $spacing-lg $spacing-lg 0; display:flex; flex-direction:column; }
.announcement-meta { display:flex; align-items:center; gap:$spacing-md; margin-bottom:$spacing-sm; }
.announcement-views { color:$gray-dark-color; font-size:$font-size-sm; display:flex; align-items:center; gap:6px; }

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #999;
  font-size: 1.1rem;
}
.announcement-date { color:$gray-dark-color; font-size:$font-size-sm; }
.announcement-tag { display:inline-block; padding:2px 8px; border-radius:$border-radius-full; font-size:$font-size-xs; font-weight:500; 
  &.tag-competition{ background:#e6f7ff; color:#0066cc; }
  &.tag-activity{ background:#f6ffed; color:#52c41a; }
  &.tag-resource{ background:#fff7e6; color:#fa8c16; }
  &.tag-system{ background:#fff2e8; color:#fa8c16; }
  &.tag-notice{ background:#f0f0f0; color:#666; }
}
.announcement-title { font-size:$font-size-lg; margin-bottom:$spacing-sm; }
.announcement-excerpt { color:$gray-dark-color; margin-bottom:$spacing-sm; line-height:1.5; }
.announcement-link { color:$primary-color; font-weight:500; }

// 简化分页样式
.simple-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-lg;
  margin: $spacing-xl 0 $spacing-2xl 0;
  padding: $spacing-lg;
}

.pagination-btn {
  padding: $spacing-sm $spacing-md;
  border: 1px solid $border-color;
  background: white;
  color: $text-color;
  border-radius: $border-radius-base;
  cursor: pointer;
  font-size: $font-size-sm;
  transition: all 0.3s ease;
  
  &:hover:not(:disabled) {
    background: $primary-color;
    color: white;
    border-color: $primary-color;
  }
  
  &:disabled {
    background: #f5f5f5;
    color: #999999;
    cursor: not-allowed;
    border-color: #d9d9d9;
    opacity: 1;
    font-weight: 500;
  }
}

.page-info {
  color: $gray-dark-color;
  font-size: $font-size-sm;
  font-weight: 500;
  padding: 0 $spacing-md;
}

@media (max-width: $breakpoint-md) {
  .page-header {
    .header-cartoon {
      display: none;
    }
    
    .page-title {
      font-size: $font-size-xl;
    }
  }
  
  .content-container {
    padding: $spacing-md;
  }
  
  .announcement-card--media { flex-direction: column; }
  .announcement-cover { width: calc(100% - 32px); height: 180px; }
  .announcement-content { padding: 0 $spacing-lg $spacing-lg $spacing-lg; }
  
  .simple-pagination {
    gap: $spacing-md;
    padding: $spacing-md;
    margin: $spacing-lg 0 $spacing-xl 0;
    
    .pagination-btn {
      padding: $spacing-xs $spacing-sm;
      font-size: $font-size-sm;
    }
  }
}
</style>