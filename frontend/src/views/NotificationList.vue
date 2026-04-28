<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="flex gap-8">
        <select v-model="typeFilter" class="form-select" style="width:140px;padding:7px 12px;font-size:13px">
          <option value="">全部类型</option>
          <option value="system">系统通知</option>
          <option value="archive">档案通知</option>
          <option value="audit">审核通知</option>
        </select>
        <select v-model="readFilter" class="form-select" style="width:140px;padding:7px 12px;font-size:13px">
          <option value="">全部状态</option>
          <option value="0">未读</option>
          <option value="1">已读</option>
        </select>
      </div>
      <button class="btn btn-ghost btn-sm" @click="markAllRead">全部标为已读</button>
    </div>
    <div class="notification-list">
      <div v-for="item in filteredList" :key="item.pkNotification" :class="['notification-item', { unread: !item.isRead }]" @click="item.isRead = 1">
        <div class="notification-icon">
          <svg v-if="item.type === 'system'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <svg v-else-if="item.type === 'archive'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        </div>
        <div class="notification-body">
          <div class="notification-header">
            <span class="notification-title">{{ item.title }}</span>
            <span :class="['badge', typeBadgeClass(item.type)]">{{ typeLabel(item.type) }}</span>
          </div>
          <p class="notification-content">{{ item.content }}</p>
          <span class="notification-time">{{ item.createTime }}</span>
        </div>
        <div v-if="!item.isRead" class="notification-dot"></div>
      </div>
      <div v-if="!filteredList.length" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--text-quaternary)" stroke-width="1.5"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        <p style="color:var(--text-quaternary);font-size:13px;margin-top:12px">暂无通知</p>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
const list = ref<any[]>([])
const typeFilter = ref('')
const readFilter = ref('')
const filteredList = computed(() => {
  return list.value.filter(i => {
    const matchType = !typeFilter.value || i.type === typeFilter.value
    const matchRead = readFilter.value === '' || i.isRead === Number(readFilter.value)
    return matchType && matchRead
  })
})
function typeLabel(type: string) {
  return { system: '系统', archive: '档案', audit: '审核' }[type] || type
}
function typeBadgeClass(type: string) {
  return { system: 'badge-info', archive: 'badge-neutral', audit: 'badge-warning' }[type] || 'badge-neutral'
}
function markAllRead() {
  list.value.forEach(i => i.isRead = 1)
}
onMounted(() => {
  list.value = [
    { pkNotification: 1, title: '系统维护通知', content: '系统将于今晚22:00-23:00进行维护升级，届时将暂停服务。', type: 'system', isRead: 0, createTime: '2024-06-15 10:30' },
    { pkNotification: 2, title: '档案审核通过', content: '您提交的学历证明文件已通过审核。', type: 'audit', isRead: 0, createTime: '2024-06-14 15:20' },
    { pkNotification: 3, title: '新档案上传提醒', content: '张三上传了成绩单档案，请及时审核。', type: 'archive', isRead: 1, createTime: '2024-06-13 09:15' },
    { pkNotification: 4, title: '密码修改成功', content: '您的账号密码已成功修改，如非本人操作请及时联系管理员。', type: 'system', isRead: 1, createTime: '2024-06-12 18:45' },
    { pkNotification: 5, title: '档案审核被退回', content: '您提交的身份证复印件因清晰度不足被退回，请重新上传。', type: 'audit', isRead: 0, createTime: '2024-06-11 14:00' },
  ]
})
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.notification-list { display: flex; flex-direction: column; gap: 1px; }
.notification-item {
  display: flex; align-items: flex-start; gap: 12px; padding: 16px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px;
  cursor: pointer; transition: background 0.15s;
}
.notification-item:hover { background: var(--bg-secondary); }
.notification-item.unread { background: rgba(94, 106, 210, 0.04); border-color: rgba(94, 106, 210, 0.12); }
.notification-icon { color: var(--text-tertiary); flex-shrink: 0; margin-top: 2px; }
.notification-item.unread .notification-icon { color: var(--accent-violet); }
.notification-body { flex: 1; min-width: 0; }
.notification-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.notification-title { font-size: 14px; font-weight: 510; color: var(--text-primary); }
.notification-content { font-size: 13px; color: var(--text-tertiary); line-height: 1.5; margin: 0; }
.notification-time { font-size: 12px; color: var(--text-quaternary); margin-top: 6px; display: inline-block; }
.notification-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--accent-violet); flex-shrink: 0; margin-top: 6px; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 0; }
</style>
