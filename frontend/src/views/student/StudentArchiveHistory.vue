<template>
  <div class="student-archive-history">
    <div class="page-header">
      <h2 class="page-title-text">档案申请记录</h2>
      <p class="page-desc">查看你的历史档案导出申请与审核记录</p>
    </div>

    <div class="filter-bar card">
      <div class="filter-group">
        <label class="filter-label">状态筛选</label>
        <select v-model="statusFilter" class="form-select" @change="filterData">
          <option value="">全部</option>
          <option value="0">待审核</option>
          <option value="1">已通过</option>
          <option value="2">已驳回</option>
        </select>
      </div>
    </div>

    <div class="card" v-if="filteredRequests.length">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>申请类型</th>
              <th>申请原因</th>
              <th>申请时间</th>
              <th>状态</th>
              <th>审核时间</th>
              <th>审核意见</th>
              <th>导出有效期</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="req in filteredRequests" :key="req.pkRequest">
              <td class="text-small-medium" style="color: var(--text-primary)">{{ req.requestType }}</td>
              <td>{{ req.reason || '-' }}</td>
              <td class="text-caption">{{ formatDateTime(req.createTime) }}</td>
              <td>
                <span :class="['badge', statusBadgeClass(req.status)]">{{ statusText(req.status) }}</span>
              </td>
              <td class="text-caption">{{ formatDateTime(req.auditTime) }}</td>
              <td>{{ req.auditRemark || '-' }}</td>
              <td>
                <template v-if="req.status === 1 && req.expireTime">
                  <span :class="['badge', isExpired(req.expireTime) ? 'badge-neutral' : 'badge-success']">
                    {{ isExpired(req.expireTime) ? '已过期' : '有效至 ' + formatDateTime(req.expireTime) }}
                  </span>
                </template>
                <template v-else>-</template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="empty-state card" v-else-if="!loading">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
      </svg>
      <p class="empty-text">暂无档案申请记录</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi, archiveApi } from '../../api/modules'

const userStore = useUserStore()
const allRequests = ref<any[]>([])
const statusFilter = ref('')
const loading = ref(true)

const filteredRequests = computed(() => {
  if (!statusFilter.value) return allRequests.value
  return allRequests.value.filter(r => String(r.status) === statusFilter.value)
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        const res: any = await archiveApi.getExportRequestsByStudentId(stuRes.data.pkStudent)
        allRequests.value = res.data || []
      }
    }
  } catch (e) {
    console.error('加载档案申请记录失败', e)
  } finally {
    loading.value = false
  }
})

function filterData() { /* computed handles it */ }

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

function formatDateTime(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function isExpired(expireStr: string): boolean {
  if (!expireStr) return true
  return new Date(expireStr).getTime() < Date.now()
}
</script>

<style scoped>
.student-archive-history {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.page-header { padding: 8px 0; }
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
.filter-bar {
  display: flex;
  align-items: center;
  gap: 16px;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-label {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-tertiary);
}
.form-select {
  padding: 6px 12px;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--bg-panel);
  color: var(--text-primary);
  font-size: 13px;
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
