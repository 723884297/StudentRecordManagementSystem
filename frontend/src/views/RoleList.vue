<template>
  <div class="page">
    <div class="page-toolbar">
      <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索角色名称" />
      <button class="btn btn-primary btn-sm" @click="openAdd">新增角色</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>角色名称</th><th>角色标识</th><th>描述</th><th>权限数</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkRole">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.roleName }}</td>
            <td><span class="badge badge-neutral" style="font-family:var(--font-mono);font-size:12px">{{ item.roleKey }}</span></td>
            <td>{{ item.description || '-' }}</td>
            <td><span class="badge badge-info">{{ item.permCount }} 项</span></td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="openEdit(item)">编辑</button>
                <button class="btn btn-subtle btn-sm" @click="openPerm(item)">权限配置</button>
                <button class="btn btn-danger btn-sm" @click="list = list.filter(i => i !== item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!filteredList.length"><td colspan="5" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <!-- 编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}角色</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">角色名称</label><input v-model="form.roleName" class="form-input" placeholder="如：教务管理员" /></div>
          <div class="form-group"><label class="form-label">角色标识</label><input v-model="form.roleKey" class="form-input" placeholder="如：admin" /></div>
          <div class="form-group"><label class="form-label">描述</label><textarea v-model="form.description" class="form-input" rows="3" placeholder="请输入角色描述"></textarea></div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button><button class="btn btn-primary btn-sm" @click="showModal = false">保存</button></div>
      </div>
    </div>
    <!-- 权限配置弹窗 -->
    <div v-if="showPermModal" class="modal-overlay" @click.self="showPermModal = false">
      <div class="modal-content" style="max-width:480px">
        <div class="modal-header"><h3 class="modal-title">权限配置 - {{ currentRole?.roleName }}</h3><button class="btn-icon" @click="showPermModal = false">✕</button></div>
        <div class="modal-body">
          <div v-for="perm in permList" :key="perm.id" class="perm-item">
            <label class="perm-label">
              <input type="checkbox" v-model="perm.checked" class="perm-checkbox" />
              <span class="text-small-medium" style="color:var(--text-primary)">{{ perm.name }}</span>
              <span class="text-caption" style="margin-left:8px">{{ perm.key }}</span>
            </label>
          </div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showPermModal = false">取消</button><button class="btn btn-primary btn-sm" @click="showPermModal = false">保存</button></div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
const list = ref<any[]>([])
const keyword = ref('')
const showModal = ref(false)
const showPermModal = ref(false)
const isEdit = ref(false)
const form = ref<any>({})
const currentRole = ref<any>(null)
const permList = ref<any[]>([])
const filteredList = computed(() => {
  if (!keyword.value) return list.value
  return list.value.filter(i => i.roleName.includes(keyword.value))
})
function openAdd() {
  isEdit.value = false
  form.value = { roleName: '', roleKey: '', description: '' }
  showModal.value = true
}
function openEdit(item: any) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}
function openPerm(item: any) {
  currentRole.value = item
  permList.value = [
    { id: 1, name: '用户管理', key: 'user:manage', checked: true },
    { id: 2, name: '学生管理', key: 'student:manage', checked: true },
    { id: 3, name: '档案管理', key: 'archive:manage', checked: true },
    { id: 4, name: '学院管理', key: 'college:manage', checked: false },
    { id: 5, name: '角色管理', key: 'role:manage', checked: false },
    { id: 6, name: '通知管理', key: 'notification:manage', checked: true },
    { id: 7, name: '系统设置', key: 'system:setting', checked: false },
  ]
  showPermModal.value = true
}
onMounted(() => {
  list.value = [
    { pkRole: 1, roleName: '超级管理员', roleKey: 'super_admin', description: '拥有系统所有权限', permCount: 7 },
    { pkRole: 2, roleName: '教务管理员', roleKey: 'edu_admin', description: '管理学生、学院、专业、班级', permCount: 4 },
    { pkRole: 3, roleName: '辅导员', roleKey: 'counselor', description: '管理所属班级学生档案', permCount: 3 },
    { pkRole: 4, roleName: '学生', roleKey: 'student', description: '查看个人档案信息', permCount: 1 },
  ]
})
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 240px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.perm-item { padding: 8px 0; border-bottom: 1px solid var(--border-subtle); }
.perm-item:last-child { border-bottom: none; }
.perm-label { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.perm-checkbox { width: 16px; height: 16px; accent-color: var(--brand-indigo); cursor: pointer; }
</style>
