<template>
  <MainLayout>
    <div class="competitions-page">
      <!-- 页面标题区 -->
      <section class="page-header">
        <div class="container">
          <h1 class="page-title">华为竞赛专区</h1>
          <p class="page-description">
            这里汇集了华为各类竞赛的最新信息，在这里尽情展现你的才华
          </p>
        </div>
      </section>

      <!-- 竞赛信息标题 -->
      <section class="section-header">
        <div class="container">
          <h2 class="section-title">竞赛信息</h2>
        </div>
      </section>

      <!-- 竞赛信息区域 -->
      <section class="competitions-info">
        <div class="container">
          <div class="filter-section">
            <div class="search-box">
              <label class="search-label">搜索竞赛</label>
              <el-input
                v-model="searchQuery"
                placeholder="输入竞赛标题或描述..."
                clearable
                @input="handleSearch"
                @clear="handleSearch"
              >
                <template #prefix>
                  <font-awesome-icon icon="search" />
                </template>
              </el-input>
            </div>
            <div class="filter-group">
              <el-select v-model="competitionStatus" placeholder="竞赛状态" @change="filterCompetitions">
                <el-option label="全部状态" value="" />
                <el-option label="即将开始" value="upcoming" />
                <el-option label="进行中" value="ongoing" />
                <el-option label="已结束" value="ended" />
              </el-select>
            </div>
          </div>

          <!-- 竞赛列表 -->
          <div class="competition-list">
            <div v-for="(competition, index) in paginatedCompetitions" :key="index" class="competition-card">
              <div class="competition-status" :class="getStatusClass(competition.status)">
                {{ getStatusText(competition.status) }}
              </div>
              <h3 class="competition-title">{{ competition.title }}</h3>
              <p class="competition-description">{{ competition.description }}</p>

              <div class="competition-footer">
                <div class="time-range" v-if="competition.startTime">
                  <font-awesome-icon icon="calendar" />
                  <span>{{ formatDateTime(competition.startTime) }}</span>
                </div>
                <div class="actions">
                  <el-button type="primary" size="small" :disabled="!competition.officialLink" @click="openOfficialLink(competition)">
                    官方链接
                  </el-button>
                  <div class="views">
                    <font-awesome-icon icon="eye" />
                    <span>{{ competition.viewCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-container" v-if="filteredCompetitions.length > 0">
            <el-pagination
              v-model="currentPage"
              :page-size="pageSize"
              :total="filteredCompetitions.length"
              layout="prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>

          <!-- 暂无数据 -->
          <div v-if="filteredCompetitions.length === 0" class="empty-container">
            <font-awesome-icon icon="trophy" class="empty-icon" />
            <h2 class="empty-title">暂无竞赛</h2>
            <p class="empty-description">
              没有找到符合条件的竞赛信息，请调整筛选条件试试。
            </p>
          </div>
        </div>
      </section>


    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { getCompetitions, incrementCompetitionView } from '../api/competitions'

const router = useRouter()

// 竞赛信息部分的数据
const competitionStatus = ref('')
const searchQuery = ref('')
const competitions = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

// 计算过滤后的竞赛
const filteredCompetitions = computed(() => {
  let result = [...competitions.value]
  
  // 前端状态筛选（确保筛选功能正常工作）
  if (competitionStatus.value) {
    result = result.filter(competition => competition.status === competitionStatus.value)
  }
  
  // 前端搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(competition => 
      competition.title.toLowerCase().includes(query) || 
      competition.description.toLowerCase().includes(query)
    )
  }
  
  return result
})

// 计算分页后的竞赛
const paginatedCompetitions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCompetitions.value.slice(start, end)
})

// 生命周期钩子
onMounted(() => {
  fetchCompetitions()
})

// API调用方法
const fetchCompetitions = async () => {
  try {
    loading.value = true
    // 获取所有竞赛数据，让前端进行筛选
    const params = { 
      page: 1, 
      size: 1000
    }
    
    const response = await getCompetitions(params)
    // 适配后端ApiResponse<PageResponse<Competition>>结构
    const payload = response.data
    const listData = payload?.data?.data || payload?.data || []
    competitions.value = listData.map(item => ({
      id: item.id,
      title: item.title,
      description: item.description,
      status: item.status || item.competitionStatus,
      startTime: item.startTime,
      endTime: item.endTime,
      officialLink: item.officialLink,
      viewCount: item.viewCount || 0
    }))
  } catch (error) {
    console.error('获取竞赛数据失败:', error)
    competitions.value = []
  } finally {
    loading.value = false
  }
}

// 方法
const filterCompetitions = () => {
  // 前端筛选，重置页码
  currentPage.value = 1
}

const handleSearch = () => {
  // 前端搜索，重置页码
  currentPage.value = 1
}

const handlePageChange = (page) => {
  currentPage.value = page
  // 滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const openOfficialLink = async (competition) => {
  try {
    if (competition?.id) {
      await incrementCompetitionView(competition.id)
      // 浏览量本地自增，提升用户感知
      competition.viewCount = (competition.viewCount || 0) + 1
    }
  } catch (e) {
    console.warn('增加浏览量失败：', e)
  } finally {
    if (competition?.officialLink) {
      window.open(competition.officialLink, '_blank')
    }
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'upcoming': '即将开始',
    'registering': '报名中',
    'ongoing': '进行中',
    'ended': '已结束'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status) => `status-${status}`

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
</script>

<style lang="scss" scoped>
@import '../styles/variables';

.competitions-page {
  padding-bottom: $spacing-3xl;
}

/* 页面标题区 */
.page-header {
  background-color: #f0f2f5;
  padding: 60px 0;
  margin-bottom: 40px;
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

/* 竞赛信息标题 */
.section-header {
  margin-bottom: $spacing-xl;
  
  .section-title {
    font-size: $font-size-2xl;
    color: $dark-color;
    text-align: center;
    margin-bottom: $spacing-md;
  }
}

/* 筛选部分 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-xl;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
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
  
  .filter-group {
    width: 200px;
    
    @media (max-width: $breakpoint-md) {
      width: 100%;
    }
  }
}

/* 竞赛卡片 */
.competition-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.competition-card {
  position: relative;
  background-color: white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  }
}

.competition-status {
  position: absolute;
  top: $spacing-md;
  right: $spacing-md;
  padding: 4px 12px;
  border-radius: $border-radius-full;
  font-size: $font-size-xs;
  font-weight: 500;
  color: white;
  
  &.status-registering {
    background-color: #52c41a;
  }
  
  &.status-upcoming {
    background-color: #1890ff;
  }
  
  &.status-ongoing {
    background-color: #fa8c16;
  }
  
  &.status-ended {
    background-color: #d9d9d9;
  }
}

.competition-title {
  font-size: $font-size-xl;
  margin-bottom: $spacing-md;
  padding-right: 100px; // 为状态标签留出空间
}

.competition-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;
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

.competition-description {
  color: $gray-dark-color;
  margin-bottom: $spacing-md;
  line-height: 1.6;
}

.competition-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;
  margin-bottom: $spacing-md;
}

.competition-tag {
  display: inline-block;
  padding: 4px 10px;
  background-color: rgba($secondary-color, 0.1);
  color: $secondary-color;
  border-radius: $border-radius-full;
  font-size: $font-size-xs;
}

.competition-actions {
  display: flex;
  gap: $spacing-md;
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
.competition-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $spacing-md;
}

.time-range {
  display: flex;
  align-items: center;
  color: $gray-dark-color;
  font-size: $font-size-sm;
  
  .svg-inline--fa {
    margin-right: 6px;
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.views {
  display: inline-flex;
  align-items: center;
  color: $gray-dark-color;
  font-size: $font-size-sm;
  
  .svg-inline--fa {
    margin-right: 6px;
  }
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: $spacing-2xl;
  margin-bottom: $spacing-xl;
}
</style>