<template>
  <MainLayout>
    <div class="events-page">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-cartoon header-cartoon--left">
          <img src="/src/assets/cartoon1.jpg" alt="cartoon decoration" />
        </div>
        <div class="header-content">
          <h1 class="page-title">活动中心</h1>
          <h2 class="page-subtitle">华创活动...还挺多？</h2>
        </div>
        <div class="header-cartoon header-cartoon--right">
          <img src="/src/assets/cartoon2.png" alt="cartoon decoration" />
        </div>
      </div>

      <!-- 内容容器 -->
      <div class="content-container">
        <!-- 筛选和搜索 -->
        <div class="filters-section">
          <div class="filter-group">
            <label class="filter-label">活动状态：</label>
            <div class="filter-buttons">
              <button 
                :class="['filter-btn', { active: selectedType === 'upcoming' }]"
                @click="handleTypeChange('upcoming')"
              >
                即将开始
              </button>
              <button 
                :class="['filter-btn', { active: selectedType === 'ongoing' }]"
                @click="handleTypeChange('ongoing')"
              >
                进行中
              </button>
              <button 
                :class="['filter-btn', { active: selectedType === 'completed' }]"
                @click="handleTypeChange('completed')"
              >
                已结束
              </button>
            </div>
          </div>

          <div class="search-group">
            <label class="search-label">搜索活动</label>
            <div class="search-box">
              <input 
                v-model="searchQuery"
                type="text" 
                placeholder="搜索活动标题或内容..."
                class="search-input"
                @keyup.enter="handleSearch"
              >
              <button class="search-btn" @click="handleSearch">
                <i class="fas fa-search"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>正在加载活动...</p>
        </div>

        <!-- 活动列表 -->
        <div v-else-if="events.length > 0" class="events-list">
          <div 
            v-for="event in events" 
            :key="event.id"
            class="event-card"
            @click="router.push(`/events/${event.id}`)"
          >
            <div class="event-header">
              <span :class="['status-badge', getEventStatus(event).class]">
                {{ getEventStatus(event).text }}
              </span>
            </div>
            
            <h3 class="event-title">{{ event.title }}</h3>
            

            
            <p class="event-description">{{ event.description || event.content }}</p>
            
            <div class="event-details">
              <div v-if="event.startTime || event.start_time" class="event-time">
                <i class="fas fa-clock"></i>
                <span>开始时间：{{ formatDateTime(event.startTime || event.start_time) }}</span>
              </div>
              <div v-if="event.endTime || event.end_time" class="event-time">
                <i class="fas fa-clock"></i>
                <span>结束时间：{{ formatDateTime(event.endTime || event.end_time) }}</span>
              </div>
              <div v-if="event.location" class="event-location">
                <i class="fas fa-map-marker-alt"></i>
                <span>{{ event.location }}</span>
              </div>
            </div>
            
            <div class="event-footer">
              <div class="event-meta">
                <span class="views">
                  <i class="fas fa-eye"></i>
                  {{ event.viewCount || event.view_count || event.views || 0 }} 次查看
                </span>
                <span v-if="event.maxParticipants || event.max_participants" class="participants">
                  <i class="fas fa-users"></i>
                  {{ event.participants || event.participant_count || 0 }} / {{ event.maxParticipants || event.max_participants }} 人
                </span>
              </div>
              <button class="join-btn" @click.stop="router.push(`/events/${event.id}`)">
                查看详情
                <i class="fas fa-arrow-right"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-icon">
            <i class="fas fa-calendar-alt"></i>
          </div>
          <h3>暂无活动</h3>
          <p>当前没有符合条件的活动，请尝试调整筛选条件</p>
        </div>

        <!-- 简单分页 -->
        <div class="simple-pagination">
          <div class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</div>
          <div class="pagination-buttons">
            <button 
              class="page-btn prev"
              :disabled="currentPage <= 1"
              @click="handlePageChange(currentPage - 1)"
            >
              上一页
            </button>
            <button 
              class="page-btn next"
              :disabled="currentPage >= totalPages || totalPages <= 1"
              @click="handlePageChange(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import axios from 'axios'
import { config } from '../utils/config.js'

const router = useRouter()

// 状态变量
const loading = ref(true)
const events = ref([])
const totalEvents = ref(0)
const totalPages = ref(1)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedType = ref('upcoming')
const sortBy = ref('date-desc')
const searchQuery = ref('')

// 获取活动数据
const fetchEvents = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: selectedType.value
    }
    
    if (searchQuery.value) {
      params.search = searchQuery.value
    }
    
    const response = await axios.get('/api/portal/events', { params })
    
    // 处理不同的响应格式
    if (response.data?.data) {
      events.value = response.data.data || []
      totalEvents.value = response.data.total || response.data.totalCount || 0
      totalPages.value = Math.max(1, Math.ceil(totalEvents.value / pageSize.value))
    } else if (response.data?.events) {
      events.value = response.data.events || []
      totalEvents.value = response.data.total || response.data.totalCount || 0
      totalPages.value = Math.max(1, Math.ceil(totalEvents.value / pageSize.value))
    } else {
      events.value = response.data || []
      totalEvents.value = events.value.length
      totalPages.value = Math.max(1, Math.ceil(totalEvents.value / pageSize.value))
    }
  } catch (error) {
    console.error('获取活动数据失败:', error)
    events.value = []
    totalEvents.value = 0
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch([selectedType, searchQuery], () => {
  currentPage.value = 1
  fetchEvents()
})

// 生命周期钩子
onMounted(() => {
  fetchEvents()
})

// 方法
const handlePageChange = (page) => {
  currentPage.value = page
  fetchEvents()
  // 滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const handleTypeChange = (type) => {
  selectedType.value = type
}

const handleSearch = () => {
  currentPage.value = 1
  fetchEvents()
}

// 辅助函数
const getEventTypeText = (type) => {
  const typeMap = {
    'upcoming': '即将开始',
    'ongoing': '进行中',
    'completed': '已结束'
  }
  return typeMap[type] || '即将开始'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getEventStatus = (event) => {
  const now = new Date()
  const startDate = new Date(event.startTime)
  const endDate = new Date(event.endTime)
  
  if (now < startDate) {
    return { status: 'upcoming', text: '即将开始', class: 'upcoming' }
  } else if (now >= startDate && now <= endDate) {
    return { status: 'ongoing', text: '进行中', class: 'ongoing' }
  } else {
    return { status: 'completed', text: '已结束', class: 'completed' }
  }
}
</script>

<style lang="scss" scoped>
@import '../styles/variables.scss';

.events-page {
  min-height: 100vh;
  background-color: $background-color;
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

.content-container {
  padding: $spacing-lg;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 60vh;
}

.filters-section {
  background: white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  box-shadow: $shadow-sm;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: $spacing-md;

  .filter-group {
    display: flex;
    align-items: center;
    gap: $spacing-md;

    .filter-label {
      font-weight: 500;
      color: $text-color;
      white-space: nowrap;
    }

    .filter-buttons {
      display: flex;
      gap: $spacing-sm;

      .filter-btn {
          padding: $spacing-sm $spacing-md;
          border: 1px solid $border-color;
          background: white;
          color: $text-color;
          border-radius: $border-radius-lg;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: $font-size-sm;

        &:hover {
          border-color: $primary-color;
          color: $primary-color;
        }

        &.active {
          background: $primary-color;
          border-color: $primary-color;
          color: white;
        }
      }
    }
  }

  .search-group {
    .search-label {
      font-size: 14px;
      font-weight: 500;
      color: #374151;
      margin-bottom: 8px;
      display: block;
    }
    
    .search-box {
        display: flex;
        align-items: center;
        border: 1px solid $border-color;
        border-radius: $border-radius-lg;
        overflow: hidden;

      .search-input {
        flex: 1;
        padding: $spacing-sm $spacing-md;
        border: none;
        outline: none;
        font-size: $font-size-sm;
        color: $text-color;
        min-width: 200px;

        &::placeholder {
          color: $gray-color;
        }
      }

      .search-btn {
        padding: $spacing-sm $spacing-md;
        background: transparent;
        color: $gray-dark-color;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        border-left: 1px solid $border-color;

        &:hover {
          background: $gray-light-color;
          color: $primary-color;
        }

        i {
          font-size: $font-size-base;
        }
      }
    }
  }
}

.loading-container {
  text-align: center;
  padding: $spacing-2xl;
  color: $gray-color;

  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid $border-color;
    border-top: 4px solid $primary-color;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto $spacing-md;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

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

.events-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-lg;

  .event-card {
    background: white;
    border-radius: $border-radius-lg;
    box-shadow: $shadow-sm;
    transition: all 0.3s ease;
    cursor: pointer;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 100%; // 确保卡片高度一致

    &:hover {
      box-shadow: $shadow-md;
      transform: translateY(-2px);
    }

    .event-header {
      padding: $spacing-md;
      border-bottom: 1px solid $border-color;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .status-badge {
        padding: 4px 12px;
        border-radius: $border-radius-sm;
        font-size: $font-size-xs;
        font-weight: 500;

        &.upcoming {
          background-color: #e3f2fd;
          color: #1976d2;
        }

        &.ongoing {
          background-color: #e8f5e8;
          color: #388e3c;
        }

        &.completed {
          background-color: #f5f5f5;
          color: #757575;
        }
      }


    }

    .event-title {
      padding: 0 $spacing-md;
      margin: $spacing-md 0;
      font-size: $font-size-lg;
      font-weight: 600;
      color: $text-color;
      line-height: 1.4;
    }

    .event-description {
      padding: 0 $spacing-md;
      margin-bottom: $spacing-md;
      color: $gray-color;
      line-height: 1.6;
      font-size: $font-size-sm;
      height: 60px; // 固定高度
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 3; // 限制为3行
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .event-details {
      padding: 0 $spacing-md;
      margin-bottom: $spacing-md;
      min-height: 80px; // 固定最小高度
      display: flex;
      flex-direction: column;
      justify-content: flex-start;

      .event-time,
      .event-location {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        margin-bottom: $spacing-xs;
        font-size: $font-size-sm;
        color: $gray-dark-color;
        padding: $spacing-xs 0;

        i {
          width: 16px;
          color: $primary-color;
          text-align: center;
          flex-shrink: 0;
        }

        span {
          flex: 1;
        }
      }
    }

    .event-footer {
      padding: $spacing-md;
      border-top: 1px solid $border-color;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: auto; // 推到底部

      .event-meta {
        display: flex;
        gap: $spacing-md;
        font-size: $font-size-xs;
        color: $gray-color;

        .views,
        .participants {
          display: flex;
          align-items: center;
          gap: $spacing-xs;

          i {
            color: $primary-color;
          }
        }
      }

      .join-btn {
        padding: $spacing-sm $spacing-md;
        background: $primary-color;
        color: white;
        border: none;
        border-radius: $border-radius-lg;
        cursor: pointer;
        font-size: $font-size-sm;
        font-weight: 500;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: $spacing-xs;

        &:hover {
          background: $secondary-color;
          transform: translateX(2px);
        }

        i {
          font-size: 12px;
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-2xl;
  color: $gray-color;

  .empty-icon {
    font-size: 4rem;
    color: $border-color;
    margin-bottom: $spacing-lg;
  }

  h3 {
    font-size: $font-size-lg;
    color: $text-color;
    margin-bottom: $spacing-sm;
  }

  p {
    font-size: $font-size-sm;
    color: $gray-color;
  }
}

.simple-pagination {
  margin: $spacing-2xl 0;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .page-info {
    color: $gray-color;
    font-size: $font-size-sm;
    font-weight: 500;
  }

  .pagination-buttons {
    display: flex;
    gap: $spacing-md;

    .page-btn {
      padding: $spacing-sm $spacing-lg;
      border: 1px solid $border-color;
      background: white;
      color: $text-color;
      border-radius: $border-radius-lg;
      cursor: pointer;
      transition: all 0.3s ease;
      font-size: $font-size-sm;
      font-weight: 500;

      &:hover:not(:disabled) {
        border-color: $primary-color;
        color: $primary-color;
      }

      &:disabled {
        opacity: 1;
        font-weight: 500;
        cursor: not-allowed;
        background-color: $background-color;
        color: $gray-color;
        border-color: $border-color;
      }
    }
  }
}

@media (max-width: 768px) {
  .content-container {
    padding: $spacing-md;
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;

    .filter-group {
      justify-content: center;
    }

    .search-group .search-box .search-input {
      min-width: 150px;
    }
  }

  .events-list {
    grid-template-columns: 1fr;
  }

  .simple-pagination {
    flex-direction: column;
    gap: $spacing-md;
    text-align: center;

    .pagination-buttons {
      justify-content: center;
    }
  }
}
</style>