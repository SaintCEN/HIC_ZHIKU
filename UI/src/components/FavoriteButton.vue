<template>
  <el-button
    :type="isFavorited ? 'danger' : 'primary'"
    :icon="isFavorited ? 'StarFilled' : 'Star'"
    :loading="loading"
    @click="handleToggleFavorite"
    v-bind="$attrs"
  >
    {{ isFavorited ? '已收藏' : '收藏' }}
  </el-button>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorites'
import { useAuthStore } from '@/stores'

// 定义props
const props = defineProps({
  resourceId: {
    type: [String, Number],
    required: true
  },
  resourceTitle: {
    type: String,
    required: true
  },
  resourceUrl: {
    type: String,
    default: ''
  }
})

// 定义emits
const emit = defineEmits(['favoriteChanged'])

// 响应式数据
const loading = ref(false)
const isFavorited = ref(false)

// 获取认证状态
const authStore = useAuthStore()

// 检查收藏状态
const checkFavoriteStatus = async () => {
  if (!authStore.isAuthenticated) {
    return
  }

  try {
    const response = await checkFavorite(props.resourceId)
    isFavorited.value = response.data.isFavorited
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    loading.value = true
    
    if (isFavorited.value) {
      // 取消收藏
      await removeFavorite(props.resourceId)
      isFavorited.value = false
      ElMessage.success('取消收藏成功')
    } else {
      // 添加收藏
      const favoriteData = {
        resourceId: props.resourceId,
        resourceTitle: props.resourceTitle,
        resourceUrl: props.resourceUrl
      }
      await addFavorite(favoriteData)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
    
    // 触发事件
    emit('favoriteChanged', {
      isFavorited: isFavorited.value,
      resourceId: props.resourceId
    })
  } catch (error) {
    console.error('操作失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('操作失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 监听认证状态变化
watch(() => authStore.isAuthenticated, (newVal) => {
  if (newVal) {
    checkFavoriteStatus()
  } else {
    isFavorited.value = false
  }
})

// 监听资源变化
watch(() => props.resourceId, () => {
  checkFavoriteStatus()
})

// 组件挂载时检查收藏状态
onMounted(() => {
  checkFavoriteStatus()
})
</script>

<style lang="scss" scoped>
// 组件样式可以根据需要添加
</style>