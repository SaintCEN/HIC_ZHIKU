<template>
  <div class="profile-wrapper">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
      <div class="wave wave-3"></div>
    </div>

    <!-- 顶部导航 -->
    <div class="top-nav">
      <el-button class="back-btn" link @click="router.push('/portal')">
        <font-awesome-icon icon="arrow-left" />
        返回门户
      </el-button>
      <h1 class="page-title">个人中心</h1>
      <div class="nav-spacer"></div>
    </div>

    <div class="profile-container">
      <!-- 左侧用户信息卡片 -->
      <div class="user-info-card">
        <div class="card-header">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :auto-upload="false"
              :before-upload="beforeUpload"
              :on-change="onFileChange"
              accept="image/*"
            >
              <div class="avatar-container">
                <img :src="avatar" class="avatar" alt="avatar" />
                <div class="avatar-overlay">
                  <font-awesome-icon icon="camera" />
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
          </div>
          <div class="user-basic-info">
            <h2 class="display-name">{{ auth.user.nickname || auth.user.username }}</h2>
            <p class="username">@{{ auth.user.username || '-' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ joinDays }}</span>
                <span class="stat-label">加入天数</span>
              </div>
              <div class="stat-item">
                <div class="hic-status" :class="{ 'verified': auth.user.hic === 1 }">
                  <font-awesome-icon :icon="auth.user.hic === 1 ? 'check-circle' : 'times-circle'" />
                  <span class="status-text">{{ auth.user.hic === 1 ? '已认证' : '未认证' }}</span>
                </div>
                <span class="stat-label">HIC认证</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧编辑表单 -->
      <div class="edit-form-card">
        <div class="card-header">
          <h3 class="card-title">个人信息</h3>
        </div>

        <el-form label-position="top" class="profile-form" :model="model">
          <div class="form-row">
            <el-form-item label="昵称" class="form-item-half">
              <el-input 
                v-model="model.nickname" 
                placeholder="请输入昵称" 
                size="large"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="邮箱" class="form-item-half">
              <el-input 
                v-model="model.email" 
                placeholder="请输入邮箱" 
                size="large"
                prefix-icon="Message"
              />
            </el-form-item>
          </div>

          <el-form-item label="学院">
            <el-select 
              v-model="model.college" 
              placeholder="请选择学院" 
              size="large"
              style="width: 100%"
            >
              <el-option label="通信工程学院" value="CE" />
              <el-option label="电子工程学院" value="EE" />
              <el-option label="计算机科学与技术学院" value="CST" />
              <el-option label="机电工程学院" value="ME" />
              <el-option label="物理学院" value="PH" />
              <el-option label="光电工程学院" value="OE" />
              <el-option label="经济与管理学院" value="EM" />
              <el-option label="数学与统计学院" value="MS" />
              <el-option label="人文学院" value="HA" />
              <el-option label="外国语学院" value="FL" />
              <el-option label="集成电路学部" value="IC" />
              <el-option label="生命科学技术学院" value="LS" />
              <el-option label="空间科学与技术学院" value="SS" />
              <el-option label="先进材料与纳米科技学院" value="AM" />
              <el-option label="网络与信息安全学院" value="NI" />
              <el-option label="人工智能学院" value="AI" />
              <el-option label="信息力学与感知工程学院" value="IM" />
              <el-option label="体育部" value="PE" />
              <el-option label="无" value="NONE" />
            </el-select>
          </el-form-item>

          <el-form-item label="个人简介">
            <el-input 
              v-model="model.bio" 
              type="textarea" 
              :rows="4" 
              placeholder="介绍一下自己吧~ 比如您的专业方向、兴趣爱好、技能特长等"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>



          <div class="form-actions">
            <el-button size="large" @click="resetForm">重置</el-button>
            <el-button type="primary" size="large" @click="save" :loading="saving">
              <font-awesome-icon icon="save" v-if="!saving" />
              {{ saving ? '保存中...' : '保存更改' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore, useAppStore } from '../stores'
import { config } from '../utils/config.js'

const router = useRouter()
const app = useAppStore()
const auth = useAuthStore()

const saving = ref(false)

// 计算加入天数
const joinDays = computed(() => {
  if (auth.user?.createdAt) {
    const joinDate = new Date(auth.user.createdAt)
    const today = new Date()
    const diffTime = Math.abs(today - joinDate)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }
  return 0
})

onMounted(() => {
  auth.loadFromStorage()
  if (!auth.isLoggedIn) {
    router.replace('/auth/login')
    return
  }
  
  // 检查token是否已过期
  if (auth.isTokenExpired()) {
    auth.logout()
    router.replace('/auth/login')
    return
  }
  
  // 获取后端资料（以最新为准）
  fetchProfile()
})

const avatar = computed(() => {
  return config.getAvatarUrl(auth.user.avatarUrl)
})

const model = reactive({
  nickname: auth.user.nickname,
  email: auth.user.email,
  college: auth.user.college,
  bio: auth.user.bio
})

function resetForm() {
  model.nickname = auth.user.nickname || ''
  model.email = auth.user.email || ''
  model.college = auth.user.college || ''
  model.bio = auth.user.bio || ''
  app.addNotification({ type: 'info', message: '表单已重置' })
}

function save() {
  updateProfile()
}

async function fetchProfile() {
  try {
    const { default: http } = await import('../utils/http')
    const resp = await http.get('/users/me')
    const data = resp.data?.data
    if (data) {
      auth.updateProfile(data)
      model.nickname = data.nickname || ''
      model.email = data.email || ''
      model.college = data.college || ''
      model.bio = data.bio || ''
    }
  } catch (e) {
    console.error('获取用户信息失败:', e)
    if (e.response?.status === 401) {
      app.addNotification({ type: 'error', message: '登录已过期，请重新登录' })
      auth.logout()
      router.replace('/auth/login')
    } else {
      app.addNotification({ type: 'error', message: '获取用户信息失败' })
    }
  }
}

async function updateProfile() {
  try {
    saving.value = true
    const { default: http } = await import('../utils/http')
    const resp = await http.put('/users/me', {
      nickname: model.nickname,
      email: model.email,
      college: model.college,
      bio: model.bio,
      avatarUrl: auth.user.avatarUrl
    })
    const data = resp.data?.data
    if (data) {
      auth.updateProfile(data)
      // 同步更新本地模型数据
      model.nickname = data.nickname || ''
      model.email = data.email || ''
      model.college = data.college || ''
      model.bio = data.bio || ''
    }
    app.addNotification({ type: 'success', message: '个人信息保存成功' })
  } catch (e) {
    console.error('保存个人信息失败:', e)
    let errorMessage = '保存失败'
    
    if (e.response?.status === 401) {
      errorMessage = '登录已过期，请重新登录'
      auth.logout()
      router.replace('/auth/login')
    } else if (e.response?.status === 400) {
      errorMessage = e.response.data?.message || '请求参数错误'
    } else if (e.response?.status === 500) {
      errorMessage = '服务器内部错误，请稍后重试'
    } else if (e.code === 'NETWORK_ERROR') {
      errorMessage = '网络连接失败，请检查网络'
    } else if (e.response?.data?.message) {
      errorMessage = e.response.data.message
    }
    
    app.addNotification({ type: 'error', message: errorMessage })
  } finally {
    saving.value = false
  }
}

function beforeUpload(rawFile) {
  const isImg = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isImg) {
    app.addNotification({ type: 'error', message: '仅支持上传图片文件' })
    return false
  }
  if (!isLt2M) {
    app.addNotification({ type: 'error', message: '图片大小需小于2MB' })
    return false
  }
  return true
}

async function onFileChange(file) {
  const raw = file.raw
  if (!raw) return
  if (!beforeUpload(raw)) return
  try {
    const form = new FormData()
    form.append('file', raw)
    const { default: http } = await import('../utils/http')
    const resp = await http.post('/upload/avatar', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    const url = resp.data?.url || resp.data?.data?.url
    if (url) {
      auth.updateProfile({ avatarUrl: url })
      app.addNotification({ type: 'success', message: '头像已上传并保存' })
    } else {
      app.addNotification({ type: 'error', message: '头像上传返回数据异常' })
    }
  } catch (e) {
    let errorMessage = '头像上传失败'
    if (e.response?.status === 401) {
      errorMessage = '认证失败，请重新登录'
    } else if (e.response?.status === 413) {
      errorMessage = '文件过大，请选择小于2MB的图片'
    } else if (e.response?.data?.message) {
      errorMessage = e.response.data.message
    }
    app.addNotification({ type: 'error', message: errorMessage })
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.profile-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow-x: hidden;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;

  .wave {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    animation: float 6s ease-in-out infinite;

    &.wave-1 {
      width: 300px;
      height: 300px;
      background: linear-gradient(135deg, $primary-color, #ff6b6b);
      top: -150px;
      right: -150px;
      animation-delay: 0s;
    }

    &.wave-2 {
      width: 200px;
      height: 200px;
      background: linear-gradient(135deg, #4ecdc4, #44a08d);
      bottom: 20%;
      left: -100px;
      animation-delay: 2s;
    }

    &.wave-3 {
      width: 150px;
      height: 150px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      top: 30%;
      left: 10%;
      animation-delay: 4s;
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

// 顶部导航
.top-nav {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2rem 2rem 1rem;

  .back-btn {
    color: #666;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      color: $primary-color;
      transform: translateX(-4px);
    }

    .fa-icon {
      margin-right: 8px;
    }
  }

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #2c3e50;
    margin: 0;
    text-align: center;
    flex: 1;
  }

  .nav-spacer {
    width: 120px; // 平衡布局
  }
}

// 主容器
.profile-container {
  position: relative;
  z-index: 10;
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 2rem;
  padding: 0 2rem 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

// 用户信息卡片
.user-info-card {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 2rem;

  .card-header {
    text-align: center;
    margin-bottom: 2rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid #f0f0f0;
  }

  .avatar-section {
    margin-bottom: 1.5rem;

    .avatar-container {
      position: relative;
      display: inline-block;
      cursor: pointer;

      .avatar {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        object-fit: cover;
        border: 4px solid white;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
      }

      .avatar-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 50%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: all 0.3s ease;
        color: white;
        font-size: 12px;

        .fa-icon {
          font-size: 20px;
          margin-bottom: 4px;
        }
      }

      &:hover {
        .avatar {
          transform: scale(1.05);
        }

        .avatar-overlay {
          opacity: 1;
        }
      }
    }
  }

  .user-basic-info {
    .display-name {
      font-size: 1.5rem;
      font-weight: 700;
      color: #2c3e50;
      margin: 0 0 0.5rem 0;
    }

    .username {
      color: #7f8c8d;
      font-size: 1rem;
      margin: 0 0 1.5rem 0;
    }

    .user-stats {
      display: flex;
      justify-content: space-around;
      gap: 1rem;

      .stat-item {
        text-align: center;

        .stat-number {
          display: block;
          font-size: 1.25rem;
          font-weight: 700;
          color: $primary-color;
        }

        .stat-label {
          font-size: 0.875rem;
          color: #7f8c8d;
        }

        .hic-status {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0.5rem;
          margin-bottom: 0.25rem;
          padding: 0.5rem;
          border-radius: 8px;
          background: rgba(107, 114, 128, 0.1);
          color: #6b7280;
          font-size: 0.875rem;
          font-weight: 600;
          transition: all 0.3s ease;

          &.verified {
            background: rgba(5, 150, 105, 0.1);
            color: #059669;
          }

          .fa-icon {
            font-size: 1rem;
          }

          .status-text {
            font-size: 0.875rem;
          }
        }
      }
    }
  }

  .quick-actions {
    .section-title {
      font-size: 1.125rem;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 1rem 0;
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;

      .action-btn {
        justify-content: flex-start;
        padding: 0.75rem 1rem;
        border: 1px solid #e9ecef;
        border-radius: 12px;
        background: #f8f9fa;
        color: #495057;
        transition: all 0.3s ease;

        .fa-icon {
          margin-right: 0.75rem;
          color: $primary-color;
        }

        &:hover {
          background: $primary-color;
          color: white;
          border-color: $primary-color;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba($primary-color, 0.3);

          .fa-icon {
            color: white;
          }
        }
      }
    }
  }
}

// 编辑表单卡片
.edit-form-card {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  height: fit-content;

  .card-header {
    margin-bottom: 2rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid #f0f0f0;

    .card-title {
      font-size: 1.5rem;
      font-weight: 700;
      color: #2c3e50;
      margin: 0 0 0.5rem 0;
    }

    .card-subtitle {
      color: #7f8c8d;
      margin: 0;
      line-height: 1.5;
      text-align: center;
    }
  }

  .profile-form {
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
      margin-bottom: 1.5rem;

      .form-item-half {
        margin-bottom: 0 !important;
      }
    }

    :deep(.el-form-item) {
      margin-bottom: 1.5rem;

      .el-form-item__label {
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 0.5rem;
      }
    }

    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        border: 1px solid #e9ecef;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          border-color: #ced4da;
        }

        &.is-focus {
          box-shadow: 0 4px 12px rgba($primary-color, 0.2);
          border-color: $primary-color;
        }
      }
    }

    :deep(.el-select) {
      .el-select__wrapper {
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        border: 1px solid #e9ecef;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          border-color: #ced4da;
        }

        &.is-focus {
          box-shadow: 0 4px 12px rgba($primary-color, 0.2);
          border-color: $primary-color;
        }
      }
    }

    :deep(.el-textarea) {
      .el-textarea__inner {
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        border: 1px solid #e9ecef;
        resize: vertical;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          border-color: #ced4da;
        }

        &:focus {
          box-shadow: 0 4px 12px rgba($primary-color, 0.2);
          border-color: $primary-color;
        }
      }
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #f0f0f0;

      :deep(.el-button) {
        border-radius: 12px;
        padding: 0.75rem 2rem;
        font-weight: 600;
        transition: all 0.3s ease;

        &.el-button--primary {
          background: linear-gradient(135deg, $primary-color 0%, #c0392b 100%);
          border: none;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba($primary-color, 0.4);
          }

          .fa-icon {
            margin-right: 0.5rem;
          }
        }

        &:not(.el-button--primary) {
          border-color: #e9ecef;
          color: #6c757d;

          &:hover {
            border-color: #ced4da;
            color: #495057;
            transform: translateY(-1px);
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .profile-container {
    grid-template-columns: 350px 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 992px) {
  .profile-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .user-info-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .top-nav {
    padding: 1.5rem 1rem 0.5rem;
    flex-direction: column;
    gap: 1rem;

    .page-title {
      font-size: 1.5rem;
    }

    .nav-spacer {
      display: none;
    }
  }

  .profile-container {
    padding: 0 1rem 1rem;
    gap: 1rem;
  }

  .user-info-card,
  .edit-form-card {
    padding: 1.5rem;
    border-radius: 16px;
  }

  .edit-form-card .profile-form .form-row {
    grid-template-columns: 1fr;
    gap: 0;

    .form-item-half {
      margin-bottom: 1.5rem !important;
    }
  }

  .edit-form-card .profile-form .form-actions {
    flex-direction: column;

    :deep(.el-button) {
      width: 100%;
    }
  }
}

@media (max-width: 480px) {
user-info-card .quick-actions .action-buttons .action-btn {
  font-size: 0.875rem;
  padding: 0.625rem 0.875rem;
  }

  .edit-form-card .card-header .card-title {
    font-size: 1.25rem;
  }
}
</style>


