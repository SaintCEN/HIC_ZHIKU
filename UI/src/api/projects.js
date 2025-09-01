import http from '../utils/http'

/**
 * 项目相关API
 */

// 获取项目列表
export function getProjects(params) {
  return http.get('/projects', { params })
}

// 获取项目详情
export function getProjectDetail(id, increaseView = false) {
  return http.get(`/projects/${id}`, {
    params: { increaseView }
  })
}

// 获取项目详情（别名）
export function getProjectById(id, increaseView = false) {
  return getProjectDetail(id, increaseView)
}

// 创建项目
export function createProject(data) {
  return http.post('/projects', data)
}

// 更新项目
export function updateProject(id, data) {
  // 检查是否包含文件
  const hasFile = data.documentFile instanceof File
  
  if (hasFile) {
    // 如果有文件，使用FormData
    const formData = new FormData()
    formData.append('title', data.title)
    formData.append('description', data.description || '')
    formData.append('category', data.category)
    if (data.githubUrl) {
      formData.append('githubUrl', data.githubUrl)
    }
    formData.append('documentFile', data.documentFile)
    
    return http.put(`/projects/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  } else {
    // 如果没有文件，使用JSON格式
    return http.put(`/projects/${id}/info`, {
      title: data.title,
      description: data.description || '',
      category: data.category,
      githubUrl: data.githubUrl || '',
      detailedDescription: data.detailedDescription || null
    })
  }
}

// 删除项目
export function deleteProject(id) {
  return http.delete(`/projects/${id}`)
}

// 增加项目浏览量
export function incrementProjectView(id) {
  return http.post(`/projects/${id}/view`)
}

// 增加项目点赞数
export function incrementProjectLike(id) {
  return http.post(`/projects/${id}/like`)
}

// 取消项目点赞
export function decrementProjectLike(id) {
  return http.delete(`/projects/${id}/like`)
}

// 增加项目收藏数
export function incrementProjectFavorite(id) {
  return http.post(`/projects/${id}/favorite`)
}

// 取消项目收藏
export function decrementProjectFavorite(id) {
  return http.delete(`/projects/${id}/favorite`)
}

// 获取项目评论列表
export function getProjectComments(projectId, params) {
  return http.get(`/projects/${projectId}/comments`, { params })
}

// 添加项目评论
export function addProjectComment(projectId, data) {
  return http.post(`/projects/${projectId}/comments`, data)
}

// 删除项目评论
export function deleteProjectComment(projectId, commentId) {
  return http.delete(`/projects/${projectId}/comments/${commentId}`)
}

// 获取项目分类
export function getProjectCategories() {
  return http.get('/projects/categories')
}

// 获取项目标签
export function getProjectTags() {
  return http.get('/projects/tags')
}

// 获取推荐项目
export function getFeaturedProjects(limit = 10) {
  return http.get('/projects/featured', { params: { limit } })
}

// 获取热门项目
export function getPopularProjects(limit = 10) {
  return http.get('/projects/popular', { params: { limit } })
}

// 获取我的项目
export function getMyProjects(params) {
  return http.get('/projects/my', { params })
}