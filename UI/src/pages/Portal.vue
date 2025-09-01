<template>
  <MainLayout>
    <div class="home-page">
      <!-- 顶部焦点轮播图 -->
      <!-- 纯前端 -->
      <section class="banner-section">
        <div class="container">
          <el-carousel 
            :interval="4000" 
            height="400px" 
            class="banner-carousel" 
            :autoplay="true"
            arrow="hover"
            indicator-position="outside"
            type="card"
          >
            <el-carousel-item v-for="(item, index) in bannerItems" :key="index">
              <div class="banner-item" :style="{ backgroundImage: `url(${item.image_url || item.image})` }">
                <div class="banner-content">
                  <h2 class="banner-title">{{ item.title }}</h2>
                  <p class="banner-description">{{ item.description }}</p>
                  <el-button type="primary" size="large" @click="navigateTo(item.link_url || item.link)">
                    {{ item.button_text || item.buttonText || '了解更多' }}
                  </el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
          
        </div>
      </section>

      <!-- 板块快捷入口 -->
      <section class="section section-features">
        <div class="container">
          <h2 class="section-title text-center">主要板块</h2>
          <div class="feature-cards grid grid-cols-3 grid-gap-lg">
            <div v-for="(feature, index) in features" :key="index" class="feature-card">
              <div class="feature-icon">
                <font-awesome-icon :icon="feature.icon" size="2x" />
              </div>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
              <router-link :to="feature.link" class="feature-link">
                <span>查看更多</span>
                <font-awesome-icon icon="arrow-right" />
              </router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- 公告栏 -->
      <section class="section section-announcements">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">最新公告</h2>
            <router-link to="/announcements" class="view-all-link" @click="handleViewAllClick('announcements')">
              查看全部 <font-awesome-icon icon="arrow-right" />
            </router-link>
          </div>
          <div class="announcements-list" v-if="!loading">
            <div v-for="(announcement, index) in announcements" :key="index" class="announcement-card announcement-card--media">
              <div class="announcement-cover">
                <img :src="getAnnouncementCoverUrl(announcement)" alt="announcement cover" />
              </div>
              <div class="announcement-content">
                <div class="announcement-meta">
                  <span class="announcement-date">{{ formatDate(announcement.publishTime || announcement.publish_time || announcement.date) }}</span>
                  <span v-if="announcement.type" :class="['announcement-tag', `tag-${announcement.type}`]">{{ getAnnouncementTypeName(announcement.type) }}</span>
                  <span class="announcement-views"><i class="fas fa-eye"></i> {{ (announcement.viewCount || announcement.view_count || announcement.views || 0) }} 次浏览</span>
                </div>
                <h3 class="announcement-title">{{ announcement.title }}</h3>
                <p class="announcement-excerpt">{{ announcement.summary || announcement.excerpt }}</p>
                <router-link :to="`/announcements/${announcement.id}`" class="announcement-link" @click="handleAnnouncementClick(announcement)">
                  阅读更多
                </router-link>
              </div>
            </div>
          </div>
          <div v-else class="loading-placeholder">
            <p>加载公告中...</p>
          </div>
        </div>
      </section>

      <!-- 最新活动 -->
      <section class="section section-events">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">近期活动</h2>
            <router-link to="/events" class="view-all-link" @click="handleViewAllClick('events')">
              查看全部 <font-awesome-icon icon="arrow-right" />
            </router-link>
          </div>
          <div class="events-list grid grid-cols-2 grid-gap-lg" v-if="!loading">
            <div v-for="(event, index) in events" :key="index" class="event-card">
              <div class="event-image" :style="{ backgroundImage: `url(${getEventImageUrl(event)})` }">
                <div class="event-date">
                  <span class="event-day">{{ formatDay(event.startTime) }}</span>
                  <span class="event-month">{{ formatMonth(event.startTime) }}</span>
                </div>
              </div>
              <div class="event-content">
                <h3 class="event-title">{{ event.title }}</h3>
                <div class="event-info">
                  <div class="event-info-item">
                    <font-awesome-icon icon="eye" />
                    <span>{{ event.viewCount || 0 }} 次浏览</span>
                  </div>
                  <div class="event-info-item">
                    <font-awesome-icon icon="location-dot" />
                    <span>{{ event.location }}</span>
                  </div>
                </div>
                <p class="event-description">{{ event.description }}</p>
                <router-link :to="`/events/${event.id}`" class="event-link">了解更多</router-link>
              </div>
            </div>
          </div>
          <div v-else class="loading-placeholder">
            <p>加载活动中...</p>
          </div>
        </div>
      </section>

      <!-- 资源等其余板块保持不变 -->
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../utils/http'
import MainLayout from '../layouts/MainLayout.vue'
import { config } from '../utils/config.js'

// 使用 new URL() 方式确保开发和构建环境都能正确处理
const banner1 = new URL('../assets/banner1.jpg', import.meta.url).href
const banner2 = new URL('../assets/banner2.jpg', import.meta.url).href
const banner3 = new URL('../assets/banner3.jpg', import.meta.url).href

const router = useRouter()

const navigateTo = (link) => {
  if (!link) return
  router.push(link)
}

// 响应式数据（banner 改为纯前端静态，不依赖后端）
const bannerItems = ref([
  {
    title: '华创知识库',
    description: '汇聚华为技术领域优质学习资源，助力你的技术提升之路',
    image_url: banner1,
    link_url: '/knowledge',
    button_text: '立即探索'
  },
  {
    title: '华俱推文',
    description: '西电华创微信公众号推文集合，最新活动和资讯',
    image_url: banner2,
    link_url: '/articles',
    button_text: '立即探索'
  },
  {
    title: '师兄师姐说',
    description: '与师兄师姐分享经验，共同成长',
    image_url: banner3,
    link_url: '/share',
    button_text: '立即探索'
  }
])

// 从后端API获取数据
const announcements = ref([])
const events = ref([])
const loading = ref(true)

// 功能板块数据（保持静态）
const features = [
  {
    title: '知识库',
    description: '涵盖华为技术领域各方向的学习资源',
    icon: 'book',
    link: '/knowledge'
  },
  {
    title: '项目分享',
    description: '分享和收藏优秀项目，助力华创er交流和协作',
    icon: 'code',
    link: '/projects'
  },
  {
    title: '华为竞赛',
    description: '最新华为竞赛信息，共同参与技术沙龙',
    icon: 'trophy',
    link: '/competitions'
  },
  {
    title: '实用工具',
    description: '各类工具集合，提高学习效率',
    icon: 'toolbox',
    link: '/tools'
  },
  {
    title: '华俱推文',
    description: '西电华创微信公众号推文集合，最新活动资讯',
    icon: 'newspaper',
    link: '/articles'
  },
  {
    title: '师兄师姐说',
    description: '经验分享，求职心得，给你不一样的人生指导',
    icon: 'comment',
    link: '/share'
  }
]

// 从后端API获取公告和活动数据
const fetchPortalData = async () => {
  loading.value = true
  try {
    // 获取首页数据
    const response = await http.get('/portal/home')

    // portal/home 接口直接返回数据，没有包装在 ApiResponse 中
    const data = response.data

    // 只显示最新2条公告
    announcements.value = (data.announcements || []).slice(0, 2)

    // 只显示最新2个活动
    events.value = (data.events || []).slice(0, 2)
  } catch (error) {
    console.error('Error fetching portal data:', error)
  } finally {
    loading.value = false
  }
}

// 工具函数
const getAbsoluteUrl = (url) => {
  if (!url) return ''
  return config.getUploadUrl(url)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

const formatDay = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.getDate().toString().padStart(2, '0')
}

const formatMonth = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[d.getMonth()]
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 获取公告类型文本
const getAnnouncementTypeText = (type) => {
  const typeMap = {
    competition: '竞赛',
    activity: '活动',
    resource: '资源',
    system: '系统',
    notice: '通知'
  }
  return typeMap[type] || '通知'
}

// 获取公告类型名称
const getAnnouncementTypeName = (type) => {
  const typeMap = {
    competition: '竞赛通知',
    activity: '活动公告',
    resource: '资源更新',
    system: '系统通知',
    notice: '一般通知'
  }
  return typeMap[type] || '一般通知'
}

// 解析公告封面图 URL
const getAnnouncementCoverUrl = (a) => {
  // 后端返回的是 coverUrl 字段
  const url = a.coverUrl || a.cover || a.cover_url || a.image || a.image_url || a.thumbnail || a.thumbnail_url || ''
  if (!url) return banner1
  return getAbsoluteUrl(url)
}

// 获取活动图片URL（修复字段映射）
const getEventImageUrl = (event) => {
  // 后端返回的是 imageUrl 字段
  if (event.imageUrl && event.imageUrl.trim() !== '') {
    return getAbsoluteUrl(event.imageUrl)
  }
  if (event.image && event.image.trim() !== '') {
    return event.image
  }
  // 如果没有图片，使用默认图片
  return banner1
}

// 处理"查看更多"点击事件
const handleViewAllClick = (type) => {
  if (type === 'announcements') {
    router.push('/announcements')
  }
}

// 处理公告点击事件，增加浏览量
const handleAnnouncementClick = async (announcement) => {
  try {
    // 调用后端API增加浏览量
    await http.post(`/portal/announcements/${announcement.id}/view`)
    // 更新本地显示的浏览量
    announcement.viewCount = (announcement.viewCount || 0) + 1
  } catch (error) {
    console.error('增加公告浏览次数失败:', error)
  }
}

// 生命周期
onMounted(() => {
  fetchPortalData()
})
</script>

<style lang="scss" scoped>
@import '../styles/variables.scss';

.home-page {
  padding-bottom: $spacing-2xl;
}

.section {
  margin-bottom: $spacing-xl;
}

.section-title {
  font-size: $font-size-xl;
  margin-bottom: $spacing-md;
  color: $dark-color;
  
  &.text-center {
    text-align: center;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-md;
}

.view-all-link {
  color: $link-color;
  font-weight: 500;
  transition: color 0.2s;
  
  &:hover {
    color: $primary-color;
  }
  
  .svg-inline--fa {
    margin-left: 4px;
    font-size: 12px;
  }
}

/* 轮播图样式 */
.banner-section {
  margin-top: 15px;
  margin-bottom: $spacing-xl;
}

.banner-carousel {
  border-radius: $border-radius-lg;
  overflow: hidden;
  
  :deep(.el-carousel__container) {
    height: 400px;
  }
  
  :deep(.el-carousel__item) {
    transition: all 0.4s ease-in-out;
  }
  
  :deep(.el-carousel__arrow) {
    background-color: rgba(255, 255, 255, 0.8);
    color: $primary-color;
    border: none;
    width: 40px;
    height: 40px;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.9);
    }
  }
  
  :deep(.el-carousel__indicators) {
    .el-carousel__indicator {
      .el-carousel__button {
        background-color: rgba(255, 255, 255, 0.5);
        border: 2px solid transparent;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        transition: all 0.3s ease;
      }
      
      &.is-active .el-carousel__button {
        background-color: $primary-color;
        border-color: white;
        transform: scale(1.2);
      }
    }
  }
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  border-radius: $border-radius-lg;
  overflow: hidden;
  transition: transform 0.3s ease;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.4) 50%, rgba(0,0,0,0.1) 100%);
    transition: opacity 0.3s ease;
  }
  
  &:hover {
    transform: scale(1.02);
    
    &::before {
      opacity: 0.8;
    }
  }
}

.banner-content {
  position: relative;
  color: white;
  max-width: 500px;
  padding: 0 $spacing-2xl;
  animation: slideInLeft 0.6s ease-out;
  
  .banner-title {
    font-size: $font-size-3xl;
    margin-bottom: $spacing-md;
    color: white;
    text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
    animation: fadeInUp 0.8s ease-out 0.2s both;
  }
  
  .banner-description {
    font-size: $font-size-lg;
    margin-bottom: $spacing-lg;
    opacity: 0.9;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
    animation: fadeInUp 0.8s ease-out 0.4s both;
  }
  
  .el-button {
    animation: fadeInUp 0.8s ease-out 0.6s both;
    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0,0,0,0.4);
    }
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 功能板块样式 */
.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: $spacing-md;
  margin-top: $spacing-md;
  
  @media (max-width: $breakpoint-md) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.feature-card {
  background-color: white;
  border-radius: $border-radius-base;
  padding: $spacing-lg;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s, box-shadow 0.3s;
  border: 1px solid #f0f0f0;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  }
}

.feature-icon {
  color: $primary-color;
  margin-bottom: $spacing-md;
  
  .svg-inline--fa {
    font-size: 40px;
  }
}

.feature-title {
  font-size: $font-size-base;
  margin-bottom: $spacing-sm;
  color: $dark-color;
}

.feature-description {
  color: $gray-dark-color;
  margin-bottom: $spacing-md;
  line-height: 1.5;
  font-size: $font-size-sm;
}

.feature-link {
  display: inline-flex;
  align-items: center;
  color: $primary-color;
  font-weight: 500;
  
  .svg-inline--fa {
    margin-left: 4px;
    transition: transform 0.2s;
  }
  
  &:hover .svg-inline--fa {
    transform: translateX(3px);
  }
}

/* 公告栏样式（更新为左图右文布局） */
.announcements-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.announcement-card {
  background-color: white;
  border-radius: $border-radius-base;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  border-left: 3px solid $primary-color;
}

.announcement-card--media {
  display: flex;
  align-items: stretch;
  gap: $spacing-sm;
}

.announcement-cover {
  flex: 0 0 180px;
  width: 180px;
  height: 120px;
  margin: $spacing-sm;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f5f5;
}

.announcement-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 自动适配与裁剪 */
  display: block;
}

.announcement-content {
  flex: 1;
  padding: $spacing-md $spacing-md $spacing-md 0;
  display: flex;
  flex-direction: column;
}

.announcement-meta {
  display: flex;
  align-items: center;
  margin-bottom: $spacing-xs;
}

.announcement-date {
  color: $gray-dark-color;
  font-size: $font-size-xs;
  margin-right: $spacing-sm;
}

.announcement-tag {
  display: inline-block;
  padding: 1px 6px;
  border-radius: $border-radius-full;
  font-size: $font-size-xs;
  font-weight: 500;
  &.tag-competition { background-color: #e6f7ff; color: #0066cc; }
  &.tag-activity { background-color: #f6ffed; color: #52c41a; }
  &.tag-resource { background-color: #fff7e6; color: #fa8c16; }
  &.tag-news { background-color: #fff2f0; color: #ff4d4f; }
}

.announcement-title {
  font-size: $font-size-base;
  margin-bottom: $spacing-xs;
  line-height: 1.4;
}

.announcement-excerpt {
  color: $gray-dark-color;
  margin-bottom: $spacing-xs;
  line-height: 1.4;
  font-size: $font-size-sm;
}

.announcement-link {
  color: $primary-color;
  font-weight: 500;
}

/* 响应式：小屏上下布局 */
@media (max-width: $breakpoint-md) {
  .announcement-card--media {
    flex-direction: column;
  }
  .announcement-cover {
    width: calc(100% - 2 * #{$spacing-md});
    height: 180px;
  }
  .announcement-content {
    padding: 0 $spacing-lg $spacing-lg $spacing-lg;
  }
}

.events-list {
  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.event-card {
  display: flex;
  flex-direction: column;
  background-color: white;
  border-radius: $border-radius-base;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s, box-shadow 0.3s;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}

.event-image {
  height: 160px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #f5f5f5;
  position: relative;
  margin: 6px;
  border-radius: 6px;
  overflow: hidden;
}

.event-date {
  position: absolute;
  top: $spacing-sm;
  left: $spacing-sm;
  background-color: white;
  border-radius: $border-radius-base;
  padding: $spacing-xs $spacing-sm;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  
  .event-day {
    display: block;
    font-size: $font-size-lg;
    font-weight: 700;
    color: $primary-color;
  }
  
  .event-month {
    display: block;
    font-size: $font-size-xs;
    color: $gray-dark-color;
  }
}

.event-content {
  padding: $spacing-md;
}

.event-title {
  font-size: $font-size-base;
  margin-bottom: $spacing-xs;
  line-height: 1.4;
}

.event-info {
  display: flex;
  margin-bottom: $spacing-sm;
  
  .event-info-item {
    display: flex;
    align-items: center;
    margin-right: $spacing-md;
    color: $gray-dark-color;
    font-size: $font-size-xs;
    
    .svg-inline--fa {
      margin-right: 3px;
      font-size: 12px;
    }
  }
}

.event-excerpt {
  color: $gray-dark-color;
  margin-bottom: $spacing-sm;
  line-height: 1.4;
  font-size: $font-size-sm;
}

.event-link {
  color: $primary-color;
  font-weight: 500;
}

/* 加载状态样式 */
.loading-placeholder {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-size: 1rem;
}

.loading-placeholder p {
  margin: 0;
  opacity: 0.7;
}


.announcement-views { color:$gray-dark-color; font-size:$font-size-sm; display:flex; align-items:center; gap:6px; }
</style>