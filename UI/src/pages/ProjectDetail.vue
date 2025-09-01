<template>
  <MainLayout>
    <div class="project-detail-page">
      <!-- 项目头部 - GitHub风格 -->
      <div class="project-header">
        <div class="project-title-section">
          <div class="project-breadcrumb">
            <div class="author-info">
              <img 
                :src="getAuthorAvatar(project.authorAvatar)" 
                alt="作者头像" 
                class="author-avatar"
                @error="handleAvatarError"
              />
              <router-link to="#" class="author-name">{{ project.authorName || '匿名用户' }}</router-link>
            </div>
            <span class="breadcrumb-separator">/</span>
            <h1 class="project-title">{{ project.title }}</h1>
          </div>
          
          <div class="project-description" v-if="project.description">
            {{ project.description }}
          </div>
          
          <div class="project-meta">
            <span class="meta-item">
              <font-awesome-icon icon="tag" />
              {{ getCategoryLabel(project.category) }}
            </span>
            <span class="meta-item">
              <font-awesome-icon icon="calendar" />
              {{ formatDate(project.createdAt) }}
            </span>
          </div>
        </div>
        
        <div class="project-actions">
          <el-button 
            :type="isStarred ? 'warning' : 'default'" 
            @click="toggleStar"
            class="star-button"
          >
            <font-awesome-icon :icon="isStarred ? 'star' : ['far', 'star']" />
            {{ isStarred ? '取消收藏' : '收藏' }}
          </el-button>
          
          <el-button type="primary" @click="openRepo" v-if="project.githubUrl" class="code-button">
            <font-awesome-icon :icon="['fab', 'github']" />
            查看代码
          </el-button>
          
          <el-button type="success" @click="openDemo" v-if="project.demoUrl" class="demo-button">
            <font-awesome-icon icon="external-link-alt" />
            在线演示
          </el-button>
        </div>
      </div>

      <!-- 项目内容 -->
      <div class="content-container" v-if="!loading">
        <div v-if="htmlContent" class="markdown-body" v-html="htmlContent"></div>
        <div v-else class="empty">暂未找到项目文档内容</div>
      </div>
      
      <!-- 加载状态 -->
      <div v-else class="loading">
        <div class="spinner"></div>
        加载中...
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ElMessage } from 'element-plus'
import http from '@/utils/http'
import { incrementProjectView } from '@/api/projects'
import { config } from '../utils/config.js'
import { useAuthStore } from '@/stores'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(true)
const project = ref({})
const htmlContent = ref('')
const isStarred = ref(false)

// 获取绝对URL
const getAbsoluteUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 使用config.getUploadUrl来处理所有上传路径
  return config.getUploadUrl(url)
}

// 转换为绝对路径（带基础路径）
const toAbsoluteWithBase = (url, base) => {
  if (!url) return ''
  // data URL 或锚点、邮件等不处理
  if (/^(data:|mailto:|tel:|#)/i.test(url)) return url
  // 已是绝对 URL
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  
  // 如果base存在且不是绝对URL，尝试拼接
  if (base && !base.startsWith('http')) {
    try {
      // 处理相对路径
      if (url.startsWith('./')) {
        url = url.substring(2)
      }
      if (url.startsWith('../')) {
        // 计算上级目录
        const baseParts = base.split('/').filter(part => part)
        const relativeParts = url.split('/').filter(part => part)
        
        // 计算需要回退的层级
        let backLevels = 0
        for (let part of relativeParts) {
          if (part === '..') {
            backLevels++
          } else {
            break
          }
        }
        
        // 构建最终路径
        const finalParts = baseParts.slice(0, -backLevels)
        const fileParts = relativeParts.slice(backLevels)
        const result = '/' + finalParts.join('/') + '/' + fileParts.join('/')
        return result
      } else {
        // 直接拼接
        const result = base + url
        return result
      }
    } catch (error) {
      console.error('路径处理错误:', error)
    }
  }
  
  // 如果base不存在或处理失败，使用config.getUploadUrl处理
  return config.getUploadUrl(url)
}

// 后处理HTML，转换相对路径
const postProcessHtmlWithBase = (html, base) => {
  if (!html) return ''
  
  // 创建临时DOM元素来处理HTML
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = html
  
  // 处理图片src
  const images = tempDiv.querySelectorAll('img')
  images.forEach(img => {
    const src = img.getAttribute('src')
    if (src) {
      const fixed = toAbsoluteWithBase(src, base)
      if (fixed) {
        img.setAttribute('src', fixed)
      }
    }
  })
  
  // 处理链接href
  const links = tempDiv.querySelectorAll('a')
  links.forEach(link => {
    const href = link.getAttribute('href')
    if (href) {
      const fixed = toAbsoluteWithBase(href, base)
      if (fixed) {
        link.setAttribute('href', fixed)
      }
    }
  })
  
  return tempDiv.innerHTML
}

// 加载并渲染Markdown
const loadMarkdownAndRender = async (markdownUrl) => {
  try {
    let mdText = ''
    let currentMdBaseUrl = ''

    const isLikelyUrl = /^https?:\/\//.test(markdownUrl) || markdownUrl.startsWith('/') || markdownUrl.startsWith('uploads/')
    
    if (isLikelyUrl) {
      const abs = getAbsoluteUrl(markdownUrl)
      
      // 记录该文档所在目录作为 base，用于修正相对路径资源
      if (abs.startsWith('http')) {
        try {
          const u = new URL(abs)
          // 去掉文件名，保留目录
          currentMdBaseUrl = `${u.origin}${u.pathname.substring(0, u.pathname.lastIndexOf('/') + 1)}`
        } catch (_) {
          currentMdBaseUrl = ''
        }
      } else {
        // 处理相对路径，计算基础路径
        const pathParts = abs.split('/').filter(part => part) // 过滤空字符串
        pathParts.pop() // 移除文件名
        currentMdBaseUrl = '/' + pathParts.join('/') + '/'
      }
      
      // 获取Markdown内容
      // 使用fetch而不是axios，避免可能的拦截器问题，并添加缓存控制
      // 添加时间戳参数强制刷新
      const urlWithTimestamp = abs + (abs.includes('?') ? '&' : '?') + '_t=' + Date.now()
      const response = await fetch(urlWithTimestamp, {
        cache: 'no-cache',
        headers: {
          'Cache-Control': 'no-cache, no-store, must-revalidate',
          'Pragma': 'no-cache',
          'Expires': '0'
        }
      })
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      mdText = await response.text()
    } else {
      mdText = markdownUrl || ''
    }

    if (!mdText || mdText.trim() === '') {
      htmlContent.value = '<p>暂无项目文档内容</p>'
      return
    }
    
    // 确保marked和DOMPurify已加载
    await loadExternalLibraries()
    
    // 渲染Markdown为HTML
    const rawHtml = window.marked.parse(mdText)
    
    // 用 DOMPurify 清洗前，先按 base 修正相对路径（避免相对资源 404）
    const withFixedLinks = postProcessHtmlWithBase(rawHtml, currentMdBaseUrl)
    
    // 后处理HTML，转换相对路径为绝对路径
    htmlContent.value = window.DOMPurify.sanitize(withFixedLinks)
  } catch (error) {
    console.error('Error loading or rendering markdown:', error)
    htmlContent.value = '<p>加载项目文档失败</p>'
  }
}

// 动态加载外部库
const loadExternalLibraries = async () => {
  // 加载marked
  if (!window.marked) {
    await new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/marked@4.3.0/marked.min.js'
      script.onload = resolve
      script.onerror = reject
      document.head.appendChild(script)
    })
  }
  
  // 加载DOMPurify
  if (!window.DOMPurify) {
    await new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/dompurify@3.0.3/dist/purify.min.js'
      script.onload = resolve
      script.onerror = reject
      document.head.appendChild(script)
    })
  }
}

// 获取项目详情
const fetchProject = async () => {
  try {
    // 添加时间戳参数确保获取最新版本
    const response = await http.get(`/projects/${route.params.id}?_t=${Date.now()}`)
    if (response.data && response.data.success) {
      project.value = response.data.data || {}
      
      // 获取收藏状态
      try {
        const starResponse = await http.get(`/projects/${route.params.id}/star/status`)
        const data = starResponse.data?.data || starResponse.data
        if (starResponse.data && starResponse.data.success) {
          isStarred.value = !!data?.isStarred
        } else {
          isStarred.value = !!data?.isStarred
        }
      } catch (error) {
        console.error('获取收藏状态失败:', error)
      }
      
      // 增加浏览量
      try {
        await incrementProjectView(route.params.id)
      } catch (error) {
        console.error('增加浏览量失败:', error)
      }
      
      // 如果项目有markdown文档路径，加载并渲染
      if (project.value.detailedDescription) {
        const markdownUrl = getAbsoluteUrl(project.value.detailedDescription)
        await loadMarkdownAndRender(markdownUrl)
      } else {
        // 如果没有markdown路径，尝试默认路径
        const defaultMarkdownUrl = `/uploads/projects/${route.params.id}/README.md`
        try {
          await loadMarkdownAndRender(defaultMarkdownUrl)
        } catch (error) {
          htmlContent.value = '<p>暂无项目文档内容</p>'
        }
      }
    } else {
      htmlContent.value = '<p>项目不存在或已被删除</p>'
    }
  } catch (error) {
    console.error('获取项目详情失败:', error)
    htmlContent.value = '<p>加载项目详情失败</p>'
  } finally {
    loading.value = false
  }
}

// 切换收藏状态
const toggleStar = async () => {
  // 检查用户是否已登录
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行收藏操作')
    // 跳转到登录页面，登录成功后返回当前页面
    router.push({
      path: '/auth/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  try {
    const response = await http.post(`/projects/${route.params.id}/star`)
    const ok = !!(response.data && response.data.success)
    if (ok) {
      const wasStarred = isStarred.value
      isStarred.value = !isStarred.value
      if (isStarred.value) {
        ElMessage.success('收藏成功！')
      } else {
        ElMessage.success('取消收藏成功！')
      }
    } else {
      const msg = response?.data?.message || '操作失败，请重试'
      ElMessage.error(msg)
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    if (error?.response?.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
      authStore.logout()
      router.push({
        path: '/auth/login',
        query: { redirect: route.fullPath }
      })
    } else {
      const msg = error?.response?.data?.message || '网络错误，请检查网络连接'
      ElMessage.error(msg)
    }
  }
}

// 打开仓库
const openRepo = () => {
  if (project.value?.githubUrl) {
    window.open(project.value.githubUrl, '_blank')
  } else {
    ElMessage.warning('暂无网址可用')
  }
}

// 打开演示
const openDemo = () => {
  if (project.value?.demoUrl) {
    window.open(project.value.demoUrl, '_blank')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 获取分类标签
const getCategoryLabel = (category) => {
  const categoryMap = {
    'software': '软件开发',
    'hardware': '硬件设计', 
    'embedded': '嵌入式系统',
    'ai': '人工智能',
    'other': '其他'
  }
  return categoryMap[category] || category
}

// 获取用户头像
const getAuthorAvatar = (avatar) => {
  return config.getAvatarUrl(avatar)
}

// 头像加载错误处理
const handleAvatarError = (event) => {
          event.target.src = new URL('../assets/about-image.jpg', import.meta.url).href
}

onMounted(() => {
  // 加载认证状态
  authStore.loadFromStorage()
  fetchProject()
})
</script>

<style scoped>
.project-detail-page {
  min-height: 100vh;
  background: #f6f8fa;
}

/* GitHub风格的项目头部 */
.project-header {
  background: #ffffff;
  border-bottom: 1px solid #d1d9e0;
  padding: 32px 0;
  margin-bottom: 32px;
}

.project-title-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
}

.project-breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: 600;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid #d1d9e0;
}

.author-name {
  color: #0969da;
  text-decoration: none;
  font-weight: 600;
  font-size: 18px;
}

.author-name:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  margin: 0 12px;
  color: #656d76;
  font-size: 20px;
}

.project-title {
  color: #0969da;
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.project-description {
  color: #656d76;
  font-size: 17px;
  margin: 16px 0 24px 0;
  line-height: 1.6;
}

.project-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  font-size: 15px;
  color: #656d76;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.project-actions {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 16px;
}

.star-button {
  background: #f6f8fa;
  border: 1px solid #d1d9e0;
  color: #24292f;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 6px;
}

.star-button:hover {
  background: #f3f4f6;
  border-color: #d1d9e0;
}

.code-button {
  background: #238636;
  border: 1px solid #238636;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 6px;
}

.code-button:hover {
  background: #2ea043;
  border-color: #2ea043;
}

.demo-button {
  background: #0969da;
  border: 1px solid #0969da;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 6px;
}

.demo-button:hover {
  background: #0860ca;
  border-color: #0860ca;
}

/* 收藏按钮样式 */
.action-buttons .el-button:first-child .fa-star {
  color: #ffd700; /* 已收藏时星星为金黄色 */
}

.action-buttons .el-button:first-child .fa-star.far {
  color: #ffffff; /* 未收藏时星星为白色 */
}    

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
  background: #ffffff;
  border: 1px solid #d1d9e0;
  border-radius: 8px;
  margin-top: 24px;
}

.markdown-body {
  color: #333;
  line-height: 1.8;
  font-size: 1rem;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 2rem;
  margin-bottom: 1rem;
  font-weight: bold;
  color: #2c3e50;
}

.markdown-body h1 {
  font-size: 2.5rem;
  border-bottom: 3px solid #667eea;
  padding-bottom: 0.5rem;
}

.markdown-body h2 {
  font-size: 2rem;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

.markdown-body h3 {
  font-size: 1.5rem;
}

.markdown-body h4 {
  font-size: 1.25rem;
}

.markdown-body p {
  margin-bottom: 1rem;
  color: #555;
}

.markdown-body ul,
.markdown-body ol {
  margin-bottom: 1rem;
  padding-left: 2rem;
}

.markdown-body li {
  margin-bottom: 0.5rem;
  color: #555;
}

.markdown-body blockquote {
  border-left: 4px solid #667eea;
  padding-left: 1rem;
  margin: 1rem 0;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
  color: #666;
}

.markdown-body code {
  background: #f1f3f4;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-family: 'Courier New', monospace;
  color: #e83e8c;
}

.markdown-body pre {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
  border: 1px solid #e9ecef;
}

.markdown-body pre code {
  background: none;
  padding: 0;
  color: #333;
}

.markdown-body img {
  max-width: 100%;
  height: auto;
  border-radius: 0.5rem;
  margin: 1rem 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.markdown-body table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  border: 1px solid #e9ecef;
  border-radius: 0.5rem;
  overflow: hidden;
}

.markdown-body th,
.markdown-body td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e9ecef;
}

.markdown-body th {
  background: #667eea;
  color: white;
  font-weight: bold;
}

.markdown-body tr:nth-child(even) {
  background: #f8f9fa;
}

.markdown-body a {
  color: #667eea;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.3s ease;
}

.markdown-body a:hover {
  border-bottom-color: #667eea;
}

.empty {
  text-align: center;
  color: #999;
  padding: 4rem 2rem;
  font-size: 1.1rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: white;
  font-size: 1.1rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .title {
    font-size: 2rem;
  }
  
  .meta {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: space-between;
  }
  
  .content-container {
    margin-top: 1rem;
    padding: 1rem;
  }
  
  .markdown-body h1 {
    font-size: 2rem;
  }
  
  .markdown-body h2 {
    font-size: 1.5rem;
  }
}
</style>