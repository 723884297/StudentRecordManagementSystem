<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="page-title-text">成绩导入</h2>
        <p class="page-desc">为学生录入课程成绩，支持单条录入和批量导入</p>
      </div>
      <div class="flex gap-8">
        <button class="btn btn-secondary" @click="openBatchModal">批量导入</button>
        <button class="btn btn-primary" @click="openAddModal">录入成绩</button>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar card">
      <input class="form-input" v-model="filter.courseName" placeholder="课程名称" @keyup.enter="loadData" style="width: 160px" />
      <select class="form-input" v-model="filter.semester" style="width: 160px">
        <option value="">全部学期</option>
        <option v-for="s in semesters" :key="s" :value="s">{{ s }}</option>
      </select>
      <select class="form-input" v-model="filter.studentId" style="width: 180px">
        <option value="">全部学生</option>
        <option v-for="s in students" :key="s.pkStudent" :value="s.pkStudent">{{ s.studentNo }} - {{ s.name }}</option>
      </select>
      <button class="btn btn-primary" @click="loadData">查询</button>
    </div>

    <!-- 表格 -->
    <div class="card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>学号</th>
              <th>姓名</th>
              <th>学期</th>
              <th>课程名称</th>
              <th>课程类型</th>
              <th>学分</th>
              <th>成绩</th>
              <th>绩点</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.pkGrade">
              <td class="text-caption">{{ item.studentNo || '-' }}</td>
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentName || '-' }}</td>
              <td>{{ item.semester }}</td>
              <td>{{ item.courseName }}</td>
              <td>{{ item.courseType || '-' }}</td>
              <td>{{ item.credit ?? '-' }}</td>
              <td><span class="text-small-medium" :style="{ color: scoreColor(item.score) }">{{ item.score ?? '-' }}</span></td>
              <td>{{ item.gpa ?? '-' }}</td>
              <td>
                <div class="flex gap-4">
                  <button class="btn btn-subtle btn-sm" @click="openEditModal(item)">编辑</button>
                  <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="list.length === 0">
              <td colspan="9" class="empty-cell">暂无成绩数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pagination" v-if="total > pageSize">
        <button class="btn btn-subtle btn-sm" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <span class="pagination-info">第 {{ pageNum }} / {{ Math.ceil(total / pageSize) }} 页，共 {{ total }} 条</span>
        <button class="btn btn-subtle btn-sm" :disabled="pageNum >= Math.ceil(total / pageSize)" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>

    <!-- 单条录入/编辑弹窗 -->
    <div class="modal-overlay" v-if="showFormModal" @click.self="showFormModal = false">
      <div class="modal-content" style="max-width: 520px">
        <div class="modal-header">
          <h3 class="modal-title">{{ editing ? '编辑成绩' : '录入成绩' }}</h3>
          <button class="btn-icon" @click="showFormModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-grid">
            <div class="form-group" v-if="!editing">
              <label class="form-label">学号 *</label>
              <input class="form-input" v-model="form.studentNo" placeholder="如 20210001" required />
            </div>
            <div class="form-group">
              <label class="form-label">学期 *</label>
              <input class="form-input" v-model="form.semester" placeholder="如 2024-2025-1" required />
            </div>
            <div class="form-group">
              <label class="form-label">课程名称 *</label>
              <input class="form-input" v-model="form.courseName" placeholder="课程名称" required />
            </div>
            <div class="form-group">
              <label class="form-label">课程类型</label>
              <select class="form-input" v-model="form.courseType">
                <option value="">请选择</option>
                <option value="必修">必修</option>
                <option value="选修">选修</option>
                <option value="公共">公共</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">学分</label>
              <input class="form-input" type="number" step="0.5" v-model="form.credit" placeholder="学分" />
            </div>
            <div class="form-group">
              <label class="form-label">成绩 *</label>
              <input class="form-input" type="number" step="0.1" v-model="form.score" placeholder="成绩" required />
            </div>
            <div class="form-group">
              <label class="form-label">绩点</label>
              <input class="form-input" type="number" step="0.01" v-model="form.gpa" placeholder="绩点（可自动计算）" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showFormModal = false">取消</button>
          <button class="btn btn-primary btn-sm" @click="handleSave">保存</button>
        </div>
      </div>
    </div>

    <!-- 批量导入弹窗 -->
    <div class="modal-overlay" v-if="showBatchModal" @click.self="showBatchModal = false">
      <div class="modal-content" style="max-width: 700px">
        <div class="modal-header">
          <h3 class="modal-title">批量导入成绩</h3>
          <button class="btn-icon" @click="showBatchModal = false">✕</button>
        </div>
        <div class="modal-body">
          <p class="text-caption" style="color: var(--text-tertiary)">
            请在下方文本框中粘贴成绩数据，每行一条，字段用 Tab 或逗号分隔。<br/>
            格式：学号, 学期, 课程名称, 课程类型, 学分, 成绩, 绩点
          </p>
          <textarea class="form-input" v-model="batchText" rows="10" style="font-family: var(--font-mono); font-size: 12px" placeholder="20210001	2024-2025-1	高等数学	必修	4.0	92	3.8&#10;20210002	2024-2025-1	高等数学	必修	4.0	85	3.3"></textarea>
          <div v-if="batchErrors.length" class="batch-errors">
            <p v-for="(err, i) in batchErrors" :key="i" class="text-caption" style="color: var(--status-red)">{{ err }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showBatchModal = false">取消</button>
          <button class="btn btn-primary btn-sm" @click="handleBatchImport">导入</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { gradeApi, studentApi } from '../../api/modules'

const userStore = useUserStore()
const list = ref<any[]>([])
const students = ref<any[]>([])
const semesters = ref<string[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const filter = ref({ courseName: '', semester: '', studentId: '' })

const showFormModal = ref(false)
const showBatchModal = ref(false)
const editing = ref(false)
const form = ref<any>({})
const batchText = ref('')
const batchErrors = ref<string[]>([])

onMounted(async () => {
  await loadStudents()
  await loadData()
})

async function loadStudents() {
  try {
    const res: any = await studentApi.getList({ pageNum: 1, pageSize: 1000 })
    students.value = res.data?.list || []
  } catch (e) { console.error(e) }
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filter.value.studentId) params.studentId = filter.value.studentId
    if (filter.value.semester) params.semester = filter.value.semester
    if (filter.value.courseName) params.courseName = filter.value.courseName
    const res: any = await gradeApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
    // 收集学期列表
    const semSet = new Set<string>(semesters.value)
    list.value.forEach((g: any) => { if (g.semester) semSet.add(g.semester) })
    semesters.value = Array.from(semSet).sort().reverse()
  } catch (e) { console.error(e) }
}

function openAddModal() {
  editing.value = false
  form.value = { studentNo: '', semester: '', courseName: '', courseType: '', credit: null, score: null, gpa: null }
  showFormModal.value = true
}

function openEditModal(item: any) {
  editing.value = true
  form.value = { ...item }
  showFormModal.value = true
}

function openBatchModal() {
  batchText.value = ''
  batchErrors.value = []
  showBatchModal.value = true
}

async function handleSave() {
  if (!form.value.semester || !form.value.courseName || form.value.score === null) {
    alert('请填写必填字段')
    return
  }
  try {
    const data = { ...form.value, importUserId: userStore.userInfo?.userId }
    if (editing.value) {
      await gradeApi.update(data)
    } else {
      if (!data.studentNo) { alert('请输入学号'); return }
      const studentMap = new Map(students.value.map((s: any) => [s.studentNo, s.pkStudent]))
      const sid = studentMap.get(data.studentNo)
      if (!sid) { alert(`学号 ${data.studentNo} 未找到`); return }
      data.studentId = sid
      delete data.studentNo
      await gradeApi.add(data)
    }
    showFormModal.value = false
    await loadData()
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  }
}

async function handleDelete(item: any) {
  if (!confirm(`确认删除 ${item.studentName || ''} 的 ${item.courseName} 成绩？`)) return
  try {
    await gradeApi.delete(item.pkGrade)
    await loadData()
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}

async function handleBatchImport() {
  batchErrors.value = []
  const lines = batchText.value.trim().split('\n').filter(l => l.trim())
  if (!lines.length) { batchErrors.value = ['请输入数据']; return }

  const studentMap = new Map(students.value.map((s: any) => [s.studentNo, s.pkStudent]))
  const grades: any[] = []

  for (let i = 0; i < lines.length; i++) {
    const cols = lines[i].split(/[,\t]/).map(c => c.trim())
    if (cols.length < 6) {
      batchErrors.value.push(`第 ${i + 1} 行：字段不足（至少需要学号、学期、课程、类型、学分、成绩）`)
      continue
    }
    const sid = studentMap.get(cols[0])
    if (!sid) {
      batchErrors.value.push(`第 ${i + 1} 行：学号 ${cols[0]} 未找到`)
      continue
    }
    grades.push({
      studentId: sid, semester: cols[1], courseName: cols[2],
      courseType: cols[3] || null, credit: cols[4] ? parseFloat(cols[4]) : null,
      score: parseFloat(cols[5]), gpa: cols[6] ? parseFloat(cols[6]) : null,
      importUserId: userStore.userInfo?.userId
    })
  }

  if (batchErrors.value.length) return
  if (!grades.length) { batchErrors.value = ['无有效数据']; return }

  try {
    await gradeApi.batchAdd(grades)
    showBatchModal.value = false
    await loadData()
  } catch (e: any) {
    alert(e.response?.data?.message || '批量导入失败')
  }
}

function scoreColor(score: number | null): string {
  if (score === null || score === undefined) return 'var(--text-secondary)'
  if (score >= 90) return 'var(--green-text)'
  if (score >= 60) return 'var(--text-primary)'
  return 'var(--red-text)'
}
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 8px 0; }
.page-title-text { font-size: 20px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.24px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
.filter-bar { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-group { display: flex; flex-direction: column; gap: 4px; }
.form-label { font-size: 13px; font-weight: 510; color: var(--text-secondary); }
.pagination { display: flex; align-items: center; justify-content: center; gap: 12px; padding: 12px 0 0; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); }
.batch-errors { margin-top: 8px; padding: 8px; background: var(--red-bg); border-radius: var(--radius-md); }
</style>
