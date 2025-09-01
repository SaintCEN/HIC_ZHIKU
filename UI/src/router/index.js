import { createRouter, createWebHistory } from 'vue-router'

// 懒加载页面组件
const Landing = () => import('../pages/Landing.vue')
const Portal = () => import('../pages/Portal.vue')
const About = () => import('../pages/About.vue')
const Knowledge = () => import('../pages/Knowledge.vue')
const Projects = () => import('../pages/Projects.vue')
const ProjectUpload = () => import('../pages/ProjectUpload.vue')
const Competitions = () => import('../pages/Competitions.vue')
const Tools = () => import('../pages/Tools.vue')
const Articles = () => import('../pages/Articles.vue')
const Share = () => import('../pages/Share.vue')
const ShareDetail = () => import('../pages/ShareDetail.vue')
const ShareUpload = () => import('../pages/ShareUpload.vue')
const Announcements = () => import('../pages/Announcements.vue')
const Events = () => import('../pages/Events.vue')
const AnnouncementDetail = () => import('../pages/AnnouncementDetail.vue')
const EventDetail = () => import('../pages/EventDetail.vue')


// 还未实现的页面组件，暂时使用占位组件
// 这些组件将在后续开发中实现
const NotFound = () => import('../pages/Portal.vue') // 暂时重定向到门户页面
const ContentDetail = { template: '<div>内容详情 - 开发中</div>' }
const ProjectDetail = { template: '<div>项目详情 - 开发中</div>' }

// 知识库页面不需要子页面，直接显示资源列表

// 竞赛专区子页面 - 暂时使用通用组件
const CompetitionInfo = { template: '<div>竞赛信息 - 开发中</div>' }
const CompetitionTeams = { template: '<div>组队社区 - 开发中</div>' }

const Login = () => import('../pages/Login.vue')
const Register = () => import('../pages/Register.vue')
const Profile = () => import('../pages/Profile.vue')
const ProjectRepository = () => import('../pages/ProjectRepository.vue')


const routes = [
  {
    path: '/',
    name: 'Landing',
    component: Landing,
    meta: { title: '华创智库 - 西电华为俱乐部' }
  },
  {
    path: '/auth/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录 - 华创智库', guestOnly: true }
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册 - 华创智库', guestOnly: true }
  },
  {
    path: '/auth/profile',
    name: 'Profile',
    component: Profile,
    meta: { title: '个人主页 - 华创智库', requiresAuth: true }
  },
  {
    path: '/repository',
    name: 'ProjectRepository',
    component: ProjectRepository,
    meta: { title: '项目仓库 - 华创智库', requiresAuth: true }
  },
  {
    path: '/project/edit/:id',
    name: 'ProjectEdit',
    component: () => import('../pages/ProjectEdit.vue'),
    meta: { requiresAuth: true, title: '编辑项目 - 华创智库' }
  },

  {
    path: '/portal',
    name: 'Portal',
    component: Portal,
    meta: { title: '门户 - 华创智库' }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { title: '关于我们 - 华创智库' }
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: Knowledge,
    meta: { title: '知识库 - 华创智库' }
  },
  {
    path: '/knowledge/:category',
    name: 'KnowledgeCategory',
    component: Knowledge,
    meta: { title: '知识库 - 华创智库' }
  },
  {
    path: '/projects',
    name: 'Projects',
    component: Projects,
    meta: { title: '项目展示 - 华创智库' }
  },
  {
    path: '/projects/upload',
    name: 'ProjectUpload',
    component: ProjectUpload,
    meta: { title: '分享我的项目 - 华创智库', requiresAuth: true }
  },
  {
    path: '/projects/:id',
    name: 'ProjectDetail',
    component: () => import('../pages/ProjectDetail.vue'),
    meta: { title: '项目详情 - 华创智库' }
  },
  {
    path: '/competitions',
    name: 'Competitions',
    component: Competitions,
    meta: { title: '竞赛专区 - 华创智库' },
    redirect: '/competitions/info',
    children: [
      {
        path: 'info',
        name: 'CompetitionInfo',
        component: CompetitionInfo,
        meta: { title: '竞赛信息 - 华创智库' }
      },
      {
        path: 'teams',
        name: 'CompetitionTeams',
        component: CompetitionTeams,
        meta: { title: '组队社区 - 华创智库' }
      }
    ]
  },
  {
    path: '/tools',
    name: 'Tools',
    component: Tools,
    meta: { title: '实用工具 - 华创智库' }
  },
  {
    path: '/articles',
    name: 'Articles',
    component: Articles,
    meta: { title: '华俱微信推文 - 华创智库' }
  },
  {
    path: '/content/:id',
    name: 'ContentDetail',
    component: ContentDetail,
    meta: { title: '内容详情 - 华创智库' }
  },
  {
    path: '/share',
    name: 'Share',
    component: Share,
    meta: { title: '师兄师姐说 - 华创智库' }
  },
  {
    path: '/share/upload',
    name: 'ShareUpload',
    component: ShareUpload,
    meta: { title: '分享你的经验 - 华创智库', requiresAuth: true }
  },
  {
    path: '/share/:id',
    name: 'ShareDetail',
    component: ShareDetail,
    meta: { title: '经验详情 - 华创智库' }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements,
    meta: { title: '最新公告 - 华创智库' }
  },
  {
    path: '/announcements/:id',
    name: 'AnnouncementDetail',
    component: AnnouncementDetail,
    meta: { title: '公告详情 - 华创智库' }
  },
  {
    path: '/events',
    name: 'Events',
    component: Events,
    meta: { title: '近期活动 - 华创智库' }
  },
  {
    path: '/events/:id',
    name: 'EventDetail',
    component: EventDetail,
    meta: { title: '活动详情 - 华创智库' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '页面未找到 - 华创智库' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 全局前置守卫 - 设置页面标题
router.beforeEach(async (to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // 认证守卫（避免在 Pinia 尚未激活时报错，直接读本地存储）
  let isLoggedIn = false
  let accessToken = ''
  try {
    const raw = localStorage.getItem('authState')
    if (raw) {
      const parsed = JSON.parse(raw)
      isLoggedIn = !!parsed.isLoggedIn && !!parsed.accessToken
      accessToken = parsed.accessToken || ''
    }
  } catch (_) {}
  
  // 如果有token，检查是否已过期
  if (isLoggedIn && accessToken) {
    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]))
      const expiration = new Date(payload.exp * 1000)
      const now = new Date()
      
      if (expiration <= now) {
        // token已过期，清除本地存储
        localStorage.removeItem('authState')
        isLoggedIn = false
      }
    } catch (error) {
      // token解析失败，清除本地存储
      localStorage.removeItem('authState')
      isLoggedIn = false
    }
  }
  
  // 已登录用户访问根目录时跳转到portal
  if (to.path === '/' && isLoggedIn) {
    return next({ path: '/portal' })
  }
  
  // 需要认证的页面，如果未登录，跳转到登录页面
  if (to.meta.requiresAuth && !isLoggedIn) {
    return next({ path: '/auth/login', query: { redirect: to.fullPath } })
  }
  
  // 已登录用户访问登录/注册页面时跳转到个人主页
  if (to.meta.guestOnly && isLoggedIn) {
    return next({ path: '/auth/profile' })
  }
  
  next()
})

export default router