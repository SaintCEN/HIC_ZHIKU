<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
      <p class="page-subtitle">管理您收藏的内容</p>
    </div>

    <!-- 收藏列表 -->
    <div class="favorites-list" v-loading="loading">
      <div v-if="favorites.length === 0 && !loading" class="empty-state">
        <div class="empty-content">
          <div class="empty-icon">📚</div>
          <h3>暂无收藏内容</h3>
          <p>快去收藏一些有趣的内容吧</p>
          <el-button type="primary" @click="$router.push('/')">
            去首页看看
          </el-button>
        </div>
      </div>

      <div v-else class="favorites-grid">
        <div
          v-for="favorite in favorites"
          :key="favorite.id"
          class="favorite-card"
        >
          <div class="card-image" v-if="favorite.resourceImage">
            <img :src="favorite.resourceImage" :alt="favorite.resourceTitle" />
            <div class="image-overlay"></div>
          </div>
          <div class="card-content">
            <div class="card-header">
              <h3 class="card-title">{{ favorite.resourceTitle }}</h3>
            </div>
            <div class="card-footer">
              <span class="card-time">
                <i class="el-icon-time"></i>
                {{ formatTime(favorite.createdAt) }}
              </span>
              <div class="card-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewProject(favorite)"
                  class="action-btn primary-btn"
                >
                  查看详情
                </el-button>
                <el-button
                  v-if="favorite.resourceUrl && favorite.resourceUrl.startsWith('http')"
                  type="info"
                  size="small"
                  @click="openResource(favorite.resourceUrl)"
                  class="action-btn secondary-btn"
                >
                  外部链接
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleRemove(favorite)"
                  class="action-btn danger-btn"
                >
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 简化分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <div class="simple-pagination">
        <el-button
          :disabled="currentPage <= 1"
          @click="handlePrevPage"
          class="page-btn"
        >
          <i class="el-icon-arrow-left"></i>
          上一页
        </el-button>
        <span class="page-info">
          第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
        </span>
        <el-button
          :disabled="currentPage >= totalPages"
          @click="handleNextPage"
          class="page-btn"
        >
          下一页
          <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavorites, removeFavoriteById } from '@/api/favorites'
import { useAuthStore } from '@/stores'

// 响应式数据
const loading = ref(false)
const favorites = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const auth = useAuthStore()

// 获取收藏列表
const fetchFavorites = async () => {
  try {
    if (!auth.isLoggedIn) {
      ElMessage.warning('请先登录后查看收藏')
      favorites.value = []
      total.value = 0
      return
    }
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    const response = await getFavorites(params)
    const data = response.data?.data || response.data // 适配后端返回 {code,message,data}
    favorites.value = data?.data || [] // PageResponse使用data字段存储列表
    total.value = data?.total || 0
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    const msg = error?.response?.data?.message || '获取收藏列表失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 上一页
const handlePrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchFavorites()
  }
}

// 下一页
const handleNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchFavorites()
  }
}

// 取消收藏
const handleRemove = async (favorite) => {
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
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      const msg = error?.response?.data?.message || '取消收藏失败'
      ElMessage.error(msg)
    }
  }
}

// 查看项目详情
const viewProject = (favorite) => {
  let projectId = favorite.resourceId
  if (favorite.resourceUrl && !favorite.resourceUrl.startsWith('http')) {
    const match = favorite.resourceUrl.match(/\/(project|projects)\/(\d+)/)
    if (match) {
      projectId = match[2]
    }
  }
  if (projectId) {
    window.open(`/projects/${projectId}`, '_blank')
  } else {
    ElMessage.warning('无法获取项目信息')
  }
}

// 打开资源链接
const openResource = (url) => {
  if (url.startsWith('http')) {
    window.open(url, '_blank')
  } else {
    window.open(url, '_blank')
  }
}

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 组件挂载时获取数据
onMounted(() => {
  // 先加载认证状态
  auth.loadFromStorage()
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.page-header {
  margin-bottom: 40px;
  text-align: center;

  .page-title {
    font-size: 2.5rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 12px;
  }

  .page-subtitle {
    color: #6c757d;
    font-size: 1.1rem;
    font-weight: 400;
  }
}

.favorites-list {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;

  .empty-content {
    text-align: center;
    padding: 40px;
    background: white;
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);

    .empty-icon {
      font-size: 4rem;
      margin-bottom: 20px;
    }

    h3 {
      color: #495057;
      margin-bottom: 12px;
      font-weight: 600;
    }

    p {
      color: #6c757d;
      margin-bottom: 24px;
    }
  }
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
}

.favorite-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.2);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  }

  .card-image {
    height: 200px;
    overflow: hidden;
    position: relative;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.1) 100%);
    }

    &:hover img {
      transform: scale(1.05);
    }
  }

  .card-content {
    padding: 24px;
  }

  .card-header {
    margin-bottom: 16px;

    .card-title {
      font-size: 1.2rem;
      font-weight: 600;
      color: #2d3748;
      margin: 0;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;

    .card-time {
      color: #718096;
      font-size: 0.9rem;
      display: flex;
      align-items: center;
      gap: 6px;

      i {
        font-size: 0.8rem;
      }
    }

    .card-actions {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;

      .action-btn {
        border-radius: 8px;
        font-weight: 500;
        transition: all 0.2s ease;

        &.primary-btn {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;
          color: white;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
          }
        }

        &.secondary-btn {
          background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
          border: none;
          color: white;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(116, 185, 255, 0.4);
          }
        }

        &.danger-btn {
          background: linear-gradient(135deg, #fd79a8 0%, #e84393 100%);
          border: none;
          color: white;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(253, 121, 168, 0.4);
          }
        }
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;

  .simple-pagination {
    display: flex;
    align-items: center;
    gap: 20px;
    background: white;
    padding: 16px 24px;
    border-radius: 50px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);

    .page-btn {
      border-radius: 25px;
      padding: 8px 16px;
      font-weight: 500;
      transition: all 0.2s ease;

      &:not(:disabled) {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;

        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
      }

      &:disabled {
        background: #e2e8f0;
        color: #a0aec0;
        border: none;
      }
    }

    .page-info {
      color: #4a5568;
      font-weight: 500;
      font-size: 0.95rem;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .favorites-page {
    padding: 16px;
  }

  .page-header {
    margin-bottom: 30px;

    .page-title {
      font-size: 2rem;
    }
  }

  .favorites-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .favorite-card {
    .card-content {
      padding: 20px;
    }

    .card-footer {
      flex-direction: column;
      align-items: stretch;

      .card-actions {
        justify-content: center;
        margin-top: 8px;
      }
    }
  }

  .pagination-wrapper {
    .simple-pagination {
      flex-direction: column;
      gap: 12px;
      padding: 16px;

      .page-info {
        order: -1;
      }
    }
  }
}
</style>