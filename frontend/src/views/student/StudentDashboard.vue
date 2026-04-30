<template>
  <div class="student-dashboard">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎回来，{{ userStore.userInfo?.realName }}</h2>
      <p class="welcome-subtitle">这里是你的个人档案中心，可以查看和管理你的学籍档案信息。</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ archiveStats.total }}</div>
        <div class="stat-label">档案文件</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ archiveStats.approved }}</div>
        <div class="stat-label">已通过</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ archiveStats.pending }}</div>
        <div class="stat-label">待审核</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ archiveStats.rejected }}</div>
        <div class="stat-label">已驳回</div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="card">
        <h3 class="card-title">我的通知</h3>
        <div class="notification-list" v-if="notifications.length">
          <div class="notification-item" v-for="n in notifications" :key="n.id">
            <div class="notification-dot" :class="{ unread: !n.isRead }"></div>
            <div class="notification-content">
              <span class="notification-text">{{ n.title }}</span>
              <span class="notification-time">{{ n.time }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" v-else>
          <span class="empty-text">暂无通知</span>
        </div>
      </div>

      <div class="card">
        <h3 class="card-title">快捷操作</h3>
        <div class="quick-actions">
          <router-link to="/student/archives" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            <span>我的档案</span>
          </router-link>
          <router-link to="/student/profile" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            <span>个人信息</span>
          </router-link>
          <router-link to="/student/notifications" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
            <span>通知中心</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { notificationApi } from '../../api/modules'

const userStore = useUserStore()

const archiveStats = ref({ total: 0, approved: 0, pending: 0, rejected: 0 })
const notifications = ref<{ id: number; title: string; time: string; isRead: boolean }[]>([])

onMounted(async () => {
  try {
    // 获取学生档案统计
    const userId = userStore.userInfo?.userId
    if (userId) {
      // 尝试获取通知
      const nRes: any = await notificationApi.getList({ targetUserId: userId, pageNum: 1, pageSize: 5 })
      if (nRes.data?.list) {
        notifications.value = nRes.data.list.map((n: any) => ({
          id: n.pkNotification || n.id,
          title: n.title || n.content,
          time: formatTime(n.createTime),
          isRead: n.isRead === 1
        }))
      }
    }
  } catch (e) {
    console.error('加载学生仪表盘数据失败', e)
  }
})

function formatTime(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.student-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-section {
  padding: 24px 0 8px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.288px;
}

.welcome-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 6px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: var(--bg-panel);
  border: 1px solid var(--border-standard);
  border-radius: var(--radius-card);
  padding: 20px;
  box-shadow: var(--shadow-subtle);
}

.stat-value {
  font-size: 28px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.704px;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-tertiary);
  margin-top: 4px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -0.182px;
}

.notification-list {
  display: flex;
  flex-direction: column;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-subtle);
}
.notification-item:last-child {
  border-bottom: none;
}

.notification-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-quaternary);
  margin-top: 6px;
  flex-shrink: 0;
}
.notification-dot.unread {
  background: var(--accent-violet);
}

.notification-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.notification-text {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-secondary);
}

.notification-time {
  font-size: 12px;
  color: var(--text-quaternary);
}

.empty-state {
  padding: 24px;
  text-align: center;
}

.empty-text {
  font-size: 13px;
  color: var(--text-quaternary);
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--surface-03);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s ease;
  font-size: 14px;
  font-weight: 510;
}
.action-card:hover {
  background: var(--surface-05);
  color: var(--text-primary);
  border-color: var(--border-standard);
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
