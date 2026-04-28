<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="flex gap-8">
        <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索用户名/姓名" />
        <select v-model="statusFilter" class="form-select" style="width:140px;padding:7px 12px;font-size:13px">
          <option value="">全部状态</option>
          <option value="1">启用</option>
          <option value="0">禁用</option>
        </select>
      </div>
      <button class="btn btn-primary btn-sm" @click="openAdd">新增用户</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>用户名</th><th>姓名</th><th>手机号</th><th>角色</th><th>状态</th><th>创建时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkUser">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.username }}</td>
            <td>{{ item.realName }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td><span class="badge badge-neutral">{{ item.roleName }}</span></td>
            <td>
              <span :class="['badge', item.status === 1 ? 'badge-success' : 'badge-danger']">{{ item.status === 1 ? '启用' : '禁用' }}</span>
            </td>
            <td>{{ item.createTime }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-subtle btn-sm" @click="resetPwd(item)">重置密码</button>
                <button class="btn btn-danger btn-sm" @click="list = list.filter(i => i !== item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!filteredList.length"><td colspan="7" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <span class="text-caption">共 {{ filteredList.length }} 条</span>
    </div>
    <!-- 新增/编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content" style="max-width:520px">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}用户</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">用户名</label><input v-model="form.username" class="form-input" placeholder="请输入用户名" :disabled="isEdit" /></div>
          <div class="form-group"><label class="form-label">姓名</label><input v-model="form.realName" class="form-input" placeholder="请输入姓名" /></div>
          <div v-if="!isEdit" class="form-group"><label class="form-label">密码</label><input v-model="form.password" type="password" class="form-input" placeholder="请输入密码" /></div>
          <div class="form-group"><label class="form-label">手机号</label><input v-model="form.phone" class="form-input" placeholder="请输入手机号" /></div>
          <div class="form-group"><label class="form-label">角色</label>
            <select v-model="form.roleId" class="form-select">
              <option :value="null">请选择角色</option>
              <option :value="1">超级管理员</option>
              <option :value="2">教务管理员</option>
              <option :value="3">辅导员</option>
              <option :value="4">学生</option>
            </select>
          </div>
          <div class="form-group"><label class="form-label">状态</label>
            <select v-model="form.status" class="form-select">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button><button class="btn btn-primary btn-sm" @click="showModal = false">保存</button></div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
const list = ref<any[]>([])
const keyword = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const form = ref<any>({})
const filteredList = computed(() => {
  return list.value.filter(i => {
    const matchKeyword = !keyword.value || i.username.includes(keyword.value) || i.realName.includes(keyword.value)
    const matchStatus = statusFilter.value === '' || i.status === Number(statusFilter.value)
    return matchKeyword && matchStatus
  })
})
function openAdd() {
  isEdit.value = false
  form.value = { username: '', realName: '', password: '', phone: '', roleId: null, status: 1 }
  showModal.value = true
}
function openEdit(item: any) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}
function resetPwd(item: any) {
  if (confirm(`确定重置 ${item.username} 的密码？`)) {
    alert('密码已重置为 123456')
  }
}
onMounted(() => {
  list.value = [
    { pkUser: 1, username: 'admin', realName: '系统管理员', phone: '13800138000', roleName: '超级管理员', roleId: 1, status: 1, createTime: '2024-01-01' },
    { pkUser: 2, username: 'jwadmin', realName: '王教务', phone: '13800138001', roleName: '教务管理员', roleId: 2, status: 1, createTime: '2024-02-15' },
    { pkUser: 3, username: 'counselor01', realName: '李辅导', phone: '13800138002', roleName: '辅导员', roleId: 3, status: 1, createTime: '2024-03-10' },
    { pkUser: 4, username: 'student01', realName: '张三', phone: '13800138003', roleName: '学生', roleId: 4, status: 0, createTime: '2024-06-01' },
  ]
})
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 200px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
