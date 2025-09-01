<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-heading">华创智库</h3>
          <p class="footer-description">
            西电华为俱乐部知识分享平台，致力于为学生提供华为技术领域的学习资源。
          </p>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">快速链接</h3>
          <ul class="footer-links">
            <li v-for="item in navItems" :key="item.id">
              <router-link :to="item.path" class="footer-link">{{ item.title }}</router-link>
            </li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">关注我们</h3>
          <div class="social-links">
            <a 
              v-for="social in socialLinks" 
              :key="social.name"
              :href="social.link" 
              :title="social.title"
              :target="social.target"
              class="social-link"
              @click="handleSocialClick(social)"
            >
              <font-awesome-icon :icon="social.icon" />
            </a>
          </div>
          <div class="about-link">
            <router-link to="/about" class="footer-link">关于我们</router-link>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p class="copyright">
          &copy; {{ currentYear }} 西电华为创新俱乐部. 保留所有权利.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

// 获取当前年份
const currentYear = computed(() => new Date().getFullYear())

// 导航菜单项
const navItems = [
  { id: 1, title: '知识库', path: '/knowledge' },
  { id: 2, title: '项目分享', path: '/projects' },
  { id: 3, title: '华为竞赛', path: '/competitions' },
  { id: 4, title: '实用工具', path: '/tools' },
  { id: 5, title: '华俱推文', path: '/articles' },
  { id: 6, title: '师兄师姐说', path: '/share' }
]

const socialLinks = [
  {
    name: 'weixin',
    title: '西电华了个为公众号',
    icon: ['fab', 'weixin'],
    link: 'https://mp.weixin.qq.com/s/ZXOC36igMBnuHWf2J4qyHg',
    target: '_blank',
    type: 'wechat' 
  },
  {
    name: 'github',
    title: 'GitHub',
    icon: ['fab', 'github'],
    link: 'https://github.com/SaintCEN/HIC_ZHIKU', 
    target: '_blank',
    type: 'link'
  },
  {
    name: 'qq',
    title: 'QQ群',
    icon: ['fab', 'qq'],
    link: 'https://mp.weixin.qq.com/s/ZXOC36igMBnuHWf2J4qyHg',
    target: '_blank',
    type: 'qq' 
  }
]

// 处理社交链接点击事件
const handleSocialClick = (social) => {
  // 简化处理逻辑，确保所有链接都能正常打开
  if (social.link && social.link !== '#') {
    console.log(`点击了${social.title}`)
    // 让浏览器的默认行为处理链接跳转，不需要手动调用 window.open
  }
}
</script>

<style lang="scss" scoped>
@import '../styles/variables';

.footer {
  background-color: #1e1e1e;
  color: #ffffff;
  padding: 48px 0 24px;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  margin-bottom: 32px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.footer-heading {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #ffffff;
}

.footer-description {
  line-height: 1.6;
  color: #cccccc;
}

.footer-links {
  list-style: none;
  padding: 0;
}

.footer-link {
  display: block;
  color: #cccccc;
  padding: 6px 0;
  transition: color 0.2s;
  
  &:hover {
    color: $primary-color;
  }
}

.social-links {
  display: flex;
  gap: 16px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  color: #ffffff;
  font-size: 20px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    color: $primary-color;
  }
  
  &:active {
    transform: translateY(0);
  }
}

.about-link {
  margin-top: 16px;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 24px;
  text-align: center;
}

.copyright {
  color: #888888;
  font-size: 14px;
}
</style>