// 环境配置工具
export const config = {
  // API基础地址
  API_BASE_URL: '/api',
  
  // 上传文件基础地址 
  UPLOAD_BASE_URL: '/uploads',
  
  // 获取完整的API地址
  getApiUrl(path) {
    return `${this.API_BASE_URL}${path.startsWith('/') ? '' : '/'}${path}`
  },
  
  // 获取完整的上传文件地址
  getUploadUrl(path) {
    if (!path) return ''
    if (path.startsWith('http')) return path
    if (path.startsWith('data:')) return path
    if (path.startsWith('blob:')) return path
    
    // 如果路径已经包含 /uploads/ 前缀，直接返回
    if (path.startsWith('/uploads/')) {
      return path
    }
    
    // 如果路径以 / 开头，直接拼接
    if (path.startsWith('/')) {
      return `${this.UPLOAD_BASE_URL}${path}`
    }
    
    // 如果路径已经包含 uploads 前缀，添加 / 前缀
    if (path.startsWith('uploads/')) {
      return `/${path}`
    }
    
    // 否则添加 /uploads/ 前缀
    return `${this.UPLOAD_BASE_URL}/${path}`
  },
  
  // 获取头像地址
  getAvatarUrl(avatar) {
    if (!avatar) return new URL('../assets/about-image.jpg', import.meta.url).href
    if (avatar.startsWith('http') || avatar.startsWith('data:') || avatar.startsWith('blob:')) return avatar
    // 如果路径以 / 开头，直接返回
    if (avatar.startsWith('/')) {
      return avatar
    }
    // 否则添加 / 前缀
    return `/${avatar}`
  }
}

export default config
