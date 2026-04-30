<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="card">
        <h3 class="card-title">学籍状态分布</h3>
        <div class="status-list">
          <div class="status-row" v-for="item in statusData" :key="item.label">
            <div class="status-info">
              <span class="status-dot" :style="{ background: item.color }"></span>
              <span class="status-name">{{ item.label }}</span>
            </div>
            <div class="status-bar-wrap">
              <div class="status-bar" :style="{ width: item.percent + '%', background: item.color }"></div>
            </div>
            <span class="status-count">{{ item.count }}</span>
          </div>
        </div>
      </div>

      <div class="card">
        <h3 class="card-title">近期操作</h3>
        <div class="activity-list">
          <div class="activity-item" v-for="(act, i) in activities" :key="i">
            <div class="activity-dot"></div>
            <div class="activity-content">
              <span class="activity-text">{{ act.text }}</span>
              <span class="activity-time">{{ act.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { dashboardApi } from '../api/modules'

const stats = ref([
  { label: '学生总数', value: '-' },
  { label: '在读学生', value: '-' },
  { label: '档案文件', value: '-' },
  { label: '待审核', value: '-' },
])

interface StatusItem {
  label: string
  count: number
  percent: number
  color: string
}

const statusColorMap: Record<string, string> = {
  '在读': '#10b981',
  '休学': '#f59e0b',
  '退学': '#ef4444',
  '毕业': '#5e6ad2',
}

const statusData = ref<StatusItem[]>([])

interface Activity {
  text: string
  time: string
}

const activities = ref<Activity[]>([])

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

onMounted(async () => {
  try {
    const res: any = await dashboardApi.getStats()
    const data = res.data

    // 学生统计
    const studentTotal = data.studentTotal || 0
    const distribution: { label: string; count: number }[] = data.statusDistribution || []
    const activeCount = distribution.find((d: any) => d.label === '在读')?.count || 0

    // 档案统计
    const archiveTotal = data.archiveTotal || 0
    const pendingAudit = data.pendingAudit || 0

    stats.value = [
      { label: '学生总数', value: studentTotal.toLocaleString() },
      { label: '在读学生', value: activeCount.toLocaleString() },
      { label: '档案文件', value: archiveTotal.toLocaleString() },
      { label: '待审核', value: pendingAudit.toLocaleString() },
    ]

    // 学籍状态分布
    if (distribution.length > 0) {
      const maxCount = Math.max(...distribution.map((d: any) => Number(d.count)))
      statusData.value = distribution.map((d: any) => ({
        label: d.label,
        count: Number(d.count),
        percent: maxCount > 0 ? Math.round((Number(d.count) / maxCount) * 100) : 0,
        color: statusColorMap[d.label] || '#6b7280',
      }))
    }

    // 近期操作
    const logs: any[] = data.recentActivities || []
    activities.value = logs.map((log: any) => ({
      text: `${log.description || log.operation}`,
      time: formatTime(log.createTime),
    }))
  } catch (e) {
    console.error('加载仪表盘数据失败', e)
  }
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
  letter-spacing: -0.13px;
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

/* Status Distribution */
.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 60px;
  flex-shrink: 0;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-name {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-secondary);
}

.status-bar-wrap {
  flex: 1;
  height: 6px;
  background: var(--surface-05);
  border-radius: var(--radius-pill);
  overflow: hidden;
}

.status-bar {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width 0.6s ease;
}

.status-count {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-tertiary);
  width: 50px;
  text-align: right;
}

/* Activity List */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-subtle);
}
.activity-item:last-child {
  border-bottom: none;
}

.activity-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent-violet);
  margin-top: 6px;
  flex-shrink: 0;
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.activity-text {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-secondary);
}

.activity-time {
  font-size: 12px;
  color: var(--text-quaternary);
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
