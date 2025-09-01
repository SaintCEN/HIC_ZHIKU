import http from '../utils/http'

/**
 * 知识库相关API
 */

// 获取知识库资源列表
export function getKnowledgeResources(params) {
  return http.get('/knowledge/resources', { params })
}

// 根据分类获取资源列表
export function getResourcesByCategory(category, params) {
  return http.get(`/knowledge/resources/category/${category}`, { params })
}

// 获取资源详情
export function getResourceDetail(id) {
  return http.get(`/knowledge/resources/${id}`)
}

// 增加资源浏览次数
export function incrementResourceView(id) {
  return http.post(`/knowledge/resources/${id}/view`)
}

// 增加资源下载次数
export function incrementResourceDownload(id) {
  return http.post(`/knowledge/resources/${id}/download`)
}

// 获取知识库统计信息
export function getKnowledgeStats() {
  return http.get('/knowledge/stats')
}

// 获取热门资源
export function getPopularResources(limit = 10) {
  return http.get('/knowledge/popular', { params: { limit } })
}

// 获取最新资源
export function getLatestResources(limit = 10) {
  return http.get('/knowledge/latest', { params: { limit } })
}