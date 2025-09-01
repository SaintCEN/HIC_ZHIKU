import http from '../utils/http'

// 添加收藏
export function addFavorite(data) {
  return http.post('/users/favorites', data)
}

// 取消收藏
export function removeFavorite(data) {
  return http.delete('/users/favorites', {
    data
  })
}

// 根据ID删除收藏
export function removeFavoriteById(favoriteId) {
  return http.delete(`/users/favorites/${favoriteId}`)
}

// 获取收藏列表
export function getFavorites(params) {
  return http.get('/users/favorites', { params })
}

// 检查是否已收藏
export function checkFavorite(data) {
  return http.get('/users/favorites/check', {
    params: data
  })
}