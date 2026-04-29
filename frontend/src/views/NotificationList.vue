<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="flex gap-8">
        <select v-model="readFilter" class="form-select" style="width:140px;padding:7px 12px;font-size:13px" @change="loadData">
          <option value="">全部状态</option>
          <option :value="0">未读</option>
          <option :value="1">已读</option>
        </select>
      </div>
      <button class="btn btn-ghost btn-sm" @click="markAllRead">全部标为已读</button>
    </div>
    <div class="notification-list">
      <div v-for="item in list" :key="item.pkNotification" :class="['notification-item', { unread: !item.isRead }]" @click="markRead(item)">
        <div class="notification-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        </div>
        <div class="notification-body">
          <div class="notification-header">
            <span class="notification-title">{{ item.title || '系统通知' }}</span>
          </div>
          <p class="notification-content">{{ item.content }}</p>
          <span class="notification-time">{{ formatDate(item.createTime) }}</span>
        </div>
        <div v-if="!item.isRead" class="notification-dot"></div>
      </div>
      <div v-if="!list.length && !loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--text-quaternary)" stroke-width="1.5"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        <p style="color:var(--text-quaternary);font-size:13px;margin-top:12px">暂无通知</p>
      </div>
    </div>
    <div class="pagination" v-if="total > pageSize">
      <span class="text-caption">共 {{ total }} 条</span>
      <div class="pagination-pages">
        <button class="pagination-btn" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <span class="pagination-info">{{ pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pageNum >= totalPages" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { notificationApi } from '../api/modules'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const readFilter = ref('' as any)
const loading = ref(true)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return '刚刚'
  if (mins < 60) return `${mins}分钟前`
  const hours = Math.floor(mins / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  return d.toLocaleDateString('zh-CN')
}

async function loadData() {
  loading.value = true
  try {
    const userId = userStore.userInfo?.userId
    if (!userId) return
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value, targetUserId: userId }
    if (readFilter.value !== '') params.isRead = readFilter.value
    const res: any = await notificationApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error('加载通知失败', e) }
  finally { loading.value = false }
}

async function markRead(item: any) {
  if (item.isRead) return
  try {
    await notificationApi.markAsRead(item.pkNotification)
    item.isRead = 1
  } catch (e) { /* ignore */ }
}

async function markAllRead() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  try {
    await notificationApi.markAllAsRead(userId)
    list.value.forEach(i => i.isRead = 1)
  } catch (e: any) { alert(e.message || '操作失败') }
}

onMounted(loadData)
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
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
</style>
