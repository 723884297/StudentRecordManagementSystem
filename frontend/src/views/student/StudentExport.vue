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
        <!-- 全部导出卡片（优先显示） -->
        <div class="export-card card export-card-all" @click="exportAll" v-if="canExport('全部')">
          <div class="export-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
          </div>
          <div class="export-info">
            <h3 class="export-title">全部档案导出</h3>
            <p class="export-desc">一键导出个人信息、档案材料、成绩单、奖惩记录（完整档案）</p>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="export-arrow">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
        </div>

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

        <div class="export-card card" @click="exportAwards" v-if="canExport('奖惩档案')">
          <div class="export-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/>
            </svg>
          </div>
          <div class="export-info">
            <h3 class="export-title">奖惩档案</h3>
            <p class="export-desc">导出个人奖励与处分记录</p>
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
import { studentApi, archiveApi, gradeApi, awardApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const detail = ref<any>(null)
const archives = ref<any[]>([])
const grades = ref<any[]>([])
const awards = ref<any[]>([])
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
          const gradeRes: any = await gradeApi.getByStudentId(stuRes.data.pkStudent)
          grades.value = gradeRes.data || []
          const awardRes: any = await awardApi.getByStudentId(stuRes.data.pkStudent)
          awards.value = awardRes.data || []
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
      if (f.filePath) {
        text += `   文件地址: ${f.filePath}\n`
      }
      text += `   上传时间: ${f.uploadTime ? new Date(f.uploadTime).toLocaleDateString('zh-CN') : '-'}\n\n`
    })
  } else {
    text += '暂无已审核通过的档案材料\n'
  }
  exportContent.value = text
  exportFileName.value = `${student.value?.studentNo}_档案清单.txt`
}

function exportAll() {
  const s = student.value
  const d = detail.value || {}
  const approved = archives.value.filter(f => f.status === 1)
  const gradeList = grades.value
  const awardRecords = awards.value

  let text = '════════════════════════════════════════\n'
  text += '        学 生 完 整 档 案              \n'
  text += '════════════════════════════════════════\n\n'
  text += `姓名: ${s.name}    学号: ${s.studentNo}\n`
  text += `学院: ${s.collegeName || '-'}    专业: ${s.majorName || '-'}\n`
  text += `班级: ${s.className || '-'}    入学年份: ${s.enrollmentYear || '-'}\n`
  text += `导出时间: ${new Date().toLocaleString('zh-CN')}\n\n`

  // ===== 一、个人信息 =====
  text += '┌─────────────────────────────────────┐\n'
  text += '│  一、个人信息                        │\n'
  text += '└─────────────────────────────────────┘\n\n'
  text += `  姓名: ${s.name}\n`
  text += `  学号: ${s.studentNo}\n`
  text += `  性别: ${s.gender === 'M' ? '男' : s.gender === 'F' ? '女' : '-'}\n`
  text += `  出生日期: ${s.birthDate || '-'}\n`
  text += `  学院: ${s.collegeName || '-'}\n`
  text += `  专业: ${s.majorName || '-'}\n`
  text += `  班级: ${s.className || '-'}\n`
  text += `  入学年份: ${s.enrollmentYear || '-'}\n`
  text += `  学籍状态: ${s.status || '-'}\n`
  text += `\n  家庭住址: ${d.homeAddress || '-'}\n`
  text += `  联系电话: ${d.phone || '-'}\n`
  text += `  电子邮箱: ${d.email || '-'}\n`
  text += `  紧急联系人: ${d.emergencyContact || '-'}\n`
  text += `  紧急联系电话: ${d.emergencyPhone || '-'}\n\n`

  // ===== 二、档案材料清单 =====
  text += '┌─────────────────────────────────────┐\n'
  text += '│  二、档案材料清单                    │\n'
  text += '└─────────────────────────────────────┘\n\n'
  if (approved.length) {
    approved.forEach((f, idx) => {
      text += `  ${idx + 1}. ${f.categoryName || '未分类'} - ${f.fileName}\n`
      text += `     描述: ${f.description || '-'}\n`
      if (f.filePath) {
        text += `     文件URL: ${f.filePath}\n`
      }
      text += `     上传时间: ${f.uploadTime ? new Date(f.uploadTime).toLocaleString('zh-CN') : '-'}\n\n`
    })
  } else {
    text += '  暂无已审核通过的档案材料\n\n'
  }

  // ===== 三、成绩档案 =====
  text += '┌─────────────────────────────────────┐\n'
  text += '│  三、成绩档案                        │\n'
  text += '└─────────────────────────────────────┘\n\n'
  if (gradeList.length) {
    const semMap = new Map<string, any[]>()
    gradeList.forEach(g => {
      const sem = g.semester || '未知学期'
      if (!semMap.has(sem)) semMap.set(sem, [])
      semMap.get(sem)!.push(g)
    })
    const semGroups = Array.from(semMap.entries()).sort((a, b) => b[0].localeCompare(a[0]))

    const allScored = gradeList.filter((g: any) => g.score != null)
    const allGpa = gradeList.filter((g: any) => g.gpa != null)
    const totalCredits = gradeList.filter((g: any) => g.credit != null).reduce((sum: number, g: any) => sum + Number(g.credit), 0)
    const avgScore = allScored.length ? (allScored.reduce((sum: number, g: any) => sum + Number(g.score), 0) / allScored.length).toFixed(1) : '-'
    const avgGpa = allGpa.length ? (allGpa.reduce((sum: number, g: any) => sum + Number(g.gpa), 0) / allGpa.length).toFixed(2) : '-'

    text += `  课程总数: ${gradeList.length}    总学分: ${totalCredits.toFixed(1)}    平均成绩: ${avgScore}    平均绩点: ${avgGpa}\n\n`

    semGroups.forEach(([semester, semGrades]) => {
      text += `  ■ ${semester}\n`
      text += `  ${'-'.repeat(36)}\n`
      text += `  ${padRight('序号', 4)}${padRight('课程名称', 18)}${padRight('类型', 8)}${padRight('学分', 6)}${padRight('成绩', 6)}${padRight('绩点', 6)}\n`
      text += `  ${'-'.repeat(36)}\n`
      semGrades.forEach((g: any, idx: number) => {
        const seq = String(idx + 1)
        const courseName = truncate(g.courseName || '-', 16)
        const courseType = g.courseType || '-'
        const credit = g.credit != null ? String(g.credit) : '-'
        const score = g.score != null ? String(g.score) : '-'
        const gpa = g.gpa != null ? String(g.gpa) : '-'
        text += `  ${padRight(seq, 4)}${padRight(courseName, 18)}${padRight(courseType, 8)}${padRight(credit, 6)}${padRight(score, 6)}${padRight(gpa, 6)}\n`
      })
      text += '\n'
    })
  } else {
    text += '  暂无成绩记录\n\n'
  }

  // ===== 四、奖惩档案 =====
  text += '┌─────────────────────────────────────┐\n'
  text += '│  四、奖惩档案                        │\n'
  text += '└─────────────────────────────────────┘\n\n'
  if (awardRecords.length) {
    const awardList = awardRecords.filter((r: any) => r.type !== 'punishment')
    const punishmentList = awardRecords.filter((r: any) => r.type === 'punishment')
    text += `  奖励总数: ${awardList.length}    处分总数: ${punishmentList.length}\n\n`

    if (awardList.length) {
      text += '  ■ 奖励记录\n'
      text += `  ${'-'.repeat(36)}\n`
      text += `  ${padRight('序号', 4)}${padRight('名称', 18)}${padRight('级别', 10)}${padRight('日期', 14)}${padRight('机构', 16)}\n`
      text += `  ${'-'.repeat(36)}\n`
      awardList.forEach((a: any, idx: number) => {
        text += `  ${padRight(String(idx + 1), 4)}${padRight(truncate(a.awardName || '-', 16), 18)}${padRight(a.awardLevel || '-', 10)}${padRight(a.awardDate || '-', 14)}${padRight(truncate(a.issuingAuthority || '-', 14), 16)}\n`
        if (a.description) text += `    说明: ${a.description}\n`
      })
      text += '\n'
    }

    if (punishmentList.length) {
      text += '  ■ 处分记录\n'
      text += `  ${'-'.repeat(36)}\n`
      text += `  ${padRight('序号', 4)}${padRight('名称', 18)}${padRight('级别', 10)}${padRight('日期', 14)}${padRight('机构', 16)}\n`
      text += `  ${'-'.repeat(36)}\n`
      punishmentList.forEach((p: any, idx: number) => {
        text += `  ${padRight(String(idx + 1), 4)}${padRight(truncate(p.awardName || '-', 16), 18)}${padRight(p.awardLevel || '-', 10)}${padRight(p.awardDate || '-', 14)}${padRight(truncate(p.issuingAuthority || '-', 14), 16)}\n`
        if (p.description) text += `    说明: ${p.description}\n`
      })
      text += '\n'
    }
  } else {
    text += '  暂无奖惩记录\n\n'
  }

  text += '════════════════════════════════════════\n'
  text += '        本档案仅供内部使用              \n'
  text += '════════════════════════════════════════\n'
  exportContent.value = text
  exportFileName.value = `${s.studentNo}_完整档案.txt`
}

function exportTranscript() {
  const s = student.value
  const gradeList = grades.value
  let text = '========================================\n'
  text += '            学 生 成 绩 单              \n'
  text += '========================================\n\n'
  text += `姓名: ${s.name}    学号: ${s.studentNo}\n`
  text += `学院: ${s.collegeName || '-'}    专业: ${s.majorName || '-'}\n`
  text += `班级: ${s.className || '-'}    入学年份: ${s.enrollmentYear || '-'}\n`
  text += `导出时间: ${new Date().toLocaleString('zh-CN')}\n\n`

  if (!gradeList.length) {
    text += '暂无成绩记录\n'
  } else {
    // 按学期分组
    const semMap = new Map<string, any[]>()
    gradeList.forEach(g => {
      const sem = g.semester || '未知学期'
      if (!semMap.has(sem)) semMap.set(sem, [])
      semMap.get(sem)!.push(g)
    })
    const semGroups = Array.from(semMap.entries())
      .sort((a, b) => b[0].localeCompare(a[0]))

    // 汇总统计
    const allScored = gradeList.filter((g: any) => g.score !== null && g.score !== undefined)
    const allGpa = gradeList.filter((g: any) => g.gpa !== null && g.gpa !== undefined)
    const totalCredits = gradeList
      .filter((g: any) => g.credit !== null && g.credit !== undefined)
      .reduce((sum: number, g: any) => sum + Number(g.credit), 0)
    const avgScore = allScored.length
      ? (allScored.reduce((sum: number, g: any) => sum + Number(g.score), 0) / allScored.length).toFixed(1)
      : '-'
    const avgGpa = allGpa.length
      ? (allGpa.reduce((sum: number, g: any) => sum + Number(g.gpa), 0) / allGpa.length).toFixed(2)
      : '-'

    text += '----------------------------------------\n'
    text += `课程总数: ${gradeList.length}    总学分: ${totalCredits.toFixed(1)}    平均成绩: ${avgScore}    平均绩点: ${avgGpa}\n`
    text += '----------------------------------------\n\n'

    semGroups.forEach(semGroup => {
      const [semester, semGrades] = semGroup
      text += `【${semester}】\n`
      text += `${'-'.repeat(40)}\n`
      text += `${padRight('序号', 6)}${padRight('课程名称', 20)}${padRight('类型', 10)}${padRight('学分', 8)}${padRight('成绩', 8)}${padRight('绩点', 8)}\n`
      text += `${'-'.repeat(40)}\n`
      semGrades.forEach((g: any, idx: number) => {
        const seq = String(idx + 1)
        const courseName = truncate(g.courseName || '-', 18)
        const courseType = g.courseType || '-'
        const credit = g.credit != null ? String(g.credit) : '-'
        const score = g.score != null ? String(g.score) : '-'
        const gpa = g.gpa != null ? String(g.gpa) : '-'
        text += `${padRight(seq, 6)}${padRight(courseName, 20)}${padRight(courseType, 10)}${padRight(credit, 8)}${padRight(score, 8)}${padRight(gpa, 8)}\n`
      })
      text += '\n'
    })
  }

  text += '========================================\n'
  text += '          本成绩单仅供内部使用          \n'
  text += '========================================\n'
  exportContent.value = text
  exportFileName.value = `${s.studentNo}_成绩单.txt`
}

function padRight(str: string, len: number): string {
  // 计算中文字符宽度（中文约占2个字符宽度）
  let width = 0
  for (const ch of str) {
    width += /[\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]/.test(ch) ? 2 : 1
  }
  const padding = Math.max(0, len - width)
  return str + ' '.repeat(padding)
}

function truncate(str: string, maxLen: number): string {
  let width = 0
  let result = ''
  for (const ch of str) {
    const charWidth = /[\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]/.test(ch) ? 2 : 1
    if (width + charWidth > maxLen) {
      result += '..'
      break
    }
    result += ch
    width += charWidth
  }
  return result || str
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
.export-card-all {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.06) 0%, rgba(99, 102, 241, 0.02) 100%);
  border-color: rgba(99, 102, 241, 0.25);
}
.export-card-all:hover {
  border-color: var(--brand-indigo);
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(99, 102, 241, 0.04) 100%);
}
.export-card-all .export-icon {
  color: var(--brand-indigo);
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
