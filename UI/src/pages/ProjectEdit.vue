<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 项目编辑页面头部 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-6xl mx-auto px-4 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <svg class="w-8 h-8 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
            </svg>
            <h1 class="text-xl font-semibold text-gray-900">编辑项目</h1>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 py-8">
      <div class="bg-white border border-gray-200 rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">项目详情</h2>
          <p class="mt-1 text-sm text-gray-600">修改项目信息，让你的作品更加完善</p>
        </div>

        <form @submit.prevent="submitProject" class="p-6 space-y-6" v-loading="loading">
          <!-- 项目名称 -->
          <div>
            <label for="title" class="block text-sm font-medium text-gray-900 mb-2">
              项目名称 <span class="text-red-500">*</span>
            </label>
            <input
              id="title"
              v-model="form.title"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="我的优秀项目"
            />
            <p class="mt-1 text-xs text-gray-500">好的项目名称应该容易记忆且高度概括</p>
          </div>

          <!-- 项目描述 -->
          <div>
            <label for="description" class="block text-sm font-medium text-gray-900 mb-2">
              项目描述 <span class="text-gray-500">(可选)</span>
            </label>
            <textarea
              id="description"
              v-model="form.description"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="简要描述你的项目"
            ></textarea>
          </div>

          <!-- 项目文档 -->
          <div>
            <label for="documentFile" class="block text-sm font-medium text-gray-900 mb-2">
              项目文档 <span class="text-gray-500">(可选)</span>
            </label>
            <div class="border-2 border-dashed border-gray-300 rounded-md p-4 text-center hover:border-gray-400 transition-colors">
              <input
                id="documentFile"
                ref="fileInput"
                type="file"
                accept=".md"
                @change="handleFileChange"
                class="hidden"
              />
              <div v-if="!form.documentFile && !currentDocumentUrl" @click="$refs.fileInput.click()" class="cursor-pointer">
                <svg class="w-8 h-8 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p class="text-sm text-gray-600 mb-1">点击上传项目文档</p>
                <p class="text-xs text-gray-500">支持 .md 格式文件</p>
              </div>
              <div v-else-if="form.documentFile" class="text-green-600">
                <svg class="w-8 h-8 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                </svg>
                <p class="text-sm font-medium">{{ form.documentFile.name }}</p>
                <button
                  type="button"
                  @click="removeFile"
                  class="mt-1 text-xs text-red-600 hover:text-red-800"
                >
                  移除文件
                </button>
              </div>
              <div v-else-if="currentDocumentUrl" class="text-blue-600">
                <svg class="w-8 h-8 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clip-rule="evenodd"/>
                </svg>
                <p class="text-sm font-medium">当前文档.md</p>
                <div class="mt-2 space-x-2">
                  <button
                    type="button"
                    @click="$refs.fileInput.click()"
                    class="text-xs text-blue-600 hover:text-blue-800"
                  >
                    更换文件
                  </button>
                  <button
                    type="button"
                    @click="removeCurrentDocument"
                    class="text-xs text-red-600 hover:text-red-800"
                  >
                    移除文档
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 项目分类 -->
          <div>
            <label for="category" class="block text-sm font-medium text-gray-900 mb-2">
              项目分类 <span class="text-red-500">*</span>
            </label>
            <select
              id="category"
              v-model="form.category"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">选择项目分类</option>
              <option value="software">软件开发</option>
              <option value="hardware">硬件设计</option>
              <option value="embedded">嵌入式系统</option>
              <option value="ai">人工智能</option>
              <option value="other">其他</option>
            </select>
          </div>

          <!-- 代码仓库链接 -->
          <div>
            <label for="githubUrl" class="block text-sm font-medium text-gray-900 mb-2">
              代码仓库链接 <span class="text-gray-500">(可选)</span>
            </label>
            <input
              id="githubUrl"
              v-model="form.githubUrl"
              type="url"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="https://github.com/username/repository"
            />
            <p class="mt-1 text-xs text-gray-500">如果有代码仓库，请提供链接地址</p>
          </div>

          <!-- 提交按钮 -->
          <div class="flex justify-end space-x-3 pt-6 border-t border-gray-200">
            <button
              type="button"
              @click="goBack"
              class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { getProjectById, updateProject } from '../api/projects'

export default {
  name: 'ProjectEdit',
  data() {
    return {
      loading: false,
      isSubmitting: false,
      currentDocumentUrl: '',
      form: {
        title: '',
        description: '',
        documentFile: null,
        category: '',
        githubUrl: ''
      }
    }
  },
  async mounted() {
    await this.fetchProject()
  },
  methods: {
    async fetchProject() {
      try {
        this.loading = true
        const projectId = this.$route.params.id
        const response = await getProjectById(projectId)
        
        if (response.data?.success) {
          const project = response.data.data
          this.form.title = project.title || ''
          this.form.description = project.description || ''
          this.form.category = project.category || ''
          this.form.githubUrl = project.githubUrl || ''
          this.currentDocumentUrl = project.detailedDescription || ''
        } else {
          this.$message.error(response.data?.message || '获取项目信息失败')
          this.goBack()
        }
      } catch (error) {
        console.error('获取项目详情失败:', error)
        this.$message.error('获取项目信息失败')
        this.goBack()
      } finally {
        this.loading = false
      }
    },

    handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 验证文件类型
        const allowedTypes = ['.md']
        const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
        
        if (!allowedTypes.includes(fileExtension)) {
          this.$message.error('只支持 .md 格式的文件')
          event.target.value = ''
          return
        }
        
        // 验证文件大小（10MB）
        if (file.size / 1024 / 1024 > 10) {
          this.$message.error('文件大小不能超过 10MB')
          event.target.value = ''
          return
        }
        
        this.form.documentFile = file
      }
    },

    removeFile() {
      this.form.documentFile = null
      this.$refs.fileInput.value = ''
    },

    removeCurrentDocument() {
      this.currentDocumentUrl = ''
    },

    async submitProject() {
      // 验证必填字段
      if (!this.form.title.trim()) {
        this.$message.error('请输入项目名称')
        return
      }

      if (!this.form.category) {
        this.$message.error('请选择项目分类')
        return
      }

      try {
        this.isSubmitting = true

        const projectId = this.$route.params.id
        
        // 准备提交数据
        const projectData = {
          title: this.form.title,
          description: this.form.description || '',
          category: this.form.category,
          githubUrl: this.form.githubUrl || ''
        }

        // 如果有新的Markdown文件，添加到提交数据中
        if (this.form.documentFile) {
          projectData.documentFile = this.form.documentFile
        }

        // 如果移除了当前文档，设置为空
        if (!this.currentDocumentUrl) {
          projectData.detailedDescription = ''
        }

        // 提交项目更新
        const response = await updateProject(projectId, projectData)
        
        if (response.data?.success) {
          this.$message.success('项目更新成功！')
          // 添加时间戳参数强制刷新，避免缓存问题
          this.$router.push(`/project/${projectId}?t=${Date.now()}`)
        } else {
          throw new Error(response.data?.message || '项目更新失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.$message.error(error.message || '提交失败，请重试')
      } finally {
        this.isSubmitting = false
      }
    },

    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
/* 响应式设计 */
@media (max-width: 768px) {
  .max-w-4xl {
    max-width: 100%;
    padding: 0 1rem;
  }
  
  .px-6 {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .py-8 {
    padding-top: 2rem;
    padding-bottom: 2rem;
  }
  
  .space-y-6 > * + * {
    margin-top: 1rem;
  }
  
  .flex.justify-end.space-x-3 {
    flex-direction: column;
    align-items: stretch;
  }
  
  .flex.justify-end.space-x-3 > * + * {
    margin-left: 0;
    margin-top: 0.5rem;
  }
}

/* 加载状态样式 */
.v-loading-mask {
  background-color: rgba(255, 255, 255, 0.8);
}

/* 文件上传区域悬停效果 */
.border-dashed:hover {
  border-color: #9ca3af;
}

/* 按钮禁用状态 */
.disabled\:opacity-50:disabled {
  opacity: 0.5;
}

.disabled\:cursor-not-allowed:disabled {
  cursor: not-allowed;
}

/* 焦点状态增强 */
input:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 过渡动画 */
.transition-colors {
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out;
}

/* 自定义滚动条 */
textarea::-webkit-scrollbar {
  width: 6px;
}

textarea::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

textarea::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

textarea::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 深色模式兼容性 - 强制保持输入框样式 */
input[type="text"],
input[type="email"],
input[type="password"],
input[type="url"],
input[type="search"],
input[type="tel"],
input[type="number"],
textarea,
select {
  background-color: white !important;
  color: #374151 !important; /* gray-700 */
  border-color: #d1d5db !important; /* gray-300 */
}

input[type="text"]:focus,
input[type="email"]:focus,
input[type="password"]:focus,
input[type="url"]:focus,
input[type="search"]:focus,
input[type="tel"]:focus,
input[type="number"]:focus,
textarea:focus,
select:focus {
  background-color: white !important;
  color: #374151 !important;
  border-color: #3b82f6 !important; /* blue-500 */
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
}

input[type="text"]::placeholder,
input[type="email"]::placeholder,
input[type="password"]::placeholder,
input[type="url"]::placeholder,
input[type="search"]::placeholder,
input[type="tel"]::placeholder,
input[type="number"]::placeholder,
textarea::placeholder {
  color: #9ca3af !important; /* gray-400 */
}

/* 文件输入框样式 */
input[type="file"] {
  background-color: transparent !important;
  color: inherit !important;
}

/* 确保下拉选择框在深色模式下也保持可读性 */
select option {
  background-color: white !important;
  color: #374151 !important;
}
</style>