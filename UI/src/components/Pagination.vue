<template>
  <div class="pagination-container" v-if="totalPages > 1">
    <div class="pagination-info">
      显示第 {{ startItem }}-{{ endItem }} 项，共 {{ total }} 项
    </div>
    
    <div class="pagination">
      <!-- 上一页按钮 -->
      <button 
        class="page-btn"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        <i class="fas fa-chevron-left"></i>
        上一页
      </button>
      
      <!-- 页码 -->
      <div class="page-numbers">
        <!-- 第一页 -->
        <button 
          v-if="showFirstPage"
          class="page-number"
          :class="{ active: currentPage === 1 }"
          @click="goToPage(1)"
        >
          1
        </button>
        
        <!-- 左侧省略号 -->
        <span v-if="showLeftEllipsis" class="ellipsis">...</span>
        
        <!-- 中间页码 -->
        <button 
          v-for="page in visiblePages"
          :key="page"
          class="page-number"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        
        <!-- 右侧省略号 -->
        <span v-if="showRightEllipsis" class="ellipsis">...</span>
        
        <!-- 最后一页 -->
        <button 
          v-if="showLastPage"
          class="page-number"
          :class="{ active: currentPage === totalPages }"
          @click="goToPage(totalPages)"
        >
          {{ totalPages }}
        </button>
      </div>
      
      <!-- 下一页按钮 -->
      <button 
        class="page-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
      >
        下一页
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    pageSize: {
      type: Number,
      required: true
    },
    total: {
      type: Number,
      required: true
    },
    maxVisiblePages: {
      type: Number,
      default: 5
    }
  },
  emits: ['page-change'],
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize);
    },
    startItem() {
      return (this.currentPage - 1) * this.pageSize + 1;
    },
    endItem() {
      const end = this.currentPage * this.pageSize;
      return end > this.total ? this.total : end;
    },
    visiblePages() {
      const pages = [];
      const half = Math.floor(this.maxVisiblePages / 2);
      let start = Math.max(1, this.currentPage - half);
      let end = Math.min(this.totalPages, start + this.maxVisiblePages - 1);
      
      // 调整起始位置以确保显示足够的页码
      if (end - start + 1 < this.maxVisiblePages) {
        start = Math.max(1, end - this.maxVisiblePages + 1);
      }
      
      // 如果显示第一页或最后一页，则从可见页码中排除
      if (start === 1) {
        start = 2;
      }
      if (end === this.totalPages) {
        end = this.totalPages - 1;
      }
      
      for (let i = start; i <= end; i++) {
        if (i > 1 && i < this.totalPages) {
          pages.push(i);
        }
      }
      
      return pages;
    },
    showFirstPage() {
      return this.totalPages > 1;
    },
    showLastPage() {
      return this.totalPages > 1 && this.totalPages !== 1;
    },
    showLeftEllipsis() {
      return this.visiblePages.length > 0 && this.visiblePages[0] > 2;
    },
    showRightEllipsis() {
      return this.visiblePages.length > 0 && 
             this.visiblePages[this.visiblePages.length - 1] < this.totalPages - 1;
    }
  },
  methods: {
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
        this.$emit('page-change', page);
      }
    }
  }
};
</script>

<style scoped>
.ellipsis {
  padding: 8px 4px;
  color: #999;
  font-size: 14px;
}
</style>