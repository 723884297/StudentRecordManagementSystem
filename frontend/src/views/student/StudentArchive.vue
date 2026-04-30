<template>
  <div class="student-archive">
    <div class="page-header">
      <h2 class="page-title-text">我的档案</h2>
      <p class="page-desc">查看个人学籍档案材料及审核状态</p>
    </div>

    <div class="archive-list" v-if="archives.length">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>材料分类</th>
              <th>文件名</th>
              <th>描述</th>
              <th>上传时间</th>
              <th>状态</th>
              <th>审核意见</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="file in archives" :key="file.pkArchiveFile">
              <td>{{ file.categoryName || '-' }}</td>
              <td>{{ file.fileName }}</td>
              <td>{{ file.description || '-' }}</td>
              <td>{{ formatDate(file.uploadTime) }}</td>
              <td>
                <span :class="['badge', statusBadgeClass(file.status)]">
                  {{ statusText(file.status) }}
                </span>
              </td>
              <td>{{ file.auditRemark || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="empty-state card" v-else>
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
      </svg>
      <p class="empty-text">暂无档案材料</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { archiveApi } from '../../api/modules'

const userStore = useUserStore()
const archives = ref<any[]>([])

onMounted(async () => {
  try {
    // 通过用户ID查找关联的学生，然后获取档案
    const userId = userStore.userInfo?.userId
    if (userId) {
      const res: any = await archiveApi.getFileList({ uploadUserId: userId, pageNum: 1, pageSize: 100 })
      if (res.data?.list) {
        archives.value = res.data.list
      }
    }
  } catch (e) {
    console.error('加载档案列表失败', e)
  }
})

function statusText(status: number): string {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已驳回'
    default: return '未知'
  }
}

function statusBadgeClass(status: number): string {
  switch (status) {
    case 0: return 'badge-warning'
    case 1: return 'badge-success'
    case 2: return 'badge-danger'
    default: return 'badge-neutral'
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.student-archive {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  padding: 8px 0;
}

.page-title-text {
  font-size: 20px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.24px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  gap: 12px;
}

.empty-icon {
  color: var(--text-quaternary);
}

.empty-text {
  font-size: 14px;
  color: var(--text-quaternary);
}
</style>
