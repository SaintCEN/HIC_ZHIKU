<template>
  <div class="auth-wrapper">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <!-- 动态装饰背景 -->
      <div class="brand-bg-decoration">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
        </div>
        <div class="gradient-orbs">
          <div class="orb orb-1"></div>
          <div class="orb orb-2"></div>
          <div class="orb orb-3"></div>
        </div>
      </div>
      
      <div class="brand-content">
        <div class="logo-container">
          <div class="logo-wrapper">
            <img src="../assets/about-image.jpg" alt="华创智库" class="logo" />
            <div class="logo-glow"></div>
          </div>
        </div>
        <div class="text-content">
          <h1 class="brand-title">
            <span class="title-line">开启新的旅程</span>
            <span class="title-main">加入华创智库</span>
          </h1>
          <p class="brand-subtitle">与志同道合的伙伴一起成长进步</p>
        </div>
        <div class="feature-grid">
          <div class="feature-card" data-delay="0">
            <div class="feature-icon-wrapper">
              <font-awesome-icon icon="book-open" class="feature-icon" />
            </div>
            <div class="feature-text">
              <h4>丰富资源</h4>
              <p>海量学习资料</p>
            </div>
          </div>
          <div class="feature-card" data-delay="100">
            <div class="feature-icon-wrapper">
              <font-awesome-icon icon="users" class="feature-icon" />
            </div>
            <div class="feature-text">
              <h4>社区交流</h4>
              <p>与优秀同学互动</p>
            </div>
          </div>
          <div class="feature-card" data-delay="200">
            <div class="feature-icon-wrapper">
              <font-awesome-icon icon="rocket" class="feature-icon" />
            </div>
            <div class="feature-text">
              <h4>项目实践</h4>
              <p>获得实战经验</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="form-section">
      <div class="auth-card">
        <div class="card-header">
          <h2 class="title">创建您的账户</h2>
          <p class="subtitle">填写信息完成注册</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="register-form" @keyup.enter="onSubmit">
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="3-20个字符，只能包含字母、数字和下划线" 
              clearable 
              size="large"
              prefix-icon="User"
              @blur="checkUsernameAvailability"
              :loading="checkingUsername"
            />
            <div v-if="usernameStatus" class="username-status" :class="usernameStatus.type">
              <el-icon v-if="usernameStatus.type === 'success'"><CircleCheck /></el-icon>
              <el-icon v-else-if="usernameStatus.type === 'error'"><CircleClose /></el-icon>
              <span>{{ usernameStatus.message }}</span>
            </div>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input 
              v-model="form.nickname" 
              placeholder="请输入昵称（可选，默认为用户名）" 
              clearable 
              size="large"
              prefix-icon="Avatar"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码（至少6位）" 
              show-password 
              size="large"
              prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item label="重复密码" prop="confirm">
            <el-input 
              v-model="form.confirm" 
              type="password" 
              placeholder="请再次输入密码" 
              show-password 
              size="large"
              prefix-icon="Lock"
            />
          </el-form-item>
          
          <div class="terms-section">
            <el-checkbox v-model="agreeTerms" size="small">
              我已阅读并同意
              <a href="#" class="terms-link">《用户协议》</a>
              和
              <a href="#" class="terms-link">《隐私政策》</a>
            </el-checkbox>
          </div>

          <el-form-item>
            <el-button 
              type="primary" 
              class="submit-btn" 
              :loading="submitting" 
              :disabled="!agreeTerms"
              size="large"
              @click="onSubmit"
            >
              <span v-if="!submitting">创建账户</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="footer">
          <span class="footer-text">已经有账户？</span>
          <router-link class="footer-link" to="/auth/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore, useAppStore } from '../stores'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const formRef = ref()
const form = reactive({ username: '', nickname: '', password: '', confirm: '' })
const agreeTerms = ref(false)
const checkingUsername = ref(false)
const usernameStatus = ref(null)
const rules = {
  username: [ 
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [ { max: 50, message: '昵称不能超过50个字符', trigger: 'blur' } ],
  password: [ 
    { required: true, message: '请输入密码', trigger: 'blur' }, 
    { min: 6, max: 128, message: '密码长度必须在6-128个字符之间', trigger: 'blur' }
  ],
  confirm: [ 
    { required: true, message: '请再次输入密码', trigger: 'blur' }, 
    { validator: (_, v, cb) => { v === form.password ? cb() : cb(new Error('两次密码不一致')) }, trigger: 'blur' } 
  ]
}

const submitting = ref(false)

// 检查用户名可用性
async function checkUsernameAvailability() {
  if (!form.username || form.username.length < 3) {
    usernameStatus.value = null
    return
  }
  
  // 验证用户名格式
  const usernamePattern = /^[a-zA-Z0-9_]+$/
  if (!usernamePattern.test(form.username)) {
    usernameStatus.value = {
      type: 'error',
      message: '用户名只能包含字母、数字和下划线'
    }
    return
  }
  
  checkingUsername.value = true
  try {
    // 调用后端API检查用户名是否可用
    const { default: http } = await import('../utils/http')
    const response = await http.get(`/auth/check-username?username=${encodeURIComponent(form.username)}`)
    
    if (response.data?.data?.available) {
      usernameStatus.value = {
        type: 'success',
        message: '用户名可用'
      }
    } else {
      usernameStatus.value = {
        type: 'error',
        message: '用户名已被使用'
      }
    }
  } catch (error) {
    // 网络错误等情况，不显示错误状态
    usernameStatus.value = null
  } finally {
    checkingUsername.value = false
  }
}

function onSubmit() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const res = await authStore.register({ 
        username: form.username, 
        password: form.password,
        nickname: form.nickname 
      })
      submitting.value = false
      if (res.success) {
        appStore.addNotification({ type: 'success', message: '注册成功，请登录' })
        router.push(`/auth/login?u=${encodeURIComponent(form.username)}`)
      } else {
        appStore.addNotification({ type: 'error', message: '注册失败，请稍后重试' })
      }
    } catch (e) {
      submitting.value = false
      const msg = e?.response?.data?.message || '注册失败'
      appStore.addNotification({ type: 'error', message: msg })
    }
  })
}

// 添加动态效果
onMounted(() => {
  // 为特性卡片添加动画效果
  const featureCards = document.querySelectorAll('.feature-card')
  featureCards.forEach((card, index) => {
    setTimeout(() => {
      card.style.opacity = '1'
      card.style.transform = 'translateY(0)'
      card.style.animation = `fadeInUp 0.6s ease-out forwards`
    }, 800 + index * 100)
  })
})
</script>

<style scoped lang="scss">
.auth-wrapper {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(0, 160, 233, 0.1), rgba(228, 30, 43, 0.1));
    animation: float 6s ease-in-out infinite;
    
    &.circle-1 {
      width: 250px;
      height: 250px;
      top: -125px;
      right: -125px;
      animation-delay: 0s;
    }
    
    &.circle-2 {
      width: 180px;
      height: 180px;
      top: 60%;
      left: -90px;
      animation-delay: 2s;
    }
    
    &.circle-3 {
      width: 120px;
      height: 120px;
      bottom: -60px;
      right: 25%;
      animation-delay: 4s;
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(180deg); }
}

// 左侧品牌区域
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, 
    rgba(255, 182, 193, 0.08) 0%, 
    rgba(255, 160, 122, 0.12) 35%, 
    rgba(255, 218, 185, 0.08) 70%, 
    rgba(255, 192, 203, 0.06) 100%
  );
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem;
  position: relative;
  overflow: hidden;
  
  // 动态装饰背景
  .brand-bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 1;
    
    .floating-shapes {
      position: absolute;
      width: 100%;
      height: 100%;
      
      .shape {
        position: absolute;
        border-radius: 50%;
        background: linear-gradient(45deg, 
          rgba(255, 182, 193, 0.08), 
          rgba(255, 160, 122, 0.08)
        );
        animation: floatShape 8s ease-in-out infinite;
        
        &.shape-1 {
          width: 80px;
          height: 80px;
          top: 10%;
          left: 10%;
          animation-delay: 0s;
        }
        
        &.shape-2 {
          width: 60px;
          height: 60px;
          top: 20%;
          right: 15%;
          animation-delay: 1s;
        }
        
        &.shape-3 {
          width: 120px;
          height: 120px;
          bottom: 20%;
          left: 5%;
          animation-delay: 2s;
        }
        
        &.shape-4 {
          width: 40px;
          height: 40px;
          top: 60%;
          right: 25%;
          animation-delay: 3s;
        }
        
        &.shape-5 {
          width: 90px;
          height: 90px;
          bottom: 10%;
          right: 10%;
          animation-delay: 4s;
        }
      }
    }
    
    .gradient-orbs {
      position: absolute;
      width: 100%;
      height: 100%;
      
      .orb {
        position: absolute;
        border-radius: 50%;
        filter: blur(40px);
        animation: orbFloat 6s ease-in-out infinite;
        
        &.orb-1 {
          width: 200px;
          height: 200px;
          background: rgba(255, 182, 193, 0.2);
          top: -50px;
          left: -50px;
          animation-delay: 0s;
        }
        
        &.orb-2 {
          width: 150px;
          height: 150px;
          background: rgba(255, 160, 122, 0.15);
          bottom: -30px;
          right: -30px;
          animation-delay: 2s;
        }
        
        &.orb-3 {
          width: 100px;
          height: 100px;
          background: rgba(255, 218, 185, 0.15);
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          animation-delay: 4s;
        }
      }
    }
  }
  
  .brand-content {
    text-align: center;
    color: #2d3748;
    max-width: 480px;
    position: relative;
    z-index: 2;
    animation: slideInLeft 1s ease-out;
  }
  
  .logo-container {
    margin-bottom: 3rem;
    
    .logo-wrapper {
      position: relative;
      display: inline-block;
      
      .logo {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.9);
        padding: 1.2rem;
        backdrop-filter: blur(20px);
        border: 2px solid rgba(255, 182, 193, 0.15);
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        position: relative;
        z-index: 2;
        
        &:hover {
          transform: scale(1.1) rotate(-5deg);
          box-shadow: 0 20px 40px rgba(255, 182, 193, 0.2);
        }
      }
      
      .logo-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 120px;
        height: 120px;
        border-radius: 50%;
        background: radial-gradient(circle, 
          rgba(255, 182, 193, 0.3) 0%, 
          transparent 70%
        );
        animation: pulse 3s ease-in-out infinite;
        z-index: 1;
      }
    }
  }
  
  .text-content {
    margin-bottom: 3rem;
    
    .brand-title {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
      margin: 0 0 1rem 0;
      
      .title-line {
        display: block;
        font-size: 1.2rem;
        font-weight: 400;
        color: #64748b;
        margin-bottom: 0.5rem;
        opacity: 0;
        animation: fadeInUp 1s ease-out 0.3s forwards;
      }
      
      .title-main {
        display: block;
        font-size: 3rem;
        font-weight: 800;
        background: linear-gradient(135deg, #ff6b6b, #ffa07a);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        opacity: 0;
        animation: fadeInUp 1s ease-out 0.5s forwards;
      }
    }
    
    .brand-subtitle {
      font-size: 1.1rem;
      color: #64748b;
      margin: 0;
      font-weight: 400;
      line-height: 1.6;
      opacity: 0;
      animation: fadeInUp 1s ease-out 0.7s forwards;
    }
  }
  
  .feature-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1.5rem;
    max-width: 320px;
    margin: 0 auto;
    
    .feature-card {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem;
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(20px);
      border-radius: 16px;
              border: 1px solid rgba(255, 182, 193, 0.1);
      transition: all 0.3s ease;
      opacity: 0;
      transform: translateY(20px);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 30px rgba(255, 182, 193, 0.15);
        background: rgba(255, 255, 255, 0.9);
      }
      
      .feature-icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #ff6b6b, #ffa07a);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .feature-icon {
          color: white;
          font-size: 20px;
        }
      }
      
      .feature-text {
        flex: 1;
        text-align: left;
        
        h4 {
          margin: 0 0 0.25rem 0;
          font-size: 1rem;
          font-weight: 600;
          color: #2d3748;
        }
        
        p {
          margin: 0;
          font-size: 0.875rem;
          color: #64748b;
          line-height: 1.4;
        }
      }
    }
  }
}

// 右侧表单区域
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #f8fafc;
  position: relative;
  z-index: 1;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  animation: slideInRight 0.8s ease-out;
  
  .card-header {
    text-align: center;
    margin-bottom: 2rem;
    
    .title {
      font-size: 1.75rem;
      font-weight: 700;
      color: #1a202c;
      margin: 0 0 0.5rem 0;
    }
    
    .subtitle {
      color: #64748b;
      margin: 0;
      font-size: 0.95rem;
    }
  }
}

.register-form {
  margin-bottom: 1.5rem;
  
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    &.is-focus {
      box-shadow: 0 0 0 3px rgba(255, 182, 193, 0.1);
    }
  }
}

.username-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  font-size: 0.875rem;
  padding: 0.5rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &.success {
    color: #059669;
    background: rgba(5, 150, 105, 0.1);
    border: 1px solid rgba(5, 150, 105, 0.2);
  }
  
  &.error {
    color: #dc2626;
    background: rgba(220, 38, 38, 0.1);
    border: 1px solid rgba(220, 38, 38, 0.2);
  }
  
  .el-icon {
    font-size: 1rem;
  }
}

.terms-section {
  margin: 1.5rem 0;
  
  :deep(.el-checkbox__label) {
    font-size: 0.875rem;
    color: #64748b;
    line-height: 1.5;
  }
  
  .terms-link {
    color: #ff6b6b;
    text-decoration: none;
    font-weight: 500;
    
    &:hover {
      color: #ffa07a;
      text-decoration: underline;
    }
  }
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa07a 100%);
  border: none;
  transition: all 0.3s ease;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(255, 182, 193, 0.2);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.footer {
  text-align: center;
  
  .footer-text {
    color: #64748b;
    font-size: 0.875rem;
  }
  
  .footer-link {
    color: #ff6b6b;
    font-weight: 600;
    text-decoration: none;
    margin-left: 0.5rem;
    transition: color 0.3s ease;
    
    &:hover {
      color: #ffa07a;
    }
  }
}

// 动画
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes floatShape {
  0%, 100% { 
    transform: translateY(0) rotate(0deg) scale(1); 
  }
  50% { 
    transform: translateY(-20px) rotate(180deg) scale(1.1); 
  }
}

@keyframes orbFloat {
  0%, 100% { 
    transform: translate(0, 0) scale(1); 
    opacity: 0.3;
  }
  50% { 
    transform: translate(10px, -15px) scale(1.2); 
    opacity: 0.6;
  }
}

@keyframes pulse {
  0%, 100% { 
    transform: translate(-50%, -50%) scale(1); 
    opacity: 0.4;
  }
  50% { 
    transform: translate(-50%, -50%) scale(1.2); 
    opacity: 0.8;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .auth-wrapper {
    flex-direction: column;
    min-height: auto;
    height: auto;
  }
  
  .brand-section {
    min-height: 25vh;
    padding: 2rem 1.5rem;
    
    .brand-title {
      .title-main {
        font-size: 2.5rem;
      }
    }
    
    .feature-grid {
      grid-template-columns: repeat(3, 1fr);
      gap: 1rem;
    }
  }
  
  .form-section {
    min-height: auto;
    padding: 2rem 1rem;
  }
}

@media (max-width: 768px) {
  .auth-wrapper {
    min-height: 100vh;
    height: auto;
    overflow-y: auto;
  }
  
  .brand-section {
    padding: 1.5rem 1rem;
    min-height: 20vh;
    
    .brand-title {
      .title-main {
        font-size: 2rem;
      }
      
      .title-line {
        font-size: 1rem;
      }
    }
    
    .brand-subtitle {
      font-size: 1rem;
    }
    
    .feature-grid {
      grid-template-columns: 1fr;
      gap: 0.75rem;
      
      .feature-card {
        padding: 0.5rem;
        
        .feature-text {
          h4 {
            font-size: 0.9rem;
          }
          
          p {
            font-size: 0.8rem;
          }
        }
      }
    }
  }
  
  .form-section {
    padding: 1rem;
    min-height: auto;
  }
  
  .auth-card {
    padding: 1.25rem;
    margin: 0.5rem 0;
    border-radius: 16px;
  }
}

@media (max-width: 480px) {
  .auth-wrapper {
    min-height: 100vh;
  }
  
  .brand-section {
    padding: 1rem 0.75rem;
    min-height: 18vh;
    
    .brand-title {
      .title-main {
        font-size: 1.75rem;
      }
      
      .title-line {
        font-size: 0.9rem;
      }
    }
    
    .brand-subtitle {
      font-size: 0.9rem;
    }
    
    .logo-container {
      margin-bottom: 2rem;
      
      .logo-wrapper {
        .logo {
          width: 80px;
          height: 80px;
          padding: 1rem;
        }
      }
    }
  }
  
  .form-section {
    padding: 0.75rem;
  }
  
  .auth-card {
    padding: 1rem;
    border-radius: 12px;
    
    .card-header {
      .title {
        font-size: 1.25rem;
      }
      
      .subtitle {
        font-size: 0.8rem;
      }
    }
  }
}
</style>



