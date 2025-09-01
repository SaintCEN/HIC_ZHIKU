import http from '../utils/http'

/**
 * 分享相关API
 */

// 获取分享列表
export function getShares(params) {
  return http.get('/senior-shares', { params })
}

// 获取分享详情
export function getShareDetail(id) {
  return http.get(`/senior-shares/${id}`)
}

// 创建分享
export function createShare(data) {
  return http.post('/senior-shares', data)
}

// 更新分享
export function updateShare(id, data) {
  return http.put(`/senior-shares/${id}`, data)
}

// 删除分享
export function deleteShare(id) {
  return http.delete(`/senior-shares/${id}`)
}