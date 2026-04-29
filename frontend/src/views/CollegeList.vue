<template>
  <div class="page">
    <div class="page-toolbar">
      <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索学院名称" @keyup.enter="loadData" />
      <button class="btn btn-primary btn-sm" @click="openAdd">新增学院</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>学院名称</th><th>学院代码</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkCollege">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.collegeName }}</td>
            <td>{{ item.collegeCode || '-' }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!filteredList.length"><td colspan="3" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}学院</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">学院名称</label><input v-model="form.collegeName" class="form-input" placeholder="请输入学院名称" /></div>
          <div class="form-group"><label class="form-label">学院代码</label><input v-model="form.collegeCode" class="form-input" placeholder="请输入学院代码" /></div>
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
import { collegeApi } from '../api/modules'

const list = ref<any[]>([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref<any>({})

const filteredList = computed(() => {
  if (!keyword.value) return list.value
  return list.value.filter(i => i.collegeName.includes(keyword.value))
})

async function loadData() {
  try {
    const res: any = await collegeApi.getAll()
    list.value = res.data || []
  } catch (e) {
    console.error('加载学院列表失败', e)
  }
}

function openAdd() {
  isEdit.value = false
  form.value = { collegeName: '', collegeCode: '' }
  showModal.value = true
}

function openEdit(item: any) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.collegeName) { alert('请输入学院名称'); return }
  saving.value = true
  try {
    if (isEdit.value) {
      await collegeApi.update(form.value)
    } else {
      await collegeApi.add(form.value)
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
  if (!confirm(`确定删除学院 ${item.collegeName}？`)) return
  try {
    await collegeApi.delete(item.pkCollege)
    loadData()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

onMounted(loadData)
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 240px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
