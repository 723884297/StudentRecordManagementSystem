<template>
  <div class="student-transcript">
    <div class="page-header">
      <div class="page-header-left">
        <h2 class="page-title-text">成绩单</h2>
        <p class="page-desc">查看个人学业成绩汇总</p>
      </div>
      <button class="btn btn-secondary" @click="printTranscript" v-if="student">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
          <rect x="6" y="14" width="12" height="8"/>
        </svg>
        打印成绩单
      </button>
    </div>

    <div class="transcript-content" ref="transcriptRef" v-if="student">
      <div class="transcript-header card">
        <h3 class="transcript-title">学生成绩单</h3>
        <div class="student-info-grid">
          <div class="info-pair">
            <span class="info-key">姓名</span>
            <span class="info-val">{{ student.name }}</span>
          </div>
          <div class="info-pair">
            <span class="info-key">学号</span>
            <span class="info-val">{{ student.studentNo }}</span>
          </div>
          <div class="info-pair">
            <span class="info-key">学院</span>
            <span class="info-val">{{ student.collegeName || '-' }}</span>
          </div>
          <div class="info-pair">
            <span class="info-key">专业</span>
            <span class="info-val">{{ student.majorName || '-' }}</span>
          </div>
          <div class="info-pair">
            <span class="info-key">班级</span>
            <span class="info-val">{{ student.className || '-' }}</span>
          </div>
          <div class="info-pair">
            <span class="info-key">入学年份</span>
            <span class="info-val">{{ student.enrollmentYear || '-' }}</span>
          </div>
        </div>
        <div class="summary-row" v-if="grades.length">
          <div class="summary-item">
            <span class="info-key">课程总数</span>
            <span class="info-val">{{ grades.length }}</span>
          </div>
          <div class="summary-item">
            <span class="info-key">总学分</span>
            <span class="info-val">{{ totalCredits }}</span>
          </div>
          <div class="summary-item">
            <span class="info-key">平均成绩</span>
            <span class="info-val">{{ avgScore }}</span>
          </div>
          <div class="summary-item">
            <span class="info-key">平均绩点</span>
            <span class="info-val">{{ avgGpa }}</span>
          </div>
        </div>
      </div>

      <!-- 按学期分组 -->
      <div class="card" v-for="sem in semesterGroups" :key="sem.semester">
        <h3 class="card-title">{{ sem.semester }}</h3>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>序号</th>
                <th>课程名称</th>
                <th>课程类型</th>
                <th>学分</th>
                <th>成绩</th>
                <th>绩点</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(g, idx) in sem.grades" :key="g.pkGrade">
                <td>{{ idx + 1 }}</td>
                <td class="text-small-medium" style="color: var(--text-primary)">{{ g.courseName }}</td>
                <td>{{ g.courseType || '-' }}</td>
                <td>{{ g.credit ?? '-' }}</td>
                <td><span :style="{ color: scoreColor(g.score) }">{{ g.score ?? '-' }}</span></td>
                <td>{{ g.gpa ?? '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card empty-state" v-if="!grades.length">
        <p class="empty-text">暂无成绩记录</p>
      </div>
    </div>

    <div class="empty-state card" v-if="!student && !loading">
      <p class="empty-text">暂未关联学生信息，请联系管理员</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi, gradeApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const grades = ref<any[]>([])
const loading = ref(true)

const semesterGroups = computed(() => {
  const map = new Map<string, any[]>()
  grades.value.forEach(g => {
    const sem = g.semester || '未知学期'
    if (!map.has(sem)) map.set(sem, [])
    map.get(sem)!.push(g)
  })
  return Array.from(map.entries())
    .sort((a, b) => b[0].localeCompare(a[0]))
    .map(([semester, grades]) => ({ semester, grades }))
})

const totalCredits = computed(() => {
  const list = grades.value.filter(g => g.credit !== null && g.credit !== undefined)
  if (!list.length) return '-'
  return list.reduce((sum, g) => sum + Number(g.credit), 0).toFixed(1)
})

const avgScore = computed(() => {
  const scored = grades.value.filter(g => g.score !== null && g.score !== undefined)
  if (!scored.length) return '-'
  return (scored.reduce((sum, g) => sum + Number(g.score), 0) / scored.length).toFixed(1)
})

const avgGpa = computed(() => {
  const gpaList = grades.value.filter(g => g.gpa !== null && g.gpa !== undefined)
  if (!gpaList.length) return '-'
  return (gpaList.reduce((sum, g) => sum + Number(g.gpa), 0) / gpaList.length).toFixed(2)
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        student.value = stuRes.data
        const gradeRes: any = await gradeApi.getByStudentId(stuRes.data.pkStudent)
        grades.value = gradeRes.data || []
      }
    }
  } catch (e) {
    console.error('加载成绩单失败', e)
  } finally {
    loading.value = false
  }
})

function printTranscript() {
  window.print()
}

function scoreColor(score: number | null): string {
  if (score === null || score === undefined) return 'var(--text-secondary)'
  if (score >= 90) return 'var(--green-text)'
  if (score >= 60) return 'var(--text-primary)'
  return 'var(--red-text)'
}
</script>

<style scoped>
.student-transcript { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 8px 0; }
.page-title-text { font-size: 20px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.24px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
.transcript-content { display: flex; flex-direction: column; gap: 20px; }
.transcript-title { font-size: 18px; font-weight: 590; color: var(--text-primary); text-align: center; margin-bottom: 20px; }
.student-info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.info-pair { display: flex; gap: 8px; align-items: baseline; }
.info-key { font-size: 13px; color: var(--text-quaternary); font-weight: 510; white-space: nowrap; }
.info-val { font-size: 14px; color: var(--text-primary); font-weight: 510; }
.summary-row { display: flex; gap: 24px; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-subtle); }
.summary-item { display: flex; gap: 8px; align-items: baseline; }
.card-title { font-size: 14px; font-weight: 590; color: var(--text-primary); margin-bottom: 16px; letter-spacing: -0.182px; }
.empty-state { padding: 40px 24px; text-align: center; }
.empty-text { font-size: 14px; color: var(--text-quaternary); }
@media print {
  .page-header button { display: none; }
  .sidebar, .app-header { display: none !important; }
  .main-area { margin-left: 0 !important; }
}
@media (max-width: 768px) { .student-info-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
