<template>
  <MainLayout>
    <div class="tools-page">
      <!-- 页面标题区 -->
      <section class="page-header">
        <div class="container">
          <h1 class="page-title">实用工具</h1>
          <p class="page-description">
            这里汇集了学习和开发过程中常用的各类工具，帮助你提高效率
          </p>
        </div>
      </section>

      <!-- 工具分类和搜索 -->
      <section class="filter-section">
        <div class="container">
          <div class="filters">
            <div class="filter-left">
              <el-radio-group v-model="selectedCategory" size="large" @change="filterTools">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button v-for="(category, index) in categories" :key="index" :label="category.value">
                  {{ category.label }}
                </el-radio-button>
              </el-radio-group>
            </div>

            <div class="filter-right">
              <div class="filter-group">
                <label class="filter-label">搜索工具</label>
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索工具名称或描述..." 
                  prefix-icon="Search"
                  clearable
                  @input="handleSearch"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 工具列表 -->
      <section class="tools-section">
        <div class="container">
          <div v-if="filteredTools.length > 0" class="tools-grid">
            <div 
              v-for="(tool, index) in paginatedTools" 
              :key="index" 
              class="tool-card"
              @click="openTool(tool)"
            >
              <div class="tool-icon" :style="{ backgroundColor: tool.cardColor }">
                <font-awesome-icon :icon="tool.icon" />
              </div>
              <div class="tool-content">
                <h3 class="tool-title">{{ tool.name }}</h3>
                <p class="tool-description">{{ tool.description }}</p>
                <div class="tool-footer">
                    <div class="tool-category">
                      <el-tag size="small" :type="getCategoryType(tool.originalCategory)">{{ tool.category }}</el-tag>
                    </div>
                    <div class="tool-views">
                      <font-awesome-icon icon="eye" />
                      <span>{{ tool.eyeCount || 0 }}</span>
                    </div>
                  </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-container" v-if="totalTools > 0">
            <el-pagination
              v-model="currentPage"
              :page-size="pageSize"
              :total="totalTools"
              layout="prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>

          <!-- 无搜索结果 -->
          <div v-else class="empty-container">
            <font-awesome-icon icon="tools" class="empty-icon" />
            <h2 class="empty-title">未找到工具</h2>
            <p class="empty-description">
              没有找到符合条件的工具，请尝试调整筛选条件或搜索关键词。
            </p>
            <el-button @click="resetFilters">重置筛选</el-button>
          </div>
        </div>
      </section>



    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import { getTools, incrementToolView } from '../api/tools'

// 工具分类
const categories = [
  { label: '开发工具', value: 'develop' },
  { label: '设计工具', value: 'design' },
  { label: '效率工具', value: 'efficiency' },
  { label: '其他工具', value: 'others' }
]

// 状态变量
const selectedCategory = ref('')
const searchQuery = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(9)

// 工具数据
const tools = ref([])
const totalTools = ref(0)

// API调用函数
const fetchTools = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      category: selectedCategory.value || '',
      search: searchQuery.value || ''
    }
    const response = await getTools(params)
    
    // 适配后端ApiResponse<PageResponse<Tool>>结构
    const payload = response.data
    const pageData = payload?.data || {}
    const listData = pageData?.data || []
    
    tools.value = listData.map(item => ({
      id: item.id,
      name: item.name,
      description: item.description,
      category: getCategoryLabel(item.category),
      originalCategory: item.category,
      icon: getCategoryIcon(item.category),
      url: item.toolUrl,
      eyeCount: item.eyeCount || 0,
      isFeatured: item.isFeatured || false,
      cardColor: getFixedColorByToolId(item.id) // 根据工具ID分配固定颜色
    }))
    
    totalTools.value = pageData?.total || 0
  } catch (error) {
    console.error('获取工具数据失败:', error)
    tools.value = []
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchTools()
})

// 计算属性：过滤后的工具（现在通过后端API进行筛选和搜索）
const filteredTools = computed(() => {
  return tools.value
})

// 分页后的工具（现在通过后端API进行分页）
const paginatedTools = computed(() => {
  return tools.value
})



// 方法
const filterTools = () => {
  currentPage.value = 1
  fetchTools()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchTools()
}

const resetFilters = () => {
  selectedCategory.value = ''
  searchQuery.value = ''
  currentPage.value = 1
  fetchTools()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchTools()
  // 平滑滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const openTool = async (tool) => {
  try {
    if (tool?.id) {
      await incrementToolView(tool.id)
      // 浏览量本地自增，提升用户感知
      tool.eyeCount = (tool.eyeCount || 0) + 1
    }
  } catch (e) {
    console.warn('增加浏览量失败：', e)
  } finally {
    if (tool?.url) {
      window.open(tool.url, '_blank')
    }
  }
}



// 辅助函数
const getCategoryLabel = (category) => {
  const categoryMap = {
    'develop': '开发工具',
    'design': '设计工具',
    'efficiency': '效率工具',
    'others': '其他工具'
  }
  return categoryMap[category] || '其他工具'
}

const getCategoryIcon = (category) => {
  const iconMap = {
    'develop': 'code',
    'design': 'palette',
    'efficiency': 'rocket',
    'others': 'tools'
  }
  return iconMap[category] || 'tools'
}

const getCategoryType = (category) => {
  const typeMap = {
    'develop': 'primary',
    'design': 'success',
    'efficiency': 'danger',
    'others': 'info'
  }
  return typeMap[category] || 'info'
}

const getRandomColor = () => {
  const colors = [
    '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399',
    '#9254DE', '#36CFC9', '#40A9FF', '#FF7875', '#FFC53D'
  ]
  return colors[Math.floor(Math.random() * colors.length)]
}

// 根据工具ID生成固定颜色，确保同一个工具始终使用相同颜色
const getFixedColorByToolId = (toolId) => {
  const colors = [
    '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399',
    '#9254DE', '#36CFC9', '#40A9FF', '#FF7875', '#FFC53D'
  ]
  // 使用工具ID作为种子，确保相同ID总是得到相同颜色
  const index = toolId ? (toolId % colors.length) : Math.floor(Math.random() * colors.length)
  return colors[index]
}


</script>

<style lang="scss" scoped>
@import '../styles/variables';

.tools-page {
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

/* 筛选部分 */
.filter-section {
  margin-bottom: $spacing-xl;
  background-color: white;
  padding: $spacing-lg 0;
  border-bottom: 1px solid #f0f0f0;
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: stretch;
    gap: $spacing-md;
  }
}

.filter-left {
  display: flex;
  gap: $spacing-lg;
  align-items: center;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: stretch;
  }
}

.filter-right {
  width: 300px;
  
  @media (max-width: $breakpoint-md) {
    width: 100%;
  }
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

/* 工具网格 */
.tools-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-2xl;
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.tool-card {
  background-color: white;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

.tool-icon {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.tool-content {
  padding: $spacing-lg;
}

.tool-title {
  font-size: $font-size-lg;
  margin-bottom: $spacing-sm;
  height: 3rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tool-description {
  color: $gray-dark-color;
  margin-bottom: $spacing-md;
  height: 4.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tool-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tool-category {
  flex: 1;
}

.tool-views {
  display: flex;
  align-items: center;
  gap: 4px;
  color: $gray-dark-color;
  font-size: $font-size-sm;
  
  svg {
    font-size: 14px;
  }
}





/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin: $spacing-2xl 0 $spacing-xl 0;
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