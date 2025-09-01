import http from '../utils/http'

// 获取文章列表
export const getArticles = (params) => {
  return http.get('/articles', { params })
}

// 根据ID获取文章详情
export const getArticleById = (id, increaseViewCount = false) => {
  return http.get(`/articles/${id}`, {
    params: { increaseViewCount }
  })
}

// 增加文章浏览量
export const incrementArticleView = (id) => {
  return http.post(`/articles/${id}/view`)
}