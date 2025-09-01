<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 分享上传页面头部 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-6xl mx-auto px-4 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <svg class="w-8 h-8 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <h1 class="text-xl font-semibold text-gray-900">创建新分享</h1>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 py-8">
      <div class="bg-white border border-gray-200 rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">分享详情</h2>
          <p class="mt-1 text-sm text-gray-600">和小东西分享你的经验</p>
        </div>

        <form @submit.prevent="submitShare" class="p-6 space-y-6">
          <!-- 分享标题 -->
          <div>
            <label for="title" class="block text-sm font-medium text-gray-900 mb-2">
              分享标题 <span class="text-red-500">*</span>
            </label>
            <input
              id="title"
              v-model="shareForm.title"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="某个领域学习经验/求职工作经历/生涯规划感悟都可以哦"
            />
            <p class="mt-1 text-xs text-gray-500">好的分享标题应该容易记忆且高度概括</p>
          </div>

          <!-- 分享描述 -->
          <div>
            <label for="description" class="block text-sm font-medium text-gray-900 mb-2">
              分享描述 <span class="text-gray-500">(可选)</span>
            </label>
            <textarea
              id="description"
              v-model="shareForm.content"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="简要描述你的分享（15个字以内）"
            ></textarea>
          </div>

          <!-- 分享文档 -->
          <div>
            <label for="documentFile" class="block text-sm font-medium text-gray-900 mb-2">
              分享文档 <span class="text-red-500">*</span>
            </label>
            <div class="border-2 border-dashed border-gray-300 rounded-md p-4 text-center hover:border-gray-400 transition-colors">
              <el-upload
                ref="uploadRef"
                class="upload-dragger"
                drag
                :action="uploadUrl"
                :headers="uploadHeaders"
                :before-upload="beforeUpload"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-progress="handleUploadProgress"
                :file-list="fileList"
                :limit="1"
                :on-exceed="handleExceed"
                accept=".md"
              >
                <div v-if="fileList.length === 0" class="cursor-pointer">
                  <svg class="w-8 h-8 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                  </svg>
                  <p class="text-sm text-gray-600 mb-1">点击上传分享文档</p>
                  <p class="text-xs text-gray-500">支持 .md 格式文件</p>
                </div>
                <div v-else class="text-green-600">
                  <svg class="w-8 h-8 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                  <p class="text-sm font-medium">文件已上传</p>
                </div>
              </el-upload>
              
              <div v-if="uploadProgress > 0 && uploadProgress < 100" class="mt-4">
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div class="bg-blue-600 h-2 rounded-full transition-all duration-300" :style="{width: uploadProgress + '%'}"></div>
                </div>
                <p class="text-xs text-gray-500 mt-1">上传进度: {{ uploadProgress }}%</p>
              </div>
            </div>
          </div>

          <!-- 分享分类 -->
          <div>
            <label for="category" class="block text-sm font-medium text-gray-900 mb-2">
              分享分类 <span class="text-red-500">*</span>
            </label>
            <select
              id="category"
              v-model="shareForm.category"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">选择分享分类</option>
              <option
                v-for="category in categories"
                :key="category.value"
                :value="category.value"
              >
                {{ category.label }}
              </option>
            </select>
          </div>

          <!-- 提交按钮 -->
          <div class="flex justify-end space-x-3 pt-6 border-t border-gray-200">
            <button
              type="button"
              @click="resetForm"
              class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              重置
            </button>
            <button
              type="button"
              disabled
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-gray-400 cursor-not-allowed opacity-50"
            >
              暂未开放
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import axios from 'axios'
import { config } from '../utils/config.js'

const router = useRouter()
const shareFormRef = ref()
const uploadRef = ref()

// 表单数据
const shareForm = reactive({
  title: '',
  category: '',
  content: '',
  textUrl: ''
})

// 分类选项
const categories = [
  { label: '学习经验', value: 'learn' },
  { label: '求职经验', value: 'job' },
  { label: '规划建议', value: 'plan' },
  { label: '其他', value: 'others' }
]

// 表单验证规则
const shareRules = {
  title: [
    { required: true, message: '请输入经验标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应在 5 到 100 个字符之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择经验类型', trigger: 'change' }
  ]
}

// 上传相关
const fileList = ref([])
const uploadProgress = ref(0)
const submitting = ref(false)

  const uploadUrl = computed(() => '/api/files/upload')
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
})

// 上传前检查
const beforeUpload = (file) => {
  const isValidType = file.name.toLowerCase().endsWith('.md')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传 .md 格式的文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!')
    return false
  }
  
  uploadProgress.value = 0
  return true
}

// 上传进度
const handleUploadProgress = (event) => {
  uploadProgress.value = Math.round((event.loaded / event.total) * 100)
}

// 上传成功
const handleUploadSuccess = (response) => {
  uploadProgress.value = 100
  if (response.success) {
    shareForm.textUrl = response.data.url || response.data.path
    ElMessage.success('文件上传成功!')
  } else {
    ElMessage.error(response.message || '文件上传失败!')
  }
}

// 上传失败
const handleUploadError = (error) => {
  uploadProgress.value = 0
  console.error('Upload error:', error)
  ElMessage.error('文件上传失败，请重试!')
}

// 文件数量超限
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件!')
}

// 重置表单
const resetForm = () => {
  shareFormRef.value?.resetFields()
  fileList.value = []
  uploadProgress.value = 0
  shareForm.textUrl = ''
}

// 提交分享
const submitShare = async () => {
  try {
    // 表单验证
    await shareFormRef.value?.validate()
    
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      title: shareForm.title,
      category: shareForm.category,
      content: shareForm.content || '',
      textUrl: shareForm.textUrl || ''
    }
    
    // 提交到后端
    const response = await axios.post('/api/senior-shares', submitData)
    
    if (response.data.success) {
      ElMessage.success('经验分享成功!')
      
      // 询问是否查看分享
      const result = await ElMessageBox.confirm(
        '经验分享成功！是否立即查看你的分享？',
        '分享成功',
        {
          confirmButtonText: '查看分享',
          cancelButtonText: '返回列表',
          type: 'success'
        }
      ).catch(() => 'cancel')
      
      if (result === 'confirm') {
        router.push(`/share/${response.data.data.id}`)
      } else {
        router.push('/share')
      }
    } else {
      ElMessage.error(response.data.message || '分享失败，请重试!')
    }
  } catch (error) {
    console.error('Submit error:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('分享失败，请重试!')
    }
  } finally {
    submitting.value = false
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

/* 上传组件样式覆盖 */
:deep(.el-upload-dragger) {
  border: none !important;
  background: transparent !important;
  width: 100% !important;
  height: auto !important;
  padding: 0 !important;
}

:deep(.el-upload-dragger:hover) {
  border: none !important;
  background: transparent !important;
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