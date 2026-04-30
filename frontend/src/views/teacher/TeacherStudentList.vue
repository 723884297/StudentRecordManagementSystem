<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <input v-model="filters.studentNo" class="form-input toolbar-input" placeholder="学号" @keyup.enter="loadData" />
        <input v-model="filters.name" class="form-input toolbar-input" placeholder="姓名" @keyup.enter="loadData" />
        <select v-model="filters.status" class="form-select toolbar-input">
          <option value="">全部状态</option>
          <option value="在读">在读</option>
          <option value="休学">休学</option>
          <option value="退学">退学</option>
          <option value="毕业">毕业</option>
        </select>
        <button class="btn btn-subtle btn-sm" @click="loadData">查询</button>
      </div>
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
            <td><span :class="['badge', statusBadge(item.status)]">{{ item.status }}</span></td>
            <td>{{ item.enrollmentYear || '-' }}</td>
            <td>
              <button class="btn btn-subtle btn-sm" @click="viewDetail(item)">查看详情</button>
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

    <!-- 学生详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal-content" style="max-width: 600px">
        <div class="modal-header">
          <h3 class="modal-title">学生详情 - {{ currentStudent?.name }}</h3>
          <button class="btn-icon" @click="showDetail = false">✕</button>
        </div>
        <div class="modal-body" v-if="currentStudent">
          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item"><span class="info-label">学号</span><span class="info-value">{{ currentStudent.studentNo }}</span></div>
              <div class="info-item"><span class="info-label">姓名</span><span class="info-value">{{ currentStudent.name }}</span></div>
              <div class="info-item"><span class="info-label">性别</span><span class="info-value">{{ currentStudent.gender === 'M' ? '男' : '女' }}</span></div>
              <div class="info-item"><span class="info-label">出生日期</span><span class="info-value">{{ currentStudent.birthDate || '-' }}</span></div>
              <div class="info-item"><span class="info-label">学院</span><span class="info-value">{{ currentStudent.collegeName || '-' }}</span></div>
              <div class="info-item"><span class="info-label">专业</span><span class="info-value">{{ currentStudent.majorName || '-' }}</span></div>
              <div class="info-item"><span class="info-label">班级</span><span class="info-value">{{ currentStudent.className || '-' }}</span></div>
              <div class="info-item"><span class="info-label">入学年份</span><span class="info-value">{{ currentStudent.enrollmentYear || '-' }}</span></div>
            </div>
          </div>
          <div class="detail-section" v-if="currentDetail">
            <h4 class="section-title">联系信息</h4>
            <div class="info-grid">
              <div class="info-item"><span class="info-label">联系电话</span><span class="info-value">{{ currentDetail.phone || '-' }}</span></div>
              <div class="info-item"><span class="info-label">电子邮箱</span><span class="info-value">{{ currentDetail.email || '-' }}</span></div>
              <div class="info-item"><span class="info-label">民族</span><span class="info-value">{{ currentDetail.nation || '-' }}</span></div>
              <div class="info-item"><span class="info-label">政治面貌</span><span class="info-value">{{ currentDetail.politicalStatus || '-' }}</span></div>
              <div class="info-item" style="grid-column:span 2"><span class="info-label">家庭住址</span><span class="info-value">{{ currentDetail.homeAddress || '-' }}</span></div>
              <div class="info-item"><span class="info-label">紧急联系人</span><span class="info-value">{{ currentDetail.emergencyContact || '-' }}</span></div>
              <div class="info-item"><span class="info-label">紧急联系电话</span><span class="info-value">{{ currentDetail.emergencyPhone || '-' }}</span></div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showDetail = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { studentApi } from '../../api/modules'

const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filters = reactive({ studentNo: '', name: '', status: '' })
const showDetail = ref(false)
const currentStudent = ref<any>(null)
const currentDetail = ref<any>(null)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function statusBadge(status: string) {
  const map: Record<string, string> = { '在读': 'badge-success', '休学': 'badge-warning', '退学': 'badge-danger', '毕业': 'badge-info' }
  return map[status] || 'badge-neutral'
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filters.studentNo) params.studentNo = filters.studentNo
    if (filters.name) params.name = filters.name
    if (filters.status) params.status = filters.status
    const res: any = await studentApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error('加载学生列表失败', e) }
}

async function viewDetail(item: any) {
  currentStudent.value = item
  currentDetail.value = null
  showDetail.value = true
  try {
    const res: any = await studentApi.getDetail(item.pkStudent)
    currentDetail.value = res.data || null
  } catch (e) { console.error('加载详情失败', e) }
}

onMounted(loadData)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.toolbar-input { width: 140px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
.detail-section { margin-bottom: 20px; }
.detail-section:last-child { margin-bottom: 0; }
.section-title { font-size: 13px; font-weight: 590; color: var(--text-primary); margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid var(--border-subtle); }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.info-item { display: flex; flex-direction: column; gap: 2px; }
.info-label { font-size: 12px; font-weight: 510; color: var(--text-quaternary); }
.info-value { font-size: 14px; font-weight: 510; color: var(--text-secondary); }
</style>
