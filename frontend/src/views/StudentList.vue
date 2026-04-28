<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <input v-model="filters.studentNo" class="form-input toolbar-input" placeholder="学号" />
        <input v-model="filters.name" class="form-input toolbar-input" placeholder="姓名" />
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
            <td>{{ item.gender === 'M' ? '男' : '女' }}</td>
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
      <span>共 {{ total }} 条</span>
      <div class="pagination-pages">
        <button class="pagination-btn" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <button class="pagination-btn active">{{ pageNum }}</button>
        <button class="pagination-btn" :disabled="pageNum * pageSize >= total" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">{{ isEdit ? '编辑学生' : '新增学生' }}</h3>
          <button class="btn-icon" @click="showModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">学号</label>
            <input v-model="form.studentNo" class="form-input" placeholder="请输入学号" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">姓名</label>
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
              <label class="form-label">入学年份</label>
              <input v-model="form.enrollmentYear" type="number" class="form-input" placeholder="如 2024" />
            </div>
            <div class="form-group">
              <label class="form-label">学制(年)</label>
              <input v-model="form.schoolLength" type="number" class="form-input" placeholder="如 4" />
            </div>
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
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button>
          <button class="btn btn-primary btn-sm" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showModal = ref(false)
const isEdit = ref(false)
const filters = reactive({ studentNo: '', name: '', status: '' })
const form = reactive<any>({})

function statusBadge(status: string) {
  const map: Record<string, string> = { '在读': 'badge-success', '休学': 'badge-warning', '退学': 'badge-danger', '毕业': 'badge-info' }
  return map[status] || 'badge-neutral'
}

function loadData() {
  // Mock data
  list.value = [
    { pkStudent: 1, studentNo: '2024001', name: '张三', gender: 'M', collegeName: '计算机学院', majorName: '软件工程', className: '软工2024-1', status: '在读', enrollmentYear: 2024 },
    { pkStudent: 2, studentNo: '2024002', name: '李四', gender: 'F', collegeName: '计算机学院', majorName: '计算机科学', className: '计科2024-2', status: '在读', enrollmentYear: 2024 },
    { pkStudent: 3, studentNo: '2023015', name: '王五', gender: 'M', collegeName: '文学院', majorName: '汉语言文学', className: '中文2023-1', status: '休学', enrollmentYear: 2023 },
    { pkStudent: 4, studentNo: '2020088', name: '赵六', gender: 'F', collegeName: '理学院', majorName: '数学', className: '数学2020-1', status: '毕业', enrollmentYear: 2020 },
  ]
  total.value = 4
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { studentNo: '', name: '', gender: 'M', birthDate: '', idCard: '', enrollmentYear: '', schoolLength: 4, status: '在读' })
  showModal.value = true
}

function openEdit(item: any) {
  isEdit.value = true
  Object.assign(form, { ...item })
  showModal.value = true
}

function handleSave() {
  showModal.value = false
  loadData()
}

function handleDelete(item: any) {
  if (confirm(`确定删除学生 ${item.name}？`)) {
    loadData()
  }
}

onMounted(loadData)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; }
.toolbar-input { width: 160px; padding: 7px 12px; font-size: 13px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
