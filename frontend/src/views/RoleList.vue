<template>
  <div class="page">
    <div class="page-toolbar">
      <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索角色名称" />
      <button class="btn btn-primary btn-sm" @click="openAdd">新增角色</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>角色名称</th><th>角色标识</th><th>描述</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkRole">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.roleName }}</td>
            <td><span class="badge badge-neutral" style="font-family:var(--font-mono);font-size:12px">{{ item.roleCode }}</span></td>
            <td>{{ item.description || '-' }}</td>
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
    <!-- 编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}角色</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">角色名称</label><input v-model="form.roleName" class="form-input" placeholder="如：教务管理员" /></div>
          <div class="form-group"><label class="form-label">角色标识</label><input v-model="form.roleCode" class="form-input" placeholder="如：admin" /></div>
          <div class="form-group"><label class="form-label">描述</label><textarea v-model="form.description" class="form-input" rows="3" placeholder="请输入角色描述"></textarea></div>
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
import { roleApi } from '../api/modules'

const list = ref<any[]>([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref<any>({})

const filteredList = computed(() => {
  if (!keyword.value) return list.value
  return list.value.filter(i => i.roleName.includes(keyword.value))
})

async function loadData() {
  try {
    const res: any = await roleApi.getList()
    list.value = res.data || []
  } catch (e) { console.error('加载角色列表失败', e) }
}

function openAdd() {
  isEdit.value = false
  form.value = { roleName: '', roleCode: '', description: '' }
  showModal.value = true
}

function openEdit(item: any) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.roleName || !form.value.roleCode) { alert('请填写角色名称和标识'); return }
  saving.value = true
  try {
    if (isEdit.value) { await roleApi.update(form.value) }
    else { await roleApi.add(form.value) }
    showModal.value = false
    loadData()
  } catch (e: any) { alert(e.message || '保存失败') }
  finally { saving.value = false }
}

async function handleDelete(item: any) {
  if (!confirm(`确定删除角色 ${item.roleName}？`)) return
  try { await roleApi.delete(item.pkRole); loadData() }
  catch (e: any) { alert(e.message || '删除失败') }
}

onMounted(loadData)
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 240px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
