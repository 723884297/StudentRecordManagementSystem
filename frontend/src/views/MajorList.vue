<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索专业名称" @keyup.enter="loadData" />
        <select v-model="collegeFilter" class="form-select toolbar-input" @change="loadData">
          <option value="">全部学院</option>
          <option v-for="c in colleges" :key="c.pkCollege" :value="c.pkCollege">{{ c.collegeName }}</option>
        </select>
      </div>
      <button class="btn btn-primary btn-sm" @click="openAdd">新增专业</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>专业名称</th><th>专业代码</th><th>所属学院</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkMajor">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.majorName }}</td>
            <td>{{ item.majorCode || '-' }}</td>
            <td>{{ item.collegeName || '-' }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!filteredList.length"><td colspan="4" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}专业</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">专业名称</label><input v-model="form.majorName" class="form-input" placeholder="请输入专业名称" /></div>
          <div class="form-group"><label class="form-label">专业代码</label><input v-model="form.majorCode" class="form-input" placeholder="请输入专业代码" /></div>
          <div class="form-group"><label class="form-label">所属学院</label>
            <select v-model="form.collegeId" class="form-select">
              <option :value="null">请选择学院</option>
              <option v-for="c in colleges" :key="c.pkCollege" :value="c.pkCollege">{{ c.collegeName }}</option>
            </select>
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
import { ref, computed, onMounted } from 'vue'
import { majorApi, collegeApi } from '../api/modules'

const list = ref<any[]>([])
const colleges = ref<any[]>([])
const keyword = ref('')
const collegeFilter = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref<any>({})

const filteredList = computed(() => {
  let result = list.value
  if (keyword.value) result = result.filter(i => i.majorName.includes(keyword.value))
  if (collegeFilter.value) result = result.filter(i => i.collegeId == collegeFilter.value)
  return result
})

async function loadData() {
  try {
    const res: any = await majorApi.getList({ pageNum: 1, pageSize: 500 })
    list.value = res.data?.list || res.data || []
  } catch (e) { console.error('加载专业列表失败', e) }
}

async function loadColleges() {
  try {
    const res: any = await collegeApi.getAll()
    colleges.value = res.data || []
  } catch (e) { console.error('加载学院失败', e) }
}

function openAdd() {
  isEdit.value = false
  form.value = { majorName: '', majorCode: '', collegeId: null }
  showModal.value = true
}

function openEdit(item: any) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.majorName) { alert('请输入专业名称'); return }
  if (!form.value.collegeId) { alert('请选择所属学院'); return }
  saving.value = true
  try {
    if (isEdit.value) { await majorApi.update(form.value) }
    else { await majorApi.add(form.value) }
    showModal.value = false
    loadData()
  } catch (e: any) { alert(e.message || '保存失败') }
  finally { saving.value = false }
}

async function handleDelete(item: any) {
  if (!confirm(`确定删除专业 ${item.majorName}？`)) return
  try { await majorApi.delete(item.pkMajor); loadData() }
  catch (e: any) { alert(e.message || '删除失败') }
}

onMounted(() => { loadColleges(); loadData() })
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; }
.toolbar-input { width: 200px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
