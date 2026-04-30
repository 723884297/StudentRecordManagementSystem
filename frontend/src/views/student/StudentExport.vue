<template>
  <div class="student-export">
    <div class="page-header">
      <h2 class="page-title-text">档案导出</h2>
      <p class="page-desc">导出你的个人档案与学籍信息</p>
    </div>

    <!-- 无有效申请提示 -->
    <div class="no-permission card" v-if="student && validRequests.length === 0 && !loading">
      <div class="no-perm-icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
        </svg>
      </div>
      <h3 class="no-perm-title">暂无有效的导出权限</h3>
      <p class="no-perm-desc">你需要先提交档案导出申请，审核通过后24小时内可导出。</p>
      <router-link to="/student/archive-apply" class="btn btn-primary">去申请</router-link>
    </div>

    <!-- 有有效申请时显示导出选项 -->
    <template v-if="student && validRequests.length > 0">
      <div class="expire-notice card">
        <div class="notice-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </div>
        <div class="notice-content">
          <span class="notice-text">导出权限有效至 {{ formatDateTime(earliestExpire) }}</span>
          <span class="notice-remaining">（最早过期剩余 {{ remainingTime(earliestExpire) }}）</span>
        </div>
      </div>

      <div class="export-options">
        <div class="export-card card" @click="exportProfile" v-if="canExport('个人信息')">
          <div class="export-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="export-info">
            <h3 class="export-title">个人信息</h3>
            <p class="export-desc">导出学籍基本信息、家庭信息等</p>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="export-arrow">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
        </div>

        <div class="export-card card" @click="exportArchives" v-if="canExport('档案材料')">
          <div class="export-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
            </svg>
          </div>
          <div class="export-info">
            <h3 class="export-title">档案材料清单</h3>
            <p class="export-desc">导出所有已通过审核的档案材料列表</p>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="export-arrow">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
        </div>

        <div class="export-card card" @click="exportTranscript" v-if="canExport('成绩单')">
          <div class="export-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
            </svg>
          </div>
          <div class="export-info">
            <h3 class="export-title">成绩单</h3>
            <p class="export-desc">导出个人成绩单（打印版）</p>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="export-arrow">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
        </div>
      </div>
    </template>

    <div class="export-preview card" v-if="exportContent">
      <div class="preview-header">
        <h3 class="card-title">导出预览</h3>
        <button class="btn btn-primary" @click="downloadText">下载文件</button>
      </div>
      <pre class="preview-content">{{ exportContent }}</pre>
    </div>

    <div class="empty-state card" v-if="!student && !loading">
      <p class="empty-text">暂未关联学生信息，请联系管理员</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi, archiveApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const detail = ref<any>(null)
const archives = ref<any[]>([])
const loading = ref(true)
const exportContent = ref('')
const exportFileName = ref('')
const validRequests = ref<any[]>([])

const earliestExpire = computed(() => {
  if (validRequests.value.length === 0) return ''
  return validRequests.value.reduce((min: string, r: any) => {
    if (!min) return r.expireTime
    return new Date(r.expireTime) < new Date(min) ? r.expireTime : min
  }, '')
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        student.value = stuRes.data
        // 检查是否有有效的导出权限
        const validRes: any = await archiveApi.getValidApprovedRequest({ studentId: stuRes.data.pkStudent })
        validRequests.value = validRes.data || []

        if (validRequests.value.length > 0) {
          const detailRes: any = await studentApi.getDetail(stuRes.data.pkStudent)
          detail.value = detailRes.data || {}
          const fileRes: any = await archiveApi.getFileList({ studentId: stuRes.data.pkStudent, pageNum: 1, pageSize: 200 })
          if (fileRes.data?.list) {
            archives.value = fileRes.data.list
          }
        }
      }
    }
  } catch (e) {
    console.error('加载导出数据失败', e)
  } finally {
    loading.value = false
  }
})

function canExport(type: string): boolean {
  if (validRequests.value.length === 0) return false
  return validRequests.value.some((r: any) => r.requestType === '全部' || r.requestType === type)
}

function exportProfile() {
  const s = student.value
  const d = detail.value || {}
  let text = '=== 个人信息 ===\n'
  text += `姓名: ${s.name}\n`
  text += `学号: ${s.studentNo}\n`
  text += `性别: ${s.gender === 'M' ? '男' : s.gender === 'F' ? '女' : '-'}\n`
  text += `出生日期: ${s.birthDate || '-'}\n`
  text += `学院: ${s.collegeName || '-'}\n`
  text += `专业: ${s.majorName || '-'}\n`
  text += `班级: ${s.className || '-'}\n`
  text += `入学年份: ${s.enrollmentYear || '-'}\n`
  text += `学籍状态: ${s.status || '-'}\n`
  text += `\n=== 家庭信息 ===\n`
  text += `家庭住址: ${d.homeAddress || '-'}\n`
  text += `紧急联系人: ${d.emergencyContact || '-'}\n`
  text += `紧急联系电话: ${d.emergencyPhone || '-'}\n`
  text += `联系电话: ${d.phone || '-'}\n`
  text += `电子邮箱: ${d.email || '-'}\n`
  exportContent.value = text
  exportFileName.value = `${s.studentNo}_个人信息.txt`
}

function exportArchives() {
  const approved = archives.value.filter(f => f.status === 1)
  let text = '=== 档案材料清单 ===\n'
  text += `学生: ${student.value?.name} (${student.value?.studentNo})\n`
  text += `导出时间: ${new Date().toLocaleString('zh-CN')}\n\n`
  if (approved.length) {
    approved.forEach((f, idx) => {
      text += `${idx + 1}. ${f.categoryName || '未分类'} - ${f.fileName}\n`
      text += `   描述: ${f.description || '-'}\n`
      text += `   上传时间: ${f.uploadTime ? new Date(f.uploadTime).toLocaleDateString('zh-CN') : '-'}\n\n`
    })
  } else {
    text += '暂无已审核通过的档案材料\n'
  }
  exportContent.value = text
  exportFileName.value = `${student.value?.studentNo}_档案清单.txt`
}

function exportTranscript() {
  const s = student.value
  let text = '=============================\n'
  text += '        学 生 成 绩 单        \n'
  text += '=============================\n\n'
  text += `姓名: ${s.name}    学号: ${s.studentNo}\n`
  text += `学院: ${s.collegeName || '-'}    专业: ${s.majorName || '-'}\n`
  text += `班级: ${s.className || '-'}    入学年份: ${s.enrollmentYear || '-'}\n\n`
  text += '--- 档案记录 ---\n'
  const approved = archives.value.filter(f => f.status === 1)
  if (approved.length) {
    approved.forEach((f, idx) => {
      text += `${idx + 1}. ${f.categoryName || '未分类'}: ${f.fileName}\n`
    })
  } else {
    text += '暂无成绩档案记录\n'
  }
  exportContent.value = text
  exportFileName.value = `${s.studentNo}_成绩单.txt`
}

function downloadText() {
  const blob = new Blob([exportContent.value], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = exportFileName.value
  a.click()
  URL.revokeObjectURL(url)
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
.student-export {
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
.no-permission {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  gap: 12px;
  text-align: center;
}
.no-perm-icon { color: var(--text-quaternary); }
.no-perm-title {
  font-size: 16px;
  font-weight: 590;
  color: var(--text-primary);
}
.no-perm-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  max-width: 360px;
}
.expire-notice {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(99, 102, 241, 0.06);
  border-color: rgba(99, 102, 241, 0.2);
}
.notice-icon {
  color: var(--brand-indigo);
  flex-shrink: 0;
}
.notice-text {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-primary);
}
.notice-remaining {
  font-size: 13px;
  color: var(--text-tertiary);
}
.export-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.export-card {
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.15s ease;
}
.export-card:hover {
  border-color: var(--brand-indigo);
}
.export-icon {
  color: var(--text-tertiary);
  flex-shrink: 0;
}
.export-info { flex: 1; }
.export-title {
  font-size: 15px;
  font-weight: 590;
  color: var(--text-primary);
}
.export-desc {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 2px;
}
.export-arrow {
  color: var(--text-quaternary);
  flex-shrink: 0;
}
.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.182px;
}
.preview-content {
  background: var(--surface-03);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  padding: 16px;
  font-size: 13px;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
  font-family: 'SF Mono', monospace;
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
