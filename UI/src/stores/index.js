import { defineStore } from 'pinia'

// 定义应用全局状态
export const useAppStore = defineStore('app', {
  state: () => ({
    isMobileMenuOpen: false,
    searchKeyword: '',
    notifications: [],
  }),
  
  actions: {
    toggleMobileMenu() {
      this.isMobileMenuOpen = !this.isMobileMenuOpen
      
      // 移动菜单打开时禁止背景滚动
      if (this.isMobileMenuOpen) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
      }
    },
    
    setSearchKeyword(keyword) {
      this.searchKeyword = keyword
    },
    
    addNotification(notification) {
      // 添加通知，并自动设置一个唯一ID
      const id = Date.now()
      this.notifications.push({
        id,
        ...notification,
        timestamp: new Date(),
      })
      
      // 如果设置了自动关闭时间，则安排自动关闭
      if (notification.autoClose !== false) {
        const timeout = notification.timeout || 3000
        setTimeout(() => {
          this.removeNotification(id)
        }, timeout)
      }
      
      return id
    },
    
    removeNotification(id) {
      const index = this.notifications.findIndex(n => n.id === id)
      if (index !== -1) {
        this.notifications.splice(index, 1)
      }
    },
    
    clearAllNotifications() {
      this.notifications = []
    }
  },
  
  getters: {
    hasNotifications: (state) => state.notifications.length > 0,
  }
})

// 认证与用户信息状态
export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    accessToken: '',
    lastTokenCheck: 0,
    user: {
      username: '',
      nickname: '',
      email: '',
      college: '',
      bio: '',
      avatarUrl: '',
      hic: 0
    }
  }),

  actions: {
    loadFromStorage() {
      const raw = localStorage.getItem('authState')
      if (raw) {
        try {
          const parsed = JSON.parse(raw)
          this.isLoggedIn = !!parsed.isLoggedIn
          this.accessToken = parsed.accessToken || ''
          this.lastTokenCheck = parsed.lastTokenCheck || 0
          this.user = parsed.user || this.user
        } catch (_) {
          // ignore parse error
        }
      }
    },

    persist() {
      localStorage.setItem('authState', JSON.stringify({
        isLoggedIn: this.isLoggedIn,
        accessToken: this.accessToken,
        lastTokenCheck: this.lastTokenCheck,
        user: this.user
      }))
    },

    async login(payload) {
      const { default: http } = await import('../utils/http')
      const resp = await http.post('/auth/login', {
        username: payload.username,
        password: payload.password
      })
      const data = resp.data?.data || {}
      this.accessToken = data.accessToken || ''
      this.isLoggedIn = !!this.accessToken
      if (data.user) {
        this.user = {
          ...this.user,
          username: data.user.username,
          nickname: data.user.nickname || data.user.username,
          avatarUrl: data.user.avatarUrl || this.user.avatarUrl,
          hic: data.user.hic !== undefined ? data.user.hic : 0
        }
      }
      this.persist()
      // 立即返回当前状态，避免外层判断异常
      return { success: this.isLoggedIn }
    },

    async register(payload) {
      const { default: http } = await import('../utils/http')
      await http.post('/auth/register', {
        username: payload.username,
        password: payload.password,
        nickname: payload.nickname
      })
      return { success: true }
    },

    logout() {
      this.isLoggedIn = false
      this.accessToken = ''
      this.lastTokenCheck = 0
      this.user = { username: '', nickname: '', email: '', college: '', bio: '', avatarUrl: '', hic: 0 }
      this.persist()
    },

    updateProfile(partial) {
      this.user = { ...this.user, ...partial }
      this.persist()
    },

    // 检查token是否有效
    async checkTokenValidity() {
      if (!this.accessToken) {
        return false
      }
      
      try {
        const { default: http } = await import('../utils/http')
        await http.get('/users/me')
        return true
      } catch (error) {
        if (error.response?.status === 401) {
          this.logout()
          return false
        }
        // 其他错误不影响token有效性判断
        return true
      }
    },

    // 解析token获取过期时间
    getTokenExpiration() {
      if (!this.accessToken) {
        return null
      }
      
      try {
        const payload = JSON.parse(atob(this.accessToken.split('.')[1]))
        return new Date(payload.exp * 1000)
      } catch (error) {
        console.warn('Failed to parse token:', error)
        return null
      }
    },

    // 检查token是否已过期
    isTokenExpired() {
      const expiration = this.getTokenExpiration()
      if (!expiration) {
        return true // 无法解析token，认为已过期
      }
      
      const now = new Date()
      return expiration.getTime() <= now.getTime()
    }
  },

  getters: {
    isAuthenticated: (state) => state.isLoggedIn
  }
})

// 知识库状态
export const useKnowledgeStore = defineStore('knowledge', {
  state: () => ({
    currentCategory: '硬件',
    difficulty: '', // 'beginner', 'intermediate', 'advanced'
    resourceType: '', // 'book', 'video', 'code', 'project'
    articles: []
  }),
  
  actions: {
    setCategory(category) {
      this.currentCategory = category
    },
    
    setDifficulty(difficulty) {
      this.difficulty = difficulty
    },
    
    setResourceType(type) {
      this.resourceType = type
    },
    
    resetFilters() {
      this.difficulty = ''
      this.resourceType = ''
    }
  },
  
  getters: {
    filteredArticles: (state) => {
      let result = state.articles
      
      // 按分类筛选
      if (state.currentCategory) {
        result = result.filter(article => article.category === state.currentCategory)
      }
      
      // 按难度筛选
      if (state.difficulty) {
        result = result.filter(article => article.difficulty === state.difficulty)
      }
      
      // 按资源类型筛选
      if (state.resourceType) {
        result = result.filter(article => article.resourceType === state.resourceType)
      }
      
      return result
    }
  }
})

// 竞赛专区状态
export const useCompetitionStore = defineStore('competition', {
  state: () => ({
    currentTab: 'info',
    teamFilter: '', // 'recruit', 'apply'
    competitionFilter: ''
  }),
  
  actions: {
    setTab(tab) {
      this.currentTab = tab
    },
    
    setTeamFilter(filter) {
      this.teamFilter = filter
    },
    
    setCompetitionFilter(filter) {
      this.competitionFilter = filter
    },
    
    resetFilters() {
      this.teamFilter = ''
      this.competitionFilter = ''
    }
  }
})

// 导出所有存储以供模块使用
export const stores = {
  useAppStore,
  useKnowledgeStore,
  useCompetitionStore
}