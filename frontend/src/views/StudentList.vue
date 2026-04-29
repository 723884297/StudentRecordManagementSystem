<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <input v-model="filters.studentNo" class="form-input toolbar-input" placeholder="学号" @keyup.enter="loadData" />
        <input v-model="filters.name" class="form-input toolbar-input" placeholder="姓名" @keyup.enter="loadData" />
        <select v-model="filters.collegeId" class="form-select toolbar-input" @change="onCollegeChange">
          <option value="">全部学院</option>
          <option v-for="c in colleges" :key="c.pkCollege" :value="c.pkCollege">{{ c.collegeName }}</option>
        </select>
        <select v-model="filters.majorId" class="form-select toolbar-input">
          <option value="">全部专业</option>
          <option v-for="m in filteredMajors" :key="m.pkMajor" :value="m.pkMajor">{{ m.majorName }}</option>
        </select>
        <select v-model="filters.status" class="form-select toolbar-input">
          <option value="">全部状态</option>
          <option value="在读">在读</option>
          <option value="休学">休学</option>
          <option value="退学">退学</option>
          <option value="毕业">毕业</option>
        </select>
        <button class="btn btn-subtle btn-sm" @click="loadData">查询</button>
      </div>
      <button class="btn btn-primary btn-sm" @click="openAdd">新增学生</button>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>性别</th>
            <th>学院</th>
            <th>专业</th>
            <th>班级</th>
            <th>状态</th>
            <th>入学年份</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkStudent">
            <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentNo }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.gender === 'M' ? '男' : item.gender === 'F' ? '女' : '-' }}</td>
            <td>{{ item.collegeName || '-' }}</td>
            <td>{{ item.majorName || '-' }}</td>
            <td>{{ item.className || '-' }}</td>
            <td>
              <span :class="['badge', statusBadge(item.status)]">{{ item.status }}</span>
            </td>
            <td>{{ item.enrollmentYear || '-' }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="list.length === 0">
            <td colspan="9" class="empty-cell">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="total > 0">
      <span class="text-caption">共 {{ total }} 条</span>
      <div class="pagination-pages">
        <button class="pagination-btn" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <span class="pagination-info">{{ pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pageNum >= totalPages" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content" style="max-width: 600px">
        <div class="modal-header">
          <h3 class="modal-title">{{ isEdit ? '编辑学生' : '新增学生' }}</h3>
          <button class="btn-icon" @click="showModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">学号 <span class="required">*</span></label>
            <input v-model="form.studentNo" class="form-input" placeholder="请输入学号" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">姓名 <span class="required">*</span></label>
              <input v-model="form.name" class="form-input" placeholder="请输入姓名" />
            </div>
            <div class="form-group">
              <label class="form-label">性别</label>
              <select v-model="form.gender" class="form-select">
                <option value="M">男</option>
                <option value="F">女</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">出生日期</label>
              <input v-model="form.birthDate" type="date" class="form-input" />
            </div>
            <div class="form-group">
              <label class="form-label">身份证号</label>
              <input v-model="form.idCard" class="form-input" placeholder="请输入身份证号" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">学院</label>
              <select v-model="form.collegeId" class="form-select" @change="onFormCollegeChange">
                <option :value="null">请选择学院</option>
                <option v-for="c in colleges" :key="c.pkCollege" :value="c.pkCollege">{{ c.collegeName }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">专业</label>
              <select v-model="form.majorId" class="form-select" @change="onFormMajorChange">
                <option :value="null">请选择专业</option>
                <option v-for="m in formMajors" :key="m.pkMajor" :value="m.pkMajor">{{ m.majorName }}</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">班级</label>
              <select v-model="form.classId" class="form-select">
                <option :value="null">请选择班级</option>
                <option v-for="c in formClasses" :key="c.pkClass" :value="c.pkClass">{{ c.className }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">入学年份</label>
              <input v-model="form.enrollmentYear" type="number" class="form-input" placeholder="如 2024" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">学制(年)</label>
              <input v-model="form.schoolLength" type="number" class="form-input" placeholder="如 4" />
            </div>
            <div class="form-group">
              <label class="form-label">学籍状态</label>
              <select v-model="form.status" class="form-select">
                <option value="在读">在读</option>
                <option value="休学">休学</option>
                <option value="退学">退学</option>
                <option value="毕业">毕业</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button>
          <button class="btn btn-primary btn-sm" @click="handleSave" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { studentApi, collegeApi, majorApi, classApi } from '../api/modules'

const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showModal = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const filters = reactive({ studentNo: '', name: '', collegeId: '' as any, majorId: '' as any, status: '' })
const form = reactive<any>({})

// 下拉选项
const colleges = ref<any[]>([])
const allMajors = ref<any[]>([])
const allClasses = ref<any[]>([])

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const filteredMajors = computed(() => {
  if (!filters.collegeId) return allMajors.value
  return allMajors.value.filter(m => m.collegeId == filters.collegeId)
})
const formMajors = computed(() => {
  if (!form.collegeId) return allMajors.value
  return allMajors.value.filter(m => m.collegeId == form.collegeId)
})
const formClasses = computed(() => {
  if (!form.majorId) return allClasses.value
  return allClasses.value.filter(c => c.majorId == form.majorId)
})

function statusBadge(status: string) {
  const map: Record<string, string> = { '在读': 'badge-success', '休学': 'badge-warning', '退学': 'badge-danger', '毕业': 'badge-info' }
  return map[status] || 'badge-neutral'
}

async function loadOptions() {
  try {
    const [cRes, mRes, clRes]: any[] = await Promise.all([
      collegeApi.getAll(),
      majorApi.getList({ pageNum: 1, pageSize: 500 }),
      classApi.getList({ pageNum: 1, pageSize: 500 })
    ])
    colleges.value = cRes.data || []
    allMajors.value = mRes.data?.list || mRes.data || []
    allClasses.value = clRes.data?.list || clRes.data || []
  } catch (e) {
    console.error('加载选项数据失败', e)
  }
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filters.studentNo) params.studentNo = filters.studentNo
    if (filters.name) params.name = filters.name
    if (filters.collegeId) params.collegeId = filters.collegeId
    if (filters.majorId) params.majorId = filters.majorId
    if (filters.status) params.status = filters.status
    const res: any = await studentApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载学生列表失败', e)
  }
}

function onCollegeChange() {
  filters.majorId = ''
  loadData()
}

function onFormCollegeChange() {
  form.majorId = null
  form.classId = null
}

function onFormMajorChange() {
  form.classId = null
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    pkStudent: null, studentNo: '', name: '', gender: 'M', birthDate: '', idCard: '',
    collegeId: null, majorId: null, classId: null,
    enrollmentYear: new Date().getFullYear(), schoolLength: 4, status: '在读'
  })
  showModal.value = true
}

function openEdit(item: any) {
  isEdit.value = true
  Object.assign(form, { ...item })
  showModal.value = true
}

async function handleSave() {
  if (!form.studentNo || !form.name) {
    alert('请填写学号和姓名')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await studentApi.update(form)
    } else {
      await studentApi.add(form)
    }
    showModal.value = false
    loadData()
  } catch (e: any) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(item: any) {
  if (!confirm(`确定删除学生 ${item.name}？`)) return
  try {
    await studentApi.delete(item.pkStudent)
    loadData()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

onMounted(() => {
  loadOptions()
  loadData()
})
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.toolbar-input { width: 140px; padding: 7px 12px; font-size: 13px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
.required { color: #ef4444; }
</style>
