<template>
  <div class="student-grades">
    <div class="page-header">
      <h2 class="page-title-text">成绩档案</h2>
      <p class="page-desc">查看个人学业成绩记录</p>
    </div>

    <div class="stats-grid" v-if="student">
      <div class="stat-card">
        <div class="stat-value">{{ student.studentNo }}</div>
        <div class="stat-label">学号</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ grades.length }}</div>
        <div class="stat-label">课程总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ avgScore }}</div>
        <div class="stat-label">平均成绩</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ avgGpa }}</div>
        <div class="stat-label">平均绩点</div>
      </div>
    </div>

    <!-- 学期筛选 -->
    <div class="card filter-bar" v-if="semesterList.length > 0">
      <select class="input" v-model="selectedSemester" style="width: 200px">
        <option value="">全部学期</option>
        <option v-for="s in semesterList" :key="s" :value="s">{{ s }}</option>
      </select>
    </div>

    <div class="card">
      <h3 class="card-title">成绩列表</h3>
      <div class="table-container" v-if="filteredGrades.length">
        <table class="data-table">
          <thead>
            <tr>
              <th>学期</th>
              <th>课程名称</th>
              <th>课程类型</th>
              <th>学分</th>
              <th>成绩</th>
              <th>绩点</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="g in filteredGrades" :key="g.pkGrade">
              <td class="text-caption">{{ g.semester }}</td>
              <td class="text-small-medium" style="color: var(--text-primary)">{{ g.courseName }}</td>
              <td>{{ g.courseType || '-' }}</td>
              <td>{{ g.credit ?? '-' }}</td>
              <td><span class="text-small-medium" :style="{ color: scoreColor(g.score) }">{{ g.score ?? '-' }}</span></td>
              <td>{{ g.gpa ?? '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="empty-state" v-else>
        <p class="empty-text">暂无成绩记录</p>
      </div>
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
const selectedSemester = ref('')

const semesterList = computed(() => {
  const set = new Set<string>()
  grades.value.forEach(g => { if (g.semester) set.add(g.semester) })
  return Array.from(set).sort().reverse()
})

const filteredGrades = computed(() => {
  if (!selectedSemester.value) return grades.value
  return grades.value.filter(g => g.semester === selectedSemester.value)
})

const avgScore = computed(() => {
  const scored = grades.value.filter(g => g.score !== null && g.score !== undefined)
  if (!scored.length) return '-'
  const avg = scored.reduce((sum, g) => sum + Number(g.score), 0) / scored.length
  return avg.toFixed(1)
})

const avgGpa = computed(() => {
  const gpaList = grades.value.filter(g => g.gpa !== null && g.gpa !== undefined)
  if (!gpaList.length) return '-'
  const avg = gpaList.reduce((sum, g) => sum + Number(g.gpa), 0) / gpaList.length
  return avg.toFixed(2)
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
    console.error('加载成绩档案失败', e)
  }
})

function scoreColor(score: number | null): string {
  if (score === null || score === undefined) return 'var(--text-secondary)'
  if (score >= 90) return 'var(--green-text)'
  if (score >= 60) return 'var(--text-primary)'
  return 'var(--red-text)'
}
</script>

<style scoped>
.student-grades { display: flex; flex-direction: column; gap: 20px; }
.page-header { padding: 8px 0; }
.page-title-text { font-size: 20px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.24px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card { background: var(--bg-panel); border: 1px solid var(--border-standard); border-radius: var(--radius-card); padding: 20px; box-shadow: var(--shadow-subtle); }
.stat-value { font-size: 24px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.704px; line-height: 1.2; }
.stat-label { font-size: 13px; font-weight: 510; color: var(--text-tertiary); margin-top: 4px; }
.filter-bar { display: flex; gap: 8px; align-items: center; }
.card-title { font-size: 14px; font-weight: 590; color: var(--text-primary); margin-bottom: 16px; letter-spacing: -0.182px; }
.empty-state { padding: 40px 24px; text-align: center; }
.empty-text { font-size: 14px; color: var(--text-quaternary); }
@media (max-width: 1024px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
