import http from '../utils/http'

/**
 * 工具相关API
 */

// 获取工具列表
export function getTools(params) {
  return http.get('/tools', { params })
}

// 获取工具详情
export function getToolDetail(id) {
  return http.get(`/tools/${id}`)
}

// 创建工具
export function createTool(data) {
  return http.post('/tools', data)
}

// 更新工具
export function updateTool(id, data) {
  return http.put(`/tools/${id}`, data)
}

// 删除工具
export function deleteTool(id) {
  return http.delete(`/tools/${id}`)
}

// 增加工具浏览量
export function incrementToolView(id) {
  return http.post(`/tools/${id}/view`)
}