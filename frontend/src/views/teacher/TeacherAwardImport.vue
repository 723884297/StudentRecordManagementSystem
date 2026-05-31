<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="page-title-text">奖惩导入</h2>
        <p class="page-desc">为学生录入奖励或处分记录，支持单条录入和批量导入</p>
      </div>
      <div class="flex gap-8">
        <button class="btn btn-secondary" @click="openBatchModal">批量导入</button>
        <button class="btn btn-primary" @click="openAddModal">单条录入</button>
      </div>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <button :class="['tab-btn', activeTab === 'award' && 'tab-active']" @click="activeTab = 'award'; loadData()">奖励管理</button>
      <button :class="['tab-btn', activeTab === 'punishment' && 'tab-active']" @click="activeTab = 'punishment'; loadData()">处分管理</button>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar card">
      <input class="form-input" v-model="filter.awardName" :placeholder="activeTab === 'award' ? '奖项名称' : '处分名称'" @keyup.enter="loadData" style="width: 160px" />
      <select class="form-input" v-model="filter.awardLevel" style="width: 140px">
        <option value="">全部级别</option>
        <option v-for="lv in currentLevels" :key="lv" :value="lv">{{ lv }}</option>
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
              <th>类型</th>
              <th>{{ activeTab === 'award' ? '奖项名称' : '处分名称' }}</th>
              <th>级别</th>
              <th>日期</th>
              <th>{{ activeTab === 'award' ? '颁发机构' : '处分机构' }}</th>
              <th>描述</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.pkAward">
              <td class="text-caption">{{ item.studentNo || '-' }}</td>
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentName || '-' }}</td>
              <td><span :class="['badge', item.type === 'punishment' ? 'badge-danger' : 'badge-success']">{{ item.type === 'punishment' ? '处分' : '奖励' }}</span></td>
              <td>{{ item.awardName }}</td>
              <td><span v-if="item.awardLevel" :class="['badge', item.type === 'punishment' ? 'badge-danger' : levelBadge(item.awardLevel)]">{{ item.awardLevel }}</span><span v-else>-</span></td>
              <td class="text-caption">{{ item.awardDate || '-' }}</td>
              <td>{{ item.issuingAuthority || '-' }}</td>
              <td class="text-caption">{{ item.description || '-' }}</td>
              <td>
                <div class="flex gap-4">
                  <button class="btn btn-subtle btn-sm" @click="openEditModal(item)">编辑</button>
                  <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="list.length === 0">
              <td colspan="9" class="empty-cell">暂无{{ activeTab === 'award' ? '奖励' : '处分' }}数据</td>
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
          <h3 class="modal-title">{{ editing ? '编辑记录' : ('录入' + (activeTab === 'award' ? '奖励' : '处分')) }}</h3>
          <button class="btn-icon" @click="showFormModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-grid">
            <div class="form-group" v-if="!editing">
              <label class="form-label">学号 *</label>
              <input class="form-input" v-model="form.studentNo" placeholder="如 20210001" required />
            </div>
            <div class="form-group">
              <label class="form-label">{{ activeTab === 'award' ? '奖项名称' : '处分名称' }} *</label>
              <input class="form-input" v-model="form.awardName" :placeholder="activeTab === 'award' ? '奖项名称' : '处分名称'" required />
            </div>
            <div class="form-group">
              <label class="form-label">{{ activeTab === 'award' ? '奖项级别' : '处分级别' }}</label>
              <select class="form-input" v-model="form.awardLevel">
                <option value="">请选择</option>
                <option v-for="lv in currentLevels" :key="lv" :value="lv">{{ lv }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">{{ activeTab === 'award' ? '获奖日期' : '处分日期' }}</label>
              <input class="form-input" type="date" v-model="form.awardDate" />
            </div>
            <div class="form-group">
              <label class="form-label">{{ activeTab === 'award' ? '颁发机构' : '处分机构' }}</label>
              <input class="form-input" v-model="form.issuingAuthority" :placeholder="activeTab === 'award' ? '颁发机构' : '处分机构'" />
            </div>
            <div class="form-group" style="grid-column: 1 / -1">
              <label class="form-label">描述</label>
              <input class="form-input" v-model="form.description" :placeholder="activeTab === 'award' ? '奖项描述' : '处分描述'" />
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
          <h3 class="modal-title">批量导入{{ activeTab === 'award' ? '奖励' : '处分' }}</h3>
          <button class="btn-icon" @click="showBatchModal = false">✕</button>
        </div>
        <div class="modal-body">
          <p class="text-caption" style="color: var(--text-tertiary)">
            请在下方文本框中粘贴{{ activeTab === 'award' ? '奖励' : '处分' }}数据，每行一条，字段用 Tab 或逗号分隔。<br/>
            格式：学号, {{ activeTab === 'award' ? '奖项名称, 奖项级别, 获奖日期' : '处分名称, 处分级别, 处分日期' }}(YYYY-MM-DD), {{ activeTab === 'award' ? '颁发机构' : '处分机构' }}, 描述
          </p>
          <textarea class="form-input" v-model="batchText" rows="10" style="font-family: var(--font-mono); font-size: 12px" :placeholder="activeTab === 'award' ? '20210001\t全国数学竞赛一等奖\t国家级\t2024-06-15\t教育部\t\n20210002\t优秀学生干部\t校级\t2024-09-01\t学校' : '20210001\t考试作弊\t警告\t2024-06-15\t学校\t\n20210002\t旷课严重\t严重警告\t2024-09-01\t学院'"></textarea>
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
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { awardApi, studentApi } from '../../api/modules'

const userStore = useUserStore()
const list = ref<any[]>([])
const students = ref<any[]>([])
const awardLevels = ['国家级', '省级', '市级', '校级', '院级']
const punishmentLevels = ['警告', '严重警告', '记过', '留校察看', '开除学籍']
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const filter = ref({ awardName: '', awardLevel: '', studentId: '' })
const activeTab = ref<'award' | 'punishment'>('award')

const currentLevels = computed(() => activeTab.value === 'award' ? awardLevels : punishmentLevels)

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
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value, type: activeTab.value }
    if (filter.value.studentId) params.studentId = filter.value.studentId
    if (filter.value.awardLevel) params.awardLevel = filter.value.awardLevel
    if (filter.value.awardName) params.awardName = filter.value.awardName
    const res: any = await awardApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

function openAddModal() {
  editing.value = false
  form.value = { studentNo: '', awardName: '', awardLevel: '', awardDate: '', issuingAuthority: '', description: '' }
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
  if (!form.value.awardName) { alert(activeTab.value === 'award' ? '请填写奖项名称' : '请填写处分名称'); return }
  try {
    const data = { ...form.value, type: activeTab.value, importUserId: userStore.userInfo?.userId }
    if (editing.value) {
      await awardApi.update(data)
    } else {
      if (!data.studentNo) { alert('请输入学号'); return }
      const studentMap = new Map(students.value.map((s: any) => [s.studentNo, s.pkStudent]))
      const sid = studentMap.get(data.studentNo)
      if (!sid) { alert(`学号 ${data.studentNo} 未找到`); return }
      data.studentId = sid
      delete data.studentNo
      await awardApi.add(data)
    }
    showFormModal.value = false
    await loadData()
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  }
}

async function handleDelete(item: any) {
  if (!confirm(`确认删除 ${item.studentName || ''} 的 ${item.awardName}？`)) return
  try {
    await awardApi.delete(item.pkAward)
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
  const awards: any[] = []

  for (let i = 0; i < lines.length; i++) {
    const cols = lines[i].split(/[,\t]/).map(c => c.trim())
    if (cols.length < 2) {
      batchErrors.value.push(`第 ${i + 1} 行：字段不足（至少需要学号、${activeTab.value === 'award' ? '奖项' : '处分'}名称）`)
      continue
    }
    const sid = studentMap.get(cols[0])
    if (!sid) {
      batchErrors.value.push(`第 ${i + 1} 行：学号 ${cols[0]} 未找到`)
      continue
    }
    awards.push({
      studentId: sid, type: activeTab.value, awardName: cols[1], awardLevel: cols[2] || null,
      awardDate: cols[3] || null, issuingAuthority: cols[4] || null,
      description: cols[5] || null, importUserId: userStore.userInfo?.userId
    })
  }

  if (batchErrors.value.length) return
  if (!awards.length) { batchErrors.value = ['无有效数据']; return }

  try {
    await awardApi.batchAdd(awards)
    showBatchModal.value = false
    await loadData()
  } catch (e: any) {
    alert(e.response?.data?.message || '批量导入失败')
  }
}

function levelBadge(level: string): string {
  switch (level) {
    case '国家级': return 'badge-danger'
    case '省级': return 'badge-warning'
    case '市级': return 'badge-info'
    case '校级': return 'badge-success'
    default: return 'badge-neutral'
  }
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
.tab-bar { display: flex; gap: 4px; background: var(--surface-02); padding: 3px; border-radius: var(--radius-md); width: fit-content; }
.tab-btn { padding: 6px 16px; border-radius: var(--radius-md); font-size: 13px; font-weight: 510; color: var(--text-tertiary); background: none; border: none; cursor: pointer; transition: all 0.15s; }
.tab-btn:hover { color: var(--text-secondary); }
.tab-active { background: var(--bg-surface); color: var(--text-primary); box-shadow: var(--shadow-subtle); }
</style>
