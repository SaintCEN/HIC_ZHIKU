<template>
  <div class="notification-container" v-if="hasNotifications">
    <TransitionGroup name="notification">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification"
        :class="[`notification--${notification.type || 'info'}`]"
      >
        <div class="notification__icon">
          <font-awesome-icon :icon="getIcon(notification.type)" />
        </div>
        <div class="notification__content">
          <div class="notification__title" v-if="notification.title">
            {{ notification.title }}
          </div>
          <div class="notification__message">
            {{ notification.message }}
          </div>
        </div>
        <button class="notification__close" @click="removeNotification(notification.id)">
          <font-awesome-icon icon="times" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '../stores'

const appStore = useAppStore()

// 获取通知
const notifications = computed(() => appStore.notifications)
const hasNotifications = computed(() => appStore.hasNotifications)

// 移除通知
const removeNotification = (id) => {
  appStore.removeNotification(id)
}

// 根据通知类型获取图标
const getIcon = (type) => {
  switch (type) {
    case 'success':
      return 'circle-check'
    case 'warning':
      return 'exclamation-triangle'
    case 'error':
      return 'circle-xmark'
    default:
      return 'info-circle'
  }
}
</script>

<style lang="scss" scoped>
.notification-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  max-width: 350px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
  
  &--info {
    border-left: 4px solid $secondary-color;
    .notification__icon {
      color: $secondary-color;
    }
  }
  
  &--success {
    border-left: 4px solid $success-color;
    .notification__icon {
      color: $success-color;
    }
  }
  
  &--warning {
    border-left: 4px solid $warning-color;
    .notification__icon {
      color: $warning-color;
    }
  }
  
  &--error {
    border-left: 4px solid $error-color;
    .notification__icon {
      color: $error-color;
    }
  }
  
  &__icon {
    margin-right: 12px;
    font-size: 18px;
  }
  
  &__content {
    flex: 1;
  }
  
  &__title {
    font-weight: 600;
    margin-bottom: 4px;
  }
  
  &__message {
    color: $gray-dark-color;
    font-size: 14px;
  }
  
  &__close {
    background: none;
    border: none;
    color: $gray-color;
    font-size: 14px;
    cursor: pointer;
    padding: 4px;
    
    &:hover {
      color: $dark-color;
    }
  }
}

// 动画
.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from,
.notification-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 