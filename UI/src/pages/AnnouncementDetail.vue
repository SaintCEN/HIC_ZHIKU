<template>
  <MainLayout>
    <div class="announcement-detail-page">
      <div class="page-header">
        <div class="header-cartoon header-cartoon--left">
          <img src="/src/assets/cartoon1.jpg" alt="cartoon decoration" />
        </div>
        <div class="header-content">
          <h1 class="page-title">{{ detail.title || '公告详情' }}</h1>
          <div class="page-meta">
            <span v-if="detail.publish_time || detail.publishTime || detail.createdAt" class="meta-item">
              <i class="fas fa-calendar-alt"></i>
              {{ formatDate(detail.publish_time || detail.publishTime || detail.createdAt) }}
            </span>
            <span v-if="(detail.viewCount || detail.view_count || detail.views) != null" class="meta-item">
              <i class="fas fa-eye"></i> {{ (detail.viewCount || detail.view_count || detail.views) }} 次查看
            </span>
          </div>
        </div>
        <div class="header-cartoon header-cartoon--right">
          <img src="/src/assets/cartoon2.png" alt="cartoon decoration" />
        </div>
      </div>

      <div class="content-container" v-if="!loading">
        <div class="document-content">
          <div v-if="htmlContent" class="markdown-content" v-html="htmlContent"></div>
          <div v-else class="empty">暂未找到文档内容</div>
        </div>
      </div>
      <div v-else class="loading">
        <div class="spinner"></div>
        加载中...
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import MainLayout from '../layouts/MainLayout.vue'
import { config } from '../utils/config.js'

const route = useRoute()
const loading = ref(true)
const detail = ref({})
const htmlContent = ref('')

// 记录 Markdown 文档的基础路径（用于修正相对资源路径）
let currentMdBaseUrl = ''

const getAbsoluteUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  return config.getUploadUrl(url)
}

const toAbsoluteWithBase = (maybeRelative, base) => {
  if (!maybeRelative) return maybeRelative
  // data URL 或锚点、邮件等不处理
  if (/^(data:|mailto:|tel:|#)/i.test(maybeRelative)) return maybeRelative
  // 已是绝对 URL
  if (/^https?:\/\//i.test(maybeRelative)) return maybeRelative
  
  // 如果base存在且不是绝对URL，尝试拼接
  if (base && !base.startsWith('http')) {
    try {
      // 处理相对路径
      if (maybeRelative.startsWith('./')) {
        maybeRelative = maybeRelative.substring(2)
      }
      if (maybeRelative.startsWith('../')) {
        // 计算上级目录
        const baseParts = base.split('/').filter(part => part)
        const relativeParts = maybeRelative.split('/').filter(part => part)
        
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
        const result = base + maybeRelative
        return result
      }
    } catch (error) {
      console.error('路径处理错误:', error)
    }
  }
  
  // 如果base不存在或处理失败，使用config.getUploadUrl处理
  return config.getUploadUrl(maybeRelative)
}

const postProcessHtmlWithBase = (rawHtml, base) => {
  if (!rawHtml) return rawHtml
  const container = document.createElement('div')
  container.innerHTML = rawHtml
  
  // 处理图片
  container.querySelectorAll('img').forEach(img => {
    const src = img.getAttribute('src')
    if (src) {
      const fixed = toAbsoluteWithBase(src, base)
      if (fixed) {
        img.setAttribute('src', fixed)
      }
    }
  })
  
  // 处理链接
  container.querySelectorAll('a').forEach(a => {
    const href = a.getAttribute('href')
    if (href) {
      const fixed = toAbsoluteWithBase(href, base)
      if (fixed) {
        a.setAttribute('href', fixed)
      }
    }
  })
  
  return container.innerHTML
}

const loadMarkdownAndRender = async (mdTextOrUrl) => {
  try {
    let mdText = ''
    currentMdBaseUrl = ''

    const isLikelyUrl = /^https?:\/\//.test(mdTextOrUrl) || mdTextOrUrl.startsWith('/') || mdTextOrUrl.startsWith('uploads/')
    if (isLikelyUrl) {
      const abs = getAbsoluteUrl(mdTextOrUrl)
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
      
      // 使用fetch而不是axios，避免可能的拦截器问题，并添加缓存控制
      const response = await fetch(abs, {
        cache: 'no-cache',
        headers: {
          'Cache-Control': 'no-cache, no-store, must-revalidate',
          'Pragma': 'no-cache',
          'Expires': '0'
        }
      })
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      mdText = await response.text()
    } else {
      mdText = mdTextOrUrl || ''
    }

    if (!mdText || mdText.trim() === '') {
      htmlContent.value = ''
      return
    }

    // 使用CDN加载的方式，但添加错误处理
    if (!window.marked || !window.DOMPurify) {
      // 如果全局对象不存在，动态加载
      await loadExternalLibraries()
    }

    window.marked.setOptions({ breaks: true, gfm: true })
    const rawHtml = window.marked.parse(mdText)

    // 用 DOMPurify 清洗前，先按 base 修正相对路径（避免相对资源 404）
    const withFixedLinks = postProcessHtmlWithBase(rawHtml, currentMdBaseUrl)

    htmlContent.value = window.DOMPurify.sanitize(withFixedLinks)
  } catch (e) {
    console.error('渲染 Markdown 失败:', e)
    htmlContent.value = `<p style="color: red;">加载文档失败: ${e.message}</p>`
  }
}

const loadExternalLibraries = async () => {
  return new Promise((resolve, reject) => {
    let loadedCount = 0
    const totalLibs = 2

    const checkComplete = () => {
      loadedCount++
      if (loadedCount === totalLibs) {
        resolve()
      }
    }

    // 加载 marked
    const markedScript = document.createElement('script')
    markedScript.src = 'https://cdn.jsdelivr.net/npm/marked@12.0.2/marked.min.js'
    markedScript.onload = checkComplete
    markedScript.onerror = () => reject(new Error('Failed to load marked'))
    document.head.appendChild(markedScript)

    // 加载 DOMPurify
    const purifyScript = document.createElement('script')
    purifyScript.src = 'https://cdn.jsdelivr.net/npm/dompurify@3.0.9/dist/purify.min.js'
    purifyScript.onload = checkComplete
    purifyScript.onerror = () => reject(new Error('Failed to load DOMPurify'))
    document.head.appendChild(purifyScript)
  })
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const { data } = await axios.get(`/api/portal/announcements/${id}?increaseView=false`)
    const a = data?.data || data || {}
    detail.value = a

    const mdUrl = a.content || a.markdownUrl || a.mdUrl || a.contentUrl || a.content_path || a.contentPath || a.md_path || a.file_url || a.documentUrl
    if (mdUrl) {
      await loadMarkdownAndRender(mdUrl)
    } else if (a.content) {
      if (typeof a.content === 'string' && (a.content.endsWith('.md') || a.content.includes('/uploads/') || a.content.startsWith('uploads/'))) {
        await loadMarkdownAndRender(a.content)
      } else {
        await loadMarkdownAndRender(a.content)
      }
    } else {
      htmlContent.value = ''
    }
  } catch (e) {
    console.error('获取公告详情失败:', e)
    detail.value = {}
    htmlContent.value = ''
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
@import '../styles/variables.scss';

.announcement-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
  position: relative;
}

.announcement-detail-page .page-header { 
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
  
  .page-meta {
    display: flex;
    justify-content: center;
    gap: 24px;
    font-size: 14px;
    color: #64748b;
    animation: metaFadeIn 1s ease-out 0.3s both;
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      background: rgba(255, 255, 255, 0.6);
      border-radius: 20px;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(79, 172, 254, 0.1);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.8);
        transform: translateY(-1px);
        box-shadow: 0 4px 15px rgba(79, 172, 254, 0.15);
      }
      
      i {
        color: #4facfe;
        font-size: 12px;
      }
    }
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
    
    .page-meta {
      flex-direction: column;
      gap: 12px;
      align-items: center;
      
      .meta-item {
        padding: 6px 12px;
        font-size: 12px;
      }
    }
  }
  
  @media (max-width: 480px) {
    .page-meta {
      .meta-item {
        width: auto;
        max-width: 200px;
        justify-content: center;
      }
    }
  }
}

.content-container {
  max-width: 900px;
  margin: 32px auto;
  padding: 0 16px;
  
  .document-content {
    background: white;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

// 动画定义
@keyframes titleFadeIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes metaFadeIn {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes underlineGrow {
  0% {
    width: 0;
    opacity: 0;
  }
  100% {
    width: 60px;
    opacity: 1;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(-50%) translateX(0);
  }
  50% {
    transform: translateY(-50%) translateX(10px) rotate(5deg);
  }
}

.loading { 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  padding: 48px;
  color: #666;
}

.spinner { 
  width: 24px; 
  height: 24px; 
  border: 3px solid #f3f3f3; 
  border-top-color: $primary-color; 
  border-radius: 50%; 
  animation: spin 1s linear infinite; 
  margin-right: 12px; 
}

@keyframes spin { 
  to { transform: rotate(360deg); } 
}

// Markdown 内容样式
:deep(.markdown-content) {
  line-height: 1.7;
  color: #333;
  font-size: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  
  h1, h2, h3, h4, h5, h6 {
    color: #2c3e50;
    margin-top: 2em;
    margin-bottom: 1em;
    font-weight: 600;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
  
  h1 {
    font-size: 2em;
    border-bottom: 2px solid #eee;
    padding-bottom: 0.5em;
    margin-top: 0;
  }
  
  h2 {
    font-size: 1.6em;
    border-bottom: 1px solid #eee;
    padding-bottom: 0.3em;
  }
  
  h3 {
    font-size: 1.4em;
  }
  
  h4 {
    font-size: 1.2em;
  }
  
  h5, h6 {
    font-size: 1em;
  }
  
  p {
    margin-bottom: 1.2em;
    text-align: justify;
    word-break: break-word;
  }
  
  blockquote {
    border-left: 4px solid #ddd;
    background: #f9f9f9;
    padding: 1em 1.5em;
    margin: 1.5em 0;
    border-radius: 0 4px 4px 0;
    font-style: italic;
    color: #666;
  }
  
  code {
    background: #f1f3f4;
    padding: 0.2em 0.4em;
    border-radius: 3px;
    font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Source Code Pro', 'Consolas', monospace;
    font-size: 0.9em;
    color: #d73a49;
  }
  
  pre {
    background: #f6f8fa;
    border: 1px solid #e1e4e8;
    border-radius: 6px;
    padding: 1.2em;
    overflow-x: auto;
    margin: 1.5em 0;
    
    code {
      background: none;
      padding: 0;
      color: #24292e;
      font-size: 0.85em;
    }
  }
  
  ul, ol {
    padding-left: 2em;
    margin-bottom: 1.2em;
    
    li {
      margin-bottom: 0.4em;
    }
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin: 1.5em 0;
    border: 1px solid #e1e4e8;
    border-radius: 6px;
    overflow: hidden;
    
    th, td {
      padding: 12px 16px;
      text-align: left;
      border-bottom: 1px solid #e1e4e8;
    }
    
    th {
      background: #f6f8fa;
      font-weight: 600;
      color: #24292e;
    }
    
    tr:nth-child(even) {
      background: #f6f8fa;
    }
  }
  
  img {
    max-width: 100%;
    height: auto;
    border-radius: 6px;
    margin: 1.5em 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  a {
    color: #0366d6;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  hr {
    border: none;
    border-top: 1px solid #e1e4e8;
    margin: 2em 0;
  }
  
  strong {
    font-weight: 600;
  }
  
  em {
    font-style: italic;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .content-container {
    margin: 20px 16px;
    
    .document-content {
      padding: 24px;
    }
  }
  
  :deep(.markdown-content) {
    font-size: 14px;
    
    h1 {
      font-size: 1.8em;
    }
    
    h2 {
      font-size: 1.5em;
    }
    
    h3 {
      font-size: 1.3em;
    }
  }
}
</style>