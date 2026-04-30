<template>
  <div class="student-archive-apply">
    <div class="page-header">
      <h2 class="page-title-text">档案申请</h2>
      <p class="page-desc">提交档案导出申请，审核通过后24小时内可导出</p>
    </div>

    <!-- 当前有效申请提示 -->
    <div class="active-notice card" v-if="activeRequests.length > 0">
      <div class="notice-icon">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
      </div>
      <div class="notice-content">
        <p class="notice-title">你有 {{ activeRequests.length }} 个有效的导出权限</p>
        <p class="notice-detail">
          类型：{{ activeRequests.map((r: any) => typeLabel(r.requestType)).join('、') }}
        </p>
      </div>
      <router-link to="/student/export" class="btn btn-primary">去导出</router-link>
    </div>

    <!-- 申请表单 -->
    <div class="apply-card card">
      <h3 class="card-title">提交新申请</h3>
      <form class="apply-form" @submit.prevent="submitRequest">
        <div class="form-group">
          <label class="form-label">申请类型 <span class="required">*</span></label>
          <select v-model="form.requestType" class="form-input" required>
            <option value="">请选择</option>
            <option value="个人信息">个人信息</option>
            <option value="档案材料">档案材料清单</option>
            <option value="成绩单">成绩单</option>
            <option value="全部">全部档案</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">申请原因</label>
          <textarea v-model="form.reason" class="form-input" rows="3" placeholder="请简要说明申请导出的用途（选填）"></textarea>
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="submitting || !form.requestType">
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </form>
    </div>

    <!-- 待处理申请 -->
    <div class="pending-card card" v-if="pendingRequests.length">
      <h3 class="card-title">待审核申请</h3>
      <div class="request-list">
        <div class="request-item" v-for="req in pendingRequests" :key="req.pkRequest">
          <div class="request-info">
            <span class="request-type">{{ typeLabel(req.requestType) }}</span>
            <span class="request-time">{{ formatDateTime(req.createTime) }}</span>
          </div>
          <span class="badge badge-warning">待审核</span>
        </div>
      </div>
    </div>

    <div class="empty-state card" v-if="!student && !loading">
      <p class="empty-text">暂未关联学生信息，请联系管理员</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi, archiveApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const loading = ref(true)
const submitting = ref(false)
const allRequests = ref<any[]>([])
const activeRequests = ref<any[]>([])

const form = reactive({
  requestType: '',
  reason: ''
})

const pendingRequests = computed(() => allRequests.value.filter(r => r.status === 0))

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        student.value = stuRes.data
        await loadRequests()
      }
    }
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
})

async function loadRequests() {
  if (!student.value) return
  try {
    const res: any = await archiveApi.getExportRequestsByStudentId(student.value.pkStudent)
    allRequests.value = res.data || []
    // 检查是否有有效的已通过申请
    const validRes: any = await archiveApi.getValidApprovedRequest({ studentId: student.value.pkStudent })
    activeRequests.value = validRes.data || []
  } catch (e) {
    console.error('加载申请记录失败', e)
  }
}

async function submitRequest() {
  if (!student.value?.pkStudent || !form.requestType) return
  submitting.value = true
  try {
    await archiveApi.submitExportRequest({
      studentId: student.value.pkStudent,
      requestType: form.requestType,
      reason: form.reason
    })
    form.requestType = ''
    form.reason = ''
    await loadRequests()
  } catch (e: any) {
    alert(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function typeLabel(type: string): string {
  const map: Record<string, string> = {
    '个人信息': '个人信息',
    '档案材料': '档案材料清单',
    '成绩单': '成绩单',
    '全部': '全部档案'
  }
  return map[type] || type || '-'
}

function formatDateTime(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function remainingTime(expireStr: string): string {
  if (!expireStr) return '-'
  const expire = new Date(expireStr).getTime()
  const now = Date.now()
  const diff = expire - now
  if (diff <= 0) return '已过期'
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  return `${hours}小时${minutes}分钟`
}
</script>

<style scoped>
.student-archive-apply {
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
.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -0.182px;
}
.active-notice {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(34, 197, 94, 0.06);
  border-color: rgba(34, 197, 94, 0.2);
}
.notice-icon {
  color: var(--status-green);
  flex-shrink: 0;
}
.notice-content { flex: 1; }
.notice-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
}
.notice-detail {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 2px;
}
.apply-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.required { color: var(--status-red); }
.form-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 8px;
}
.request-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.request-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--surface-03);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
}
.request-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.request-type {
  font-size: 14px;
  font-weight: 510;
  color: var(--text-primary);
}
.request-time {
  font-size: 12px;
  color: var(--text-quaternary);
}
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
}
.empty-text {
  font-size: 14px;
  color: var(--text-quaternary);
}
</style>
