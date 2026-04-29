<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="flex gap-8">
        <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索用户名/姓名" @keyup.enter="loadData" />
        <select v-model="statusFilter" class="form-select" style="width:140px;padding:7px 12px;font-size:13px" @change="loadData">
          <option value="">全部状态</option>
          <option value="1">启用</option>
          <option value="0">禁用</option>
        </select>
      </div>
      <button class="btn btn-primary btn-sm" @click="openAdd">新增用户</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>用户名</th><th>姓名</th><th>角色</th><th>状态</th><th>创建时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkUser">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.username }}</td>
            <td>{{ item.realName }}</td>
            <td>
              <span v-for="(r, idx) in (item.roleNames || [])" :key="idx" class="badge badge-neutral" style="margin-right:4px">{{ r }}</span>
              <span v-if="!item.roleNames?.length">-</span>
            </td>
            <td>
              <span :class="['badge', item.status === 1 ? 'badge-success' : 'badge-danger']">{{ item.status === 1 ? '启用' : '禁用' }}</span>
            </td>
            <td class="text-caption">{{ formatDate(item.createTime) }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-subtle btn-sm" @click="toggleStatus(item)">{{ item.status === 1 ? '禁用' : '启用' }}</button>
                <button class="btn btn-subtle btn-sm" @click="resetPwd(item)">重置密码</button>
                <button class="btn btn-danger btn-sm" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length"><td colspan="6" class="empty-cell">暂无数据</td></tr>
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
    <!-- 新增/编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content" style="max-width:520px">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}用户</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">用户名</label><input v-model="form.username" class="form-input" placeholder="请输入用户名" :disabled="isEdit" /></div>
          <div class="form-group"><label class="form-label">姓名</label><input v-model="form.realName" class="form-input" placeholder="请输入姓名" /></div>
          <div v-if="!isEdit" class="form-group"><label class="form-label">密码</label><input v-model="form.password" type="password" class="form-input" placeholder="请输入密码" /></div>
          <div class="form-group"><label class="form-label">角色</label>
            <div class="role-checkboxes">
              <label v-for="role in allRoles" :key="role.pkRole" class="role-checkbox-label">
                <input type="checkbox" :value="role.pkRole" v-model="form.selectedRoleIds" class="role-checkbox" />
                <span>{{ role.roleName }}</span>
              </label>
            </div>
          </div>
          <div class="form-group"><label class="form-label">状态</label>
            <select v-model="form.status" class="form-select">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
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
import { userApi, roleApi } from '../api/modules'

const list = ref<any[]>([])
const allRoles = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref<any>({})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function loadRoles() {
  try {
    const res: any = await roleApi.getList()
    allRoles.value = res.data || []
  } catch (e) { console.error('加载角色失败', e) }
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    if (statusFilter.value !== '') params.status = Number(statusFilter.value)
    const res: any = await userApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error('加载用户列表失败', e) }
}

function openAdd() {
  isEdit.value = false
  form.value = { username: '', realName: '', password: '', selectedRoleIds: [], status: 1 }
  showModal.value = true
}

async function openEdit(item: any) {
  isEdit.value = true
  // 加载用户的角色
  let userRoleIds: number[] = []
  try {
    const res: any = await roleApi.getByUserId(item.pkUser)
    userRoleIds = (res.data || []).map((r: any) => r.pkRole)
  } catch (e) { /* ignore */ }
  form.value = { ...item, selectedRoleIds: userRoleIds }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.username || !form.value.realName) { alert('请填写用户名和姓名'); return }
  if (!isEdit.value && !form.value.password) { alert('请输入密码'); return }
  saving.value = true
  try {
    if (isEdit.value) {
      await userApi.update({ pkUser: form.value.pkUser, realName: form.value.realName, status: form.value.status })
    } else {
      await userApi.add({ username: form.value.username, realName: form.value.realName, password: form.value.password, status: form.value.status })
    }
    // 分配角色
    const userId = form.value.pkUser || (list.value.find(u => u.username === form.value.username)?.pkUser)
    if (userId && form.value.selectedRoleIds?.length >= 0) {
      await roleApi.assignRoles(userId, form.value.selectedRoleIds)
    }
    showModal.value = false
    loadData()
  } catch (e: any) { alert(e.message || '保存失败') }
  finally { saving.value = false }
}

async function toggleStatus(item: any) {
  const newStatus = item.status === 1 ? 0 : 1
  try {
    await userApi.updateStatus(item.pkUser, newStatus)
    loadData()
  } catch (e: any) { alert(e.message || '操作失败') }
}

async function resetPwd(item: any) {
  if (!confirm(`确定重置 ${item.username} 的密码为 123456？`)) return
  try {
    await userApi.resetPassword(item.pkUser, '123456')
    alert('密码已重置为 123456')
  } catch (e: any) { alert(e.message || '重置失败') }
}

async function handleDelete(item: any) {
  if (!confirm(`确定删除用户 ${item.username}？`)) return
  try { await userApi.delete(item.pkUser); loadData() }
  catch (e: any) { alert(e.message || '删除失败') }
}

onMounted(() => { loadRoles(); loadData() })
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 200px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
.role-checkboxes { display: flex; flex-wrap: wrap; gap: 12px; }
.role-checkbox-label { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); cursor: pointer; }
.role-checkbox { width: 16px; height: 16px; accent-color: var(--brand-indigo); cursor: pointer; }
</style>
