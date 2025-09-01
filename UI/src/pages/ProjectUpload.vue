<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 项目上传页面头部 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-6xl mx-auto px-4 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <svg class="w-8 h-8 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <h1 class="text-xl font-semibold text-gray-900">创建新项目</h1>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 py-8">
      <div class="bg-white border border-gray-200 rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">项目详情</h2>
          <p class="mt-1 text-sm text-gray-600">创建一个新项目在华创社区分享你的巧思</p>
        </div>

        <form @submit.prevent="submitProject" class="p-6 space-y-6">
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
              placeholder="完全品/半成品，项目/笔记都可以哦"
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
              placeholder="简要描述你的项目（20个字以内）"
            ></textarea>
          </div>

          <!-- 项目文档 -->
          <div>
            <label for="documentFile" class="block text-sm font-medium text-gray-900 mb-2">
              项目文档 <span class="text-red-500">*</span>
            </label>
            <div class="border-2 border-dashed border-gray-300 rounded-md p-4 text-center hover:border-gray-400 transition-colors">
              <input
                id="documentFile"
                ref="fileInput"
                type="file"
                accept=".md"
                @change="handleFileChange"
                class="hidden"
                required
              />
              <div v-if="!form.documentFile" @click="$refs.fileInput.click()" class="cursor-pointer">
                <svg class="w-8 h-8 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p class="text-sm text-gray-600 mb-1">点击上传项目文档</p>
                <p class="text-xs text-gray-500">支持 .md 格式文件</p>
              </div>
              <div v-else class="text-green-600">
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
              @click="$router.push('/projects')"
              class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? '创建中...' : '创建项目' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { createProject } from '../api/projects'

export default {
  name: 'ProjectUpload',
  data() {
     return {
       isSubmitting: false,
       form: {
         title: '',
         description: '',
         documentFile: null,
         category: '',
         githubUrl: '',
         visibility: 'public'
       },
       projectTypes: [
         { value: 'software', label: 'Software Development' },
         { value: 'hardware', label: 'Hardware Design' },
         { value: 'embedded', label: 'Embedded Systems' },
         { value: 'ai', label: 'Artificial Intelligence' },
         { value: 'other', label: 'Others' }
       ]
     }
   },
  methods: {
     handleFileChange(event) {
       const file = event.target.files[0]
       if (file) {
         // Validate file type
         const allowedTypes = ['.md']
         const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
         
         if (!allowedTypes.includes(fileExtension)) {
           alert('Please upload a .md file')
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
    async submitProject() {
      // Validate required fields
      if (!this.form.title.trim()) {
        alert('Please enter a repository name')
        return
      }
      if (!this.form.documentFile) {
         alert('Please upload a README file')
         return
       }
       if (!this.form.category) {
         alert('Please select a category')
         return
       }

      // Validate GitHub URL format (if provided)
      if (this.form.githubUrl && !this.isValidUrl(this.form.githubUrl)) {
        alert('Please enter a valid GitHub URL')
        return
      }

      this.isSubmitting = true

      try {
        // Create FormData object for file upload
         const formData = new FormData()
         formData.append('title', this.form.title)
         formData.append('description', this.form.description)
         formData.append('category', this.form.category)
         formData.append('githubUrl', this.form.githubUrl || '')
         formData.append('visibility', this.form.visibility)
         formData.append('documentFile', this.form.documentFile)

        const result = await createProject(formData)
        alert('Repository created successfully!')
        // 添加时间戳参数强制刷新，避免缓存问题
        this.$router.push('/projects?t=' + Date.now())
      } catch (error) {
        console.error('Failed to create repository:', error)
        alert('Failed to create repository. Please try again.')
      } finally {
        this.isSubmitting = false
      }
    },
    isValidUrl(string) {
      try {
        new URL(string)
        return true
      } catch (_) {
        return false
      }
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
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