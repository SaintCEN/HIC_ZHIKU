<template>
  <div class="landing-page">
    <!-- 头部导航 -->
    <header class="landing-header">
      <div class="container">
        <div class="landing-nav">
          <div class="logo">
            <router-link to="/">
              <img src="../assets/about-image.jpg" alt="华创智库" class="logo-img" />
              <span class="logo-text">华创智库</span>
            </router-link>
          </div>
          
          <!-- 认证按钮 -->
          <div class="auth-actions">
            <template v-if="!authStore.isLoggedIn">
              <router-link to="/auth/login" class="auth-btn login-btn">
                <font-awesome-icon icon="sign-in-alt" />
                登录
              </router-link>
              <router-link to="/auth/register" class="auth-btn register-btn">
                <font-awesome-icon icon="user-plus" />
                注册
              </router-link>
            </template>
            <template v-else>
              <router-link to="/auth/profile" class="auth-btn profile-btn">
                <font-awesome-icon icon="user" />
                {{ authStore.user.nickname || authStore.user.username }}
              </router-link>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- 英雄区 -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">华创智库</h1>
          <h2 class="hero-subtitle">西电华为创新俱乐部知识分享与社团展示平台</h2>
          <p class="hero-description">
            连接学习与创新，助力技术成长
          </p>
          <div class="hero-actions">
            <router-link to="/portal" class="btn-primary">
              <font-awesome-icon icon="compass" />
              探索智库
            </router-link>
            <router-link to="/about" class="btn-secondary">
              <font-awesome-icon icon="info-circle" />
              了解更多
            </router-link>
          </div>
        </div>
      </div>
      <div class="hero-backdrop"></div>
    </section>

    <!-- 板块介绍 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">智库资源</h2>
        <div class="features-grid">
          <div class="feature-card" @click="navigateTo('/knowledge')">
            <div class="feature-icon">
              <font-awesome-icon icon="book" />
            </div>
            <h3 class="feature-title">知识库</h3>
            <p class="feature-description">
              华为技术主要领域的丰富学习资源
            </p>
          </div>
          <div class="feature-card" @click="navigateTo('/projects')">
            <div class="feature-icon">
              <font-awesome-icon icon="code" />
            </div>
            <h3 class="feature-title">项目分享</h3>
            <p class="feature-description">
              在项目展示平台发表和收藏奇思妙想
            </p>
          </div>
          <div class="feature-card" @click="navigateTo('/competitions')">
            <div class="feature-icon">
              <font-awesome-icon icon="trophy" />
            </div>
            <h3 class="feature-title">华为竞赛</h3>
            <p class="feature-description">
              提供华为竞赛信息，方便成员了解
            </p>
          </div>
          <div class="feature-card" @click="navigateTo('/tools')">
            <div class="feature-icon">
              <font-awesome-icon icon="toolbox" />
            </div>
            <h3 class="feature-title">实用工具</h3>
            <p class="feature-description">
              整合常用工具链接，提升学习效率
            </p>
          </div>
          <div class="feature-card" @click="navigateTo('/articles')">
            <div class="feature-icon">
              <font-awesome-icon icon="newspaper" />
            </div>
            <h3 class="feature-title">华俱推文</h3>
            <p class="feature-description">
             XDU华创微信公众号推文最新动态
            </p>
          </div>
          <div class="feature-card" @click="navigateTo('/share')">
            <div class="feature-icon">
              <font-awesome-icon icon="comment" />
            </div>
            <h3 class="feature-title">师兄师姐说</h3>
            <p class="feature-description">
              优秀学长学姐分享成长经验
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 快速入口 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-cards">
          <div class="cta-card" @click="navigateTo('/portal')">
            <div class="cta-content">
              <h3 class="cta-title">浏览全部内容</h3>
              <p class="cta-description">
                进入智库首页，浏览所有板块内容
              </p>
            </div>
            <div class="cta-icon">
              <font-awesome-icon icon="arrow-right" />
            </div>
          </div>
          <div class="cta-card" @click="navigateTo('/about')">
            <div class="cta-content">
              <h3 class="cta-title">了解华创智库</h3>
              <p class="cta-description">
                查看智库介绍及联系方式
              </p>
            </div>
            <div class="cta-icon">
              <font-awesome-icon icon="arrow-right" />
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部信息 -->
    <TheFooter />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores'
import TheFooter from '../components/TheFooter.vue'

const router = useRouter()
const authStore = useAuthStore()

// 加载认证状态
authStore.loadFromStorage()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
@import '../styles/variables';

.landing-page {
  min-height: 100vh;
  background-color: #fafafa;
}

/* 头部导航 */
.landing-header {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  padding: 20px 0;
}

.landing-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  a {
    display: flex;
    align-items: center;
    text-decoration: none;
  }

  .logo-img {
    height: 60px;
  }

  .logo-text {
    font-size: 24px;
    font-weight: 700;
    color: red;
  }
}

/* 认证按钮 */
.auth-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auth-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  
  svg {
    font-size: 14px;
  }
  
  &:hover {
    transform: translateY(-1px);
  }
}

.login-btn {
  color: $primary-color;
  border-color: $primary-color;
  background-color: transparent;
  
  &:hover {
    background-color: $primary-color;
    color: white;
  }
}

.register-btn {
  background-color: $primary-color;
  color: white;
  
  &:hover {
    background-color: darken($primary-color, 10%);
  }
}

.profile-btn {
  background-color: rgba(255, 255, 255, 0.1);
  color: $dark-color;
  backdrop-filter: blur(5px);
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.2);
  }
}

/* 英雄区 */
.hero-section {
  position: relative;
  height: 100vh;
  min-height: 600px;
  display: flex;
  align-items: center;
  padding: 100px 0;
  color: white;
  overflow: hidden;
}

.hero-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: -1;
}

.hero-content {
  max-width: 800px;
  text-align: center;
  margin: 0 auto;
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);

  @media (max-width: $breakpoint-md) {
    font-size: 48px;
  }

  @media (max-width: $breakpoint-sm) {
    font-size: 36px;
  }
}

.hero-subtitle {
  font-size: 32px;
  font-weight: 300;
  margin-bottom: 24px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);

  @media (max-width: $breakpoint-md) {
    font-size: 24px;
  }

  @media (max-width: $breakpoint-sm) {
    font-size: 20px;
  }
}

.hero-description {
  font-size: 20px;
  max-width: 600px;
  margin: 0 auto 40px;
  opacity: 0.9;

  @media (max-width: $breakpoint-sm) {
    font-size: 16px;
  }
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 20px;

  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    align-items: center;
  }

  a {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 14px 32px;
    border-radius: 8px;
    font-size: 18px;
    font-weight: 600;
    text-decoration: none;
    transition: all 0.3s;

    svg {
      font-size: 20px;
    }
  }

  .btn-primary {
    background-color: $primary-color;
    color: white;
    box-shadow: 0 4px 12px rgba($primary-color, 0.4);

    &:hover {
      background-color: darken($primary-color, 10%);
      transform: translateY(-3px);
      box-shadow: 0 6px 16px rgba($primary-color, 0.5);
    }
  }

  .btn-secondary {
    background-color: rgba(255, 255, 255, 0.1);
    color: black;
    backdrop-filter: blur(5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);

    &:hover {
      background-color: rgba(255, 255, 255, 0.2);
      transform: translateY(-3px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
    }
  }
}

/* 板块介绍 */
.features-section {
  padding: 60px 0;
  background-color: white;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 40px;
  color: $dark-color;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    width: 50px;
    height: 3px;
    background-color: $primary-color;
    border-radius: 2px;
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.feature-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);

    .feature-icon {
      background-color: $primary-color;
      color: white;
    }
  }
}

.feature-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba($primary-color, 0.1);
  color: $primary-color;
  border-radius: 50%;
  font-size: 24px;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: $dark-color;
}

.feature-description {
  color: $gray-dark-color;
  line-height: 1.5;
  font-size: 14px;
}

/* 快速入口 */
.cta-section {
  padding: 50px 0;
  background-color: #f8f9fa;
}

.cta-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.cta-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);

    .cta-icon {
      background-color: $primary-color;
      color: white;
      transform: translateX(3px);
    }
  }
}

.cta-content {
  flex: 1;
}

.cta-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
  color: $dark-color;
}

.cta-description {
  color: $gray-dark-color;
  line-height: 1.4;
  font-size: 14px;
}

.cta-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: $dark-color;
  border-radius: 50%;
  font-size: 16px;
  transition: all 0.3s;
}



/* 通用容器 */
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>