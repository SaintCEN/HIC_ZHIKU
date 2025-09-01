import http from '../utils/http'

/**
 * 竞赛相关API
 */

// 获取竞赛列表
export function getCompetitions(params) {
  return http.get('/competitions', { params })
}

// 获取竞赛详情
export function getCompetitionDetail(id) {
  return http.get(`/competitions/${id}`)
}

// 创建竞赛
export function createCompetition(data) {
  return http.post('/competitions', data)
}

// 更新竞赛
export function updateCompetition(id, data) {
  return http.put(`/competitions/${id}`, data)
}

// 删除竞赛
export function deleteCompetition(id) {
  return http.delete(`/competitions/${id}`)
}

// 增加竞赛浏览量
export function incrementCompetitionView(id) {
  return http.post(`/competitions/${id}/view`)
}

// 获取竞赛分类
export function getCompetitionCategories() {
  return http.get('/competitions/categories')
}

// 获取竞赛标签
export function getCompetitionTags() {
  return http.get('/competitions/tags')
}